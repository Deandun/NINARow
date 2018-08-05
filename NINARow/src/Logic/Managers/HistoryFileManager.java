package Logic.Managers;

import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;
import Logic.jaxb.schema.generated.Players;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Collection;
import java.util.List;


public class HistoryFileManager{

    //Convert Object to XML
    public static void saveGameHistoryInXMLFile(String path, List<PlayerTurn> currentGameHistory) throws IOException, ClassNotFoundException, Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(GameSettings.getInstance().getPlayers()); //save players
            out.writeObject(currentGameHistory); //save history turns
            out.flush();
        }
    }

    //Convert XML to Object
    public static List<PlayerTurn> readGameHistoryFromXMLFile(String path) throws IOException, ClassNotFoundException {
        List<PlayerTurn> turnHistory = null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            GameSettings.getInstance().getPlayers().clear();
            GameSettings.getInstance().getPlayers().addAll((List<Player>)in.readObject()); //load players
            turnHistory = (List<PlayerTurn>) in.readObject(); //load history turns
        }

        return turnHistory;
    }
}