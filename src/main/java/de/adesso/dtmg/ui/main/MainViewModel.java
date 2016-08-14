package de.adesso.dtmg.ui.main;

import com.codepoetics.protonpack.Indexed;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import de.adesso.dtmg.analysis.structure.AnalysisResultEmitter;
import de.adesso.dtmg.analysis.structure.Indicator;
import de.adesso.dtmg.analysis.structure.StructuralAnalysis;
import de.adesso.dtmg.events.*;
import de.adesso.dtmg.exception.ExceptionHandler;
import de.adesso.dtmg.export.ExportManager;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.io.PersistenceManager;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.DeclarationTableViewModel;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import de.adesso.dtmg.ui.menu.RecentItems;
import de.adesso.dtmg.ui.scopes.RuleScope;
import de.adesso.dtmg.util.DtFunctions;
import de.adesso.dtmg.util.List2DFunctions;
import de.adesso.dtmg.util.MoreCollectors;
import de.adesso.dtmg.util.ObservableListFunctions;
import de.adesso.dtmg.util.tuple.Tuple;
import de.adesso.dtmg.util.tuple.Tuple2;
import de.adesso.dtmg.util.tuple.Tuple3;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Multimaps0.emptyMultimap;
import static de.adesso.dtmg.analysis.completeness.detailed.ConditionDetailedCompletenessCheck.isFormalComplete;
import static de.adesso.dtmg.ui.Notifications.*;
import static de.adesso.dtmg.util.Adapters.Matrix.adapt;
import static de.adesso.dtmg.util.DtFunctions.fullExpandActions;
import static de.adesso.dtmg.util.DtFunctions.limitedExpandConditions;
import static de.adesso.dtmg.util.List2DFunctions.insertColumnsAt;
import static java.util.Collections.emptyList;

@Singleton
@ScopeProvider(scopes={RuleScope.class})
public class MainViewModel implements ViewModel {

    public static final String QMARK = "?";
    public static final String ELSE = "E";
    public static final Object[] NO_ARGS = {};
    public static final String COND_ROW_HEADER = "C%02d";
    public static final String DASH = "-";
    private static final String ACT_ROW_HEADER = "A%02d";
    private final static Predicate<ObservableList<String>> HAS_ELSE_RULE =
            c -> (c.isEmpty()) ? false : c.get(c.size() - 1).equals(ELSE);
    public static final String STR_RULES_COMPLETE = "RULES COMPLETE";
    private final ObservableList<ConditionDeclTableViewModel> conditionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> conditionDefinitions = FXCollections.observableArrayList();
    private final ObservableList<ActionDeclTableViewModel> actionDeclarations = FXCollections.observableArrayList();
    private final ObservableList<ObservableList<String>> actionDefinitions = FXCollections.observableArrayList();
    private final BooleanProperty elseRule = new SimpleBooleanProperty(false);

    private final DtEntity data = new DtEntity(
            conditionDeclarations,
            conditionDefinitions,
            actionDeclarations,
            actionDefinitions
    );

    @InjectScope
    private RuleScope mdScope;

    @Inject
    private StructuralAnalysis structuralAnalysis;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    private AnalysisResultEmitter resultEmitter;

    @Inject
    private PersistenceManager<DtEntity> persistenceManager;

    @Inject
    private ExportManager<DtEntity> exportManager;

    private List<String> missingRules = emptyList();
    private Multimap<Integer, Integer> compressibleRules = emptyMultimap();
    private Multimap<Integer, Integer> dupplicateRules = emptyMultimap();
    private DtEntity loadedData = null;
    private boolean elseRuleSet = true;

    private final ListChangeListener<DeclarationTableViewModel> decRowListener;
    private final ListChangeListener<ObservableList<String>> defRowListener;
    private final ListChangeListener<String> defColListener;
    private final BooleanProperty changed = new SimpleBooleanProperty(false);

