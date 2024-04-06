package com.example.Astar.controller;

import com.example.Astar.model.ShortestPathResult;
import com.example.Astar.service.ShortestPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class ShortestPathController {

    private final ShortestPathService shortestPathService;

    @Autowired
    public ShortestPathController(ShortestPathService shortestPathService) {
        this.shortestPathService = shortestPathService;
    }

    @GetMapping("/shortest-path")
    public ShortestPathResult findShortestPath(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return shortestPathService.findShortestPath(start, end);
    }
}
