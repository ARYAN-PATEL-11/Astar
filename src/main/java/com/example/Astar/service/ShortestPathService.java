package com.example.Astar.service;

import com.example.Astar.model.ShortestPathResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class ShortestPathService {

    private static final Logger logger = LogManager.getLogger(ShortestPathService.class);

    private static final Map<String, List<Edge>> graph = new HashMap<>();

    static {
        String graphData = "{\n" +
                "  \"p1\": [{\"destination\": \"p2\", \"weight\": 48}, {\"destination\": \"MG\", \"weight\": 64}, {\"destination\": \"FIG\", \"weight\": 10}],\n" +
                "  \"p2\": [{\"destination\": \"p1\", \"weight\": 48}, {\"destination\": \"p3\", \"weight\": 36}],\n" +
                "  \"p3\": [{\"destination\": \"p2\", \"weight\": 36}, {\"destination\": \"p4\", \"weight\": 19}, {\"destination\": \"p19\", \"weight\": 55}],\n" +
                "  \"p4\": [{\"destination\": \"p3\", \"weight\": 19}, {\"destination\": \"p5\", \"weight\": 25}, {\"destination\": \"IC\", \"weight\": 29}],\n" +
                "  \"p5\": [{\"destination\": \"p4\", \"weight\": 25}, {\"destination\": \"p6\", \"weight\": 76}],\n" +
                "  \"p6\": [{\"destination\": \"p5\", \"weight\": 76}, {\"destination\": \"p7\", \"weight\": 46}],\n" +
                "  \"p7\": [{\"destination\": \"p6\", \"weight\": 46}, {\"destination\": \"p8\", \"weight\": 58}, {\"destination\": \"p12\", \"weight\": 38}, {\"destination\": \"p13\", \"weight\": 51}],\n" +
                "  \"p8\": [{\"destination\": \"p7\", \"weight\": 58}, {\"destination\": \"p9\", \"weight\": 91}],\n" +
                "  \"p9\": [{\"destination\": \"Exit\", \"weight\": 15}, {\"destination\": \"p8\", \"weight\": 91}],\n" +
                "  \"p10\": [{\"destination\": \"Exit\", \"weight\": 31}, {\"destination\": \"p11\", \"weight\": 39}],\n" +
                "  \"p11\": [{\"destination\": \"p13\", \"weight\": 18}, {\"destination\": \"p10\", \"weight\": 39}],\n" +
                "  \"p12\": [{\"destination\": \"p7\", \"weight\": 38}, {\"destination\": \"p13\", \"weight\": 13}, {\"destination\": \"Gym\", \"weight\": 5}],\n" +
                "  \"p13\": [{\"destination\": \"p7\", \"weight\": 51}, {\"destination\": \"p11\", \"weight\": 18}, {\"destination\": \"p12\", \"weight\": 13}, {\"destination\": \"p14\", \"weight\": 70}, {\"destination\": \"TC\", \"weight\": 49}],\n" +
                "  \"p14\": [{\"destination\": \"p13\", \"weight\": 70}, {\"destination\": \"IC\", \"weight\": 75}, {\"destination\": \"Ram\", \"weight\": 45}, {\"destination\": \"Recep\", \"weight\": 25}, {\"destination\": \"TC\", \"weight\": 21}],\n" +
                "  \"p15\": [{\"destination\": \"Ram\", \"weight\": 52}, {\"destination\": \"p16\", \"weight\": 46}],\n" +
                "  \"p16\": [{\"destination\": \"p15\", \"weight\": 46}, {\"destination\": \"p17\", \"weight\": 90}],\n" +
                "  \"p17\": [{\"destination\": \"p16\", \"weight\": 90}, {\"destination\": \"p18\", \"weight\": 47}],\n" +
                "  \"p18\": [{\"destination\": \"p17\", \"weight\": 47}, {\"destination\": \"Ram\", \"weight\": 43}, {\"destination\": \"p19\", \"weight\": 67}, {\"destination\": \"MG\", \"weight\": 41}, {\"destination\": \"FG\", \"weight\": 17}],\n" +
                "  \"p19\": [{\"destination\": \"FIG\", \"weight\": 3}, {\"destination\": \"Recep\", \"weight\": 5}, {\"destination\": \"p18\", \"weight\": 67}, {\"destination\": \"p3\", \"weight\": 55}],\n" +
                "  \"MG\": [{\"destination\": \"FG\", \"weight\": 23}, {\"destination\": \"p1\", \"weight\": 64}, {\"destination\": \"p18\", \"weight\": 41}],\n" +
                "  \"IC\": [{\"destination\": \"p4\", \"weight\": 29}, {\"destination\": \"p14\", \"weight\": 75}],\n" +
                "  \"Recep\": [{\"destination\": \"p19\", \"weight\": 5}, {\"destination\": \"p14\", \"weight\": 25}],\n" +
                "  \"FIG\": [{\"destination\": \"p1\", \"weight\": 10}, {\"destination\": \"p19\", \"weight\": 3}],\n" +
                "  \"FG\": [{\"destination\": \"MG\", \"weight\": 23}, {\"destination\": \"p18\", \"weight\": 17}],\n" +
                "  \"Ram\": [{\"destination\": \"p14\", \"weight\": 45}, {\"destination\": \"p15\", \"weight\": 52}, {\"destination\": \"p18\", \"weight\": 43}],\n" +
                "  \"TC\": [{\"destination\": \"p13\", \"weight\": 49}, {\"destination\": \"p14\", \"weight\": 21}],\n" +
                "  \"Gym\": [{\"destination\": \"Mess\", \"weight\": 15}, {\"destination\": \"p12\", \"weight\": 5}],\n" +
                "  \"Mess\": [{\"destination\": \"Gym\", \"weight\": 15}],\n" +
                "  \"Exit\": [{\"destination\": \"p9\", \"weight\": 15}, {\"destination\": \"p10\", \"weight\": 31}]\n" +
                "}";

        Map<String, List<Map<String, Object>>> parsedGraph = parseGraphData(graphData);
        for (Map.Entry<String, List<Map<String, Object>>> entry : parsedGraph.entrySet()) {
            String vertex = entry.getKey();
            List<Edge> edges = new ArrayList<>();
            for (Map<String, Object> edgeData : entry.getValue()) {
                String destination = (String) edgeData.get("destination");
                int weight = (int) edgeData.get("weight");
                edges.add(new Edge(destination, weight));
            }
            graph.put(vertex, edges);
        }
    }

    public ShortestPathResult findShortestPath(String start, String end) {
        logger.info("Finding shortest path from {} to {}", start, end);
        Map<String, Integer> gScore = new HashMap<>();
        Map<String, Integer> fScore = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();

        PriorityQueue<String> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));
        openSet.add(start);

        gScore.put(start, 0);
        fScore.put(start, heuristicCostEstimate(start, end));

        while (!openSet.isEmpty()) {
            String current = openSet.poll();

            if (current.equals(end)) {
                return reconstructPath(start,cameFrom, end);
            }

            List<Edge> neighbors = graph.get(current);
            if (neighbors != null) {
                for (Edge neighbor : neighbors) {
                    int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + neighbor.weight;
                    if (tentativeGScore < gScore.getOrDefault(neighbor.destination, Integer.MAX_VALUE)) {
                        cameFrom.put(neighbor.destination, current);
                        gScore.put(neighbor.destination, tentativeGScore);
                        fScore.put(neighbor.destination, tentativeGScore + heuristicCostEstimate(neighbor.destination, end));

                        if (!openSet.contains(neighbor.destination)) {
                            openSet.add(neighbor.destination);
                        }
                    }
                }
            }
        }
