package com.openclassrooms.tourguide.service;

import gpsUtil.location.Attraction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GpsUtilService {

    public List<Attraction> getAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Universal Studios", "Los Angeles", "CA", 34.138117D, -118.353378D));
        attractions.add(new Attraction("Griffith Observatory", "Los Angeles", "CA", 34.118434D, -118.300393D));
        attractions.add(new Attraction("Hollywood Sign", "Los Angeles", "CA", 34.134115D, -118.321548D));
        attractions.add(new Attraction("Santa Monica Pier", "Santa Monica", "CA", 34.009242D, -118.497604D));

        return attractions;
    }
}
