package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>(new Bid(0L, 0L, 0L));

    public boolean propose(Bid bid) {
        Bid currentBid;
        do {
            currentBid = latestBid.get();
            if (currentBid.getPrice() >= bid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(currentBid, bid));

        notifier.sendOutdatedMessage(latestBid.get());
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
