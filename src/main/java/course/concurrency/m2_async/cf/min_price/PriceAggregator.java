package course.concurrency.m2_async.cf.min_price;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        Executor executor = Executors.newFixedThreadPool(shopIds.size());
        List<CompletableFuture<Double>> priceFutures = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                        .orTimeout(2900, TimeUnit.MILLISECONDS)
                        .exceptionally(ex -> {
                            System.out.println("An exception occurred: " + ex.getMessage());
                            return Double.NaN;
                        }))
                .collect(Collectors.toList());

        CompletableFuture.allOf(priceFutures.toArray(CompletableFuture[]::new))
                .join();

        return priceFutures.stream()
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite)
                .min()
                .orElse(Double.NaN);
    }

}
