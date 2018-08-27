package Logic.SequenceSearchers;

import Logic.Enums.eSequenceSearcherType;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.SequenceSearchers.Strategy.*;

import java.util.HashMap;
import java.util.Map;

import Logic.Enums.eSequenceSearcherType;
import Logic.Enums.eVariant;
import Logic.Interfaces.ISequenceSearcherStrategy;
import Logic.Models.GameSettings;
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

    private static ISequenceSearcherStrategy createSequenceSearcherForType(eSequenceSearcherType type) {
        boolean isCircularGameMode = GameSettings.getInstance().getVariant() == eVariant.Circular;
        ISequenceSearcherStrategy sequenceSearcherStrategy;

        switch(type) {
            case Top:
                sequenceSearcherStrategy = new TopSequenceSearcherStrategy();
                break;
            case Bottom:
                sequenceSearcherStrategy = new BottomSequenceSearcherStrategy();
                break;
            case Right:
                sequenceSearcherStrategy = new RightSequenceSearcherStrategy();
                break;
            case Left:
                sequenceSearcherStrategy = new LeftSequenceSearcherStrategy();
                break;
            case TopRight:
                sequenceSearcherStrategy = new TopRightSequenceSearcherStrategy();
                break;
            case TopLeft:
                sequenceSearcherStrategy = new TopLeftSequenceSearcherStrategy();
                break;
            case BottomRight:
                sequenceSearcherStrategy = new BotRightSequenceSearcherStrategy();
                break;
            case BottomLeft:
                sequenceSearcherStrategy = new BotLeftSequenceSearcherStrategy();
                break;
            default:
                sequenceSearcherStrategy = null;
        }

        // If needed, wrap strategy with a circular strategy.
        if(isCircularGameMode && doesStrategySupportCircularGameMode(type)) {
            ISequenceSearcherStrategy innerStrategy = sequenceSearcherStrategy;
            sequenceSearcherStrategy = new CircularSequenceSearcherStrategy(innerStrategy, GameSettings.getInstance().getRows(),
                    GameSettings.getInstance().getColumns());
        }

        return sequenceSearcherStrategy;
    }

    public static void ClearCache() {
        strategyMap.clear();
    }

    private static boolean doesStrategySupportCircularGameMode(eSequenceSearcherType type) {
        return type == eSequenceSearcherType.Bottom || type == eSequenceSearcherType.Top
                || type == eSequenceSearcherType.Right || type == eSequenceSearcherType.Left;
    }
}