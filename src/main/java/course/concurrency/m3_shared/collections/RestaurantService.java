package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RestaurantService {

    private Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final ConcurrentHashMap<String, Integer> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        if (stat.containsKey(restaurantName)) {
            stat.compute(restaurantName, (k, v) -> v + 1);
        } else {
            stat.put(restaurantName, 1);
        }
    }

    public Set<String> printStat() {
        Set<String> forPrint = new HashSet<>();

        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
            forPrint.add(entry.getKey() + " - " + stat.getOrDefault(entry.getKey(), 0));
        }
        return forPrint;
    }
}
