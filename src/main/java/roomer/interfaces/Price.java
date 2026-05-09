package roomer.interfaces; // Rename?

public class Price {
    boolean auction; // When do auctions close out? Do we even need to have auction capabilities?
    double sellerPrice; // Asking min bid or asking bin price
    double buyerPrice; // Best offer recieved
    double minBid; // What the minimum bid is, optional (i.e. needs to be 10% higher than best
                   // buyer price, needs to be 1
                   // dollar higher, etc)

    public Price(boolean auction, double sellerPrice) {
        this.auction = auction;
        this.sellerPrice = sellerPrice;
        this.buyerPrice = sellerPrice;
    }

    public Price() { // is this needed?

    }

    public boolean isAuction() {
        return auction;
    }

    public double getSellerPrice() {
        return sellerPrice;
    }

    public double getBuyerPrice() {
        return buyerPrice;
    }

    public void setAuction(boolean auction) {
        this.auction = auction;
    }

    public void setSeller(double sellerPrice) {
        this.sellerPrice = sellerPrice;
    }

    public void bid(double buyerPrice) {
        this.buyerPrice = buyerPrice;

    }
}
