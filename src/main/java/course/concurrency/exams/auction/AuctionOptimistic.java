package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid;

    public boolean propose(Bid bid) {
        if (bid.getPrice() > getLatestBid().getPrice()) {
//            notifier.sendOutdatedMessage(latestBid);
            setLatestBid(bid);
            return true;
        }
        return false;
    }

    public void setLatestBid(Bid bid) {
        latestBid = new AtomicReference<>(bid);
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
