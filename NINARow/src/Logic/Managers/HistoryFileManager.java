package Logic.Managers;

import Logic.Models.Cell;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;


public class HistoryFileManager {

    //Convert Object to XML
         public static void saveGameHistorInXMLFile(String path, HistoryManager historyManager) throws Exception {

             System.out.println("SIZE + " + historyManager.GetGameHistory().size());
             File file = new File(path);
             try {
                 JAXBContext jaxbContext = JAXBContext.newInstance(HistoryFileManager.class , PlayerTurn.class, Cell.class, Player.class);
                 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                 List<PlayerTurn> pT = historyManager.GetGameHistory();
                 // output pretty printed
                 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                 jaxbMarshaller.marshal(historyManager.GetGameHistory().get(0), file);
                 jaxbMarshaller.marshal(historyManager.GetGameHistory().get(0), System.out);
                //jaxbMarshaller.marshal(pT, file);
                 // jaxbMarshaller.marshal(pT, System.out);
             } catch (JAXBException e) {
                 e.printStackTrace();
             }
         }

         //TODO: change the output to historyManager instead of player
    public static Player readGameHistoryFromXMLFile(String path) {

        try {

            File file = new File(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(Player.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Player player = (Player) jaxbUnmarshaller.unmarshal(file);
            System.out.println(player);
            return player;

        } catch (JAXBException e) {
            e.printStackTrace(); //TODO: Change exception
        }

        return null;
    }
}