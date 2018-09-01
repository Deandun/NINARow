package UI.Controllers;

import Logic.Models.Player;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class PlayerDetailsController {

    private VBox mVBox;
    private List<PlayerDetails> mPlayerDetailsList;
    private ListIterator<PlayerDetails> mPlayerDetailsIterator;
    private Node[] nodesArray = new Node[2];

    public PlayerDetailsController(Collection<Player> players) {
        this.mVBox = new VBox();
        this.mPlayerDetailsList = new ArrayList<>();
        this.setPlayers(players);
        this.mPlayerDetailsIterator = this.mPlayerDetailsList.listIterator();
        this.mPlayerDetailsIterator.next();
    }

    public void setPlayers(Collection<Player> players){
        //TODO
        for (Player player : players){
//            PlayerDetails playerDetails = new PlayerDetails();
        }
    }

    public void updateToNextTurn(){
        this.mVBox.getChildren().remove(0);

        if(this.mPlayerDetailsIterator.hasNext()) {
            this.mVBox.getChildren().add(this.mPlayerDetailsIterator.next().getRoot());
        }

    }

    public void updateToPreviousTurn(){

    }

    private class PlayerDetails {
        private VBox mVBox;
        private Label mTitle;
        private Label mName;
        private Label mID;
        private Label mType;
        private Label mNumTurn;
        private ImageView mDiscImg;

        public PlayerDetails(String title, String name, String ID, String type, Image discImg) {
            this.mVBox = new VBox();
            this.mTitle.setText(title);
            this.mName.setText(name);
            this.mID.setText(ID);
            this.mType.setText(type);
            this.mDiscImg.setImage(discImg);
        }

        public Node getRoot() {
            return this.mVBox;
        }
    }
}
