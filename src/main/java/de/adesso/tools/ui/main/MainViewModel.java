package de.adesso.tools.ui.main;

import com.codepoetics.protonpack.Indexed;
import de.adesso.tools.Dump;
import de.adesso.tools.events.*;
import de.adesso.tools.functions.MatrixFunctions;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
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

import static de.adesso.tools.analysis.completeness.detailed.ConditionDetailedCompletenessCheck.isFormalComplete;
import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.DtFunctions.fullExpandActions;
import static de.adesso.tools.functions.DtFunctions.limitedExpandConditions;
import static de.adesso.tools.ui.Notifications.*;

@Singleton
public class MainViewModel implements ViewModel {

    public static final String QMARK = "?";
    public static final String EMPTY = "";
    public static final Object[] NO_ARGS = {};
    public static final String COND_ROW_HEADER = "C%02d";
    private static final Logger LOG = LoggerFactory.getLogger(MainViewModel.class);
    private static final String ACT_ROW_HEADER = "A%02d";
    private static String TPL2 = "RULE %04d NOT DEFINED %s";
    private final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> conditionDefinitions = FXCollections.observableArrayList();
    private final ObservableList<ActionDeclTableViewModel> actionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> actionDefinitions = FXCollections.observableArrayList();
    @Inject
    private NotificationCenter notificationCenter;

    public MainViewModel() {
        super();
    }

    /*
    public void onRemoveConditionDefsWithoutActions(@Observes RemoveRulesWithoutActionsEvent event) {

        final List<List<String>> transposed = MatrixFunctions.transpose(adapt(this.actionDefinitions));
        final List<Integer> indices1 = StreamUtils.zipWithIndex(transposed.stream())
                .filter(i -> isBlank(i.getValue()))
                .map(q -> Integer.valueOf((int) q.getIndex()))
                .sorted((aa, bb) -> bb.intValue() - aa.intValue())
                .collect(Collectors.toList());
        final List<Integer> indices = indices1;
        publish(REM_RULES_WITHOUT_ACTIONS.name(), indices);
    }
*/
    private static boolean isBlank(ObservableList<String> ol) {
        return ol.stream().allMatch(ss -> (ss.trim().length() == 0));
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
        List<List<String>> newDefns = MatrixFunctions.addColumn(adapt(this.conditionDefinitions), () -> QMARK);
        this.conditionDefinitions.clear();
        List<List<String>> newDefns0 = MatrixFunctions.addColumn(adapt(this.actionDefinitions), () -> QMARK);
        this.actionDefinitions.clear();
        publish(ADD_RULE.name(), adapt(newDefns), adapt(newDefns0));
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
            List<List<String>> newDefns = MatrixFunctions.addRow(adapt(this.conditionDefinitions), () -> QMARK);
            this.conditionDefinitions.clear();
            adapt(newDefns).stream().forEach(this.conditionDefinitions::add);
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
        List<List<String>> newDefns = FXCollections.observableArrayList();
        if (this.actionDefinitions.isEmpty()) {
            if (!this.conditionDefinitions.isEmpty()) {
                final int countColumns = this.conditionDefinitions.get(0).size();
                List<String> indicators = IntStream.range(0, countColumns)
                        .mapToObj(k -> "?")
                        .collect(Collectors.toList());
                newDefns.add(indicators);
            }
        } else {
            newDefns = MatrixFunctions.addRow(adapt(this.actionDefinitions), () -> QMARK);

        }
        this.actionDefinitions.clear();
        adapt(newDefns).stream().forEach(this.actionDefinitions::add);

        Dump.dumpTableItems("CON", this.conditionDefinitions);
        Dump.dumpTableItems("ACT", this.actionDefinitions);

    }

    public void onSimpleCompletenessCheck(@Observes FormalCompletenessCheckEvent event) {
        final List<Indexed<List<String>>> result = isFormalComplete(this.conditionDeclarations, this.conditionDefinitions);
        final StringBuilder message = new StringBuilder();
        result.forEach(i -> message.append(String.format(TPL2, i.getIndex(), String.join(",", i.getValue()))).append(System.lineSeparator()));
        notificationCenter.publish(PREPARE_CONSOLE.name(), message.toString());
    }

    public void onRemoveConditionDecl(@Observes RemoveConditionDeclEvent event) {
        publish(REM_CONDITION.name(), NO_ARGS);
    }

    public void onRemoveActionDecl(@Observes RemoveActionDeclEvent event) {
        publish(REM_ACTION.name(), NO_ARGS);
    }

    public void onRemoveRule(@Observes RemoveRuleEvent event) {
        publish(REM_RULE.name(), NO_ARGS);
    }

    public void onInsertConditionDecl(@Observes InsertConditionDeclEvent event) {
        publish(INS_CONDITION.name(), NO_ARGS);
    }

    public void onInsertRuleDef(@Observes InsertRuleDefEvent event) {
        publish(INS_RULE.name(), NO_ARGS);
    }

    public void onInsertActionDecl(@Observes InsertActionDeclEvent event) {
        publish(INS_ACTION.name(), NO_ARGS);
    }

    public void onMoveActionDeclUp(@Observes MoveActionDeclUpEvent event) {
        publish(MOVE_ACTION_DECL_UP.name(), NO_ARGS);
    }

    public void onMoveActionDeclDown(@Observes MoveActionDeclDownEvent event) {
        publish(MOVE_ACTION_DECL_DOWN.name(), NO_ARGS);
    }

    public void onMoveConditionDeclUp(@Observes MoveConditionDeclUpEvent event) {
        publish(MOVE_COND_DECL_UP.name(), NO_ARGS);
    }

    public void onMoveConditionDeclDown(@Observes MoveConditionDeclDownEvent event) {
        publish(MOVE_COND_DECL_DOWN.name(), NO_ARGS);
    }

    public void onMoveRuleLeft(@Observes MoveRuleLeftEvent event) {
        publish(MOVE_RULE_LEFT.name(), NO_ARGS);
    }

    public void onMoveRuleRight(@Observes MoveRuleRightEvent event) {
        publish(MOVE_RULE_RIGHT.name(), NO_ARGS);
    }

    public void onAddElseRule(@Observes AddElseRuleEvent event) {
        List<List<String>> newDefns = MatrixFunctions.addColumn(adapt(this.conditionDefinitions), () -> QMARK);
        this.conditionDefinitions.clear();
        List<List<String>> newDefns0 = MatrixFunctions.addColumn(adapt(this.actionDefinitions), () -> QMARK);
        this.actionDefinitions.clear();
        publish(ADD_ELSE_RULE.name(), adapt(newDefns), adapt(newDefns0));
    }

    public void updateRowHeader() {
        int counter[] = {1};
        this.conditionDeclarations
                .forEach(d -> d.lfdNrProperty().setValue(String.format(COND_ROW_HEADER, counter[0]++)));
        counter[0] = 1;
        this.actionDeclarations
                .forEach(d -> d.lfdNrProperty().setValue(String.format(ACT_ROW_HEADER, counter[0]++)));
    }

    public void onFormalCompletenessCheckAction(@Observes FormalCompletenessCheckEvent event) {

    }

    public void onConsolidateRulesAction(@Observes ConsolidateRulesEvent event) {

    }

    public void onCompleteReportAction(@Observes CompleteReportEvent event) {

    }

    public void onStructuralAnalysisAction(@Observes StructuralAnalysisEvent event) {

    }
}
