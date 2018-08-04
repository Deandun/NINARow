package Logic.Enums;

public enum ePlayerType {
    Human,
    Computer;

    public static ePlayerType ConvertStringToePlayerType(String playerTypeStr) {
        if (Human.name().equals(playerTypeStr)){
            return Human;
        }
        else if(Computer.name().equals(playerTypeStr)){
            return Computer;
        }
        else{
            return null;
        }
    }
}
