package de.adesso.tools.ui.main;

import com.codepoetics.protonpack.Indexed;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import de.adesso.tools.analysis.structure.AnalysisResultEmitter;
import de.adesso.tools.analysis.structure.Indicator;
import de.adesso.tools.analysis.structure.Operators;
import de.adesso.tools.analysis.structure.StructuralAnalysis;
import de.adesso.tools.events.*;
import de.adesso.tools.exception.ExceptionHandler;
import de.adesso.tools.functions.DtFunctions;
import de.adesso.tools.functions.List2DFunctions;
import de.adesso.tools.functions.MoreCollectors;
import de.adesso.tools.io.DTDataPacket;
import de.adesso.tools.model.ActionDecl;
import de.adesso.tools.model.ConditionDecl;
import de.adesso.tools.ui.action.ActionDeclTableViewModel;
import de.adesso.tools.ui.condition.ConditionDeclTableViewModel;
import de.adesso.tools.ui.scopes.RuleScope;
import de.adesso.tools.util.tuple.Tuple;
import de.adesso.tools.util.tuple.Tuple2;
import de.adesso.tools.util.tuple.Tuple3;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.collect.Multimaps0.emptyMultimap;
import static de.adesso.tools.analysis.completeness.detailed.ConditionDetailedCompletenessCheck.isFormalComplete;
import static de.adesso.tools.functions.Adapters.Matrix.adapt;
import static de.adesso.tools.functions.DtFunctions.*;
import static de.adesso.tools.functions.List2DFunctions.insertColumnsAt;
import static de.adesso.tools.ui.Notifications.*;
import static java.util.Collections.emptyList;

@Singleton
public class MainViewModel implements ViewModel {

    public static final String QMARK = "?";
    public static final Object[] NO_ARGS = {};
    public static final String COND_ROW_HEADER = "C%02d";
    public static final String DASH = "-";
    private static final String ACT_ROW_HEADER = "A%02d";
    private final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> conditionDefinitions = FXCollections.observableArrayList();
    private final ObservableList<ActionDeclTableViewModel> actionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> actionDefinitions = FXCollections.observableArrayList();
    private final DTDataPacket data = new DTDataPacket(conditionDeclarations, conditionDefinitions, actionDeclarations, actionDefinitions);

    @InjectScope
    private RuleScope mdScope;

    @Inject
    private NotificationCenter notificationCenter;

    @Inject
    private StructuralAnalysis structuralAnalysis;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private AnalysisResultEmitter resultEmitter;
    private List<String> missingRules = emptyList();
    private Multimap<Integer, Integer> compressibleRules = emptyMultimap();
    private Multimap<Integer, Integer> dupplicateRules = emptyMultimap();
    private DTDataPacket loadedData = null;

