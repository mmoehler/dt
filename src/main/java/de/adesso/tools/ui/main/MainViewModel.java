package de.adesso.tools.ui.main;

import com.codepoetics.protonpack.StreamUtils;
import de.adesso.tools.events.*;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.Notifications;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.util.tuple.Tuple3;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adesso.tools.analysis.ConditionCompletenessCheck.isFormalComplete;
import static de.adesso.tools.functions.DtFunctions.fullExpandActions;
import static de.adesso.tools.functions.DtFunctions.limitedExpandConditions;

@Singleton
public class MainViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(MainViewModel.class);

    public static final String QMARK = "?";
    public static final String EMPTY = "";
    public static final Object[] NO_ARGS = {};

    private static final String ACT_ROW_HEADER = "A%02d";
    public static final String COND_ROW_HEADER = "C%02d";

    private final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> conditionDefinitions = FXCollections.observableArrayList();
    private final ObservableList<ActionDeclTableViewModel> actionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> actionDefinitions = FXCollections.observableArrayList();

    @Inject
    private NotificationCenter notificationCenter;



    public MainViewModel() {
        super();
    }

    public ObservableList<ConditionDeclTableViewModel> getConditionDeclarations() {
        return conditionDeclarations;
    }

    public ObservableList<ObservableList<String>> getConditionDefinitions() {
        return conditionDefinitions;
    }

    public ObservableList<ActionDeclTableViewModel> getActionDeclarations() {
        return actionDeclarations;
    }

    public ObservableList<ObservableList<String>> getActionDefinitions() {
        return actionDefinitions;
    }

    public void onAddRuleDef(@Observes AddRuleDefEvent event) {
        ObservableList<ObservableList<String>> newDefns = MatrixFunctions.addColumn(this.conditionDefinitions, () -> QMARK);
        this.conditionDefinitions.clear();
        ObservableList<ObservableList<String>> newDefns0 = MatrixFunctions.addColumn(this.actionDefinitions, () -> QMARK);
        this.actionDefinitions.clear();
        publish(Notifications.ADD_RULE.name(), newDefns, newDefns0);
    }

    public ObservableList<ObservableList<String>> initializeConditionDefnsData(final int requestedColCount, final boolean shouldPopulateData) {
        final ObservableList<ObservableList<String>> data
                = limitedExpandConditions(this.conditionDeclarations, requestedColCount, !shouldPopulateData);
        this.conditionDefinitions.clear();
        data.stream().forEach(this.conditionDefinitions::add);
        return this.conditionDefinitions;
    }

    public ObservableList<ObservableList<String>> initializeActionDefnsData(final int requestedColCount) {
        final ObservableList<ObservableList<String>> data
                = fullExpandActions(this.actionDeclarations, requestedColCount);
        this.actionDefinitions.clear();
        data.stream().forEach(this.actionDefinitions::add);
        return this.actionDefinitions;
    }

    public void onAddConditionDecl(@Observes AddConditionDeclEvent event) {
        // #1 Always replace the table content with new created content
        ObservableList<ConditionDeclTableViewModel> tmp = FXCollections.observableArrayList(this.conditionDeclarations);
        tmp.add(new ConditionDeclTableViewModel(new ConditionDecl()));
        this.conditionDeclarations.clear();

        int i[] = {1};
        tmp.forEach(x -> {
            x.lfdNrProperty().setValue(String.format(COND_ROW_HEADER, i[0]++));
            this.conditionDeclarations.add(x);
        });

        // #2 If there are at least one condition defined, then this must also be updated
        if (!this.conditionDefinitions.isEmpty()) {
            ObservableList<ObservableList<String>> newDefns = MatrixFunctions.addRow(this.conditionDefinitions, () -> QMARK);
            this.conditionDefinitions.clear();
            newDefns.stream().forEach(this.conditionDefinitions::add);
        }
    }

    public void onAddActionDecl(@Observes AddActionDeclEvent event) {
        // #1 Always replace the table content with new created content
        ObservableList<ActionDeclTableViewModel> tmp = FXCollections.observableArrayList(this.actionDeclarations);
        tmp.add(new ActionDeclTableViewModel(new ActionDecl()));
        this.actionDeclarations.clear();

        int i[] = {1};
        tmp.forEach(x -> {
            x.lfdNrProperty().setValue(String.format(ACT_ROW_HEADER, i[0]++));
            this.actionDeclarations.add(x);
        });

        // #2 If there are at least one action defined, then this must also be updated
        ObservableList<ObservableList<String>> newDefns = FXCollections.observableArrayList();
        if (this.actionDefinitions.isEmpty()) {
            if (!this.conditionDefinitions.isEmpty()) {
                final int countColumns = this.conditionDefinitions.get(0).size();
                ObservableList<String> indicators = IntStream.range(0, countColumns)
                        .mapToObj(k -> "?")
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
                newDefns.add(indicators);
            }
        } else {
            newDefns = MatrixFunctions.addRow(this.actionDefinitions, () -> QMARK);

        }
        this.actionDefinitions.clear();
        newDefns.stream().forEach(this.actionDefinitions::add);

    }

    public void onSimpleCompletenessCheck(@Observes SimpleCompletenessCheckEvent event) {
        final Tuple3<Boolean, Integer, Integer> result = isFormalComplete(this.conditionDeclarations, this.conditionDefinitions);
        final String message = String.valueOf(result);
        notificationCenter.publish(Notifications.PREPARE_CONSOLE.name(), message);
    }

    public void onRemoveConditionDecl(@Observes RemoveConditionDeclEvent event) {
        publish(Notifications.REM_CONDITION.name(), NO_ARGS);
    }

    public void onRemoveActionDecl(@Observes RemoveActionDeclEvent event) {
        publish(Notifications.REM_ACTION.name(), NO_ARGS);
    }

    public void onRemoveConditionDefsWithoutActions(@Observes RemoveRulesWithoutActionsEvent event) {
        final ObservableList<ObservableList<String>> transposed = MatrixFunctions.transpose/*Observable*/(this.actionDefinitions);
        final List<Integer> indices1 = StreamUtils.zipWithIndex(transposed.stream())
                .filter(i -> isBlank(i.getValue()))
                .map(q -> Integer.valueOf((int) q.getIndex()))
                .sorted((aa, bb) -> bb.intValue() - aa.intValue())
                .collect(Collectors.toList());
        final List<Integer> indices = indices1;
        publish(Notifications.REM_RULES_WITHOUT_ACTIONS.name(), indices);
    }

    private static boolean isBlank(ObservableList<String> ol) {
        return ol.stream().allMatch(ss -> (ss.trim().length() == 0));
    }

    public void onRemoveRule(@Observes RemoveRuleEvent event) {
        publish(Notifications.REM_RULE.name(), NO_ARGS);
    }

    public void onInsertConditionDecl(@Observes InsertConditionDeclEvent event) {
        publish(Notifications.INS_CONDITION.name(), NO_ARGS);
    }

    public void onInsertRuleDef(@Observes InsertRuleDefEvent event) {
        publish(Notifications.INS_RULE.name(), NO_ARGS);
    }

    public void onInsertActionDecl(@Observes InsertActionDeclEvent event) {
        publish(Notifications.INS_ACTION.name(), NO_ARGS);
    }

    public void onMoveDeclUp(@Observes MoveDeclUpEvent event) {

    }

    public void onMoveDeclDown(@Observes MoveDeclDownEvent event) {
    }

    public void updateRowHeader() {
        int counter[] = {1};
        this.conditionDeclarations
                .forEach(d -> d.lfdNrProperty().setValue(String.format(COND_ROW_HEADER, counter[0]++)));
        counter[0] = 1;
        this.actionDeclarations
                .forEach(d -> d.lfdNrProperty().setValue(String.format(ACT_ROW_HEADER, counter[0]++)));
    }



}
