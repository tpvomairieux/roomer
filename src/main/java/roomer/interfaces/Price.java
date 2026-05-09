package roomer.interfaces; // Rename?

public class Price {
    boolean auction; // When do auctions close out?
    double sellerPrice; // Asking min bid or asking bin price
    double buyerPrice; // Best offer recieved

    public Price(boolean auction, double sellerPrice) {
        this.auction = auction;
        this.sellerPrice = sellerPrice;
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

    public void updateBuyer(double buyerPrice) {
        this.buyerPrice = buyerPrice;
    }
}
