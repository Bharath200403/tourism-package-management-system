package com.tourism.controller;

import com.tourism.dto.request.TripPlannerRequest;
import com.tourism.dto.response.TourPackageResponseDTO;
import com.tourism.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip-planner")
@RequiredArgsConstructor
public class TripPlannerController {

    private final RecommendationService recommendationService;

    @PostMapping("/plan")
    public ResponseEntity<List<TourPackageResponseDTO>> plan(@RequestBody TripPlannerRequest request) {
        return ResponseEntity.ok(recommendationService.planTrip(request));
    }
}