    public MainViewModel() {
        super();

        defRowListener = new ListChangeListener<ObservableList<String>>() {
            @Override
            public void onChanged(Change<? extends ObservableList<String>> c) {
                changed.set(true);
            }
        };

        defColListener = new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                changed.set(true);
            }
        };

        decRowListener = new ListChangeListener<DeclarationTableViewModel>() {
            @Override
            public void onChanged(Change<? extends DeclarationTableViewModel> c) {
                changed.set(true);
            }
        };


        Lists.newArrayList(conditionDefinitions,actionDefinitions).forEach(e -> {
            e.forEach(f -> f.addListener(defColListener));
            e.addListener(defRowListener);
        });

    }

    @PreDestroy
    protected void preDestroy() {
        Lists.newArrayList(conditionDefinitions,actionDefinitions).forEach(e -> {
            e.forEach(f -> f.removeListener(defColListener));
            e.removeListener(defRowListener);
        });

    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    public void setUnchanged() {
        this.changed.set(false);
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
        if (hasNoElseRule().test(this.conditionDefinitions.get(0))) {
            List<List<String>> newDefns = List2DFunctions.addColumn(adapt(this.conditionDefinitions), () -> QMARK);
            this.conditionDefinitions.clear();
            List<List<String>> newDefns0 = List2DFunctions.addColumn(adapt(this.actionDefinitions), () -> DASH);
            this.actionDefinitions.clear();
            publish(ADD_RULE.name(), adapt(newDefns), adapt(newDefns0));
        } else {
            publish(INS_RULE.name(), this.conditionDefinitions.get(0).size() - 1);
        }
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
            List<List<String>> newDefns = internalAddConditionDefnsRow();
            this.conditionDefinitions.clear();
            adapt(newDefns).stream().forEach(this.conditionDefinitions::add);
        }
    }

    private List<List<String>> internalAddConditionDefnsRow() {
        List<List<String>> row = (HAS_ELSE_RULE.test(this.conditionDefinitions.get(0)))
                ? List2DFunctions.addRowWithElseRule(adapt(this.conditionDefinitions), () -> QMARK)
                : List2DFunctions.addRow(adapt(this.conditionDefinitions), () -> QMARK);
        return row;
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
        if (hasNoElseRule().test(this.conditionDefinitions.get(0))) {

            List<List<String>> newCDefns = List2DFunctions.addColumn(adapt(this.conditionDefinitions), () -> ELSE);
            this.conditionDefinitions.clear();

            List<List<String>> newADefns = List2DFunctions.addColumn(adapt(this.actionDefinitions), () -> DASH);
            this.actionDefinitions.clear();

            publish(ADD_ELSE_RULE.name(), adapt(newCDefns), adapt(newADefns));

            this.elseRuleSet = true;
            this.elseRule.set(true);
            notifyElseRuleSet();
        }
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
        if (s.isPresent()) {
            this.publish(PREPARE_CONSOLE.name(), s.get());
        }
    }

    public void onUpdateDeclDoc(@Observes UpdateDeclDocEvent event) {
        Optional<String> s = Optional.ofNullable(event.getText());
        if (s.isPresent()) {
            this.publish(UPD_DOCUMENT.name(), s.get());
        }
    }



    private Predicate<ObservableList<String>> hasNoElseRule() {
        return c -> (c.isEmpty()) ? true : !c.get(c.size() - 1).equals(ELSE);
    }

    private Function<ObservableList<String>, ObservableList<String>> suppressElseRule() {
        return i -> (hasNoElseRule().test(i)) ? (i) : (ObservableListFunctions.take(i, i.size() - 1));
    }

    private Optional<String> internalFormalCompletenessCheck() {
        String ret = null;
        try {
            if(hasNoElseRule().test(this.conditionDefinitions.get(0))) {
                final List<Indexed<List<String>>> result = isFormalComplete(this.conditionDeclarations, this.conditionDefinitions);
                Tuple2<String, List<String>> tuple2 = resultEmitter.emitFormalCompletenessCheckResults().apply(result);
                missingRules = tuple2._2();
                ret = tuple2._1();
                ret = (Strings.isNullOrEmpty(ret)) ? STR_RULES_COMPLETE : ret;
            } else {
                ret = STR_RULES_COMPLETE;
            }
        } catch (Exception e) {
            exceptionHandler.showAndWaitAlert(e);
        }
        return Optional.ofNullable(ret);
    }

    public void onCompleteReportAction(@Observes CompleteReportEvent event) {
        final Optional<String> part1 = internalStructuralAnalysis();
        final Optional<String> part2 = internalFormalCompletenessCheck();

        String message;
        if (part1.isPresent() && part2.isPresent()) {
            message = part1.get() + System.lineSeparator() + part2.get();
        } else {
            message = (part1.isPresent())
                    ? part1.get()
                    : (part2.isPresent()
                    ? part2.get()
                    : "");
        }

        this.publish(PREPARE_CONSOLE.name(), message);
        notifyRulesConsolidated();
    }

    public void onStructuralAnalysisAction(@Observes StructuralAnalysisEvent event) {
        Optional<String> s = internalStructuralAnalysis();
        if (s.isPresent()) {
            this.publish(PREPARE_CONSOLE.name(), s.get());
            notifyRulesConsolidated();
        }
    }

    private Optional<String> internalStructuralAnalysis() {
        String ret = null;
        try {

            Preconditions.checkArgument(!this.conditionDefinitions.isEmpty(), "Structural Analysis without Condition Definitions is not possible!");
            Preconditions.checkArgument(!this.actionDefinitions.isEmpty(), "Structural Analysis without Action Definitions is not possible!");

            // check whether an else rule is defined! If yes make it unavailable for this action!!
            ObservableList<ObservableList<String>> clearedConditions = this.conditionDefinitions;
            ObservableList<ObservableList<String>> clearedActions = this.actionDefinitions;
            if (!this.hasNoElseRule().test(this.conditionDefinitions.get(0))) {
                clearedConditions = this.conditionDefinitions.stream().map(suppressElseRule()).collect(MoreCollectors.toObservableList());
                clearedActions = this.actionDefinitions.stream().map(c -> ObservableListFunctions.take(c, c.size() - 1)).collect(MoreCollectors.toObservableList());
            }
            List<Indicator> result = structuralAnalysis.apply(adapt(clearedConditions), adapt(clearedActions));
            final Tuple3<String, Multimap<Integer, Integer>, Multimap<Integer, Integer>> tuple3 =
                    resultEmitter.emitStructuralAnalysisResult().apply(result, clearedConditions.get(0).size());

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

    private void notifyRulesConsolidated() {
        mdScope.consolidateRulesProperty().set(compressibleRules.isEmpty());
    }

    private void notifyElseRuleSet() {
        mdScope.elseRuleProperty().set(elseRuleSet);
    }

    public void onFileOpenAction(@Observes FileOpenEvent event) {
        publish(FILE_OPEN.name(), (null == event.getUri()) ? NO_ARGS : event.getUri());
    }

    public void onFileSaveAsAction(@Observes FileSaveAsEvent event) {
        publish(FILE_SAVE_AS.name(), NO_ARGS);
    }

    public void onFileExportAsAction(@Observes FileExportAsEvent event) {
        publish(FILE_EXPORT_AS.name(), NO_ARGS);
    }

    public void onFileSaveAction(@Observes FileSaveEvent event) {

    }

    public void onDocumentDeclaration(@Observes DocumentDeclarationEvent event) {
        publish(DOCUMENT_DECLARATION.name(), NO_ARGS);
    }




    public int openFile(URI url) throws IOException, ClassNotFoundException {
        loadedData = persistenceManager.read(url);
        return loadedData.getConditionDefinitions().get(0).size();
    }

    public DtEntity populateLoadedData() {
        this.data.become(loadedData);
        return this.data;
    }

    public void saveFile(URI url) throws IOException {
        persistenceManager.write(this.data, url);
    }

    public void exportFile(URI url) throws IOException {
        exportManager.export(this.data, url);
    }


    public Tuple2<List<? extends List<String>>, List<? extends List<String>>>
    doInsRule(OptionalInt index, Tuple2<ObservableList, ObservableList> oldDefs) {

        final List<? extends List<String>> newConDefs =
                insertColumnsAt(oldDefs._1(), index.getAsInt(), () -> DtFunctions.QMARK);
        final List<? extends List<String>> newActDefs =
                insertColumnsAt(oldDefs._2(), index.getAsInt(), () -> DtFunctions.DASH);
        return Tuple.of(newConDefs, newActDefs);
    }

    public Optional<RecentItems> getRecentItems() {
        return Optional.ofNullable(mdScope.recentItems());
    }

    public Collection<Scope> getDialogScopes() {
        return Collections.singletonList(mdScope);
    }
}
