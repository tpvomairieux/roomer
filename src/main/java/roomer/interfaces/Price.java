package roomer.interfaces; // Rename?

public class Price {
    boolean auction; // When do auctions close out?
    double sellerPrice; // Asking min bid or asking bin price
    double buyerPrice; // Best offer recieved

    public Price(boolean auction, double sellerPrice) {
        this.auction = auction;
        this.sellerPrice = sellerPrice;
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

    public void updateBuyer(double buyerPrice) {
        this.buyerPrice = buyerPrice;
    }
}
