package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

public class TestPerformance {

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */


	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(100000); // augmenter progressivement

		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// ✅ Appel à la méthode optimisée (au lieu du for)
		tourGuideService.trackAllUsersLocationParallel();

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}



	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setTestMode(true);


		// Définition explicite du nombre d'utilisateurs pour le test
		InternalTestHelper.setInternalUserNumber(100); // Modifie selon le besoin (30000 ou 100000)

		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		// Ajout d'une attraction visitée à chaque utilisateur pour garantir l'attribution d'une récompense
		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = tourGuideService.getAllUsers();

		allUsers.forEach(u -> u.addToVisitedLocations(
				new VisitedLocation(u.getUserId(), attraction, new Date())));

		// Initialiser le chronomètre pour mesurer le temps d'exécution
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		// Appel unique et batché (par lots) aux récompenses
		rewardsService.calculateRewardsInBatches(allUsers, 2000);

		// Fin du chronométrage
		stopWatch.stop();

		// Arrêt explicite du tracker pour éviter des threads inutiles
		tourGuideService.tracker.stopTracking();

		// Vérification de la bonne attribution des récompenses
		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		// Affichage du temps d'exécution
		long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime());
		System.out.println("highVolumeGetRewards: Time Elapsed: " + durationInSeconds + " seconds.");

		// Vérification que le temps total est dans les limites imposées (< 20 minutes)
		assertTrue(durationInSeconds <= TimeUnit.MINUTES.toSeconds(20));
	}



}


