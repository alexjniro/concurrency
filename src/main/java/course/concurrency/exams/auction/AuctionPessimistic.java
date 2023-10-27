package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            notifier.sendOutdatedMessage(latestBid);
            setLatestBid(bid);
            return true;
        }
        return false;
    }

    public synchronized void setLatestBid(Bid bid) {
        this.latestBid = bid;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
