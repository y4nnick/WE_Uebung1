package at.ac.tuwien.big.we16.ue2.model;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mstrasser on 4/12/16.
 */
public class Product {
    private Integer id;
    private String name;
    private String img;
    private int year;
    private Bid highestBid = null;
    private Deque<Bid> bids = new ArrayDeque<Bid>();

    private Date auctionEnd;
    private boolean isRunning = true;

    public Product(Integer ID, String name, String img, int year) {
        this.id = ID;
        this.name = name;
        this.img = img;
        this.year = year;
    }

    public String getHighestBidName(){

        if(this.highestBid == null || this.highestBid.getUser() == null){
            return "Kein Gebot";
        }

        return this.highestBid.getUser().getName();
    }

    public Boolean hasBid(){
        return (this.highestBid != null);
    }

    public Bid getTopBid() { return this.highestBid; }

    /**
     * Checks whether the given user is bidding on this product.
     * @param u The user.
     * @return True, if the user has placed a bid on this product.
     */
    public boolean isBidding(User u) {
        for(Bid b:this.bids) {
            if(b.getUser().getId() == u.getId())
                return true;
        }

        return false;
    }

    /**
     * Returns the last bid that has been placed by the specified user.
     * @param u The user.
     * @return The last bid of the user.
     */
    public Bid getLastBidOf(User u) {
        if(u == null) return null;

        for(Bid b:this.bids) {
            if(b.getUser().getId() == u.getId())
                return b;
        }

        return null;
    }

    public void addBid(Bid b) {
        this.highestBid = b;
        this.bids.push(b);
    }

    public Bid addBid(User u, float price) {
        Bid b = new Bid(this, u, price);
        addBid(b);
        return b;
    }

    public double getPrice() {
        if(this.highestBid == null)
            return 0;

        return this.highestBid.getPrice();
    }

    public int getYear() { return this.year; }
    public void setYear(int year) { this.year = year; }

    public String getImg() { return this.img; }
    public void setImg() { this.img = img; }

    public String getName() { return this.name; }
    public Integer getID() { return this.id; }

    public Date getAuctionEnd() {
        return auctionEnd;
    }
    public void setAuctionEnd(Date auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    /**
     * Returns the Auction end date in the format YYYY,MM,dd,HH,mm,ss,SSS
     * @return the Auction end date in the format YYYY,MM,dd,HH,mm,ss,SSS
     */
    public String getAuctionEndString(){
        //2016,03,14,14,30,23,288
        // endTime = new Date(endTime[0],endTime[1]-1,endTime[2],endTime[3],endTime[4],endTime[5],endTime[6]);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY,MM,dd,HH,mm,ss,SSS");
        return sdf.format(auctionEnd);
    }

    public boolean isRunning() { return this.isRunning; }
    public void setRunning(boolean running) {
        isRunning = running;
    }
}
