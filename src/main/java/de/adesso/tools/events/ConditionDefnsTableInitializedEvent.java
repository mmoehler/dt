package de.adesso.tools.events;

/**
 * Created by mohler on 05.02.16.
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
        final StringBuffer sb = new StringBuffer("ConditionDefnsTableInitializedEvent{");
        sb.append("columnCount=").append(columnCount);
        sb.append('}');
        return sb.toString();
    }
}
