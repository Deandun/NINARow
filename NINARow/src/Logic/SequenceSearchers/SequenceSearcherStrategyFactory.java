package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.SequenceSearchers.Strategy.*;

import java.util.HashMap;
import java.util.Map;

public class SequenceSearcherStrategyFactory {
    private static Map<eSequenceSearcherType, ISequenceSearcherStrategy> strategyMap = new HashMap<>();

    public static ISequenceSearcherStrategy getSequenceSearcherStrategyForType(eSequenceSearcherType type) {
        ISequenceSearcherStrategy selectedStrategy = strategyMap.get(type);

        if(selectedStrategy == null) { // Cache sequence searchers
            selectedStrategy = createSequenceSearcherForType(type);
            strategyMap.put(type, selectedStrategy);
        }

        return selectedStrategy;
    }

    public static ISequenceSearcherStrategy createSequenceSearcherForType(eSequenceSearcherType type) {
        ISequenceSearcherStrategy sequenceSearcher;

        switch(type) {
            case Top:
                sequenceSearcher = new TopSequenceSearcherStrategy();
                break;
            case Bottom:
                sequenceSearcher = new BottomSequenceSearcherStrategy();
                break;
            case Right:
                sequenceSearcher = new RightSequenceSearcherStrategy();
                break;
            case Left:
                sequenceSearcher = new LeftSequenceSearcherStrategy();
                break;
            case TopRight:
                sequenceSearcher = new TopRightSequenceSearcherStrategy();
                break;
            case TopLeft:
                sequenceSearcher = new TopLeftSequenceSearcherStrategy();
                break;
            case BottomRight:
                sequenceSearcher = new BotRightSequenceSearcherStrategy();
                break;
            case BottomLeft:
                sequenceSearcher = new BotLeftSequenceSearcherStrategy();
                break;
            default:
                sequenceSearcher = null;
        }

        return sequenceSearcher;
    }
}
