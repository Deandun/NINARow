package UI.Controllers;

import Logic.Enums.ePlayerType;
import Logic.Models.Player;
import UI.UIMisc.ImageManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class PlayerDetailsController {

    private VBox mVBox;
    private List<PlayerDetails> mPlayerDetailsList;
    private ListIterator<PlayerDetails> mPlayerDetailsIterator;

    public PlayerDetailsController() {
        this.mVBox = new VBox();
        this.mPlayerDetailsList = new ArrayList<>();
    }

    public void setPlayersDetails(Collection<Player> players){
       players.forEach(
               player -> {
                   PlayerDetails playerDetails = new PlayerDetails(player);
                   this.mPlayerDetailsList.add(playerDetails); // Add to player details list.
                   this.mVBox.getChildren().add(playerDetails.getRoot()); // Add player details root to VBox.
               }
       );

        this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator();
    }

    public Node getRoot() {
        return this.mVBox;
    }

    public void updateToNextTurn(){
        PlayerDetails currentPlayerDetails = this.getNextPlayerDetails();

        currentPlayerDetails.incTurn();
        currentPlayerDetails.unMarkPlayerDetails();

        // Remove current player's details from the top of the "queue"
        this.mVBox.getChildren().remove(currentPlayerDetails.mVBox);

        // Add current player's details to the bottom of the players details "queue".
        this.mVBox.getChildren().add(currentPlayerDetails.mVBox);

        // Next player should automatically be at the top of the queue, no need to perform actions on the queue.
        PlayerDetails nextPlayerDetails = this.getNextPlayerDetailsWithoutMovingIterator();

        nextPlayerDetails.markPlayerDetails();
    }

    public void updateToPreviousTurn(){
        PlayerDetails currentPlayerDetails = this.getPreviousPlayerDetailsWithoutMovingIterator();

        currentPlayerDetails.decTurn();
        currentPlayerDetails.unMarkPlayerDetails();


        // Get previous players details from the bottom of the queue.
        PlayerDetails previousPlayerDetails = this.getPreviousPlayersDetails();

        //Remove previous player details from the bottom of the queue.
        this.mVBox.getChildren().remove(previousPlayerDetails.getRoot());

        // Insert previous player at the top of the queue.
        this.mVBox.getChildren().add(0, previousPlayerDetails.getRoot());
        previousPlayerDetails.markPlayerDetails();
    }

    public void playerQuit() {
        PlayerDetails currentPlayerDetails = this.getNextPlayerDetails();

        // Remove current player's details from the top of the "queue"
        this.mVBox.getChildren().remove(currentPlayerDetails.mVBox);

        // Remove the last player that was returned by using "next()"
        this.mPlayerDetailsIterator.remove();
    }

    // Helper functions.
    private PlayerDetails getNextPlayerDetails() {
        PlayerDetails nextPlayerDetails;

        if(this.mPlayerDetailsIterator.nextIndex() < this.mPlayerDetailsList.size()) {
            nextPlayerDetails = this.mPlayerDetailsIterator.next(); // Get next player details.
        } else {
            // Set iterator to the start of the collection.
            this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator();
            nextPlayerDetails = this.mPlayerDetailsIterator.next(); // Get first player details.
        }

        return nextPlayerDetails;
    }

    private PlayerDetails getPreviousPlayersDetails() {
        PlayerDetails previousPlayerDetails;

        if(this.mPlayerDetailsIterator.previousIndex() >= 0) {
            previousPlayerDetails = this.mPlayerDetailsIterator.previous(); // Get previous player details.
        } else {
            // Set iterator to the end of the collection.
            this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator(this.mPlayerDetailsList.size());
            previousPlayerDetails = this.mPlayerDetailsIterator.previous(); // Get last player details.
        }

        return previousPlayerDetails;
    }

    private PlayerDetails getNextPlayerDetailsWithoutMovingIterator() {
        int nextPlayerDetailsIndex;

        if(this.mPlayerDetailsIterator.nextIndex() < this.mPlayerDetailsList.size()) {
            nextPlayerDetailsIndex = this.mPlayerDetailsIterator.nextIndex(); // Get next player details index.
        } else {
            // Get first player details.
            nextPlayerDetailsIndex = 0;
        }

        return this.mPlayerDetailsList.get(nextPlayerDetailsIndex);
    }

    private PlayerDetails getPreviousPlayerDetailsWithoutMovingIterator() {
        int previousPlayerDetailsIndex;

        if(this.mPlayerDetailsIterator.previousIndex() >= 0) {
            previousPlayerDetailsIndex = this.mPlayerDetailsIterator.previousIndex(); // Get previous player details index.
        } else {
            // Get last player details.
            previousPlayerDetailsIndex = this.mPlayerDetailsList.size() - 1;
        }

        return this.mPlayerDetailsList.get(previousPlayerDetailsIndex);
    }

    private class PlayerDetails {

        private VBox mVBox;
        private Label mTitle;
        private Label mName;
        private Label mID;
        private Label mType;
        private Label mNumTurn;
        private ImageView mDiscImg;
        private Separator mSeparator;

        private PlayerDetails(Player player) {
            this.mVBox = new VBox();
            this.mTitle = new Label("Title!");
            this.mName = new Label(player.getName());
            this.mID = new Label(player.getID());
            this.mType = new Label(player.getType().equals(ePlayerType.Human) ? "Human" : "Computer");
            this.mNumTurn = new Label("0");
            this.mDiscImg = new ImageView(ImageManager.getImageForPlayerID(player.getID()));
            this.mSeparator = new Separator();

            this.initUI();
        }

        private void initUI() {
            this.mVBox.setSpacing(4);

            this.initImage();
            this.mVBox.getChildren().add(this.mTitle);
            this.mVBox.getChildren().add(this.mName);
            this.mVBox.getChildren().add(this.mID);
            this.mVBox.getChildren().add(this.mType);
            this.mVBox.getChildren().add(this.mNumTurn);
            this.mVBox.getChildren().add(this.mDiscImg);
            this.mVBox.getChildren().add(this.mSeparator);
        }

        private void initImage() {
            this.mDiscImg.setFitHeight(20);
            this.mDiscImg.setFitWidth(20);

            Rectangle clip = new Rectangle(
                    this.mDiscImg.getFitWidth(), this.mDiscImg.getFitHeight()
            );
            clip.setArcWidth(40);
            clip.setArcHeight(40);
            this.mDiscImg.setClip(clip);

        }

        private Node getRoot() {
            return this.mVBox;
        }

        private void incTurn() {
            int incrementedTurn = Integer.parseInt(this.mNumTurn.getText()) + 1;
            this.mNumTurn.setText(Integer.toString(incrementedTurn));
        }

        private void decTurn() {
            int decrementedTurn = Integer.parseInt(this.mNumTurn.getText()) - 1;
            this.mNumTurn.setText(Integer.toString(decrementedTurn));
        }

        //todo: UI functionality.
        private void markPlayerDetails() {
            //TODO: set background color or effect
        }

        private void unMarkPlayerDetails() {
            // TODO: remove bg color/effect
        }
    }
}
