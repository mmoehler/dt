package de.adesso.dtmg.events;

/**
 * CDI event class that is used to indicate that a contact was updated/added/removed.
 */
public class InitializeConditionsEvent {
    private final int conditionCount;
    private final boolean dontPopulateIndicatorCombinations;

    public InitializeConditionsEvent(int conditionCount, boolean dontPopulateIndicatorCombinations) {
        this.conditionCount = conditionCount;
        this.dontPopulateIndicatorCombinations = dontPopulateIndicatorCombinations;
    }

    public int getConditionCount() {
        return conditionCount;
    }

    public boolean isDontPopulateIndicatorCombinations() {
        return dontPopulateIndicatorCombinations;
    }
}
