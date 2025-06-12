package com.openclassrooms.tourguide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class RewardsService {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private int defaultProximityBuffer = 1;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;

	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	private final ExecutorService executorService;
	private final Semaphore semaphore;
	private final Map<UUID, Integer> attractionRewardsCache = new ConcurrentHashMap<>();

	private final AtomicInteger rewardCentralCallCount = new AtomicInteger(0);
	private final AtomicLong rewardCentralTotalTimeMs = new AtomicLong(0);


	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardsCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardsCentral;
		this.executorService = Executors.newFixedThreadPool(128);
		this.semaphore = new Semaphore(128);

		// Initialisation du cache global (1 seule fois)
		gpsUtil.getAttractions().forEach(attraction ->
				attractionRewardsCache.put(attraction.attractionId,
						rewardsCentral.getAttractionRewardPoints(attraction.attractionId, UUID.randomUUID()))
		);
	}

	// Setter pour ajuster la distance de proximité si besoin
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		this.proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtil.getAttractions();//.stream().limit(1).toList();
		//System.out.println("Total attractions: " + attractions.size());

		Set<String> rewardedAttractions = user.getUserRewards().stream()
				.map(r -> r.attraction.attractionName)
				.collect(Collectors.toSet());
		//System.out.println("Already rewarded: " + rewardedAttractions);

		for (VisitedLocation visitedLocation : user.getVisitedLocations()) {

			for (Attraction attraction : attractions) {

				if (!rewardedAttractions.contains(attraction.attractionName)
						&& nearAttraction(visitedLocation, attraction)) {
					Integer points = attractionRewardsCache.get(attraction.attractionId);
					if (points == null) {
						long t0 = System.currentTimeMillis();
						points = rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
						long t1 = System.currentTimeMillis();
						rewardCentralCallCount.incrementAndGet();
						rewardCentralTotalTimeMs.addAndGet(t1 - t0);

						attractionRewardsCache.put(attraction.attractionId, points);
					}
					user.addUserReward(new UserReward(visitedLocation, attraction, points));

					//return;
				}
			}
		}

	}

	public void calculateRewardsInBatches(List<User> users, int batchSize) {
		int totalUsers = users.size();
		for (int i = 0; i < totalUsers; i += batchSize) {
			int end = Math.min(i + batchSize, totalUsers);
			List<User> batch = users.subList(i, end);

			List<CompletableFuture<Void>> futures = batch.stream()
					.map(user -> CompletableFuture.runAsync(() -> {
						try {
							semaphore.acquire();
							calculateRewards(user);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} finally {
							semaphore.release();
						}
					}, executorService))
					.toList();

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		}
		System.out.println("RewardCentral - Appels total : " + rewardCentralCallCount.get());
		System.out.println("RewardCentral - Temps cumulé : " + rewardCentralTotalTimeMs.get() + " ms");
		if (rewardCentralCallCount.get() > 0) {
			System.out.println("RewardCentral - Durée moyenne par appel : " + (rewardCentralTotalTimeMs.get() / rewardCentralCallCount.get()) + " ms");
		}

	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) <= attractionProximityRange;
	}

	// <-- Ajout de la méthode manquante
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
	}

	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}

	// Getter performant pour le cache, utile si besoin ailleurs
	public int getRewardPoints(Attraction attraction, User user) {
		Integer points = attractionRewardsCache.get(attraction.attractionId);
		if (points == null) {
			points = rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
			attractionRewardsCache.put(attraction.attractionId, points);
		}
		return points;
	}
}