//        logger.info("Shortest path found: {}", shortestPath);

        return new ShortestPathResult(Collections.emptyList()); // No path found
    }

    private int heuristicCostEstimate(String start, String end) {
        // Get the coordinates of the start and end vertices
        double[] startCoords = getCoordinates(start);
        double[] endCoords = getCoordinates(end);

        // Calculate Euclidean distance between the vertices
        double distance = Math.sqrt(Math.pow(endCoords[0] - startCoords[0], 2) + Math.pow(endCoords[1] - startCoords[1], 2));

        // Return the rounded distance as the heuristic estimate
        return (int) Math.round(distance);
    }
    private double[] getCoordinates(String vertex) {
        // Map of vertex names to coordinates
        Map<String, double[]> vertexCoordinates = new HashMap<>();
        vertexCoordinates.put("p1", new double[]{12.845403, 77.663211});
        vertexCoordinates.put("p2", new double[]{12.845378, 77.662713});
        vertexCoordinates.put("p3", new double[]{12.845054, 77.662687});
        vertexCoordinates.put("p4", new double[]{12.844885, 77.662690});
        vertexCoordinates.put("p5", new double[]{12.844875546679772, 77.66243298219361});
        vertexCoordinates.put("p6", new double[]{12.844199, 77.662433});
        vertexCoordinates.put("p7", new double[]{12.844080, 77.662858});
        vertexCoordinates.put("p8", new double[]{12.843593, 77.662676});
        vertexCoordinates.put("p9", new double[]{12.843240, 77.663470});
        vertexCoordinates.put("p10", new double[]{12.843672, 77.663658});
        vertexCoordinates.put("p11", new double[]{12.843785, 77.663293});
        vertexCoordinates.put("p12", new double[]{12.843855, 77.663158});
        vertexCoordinates.put("p13", new double[]{12.843950, 77.663302});
        vertexCoordinates.put("p14", new double[]{12.844581, 77.663379});
        vertexCoordinates.put("p15", new double[]{12.844123, 77.663842});
        vertexCoordinates.put("p16", new double[]{12.844130, 77.664261});
        vertexCoordinates.put("p17", new double[]{12.844931, 77.664245});
        vertexCoordinates.put("p18", new double[]{12.845029, 77.663798});
        vertexCoordinates.put("p19", new double[]{12.845045, 77.663185});
        vertexCoordinates.put("MG", new double[]{12.845407, 77.663799});
        vertexCoordinates.put("IC", new double[]{12.844614, 77.662680});
        vertexCoordinates.put("Recep", new double[]{12.844810544235095, 77.66316521297155});
        vertexCoordinates.put("FIG", new double[]{12.845168263868324, 77.66316000401639});
        vertexCoordinates.put("FG", new double[]{12.845184, 77.663984});
        vertexCoordinates.put("Ram", new double[]{12.844585, 77.663767});
        vertexCoordinates.put("TC", new double[]{12.844403, 77.663484});
        vertexCoordinates.put("Gym", new double[]{12.843733002476245, 77.66308593181043});
        vertexCoordinates.put("Mess", new double[]{12.843437794853076, 77.66333069400783});
        vertexCoordinates.put("Exit", new double[]{12.843367225974232, 77.66358402305961});

        // Get coordinates for the given vertex
        double[] coords = vertexCoordinates.get(vertex);
        if (coords != null) {
            return coords;
        } else {
            // Return default coordinates if vertex not found
            return new double[]{0, 0};
        }
    }

    private ShortestPathResult reconstructPath(String start, Map<String, String> cameFrom, String current) {
        List<String> path = new ArrayList<>();
         // Add the start vertex to the path
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        path.add(0,start);
        return new ShortestPathResult(path);
    }


    private static Map<String, List<Map<String, Object>>> parseGraphData(String graphData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    graphData,
                    new TypeReference<Map<String, List<Map<String, Object>>>>() {}
            );
        } catch (JsonProcessingException e) {
            // Handle parsing exception
            e.printStackTrace();
            logger.error("Error parsing graph data: {}", e.getMessage());
            return null;
        }
    }

    private static class Edge {
        String destination;
        int weight;

        public Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Other methods...
}