    public MainViewModel() {
        super();
    }

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
        List<List<String>> newDefns = List2DFunctions.addColumn(adapt(this.conditionDefinitions), () -> QMARK);
        this.conditionDefinitions.clear();
        List<List<String>> newDefns0 = List2DFunctions.addColumn(adapt(this.actionDefinitions), () -> DASH);
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
            List<List<String>> newDefns = List2DFunctions.addRow(adapt(this.conditionDefinitions), () -> QMARK);
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
                        .mapToObj(k -> DASH)
                        .collect(Collectors.toList());
                newDefns.add(indicators);
            }
        } else {
            newDefns = List2DFunctions.addRow(adapt(this.actionDefinitions), () -> DASH);

        }
        this.actionDefinitions.clear();
        adapt(newDefns).stream().forEach(this.actionDefinitions::add);
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
        List<List<String>> newDefns = List2DFunctions.addColumn(adapt(this.conditionDefinitions), () -> QMARK);
        this.conditionDefinitions.clear();
        List<List<String>> newDefns0 = List2DFunctions.addColumn(adapt(this.actionDefinitions), () -> DASH);
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
        Optional<String> s = internalFormalCompletenessCheck();
        if(s.isPresent()) {
            notificationCenter.publish(PREPARE_CONSOLE.name(), s.get());
            notifyMissingRulesCleared();
        }
    }

    private Optional<String> internalFormalCompletenessCheck() {
        String ret = null;
        try {
            final List<Indexed<List<String>>> result = isFormalComplete(this.conditionDeclarations, this.conditionDefinitions);
            Tuple2<String, List<String>> tuple2 = resultEmitter.emitFormalCompletenessCheckResults().apply(result);
            missingRules = tuple2._2();
            ret = tuple2._1();
            ret = (Strings.isNullOrEmpty(ret)) ? "RULES COMPLETE" : ret;
        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
        }
        return Optional.ofNullable(ret);
    }

    public void onCompleteReportAction(@Observes CompleteReportEvent event) {
        final Optional<String> part1 = internalStructuralAnalysis();
        final Optional<String> part2 = internalFormalCompletenessCheck();

        String message;
        if(part1.isPresent() && part2.isPresent()) {
            message = part1.get() + System.lineSeparator() + part2.get();
        } else {
            message = (part1.isPresent())
                    ? part1.get()
                    : (part2.isPresent()
                    ? part2.get()
                    : "");
        }

        notificationCenter.publish(PREPARE_CONSOLE.name(), message);
        notifyMissingRulesCleared();
        notifyRulesConsolidated();
        notifyRuleDuplicatesCleared();
    }

    public void onStructuralAnalysisAction(@Observes StructuralAnalysisEvent event) {
        Optional<String> s = internalStructuralAnalysis();
        if(s.isPresent()) {
            notificationCenter.publish(PREPARE_CONSOLE.name(), s.get());
            notifyRulesConsolidated();
            notifyRuleDuplicatesCleared();
        }
    }

    // TODO Validation!! PRE: all table containers must not be empty!!
    private Optional<String> internalStructuralAnalysis() {
        String ret = null;
        try {

            List<Indicator> result = structuralAnalysis.apply(adapt(this.conditionDefinitions), adapt(this.actionDefinitions));

            final Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>> tuple3 =
                    resultEmitter.emitStructuralAnalysisResult().apply(result, this.conditionDefinitions.get(0).size());

            compressibleRules = tuple3._2();
            dupplicateRules = tuple3._3();
            ret = tuple3._1();

        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
        }

        return Optional.ofNullable(ret);
    }

    public void onFileNewAction(@Observes FileNewEvent event) {

    }

    public void onConsolidateRulesAction(@Observes ConsolidateRulesEvent event) {
        publish(EV_CONSOLIDATE_RULES.name(), compressibleRules);
        compressibleRules.clear();
        notifyRulesConsolidated();

    }

    public void onAddMissingRules(@Observes AddMissingRulesEvent event) {
        missingRules.clear();
        notifyMissingRulesCleared();
        throw new UnsupportedOperationException("Not implemented yet!!");
    }

    public void onDeleteRedundantRules(@Observes DeleteRedundantRulesEvent event) {
        Stream<Tuple2<List<List<String>>, List<List<String>>>> dt
                = Stream.of(Tuple.of(adapt(getConditionDefinitions()), adapt(getActionDefinitions())));
        Tuple2<List<List<String>>, List<List<String>>> clearedDt
                = dt.map(Operators.rejectDupplicateRules()).collect(MoreCollectors.toSingleObject());



        dupplicateRules.clear();
        notifyRuleDuplicatesCleared();
        throw new UnsupportedOperationException("Not implemented yet!!");

    }

    private void notifyRulesConsolidated() {
        mdScope.consolidateRulesProperty().set(compressibleRules.isEmpty());
    }

    private void notifyMissingRulesCleared() {
        mdScope.missingRulesProperty().set(missingRules.isEmpty());
    }

    private void notifyRuleDuplicatesCleared() {
        mdScope.removeDuplicateRulesProperty().set(dupplicateRules.isEmpty());
    }

    public void onFileOpenAction(@Observes FileOpenEvent event) {
        publish(FILE_OPEN.name(), NO_ARGS);
    }

    public void onFileSaveAsAction(@Observes FileSaveAsEvent event) {
        publish(FILE_SAVE_AS.name(), NO_ARGS);
    }

    public void onFileSaveAction(@Observes FileSaveEvent event) {

    }

    public int openFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            loadedData = ((DTDataPacket) in.readObject());
            return loadedData.getConditionDefinitions().get(0).size();
        }
    }

    public void populateLoadedData() {
        this.data.become(loadedData);
    }

    public void saveFile(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this.data);
            out.flush();
        }
    }

    public Tuple2<List<? extends  List<String>>, List<? extends List<String>>>
    doInsRule(OptionalInt index, Tuple2<ObservableList, ObservableList> oldDefs) {

        final List<? extends List<String>> newConDefs =
                insertColumnsAt(oldDefs._1(), index.getAsInt(), () -> DtFunctions.QMARK);
        final List<? extends List<String>> newActDefs =
                insertColumnsAt(oldDefs._2(), index.getAsInt(), () -> DtFunctions.DASH);
        return Tuple.of(newConDefs, newActDefs);
    }
}
