package de.adesso.tools.events;

/**
 * Created by mohler ofList 05.02.16.
 */
public class ConditionDefnsTableInitializedEvent {
    private final Integer columnCount;

    public ConditionDefnsTableInitializedEvent(Integer columnCount) {
        super();
        this.columnCount = columnCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    @Override
    public String toString() {
        String sb = "ConditionDefnsTableInitializedEvent{" + "columnCount=" + columnCount +
                '}';
        return sb;
    }
}
