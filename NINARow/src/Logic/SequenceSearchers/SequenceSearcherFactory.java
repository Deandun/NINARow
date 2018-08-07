package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcher;

public class SequenceSearcherFactory {
    public static ISequenceSearcher CreateSequenceSearcher(eSequenceSearcherType type) {
        ISequenceSearcher sequenceSearcher;

        switch(type) {
            case Top:
                sequenceSearcher = new TopSequenceSearcher();
                break;
            case Bottom:
                sequenceSearcher = new BottomSequenceSearcher();
                break;
            case Right:
                sequenceSearcher = new RightSequenceSearcher();
                break;
            case Left:
                sequenceSearcher = new LeftSequenceSearcher();
                break;
            case TopRight:
                sequenceSearcher = new TopRightSequenceSearcher();
                break;
            case TopLeft:
                sequenceSearcher = new TopLeftSequenceSearcher();
                break;
            case BottomRight:
                sequenceSearcher = new BotRightSequenceSearcher();
                break;
            case BottomLeft:
                sequenceSearcher = new BotLeftSequenceSearcher();
                break;
            default:
                sequenceSearcher = null;
        }

        return sequenceSearcher;
    }
}
