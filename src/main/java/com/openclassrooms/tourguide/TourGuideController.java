package com.openclassrooms.tourguide;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.service.GpsUtilService;
import com.openclassrooms.tourguide.service.RewardsService;
import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;



    @Autowired
    private GpsUtil gpsUtil;


    @Autowired
    private RewardsService rewardsService;


    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public List<NearbyAttractionDTO> getNearbyAttractions(@RequestParam String userName) {
        User user = getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        Location userLocation = visitedLocation.location;

        // Récupérer toutes les attractions
        List<Attraction> attractions = gpsUtil.getAttractions();

        // Trier les attractions par distance et limiter à 5 attractions
        return attractions.stream()
                .sorted(Comparator.comparingDouble(a -> rewardsService.getDistance(a, userLocation)))
                .limit(5)  // Retourner les 5 plus proches

                .map(attraction -> {
                    double distance = rewardsService.getDistance(attraction, userLocation);
                    int rewardPoints = rewardsService.getRewardPoints(attraction, user);
                    return new NearbyAttractionDTO(
                            attraction.attractionName,
                            attraction.latitude,
                            attraction.longitude,
                            userLocation.latitude,
                            userLocation.longitude,
                            distance,
                            rewardPoints
                    );
                })
                .collect(Collectors.toList());
    }



    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {

        return tourGuideService.getUser(userName);
    }
   

}