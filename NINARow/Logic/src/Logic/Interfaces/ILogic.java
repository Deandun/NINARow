package Logic.Interfaces;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Models.Board;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ILogic {

    public void ReadGameFile(String filePath) throws FileNotFoundException, InvalidFileInputException, IOException, JAXBException;
    public void StartGame();
    public PlayerTurn PlayTurn(int column) throws InvalidUserInputException, Exception;
    public List<PlayerTurn> GetTurnHistory();
    public Player GetCurrentPlayer();
    public Board getBoard();
    public void SaveGame() throws  IOException, ClassNotFoundException, Exception;
    public void LoadExistsGame() throws IOException, ClassNotFoundException, Exception;
    public eGameState GetGameState();

}
