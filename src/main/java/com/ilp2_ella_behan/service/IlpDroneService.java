package com.ilp2_ella_behan.service;

import com.ilp2_ella_behan.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.core.annotation.AnnotationUtils.getValue;

@Service
public class IlpDroneService {
    private final RestTemplate restTemplate;
    private final String ilpEndpoint;

    @Autowired
    public IlpDroneService(RestTemplate restTemplate, @Qualifier("ilpEndpoint") String ilpEndpoint) {
        this.restTemplate = restTemplate;
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<Drone> getAllDrones() {
        String url = ilpEndpoint + "/drones";
        Drone[] response = restTemplate.getForObject(url, Drone[].class);
        if (response == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(response);
    }

    public List<String> getDroneIdsWithCooling(boolean cooling) {
        return getAllDrones().stream()
                .filter(d -> d.getCapability() != null && d.getCapability().getCooling() == cooling)
                .map(Drone::getId).collect(Collectors.toList());
    }

    public Optional<Drone> getDroneById(String id) {
        List<Drone> drones = getAllDrones();
        return drones.stream()
                .filter(d -> id.equals(d.getId()))
                .findFirst();
    }

    public List<String> queryAsPath(String attributeName, String attributeValue) {
        String url = ilpEndpoint + "/drones";

        List<Map<String, Object>> drones =
                restTemplate.getForObject(url, List.class);
        if (drones == null) {
            return Collections.emptyList();
        }
         return drones.stream()
                 .filter(d-> droneMatchesAttribute(d, attributeName, attributeValue))
                 .map(d-> String.valueOf(d.get("id")))
                 .collect(Collectors.toList());
    }

    public List<String> query(List<QueryAttribute> queries) {
        String url = ilpEndpoint + "/drones";

        List<Map<String, Object>> drones =
                restTemplate.getForObject(url, List.class);

        if (drones == null || queries == null || queries.isEmpty()) {
            return Collections.emptyList();
        }

        return drones.stream()
                .filter(d-> droneMatchesAllAttributes(d, queries))
                .map(d-> String.valueOf(d.get("id")))
                .collect(Collectors.toList());
    }

    private boolean droneMatchesAllAttributes(Map<String, Object> drone, List<QueryAttribute> queries) {
        for (QueryAttribute q : queries) {
            if(!droneMatchesSingle(drone, q)) {
                return false;
            }
        }
        return true;
    }

    private Object findAttributeValue(Map<String, Object> drone, String attributeName) {
        Object raw = drone.get(attributeName);

        if (raw == null) {
            Object capsObj = drone.get("capability");
            if (capsObj instanceof Map) {
                Map<String, Object> caps = (Map<String, Object>) capsObj;
                raw = caps.get(attributeName);
            }
        }
        return raw;
    }

    private boolean droneMatchesSingle(Map<String, Object> drone, QueryAttribute q) {
        String attributeName = q.getAttribute();
        String op = q.getOperator();
        String valueStr = q.getValue();

        Object raw = findAttributeValue(drone, attributeName);
        if (raw == null) {
            return false;
        }

        if (raw instanceof Number) {
            double actual = ((Number) raw).doubleValue();
            double expected;
            try {
                expected = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                return false;
            }

            return switch (op) {
                case "=" -> Double.compare(actual, expected) == 0;
                case "!=" -> Double.compare(actual, expected) != 0;
                case "<" -> actual < expected;
                case ">" -> actual > expected;
                default -> false;
            };
        }
        if (raw instanceof Boolean){
            boolean actual = (Boolean) raw;
            boolean expected = Boolean.parseBoolean(valueStr);
            return switch (op) {
                case "=" -> actual == expected;
                case "!=" -> actual != expected;
                default -> false;
            };
        }

        String actualStr = raw.toString();
        return switch (op) {
            case "=" -> actualStr.equals(valueStr);
            case "!=" -> !actualStr.equals(valueStr);
            default -> false;
        };

    }


    private boolean droneMatchesAttribute(Map<String, Object> drone,
                                          String attributeName,
                                          String attributeValue) {
        Object raw = drone.get(attributeName);
        System.out.println("root '" + attributeName + "' = " + raw);

        if (raw == null) {
            Object capsObj = drone.get("capability");
            if (capsObj instanceof Map) {
                Map<String, Object> caps = (Map<String, Object>) capsObj;
                raw = caps.get(attributeName);
                System.out.println("capability'" + attributeName + "'=" + raw);
            }
        }

        if (raw == null) {
            return false;
        }

        if (raw instanceof Number) {
            try {
                double expected = Double.parseDouble(attributeValue);
                double actual = ((Number) raw).doubleValue();
                return Double.compare(actual, expected) == 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (raw instanceof Boolean) {
            boolean expected = Boolean.parseBoolean(attributeValue);
            return ((Boolean) raw) == expected;
        }

        return raw.toString().equals(attributeValue);


    }

    public Map<String, List<Availability>> getAvailabilityByDroneId() {

        String url = ilpEndpoint + "/drone-for-service-point";

        DroneForServicePoint[] response =
                restTemplate.getForObject(url, DroneForServicePoint[].class);

        Map<String, List<Availability>> map = new HashMap<>();

        for (DroneForServicePoint sp : response) {
            for (DroneAvailabilityItem item : sp.getDrones()) {
                map.put(item.getId(), item.getAvailability());
            }
        }

        return map;
    }

    public List<String> queryAvailableDrones(List<MedDispatchRec> request) {
        return getAvailableDronesCombined(request);
    }
    public List<String> getAvailableDronesCapability(List<MedDispatchRec> request) {
        String url = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones =
                restTemplate.getForObject(url, List.class);

        if (drones == null) {
            return Collections.emptyList();
        }

        return drones.stream()
                .filter(d -> {
                    Map<String, Object> capability = (Map<String, Object>) d.get("capability");
                    if (capability == null) return false;

                    boolean droneCooling = Boolean.TRUE.equals(capability.get("cooling"));
                    boolean droneHeating = Boolean.TRUE.equals(capability.get("heating"));

                    double droneCapacity = ((Number) capability.get("capacity")).doubleValue();

                    double costInitial = ((Number) capability.get("costInitial")).doubleValue();
                    double costFinal = ((Number) capability.get("costFinal")).doubleValue();
                    double costPerMove = ((Number) capability.get("costPerMove")).doubleValue();
                    double maxMoves = ((Number) capability.get("maxMoves")).doubleValue();

                    double droneCost = costInitial + costFinal + (costPerMove * maxMoves);

                    for (MedDispatchRec rec : request) {
                        Requirements req = rec.getRequirements();
                        boolean capacityMatches = droneCapacity >= req.getCapacity();

                        boolean heatingMatches =
                                (req.getHeating() == null || !req.getHeating()) || droneHeating;

                        boolean coolingMatches =
                                (req.getCooling() == null || !req.getCooling()) || droneCooling;

                        boolean costMatches =
                                (req.getMaxCost() == null || req.getMaxCost() == 0.0)
                                        || droneCost <= req.getMaxCost();

                        if (!(capacityMatches && heatingMatches && coolingMatches && costMatches)) {
                            return false;
                        }
                    }

                    return true;
                })
                .map(d -> (String) d.get("id"))
                .collect(Collectors.toList());
    }

    public List<String> getAvailableDronesDateTime(List<MedDispatchRec> request) {
        String url = ilpEndpoint + "/drones-for-service-points";

        DroneForServicePoint[] servicePoints = restTemplate.getForObject(url, DroneForServicePoint[].class);

        List<DroneAvailabilityItem> allDrones = Arrays.stream(servicePoints)
                .flatMap(sp -> sp.getDrones().stream())
                .toList();

        return allDrones.stream()
                .filter(drone ->
                        request.stream()
                                .allMatch(req -> isDroneAvailableForDispatch(drone, req)))
                .map(DroneAvailabilityItem::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isDroneAvailableForDispatch(DroneAvailabilityItem drone, MedDispatchRec rec) {
        List<Availability> availability = drone.getAvailability();

        LocalDate date = rec.getDate();
        LocalTime time = rec.getTime();

        return availability.stream().anyMatch(window -> {

            String droneDay = window.getDayOfWeek();
            LocalTime droneFrom = window.getFrom();
            LocalTime droneUntil = window.getUntil();

            if (date == null && time != null) {
                return !time.isBefore(droneFrom) && !time.isAfter(droneUntil);
            }
            if (date != null && time == null) {
                return droneDay.equalsIgnoreCase(date.getDayOfWeek().toString());
            }
            if (date == null && time == null) {
                return true;
            }

            // Case 4: Both provided
            return droneDay.equalsIgnoreCase(date.getDayOfWeek().toString())
                    && !time.isBefore(droneFrom)
                    && !time.isAfter(droneUntil);
        });
    }

    public List<String> getAvailableDronesCombined(List<MedDispatchRec> request) {
        List<String> capabilityDrones = getAvailableDronesCapability(request);
        List<String> dateTimeDrones = getAvailableDronesDateTime(request);

        capabilityDrones.retainAll(dateTimeDrones);

        return capabilityDrones;

    }

    public List<RestrictedArea> getRestrictedAreas() {
        String url = ilpEndpoint +"/restricted-areas";
        RestrictedArea[] response =
                restTemplate.getForObject(url, RestrictedArea[].class);
        return response == null ? List.of() : Arrays.asList(response);
    }

    private static final int MAX_STEPS_CHECK = 20_000;

    public List<Position> findPath(Position start,
                                          Position target,
                                          List<RestrictedArea> restrictedAreas) {

        List<Position> path = new ArrayList<>();
        Position current = start;
        path.add(current);

        double approxAngle;
        int safety = MAX_STEPS_CHECK;

        while (!Geometry.isCloseTo(current, target) && safety-- > 0) {

            approxAngle = chooseBearingToward(current, target);

            Position next = Geometry.nextPosition(current, approxAngle);

            if (isValidMove(current, next, restrictedAreas)) {
                path.add(next);
                current = next;
            } else {
                boolean moved = false;
                double angle = approxAngle;

                for (int i = 0; i < 15; i++) {
                    angle = (angle + 22.5) % 360.0;
                    Position alt = Geometry.nextPosition(current, angle);

                    if (isValidMove(current, alt, restrictedAreas)) {
                        path.add(alt);
                        current = alt;
                        moved = true;
                        break;
                    }
                }

                if (!moved) {
                    break;
                }
            }
        }

        if (Geometry.isCloseTo(current, target)) {
            Position last = path.get(path.size() - 1);
            if (!Geometry.isCloseTo(last, target)) {
                path.add(target);
            }
        }

        return path;
    }


    private double chooseBearingToward(Position from, Position to) {
        double dx = to.getLng()- from.getLng();
        double dy = to.getLat()- from.getLat();

        double rawAngleRad = Math.atan2(dy,dx);
        double rawAngleDeg = Math.toDegrees(rawAngleRad);

        if (rawAngleDeg < 0) {
            rawAngleDeg += 360.0;
        }
        double stepAngle = 22.5;
        return Math.round(rawAngleDeg/stepAngle)*stepAngle;
    }

    private boolean isValidMove(Position start, Position end, List<RestrictedArea> restrictedAreas) {
        for (RestrictedArea area : restrictedAreas) {
            List<Position> vertices = area.getVertices();

            if (Geometry.isInPolygon(end, vertices)) {
                return false;
            }
            if (Geometry.pathCrossesRegion(start, end, vertices)) {
                return false;
            }
        }
        return true;
    }

    public DeliveryPlan calcDeliveryPath(List<MedDispatchRec> dispatches) {
        DeliveryPlan plan = new DeliveryPlan();
        if (dispatches == null || dispatches.isEmpty()){
            plan.setDronePaths(List.of());
            plan.setTotalMoves(0);
            plan.setTotalCost(0.0);
            return plan;
        }
        List<RestrictedArea> restrictedAreas = getRestrictedAreas();
        Map<Drone, List<MedDispatchRec>> assignment = assignDeliveriesToDrones(dispatches);

        List<DronePath> dronePaths = new ArrayList<>();
        int totalMoves = 0;
        double totalCost = 0.0;

        for (Map.Entry<Drone, List<MedDispatchRec>> entry : assignment.entrySet()) {
            Drone drone = entry.getKey();
            List<MedDispatchRec> jobs = entry.getValue();

            Position spPosition = findServicePointPositionForDrone(drone);

            DronePath path = buildDronePath(drone, jobs, restrictedAreas, spPosition);
            dronePaths.add(path);

            int moves = countMoves(path);
            totalMoves += countMoves(path);
            totalCost += computeCostForDrone(drone, path);
        }

        plan.setDronePaths(dronePaths);
        plan.setTotalMoves(totalMoves);
        plan.setTotalCost(totalCost);
        return plan;
    }

    private Map<Drone, List<MedDispatchRec>> assignDeliveriesToDrones(List<MedDispatchRec> dispatches) {
        Map<Drone, List<MedDispatchRec>> assignment = new HashMap<>();
        if (dispatches == null || dispatches.isEmpty()) {
            return assignment;
        }

        List<Drone> allDrones = getAllDrones();

        Map<MedDispatchRec, List<Drone>> candidatesByRec = new HashMap<>();
        for (MedDispatchRec rec : dispatches) {
            List<String> availableIdsForRec = getAvailableDronesCombined(List.of(rec));
            List<Drone> candidatesForRec = allDrones.stream().filter(d->availableIdsForRec.contains(d.getId()))
                    .collect(Collectors.toList());
            candidatesByRec.put(rec, candidatesForRec);
        }
        for (MedDispatchRec rec : dispatches) {
            List<String> ids = getAvailableDronesCombined(List.of(rec));
        }

        Map<Drone, Integer> remainingMoves = new HashMap<>();
        for (Drone d : allDrones) {
            if (d.getCapability() != null) {
                remainingMoves.put(d, d.getCapability().getMaxMoves());
                assignment.put(d, new ArrayList<>());
            }
        }
        List<MedDispatchRec> remainingDispatches = new ArrayList<>(dispatches);

        while (!remainingDispatches.isEmpty()) {
            MedDispatchRec chosenRec = null;
            Drone chosenDrone = null;
            int chosenMovesEstimate = 0;

            for (MedDispatchRec rec : remainingDispatches) {
                List<Drone> candidates = candidatesByRec.getOrDefault(rec, List.of());
                for (Drone drone : candidates) {
                    Integer rem = remainingMoves.get(drone);
                    if (rem == null || rem <= 0) {
                        continue;
                    }
                    int movesEstimate = estimateMovesForSingle(drone, rec);
                    if (movesEstimate <= rem) {
                        chosenRec = rec;
                        chosenDrone = drone;
                        chosenMovesEstimate = movesEstimate;
                        break;
                    }
                }
                if (chosenRec!= null) break;
            }
            if (chosenRec == null || chosenDrone == null) {
                break;
            }

            assignment.get(chosenDrone).add(chosenRec);
            remainingMoves.put(chosenDrone, remainingMoves.get(chosenDrone) -chosenMovesEstimate);
            remainingDispatches.remove(chosenRec);
        }

        assignment.entrySet().removeIf(e->e.getValue().isEmpty());
        return assignment;

    }

    private int estimateMovesForSingle(Drone drone, MedDispatchRec rec) {
        Position spPosition = findServicePointPositionForDrone(drone);
        Position deliveryPosition = toPosition(rec);

        if (spPosition == null || deliveryPosition == null) {
            return Integer.MAX_VALUE;
        }

        double distOut = Geometry.distance(spPosition, deliveryPosition);
        double distBack = Geometry.distance(deliveryPosition, spPosition);

        int movesOut = estimateMovesBetween(spPosition, deliveryPosition);
        int movesBack = estimateMovesBetween(deliveryPosition, spPosition);

        return  movesOut + movesBack;
    }

    private  int estimateMovesBetween(Position a, Position b) {
        double dist = Geometry.distance(a, b);
        return (int) Math.ceil(dist / Geometry.singleMove);
    }
    private Position toPosition(MedDispatchRec rec) {
        Position p = new Position();
        p.setLng(rec.getDelivery().getLng());
        p.setLat(rec.getDelivery().getLat());
        return p;
    }

    private Position findServicePointPositionForDrone(Drone drone) {
        String url1 = ilpEndpoint +"/drones-for-service-points";
        DroneForServicePoint[] mappings =
                restTemplate.getForObject(url1, DroneForServicePoint[].class);
        Integer servicePointId = null;

        for (DroneForServicePoint sp : mappings) {
            for (DroneAvailabilityItem item : sp.getDrones()) {
                if (item.getId().equals(drone.getId())) {
                    servicePointId= sp.getServicePointId();
                    break;
                }
            }
        }

        if (servicePointId == null) {
            return null;
        }

        String url2 = ilpEndpoint + "/service-points";
        ServicePoint[] servicePoints =
                restTemplate.getForObject(url2, ServicePoint[].class);
        for (ServicePoint sp : servicePoints){
            if (Objects.equals(sp.getId(), servicePointId)) {
                return sp.getLocation();
            }
        }

        return null;
    }

    private DronePath buildDronePath(Drone drone,
                                     List<MedDispatchRec> jobs,
                                     List<RestrictedArea> restrictedAreas,
                                     Position servicePointPosition) {
        DronePath result = new DronePath();
        result.setDroneId(drone.getId());
        List<DeliveryPath> deliveries = new ArrayList<>();
        result.setDeliveries(deliveries);

        if (jobs == null || jobs.isEmpty()) {
            return result;
        }

        int maxMoves = drone.getCapability().getMaxMoves();
        int usedMoves = 0;

        List<MedDispatchRec> remaining = new ArrayList<>(jobs);
        Position current = servicePointPosition;

        while (!remaining.isEmpty()) {
            MedDispatchRec nextJob = findNearestJob(current, remaining);
            Position target = toPosition(nextJob);

            List<Position> legPath = findPath(current, target, restrictedAreas);
            if (legPath == null || legPath.size() < 2) {
                break;
            }
            int legMoves = legPath.size() - 1;

            int backMovesEstimate =
                    estimateMovesBetween(target, servicePointPosition);

            if (usedMoves + legMoves + backMovesEstimate > maxMoves) {
                break;
            }

            Position last = legPath.getLast();
            legPath.add(last);

            DeliveryPath dp = new DeliveryPath();
            dp.setDeliveryId(nextJob.getId());
            dp.setFlightPath(legPath);
            deliveries.add(dp);

            usedMoves += legMoves;
            current = last;
            remaining.remove(nextJob);
        }

        if (!deliveries.isEmpty()) {
            DeliveryPath lastDelivery = deliveries.getLast();
            List<Position> flightPath = lastDelivery.getFlightPath();
            Position from = flightPath.getLast();

            List<Position> backPath= findPath(from, servicePointPosition, restrictedAreas);
            if (backPath != null && backPath.size() > 1) {
                int backMoves = backPath.size() - 1;
                DeliveryPath dp = new DeliveryPath();
                dp.setDeliveryId(-1);
                dp.setFlightPath(backPath);
                deliveries.add(dp);

            }
        }
        return result;
    }

    private MedDispatchRec findNearestJob(Position current,
                                          List<MedDispatchRec> candidates) {
        MedDispatchRec best = null;
        double bestDist = Double.POSITIVE_INFINITY;

        for (MedDispatchRec rec : candidates) {
            Position p = toPosition(rec);
            double dist = Geometry.distance(current, p);
            if (dist < bestDist) {
                bestDist = dist;
                best = rec;
            }
        }
        return best;
    }

    private int countMoves(DronePath path) {
        int moves = 0;
        for (DeliveryPath d: path.getDeliveries()) {
            List<Position> fp = d.getFlightPath();
            if (fp == null) continue;
            for (int i =0; i < fp.size() - 1; i++) {
                Position a = fp.get(i);
                Position b = fp.get(i+1);
                if (!a.getLng().equals(b.getLng()) || !a.getLat().equals(b.getLat())) {
                    moves++;
                }
            }
        }
        return moves;
    }

    private double computeCostForDrone(Drone drone, DronePath path) {
        DroneCapability caps = drone.getCapability();
        int moves = countMoves(path);

        return caps.getCostInitial() +caps.getCostFinal()
                + caps.getCostPerMove() * moves;
    }

    public Map<String, Object> calcDeliveryPathAsGeoJson(List<MedDispatchRec> dispatches) {
        Map<String, Object> empty = Map.of(
                "type", "LineString",
                "coordinates", List.of()
        );

        if (dispatches == null || dispatches.isEmpty()) {
            return empty;
        }

        DeliveryPlan plan = calcDeliveryPath(dispatches);

        if (plan.getDronePaths() == null || plan.getDronePaths().isEmpty()) {
            return empty;
        }

        DronePath firstDronePath = plan.getDronePaths().get(0);

        if (firstDronePath.getDeliveries() == null || firstDronePath.getDeliveries().isEmpty()) {
            return empty;
        }

        List<List<Double>> coords = new ArrayList<>();
        for (DeliveryPath dp : firstDronePath.getDeliveries()) {
            if (dp.getFlightPath() == null) continue;
            for (Position p : dp.getFlightPath()) {
                if (p.getLng() != null && p.getLat() != null) {
                    coords.add(List.of(p.getLng(), p.getLat()));
                }
            }
        }

        if (coords.isEmpty()) {
            return empty;
        }

        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "LineString");
        geoJson.put("coordinates", coords);
        return geoJson;
    }
}
