package at.ac.tuwien.big.we16.ue2.service;

import at.ac.tuwien.big.we16.ue2.model.Bid;
import at.ac.tuwien.big.we16.ue2.model.Product;
import at.ac.tuwien.big.we16.ue2.model.User;
import at.ac.tuwien.big.we16.ue2.productdata.JSONDataLoader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "Bidding", urlPatterns = {"/bidding"})
public class BiddingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject data = new Gson().fromJson(request.getReader(), JsonObject.class);

        float newPrice = data.get("newPrice").getAsFloat();
        int id = data.get("id").getAsInt();

        Product product = JSONDataLoader.getById(id);
        float highestPrice = (float) product.getPrice();

        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("currentSessionUser");

        User oldHighestBidder;
        Bid newBid;
        Bid oldTopBid = product.getTopBid() != null ? new Bid(product.getTopBid()) : null;

        if (newPrice > highestPrice) {
            // Check for existing bid
            List<Bid> bidList = user.getRunningActionsList();
            Optional<Bid> existingBid = bidList
                    .stream()
                    .filter(bid -> bid.getProduct().getID() == id)
                    .findAny();

            //set new balance
            double balanceUpdated = user.getBalance() - newPrice;
            if (balanceUpdated > 0) {
                user.setBalance(balanceUpdated);
            } else {
                response.setStatus(409);
                response.getWriter().write("Nicht genug Geld.");
                return;
            }

            oldHighestBidder = (oldTopBid != null) ? oldTopBid.getUser() : null;

            //set new highest bid
            Bid bid = product.addBid(user, newPrice);
            NotifierService.sendNewBidNotification(bid);

            newBid = product.getTopBid();

            //add new auction to running auctions
            if (existingBid.isPresent()) {
                existingBid.get().setPrice(newPrice);
            } else {
                user.getRunningActionsList().add(new Bid(product, user, newPrice));
            }
        } else {
            response.setStatus(409);
            response.getWriter().write("Gebot ist nicht hoch genug.");
            return;
        }

        //Send notification to surpassed user
        if (oldHighestBidder != null) {
            NotifierService.sendNewHighestBidNotification(oldTopBid, oldHighestBidder);
        }

        //Send notification to all users about the new Bid
        NotifierService.sendNewBidNotification(newBid);

        JsonObject json = new JsonObject();
        System.out.println("C: " + user.getBalance());
        json.addProperty("balance", user.getBalance());
        json.addProperty("running", user.getRunningAuctions());

        json.addProperty("price", Double.toString(product.getPrice()));

        response.getWriter().write(json.toString());
    }

    /**
     * Check if user has placed a bid on the product and update lists accordingly.
     *
     * @param product The product.
     * @param u       The user.
     */
    public static void auctionEnded(Product product, User u) {
        if (product.isBidding(u)) {
            Bid top = product.getTopBid();

            if (top.getUser().getId() == u.getId())
                u.addWonAuction(top);
            else {
                Bid last = product.getLastBidOf(u);
                u.addLostAuction(last);
            }
        }
    }
}
