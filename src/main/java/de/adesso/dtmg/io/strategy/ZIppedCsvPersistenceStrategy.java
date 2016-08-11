/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.adesso.dtmg.io.strategy;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import de.adesso.dtmg.util.MoreCollectors;
import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import de.adesso.dtmg.ui.condition.ConditionDeclTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.net.www.ParseUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adesso.dtmg.exception.LambdaExceptionUtil.rethrowConsumer;

/**
 * Created by moehler on 01.08.2016.
 */
public class ZIppedCsvPersistenceStrategy extends AbstractPersistenceStrategy<DtEntity> {

    public static final String EXTENSION = "dtz";
    public static final String STR_DASH = "-";
    public static final String STR_SEMICOLON = ";";
    public static final String STR_EMPTY = "";
    public static final String STR_PROTOCOL_JAR = "jar:";
    public static final String STR_TRUE = "true";
    public static final String STR_CREATE = "create";
    public static final String TPL_001_S_D = "part.%04d";
    public static final String STR_UTF_8 = "utf8";
    private static final String STR_READ = "read";
    private static final String STR_FALSE = "false";

    public ZIppedCsvPersistenceStrategy(ExecutorService pool) {
        super(pool);
    }

    @Nonnull
    @Override
    public String extension() {
        return EXTENSION;
    }

    @Override
    public DtEntity read(URI source) {

        Function<String, ConditionDeclTableViewModel> csv2conditionDecl
                = (s) -> new ConditionDeclTableViewModel(ConditionDecl.of(Splitter.on(STR_SEMICOLON).trimResults().splitToList(s)));
        Function<String, ActionDeclTableViewModel> csv2actionDecl
                = (s) -> new ActionDeclTableViewModel(ActionDecl.of(Splitter.on(STR_SEMICOLON).trimResults().splitToList(s)));
        Function<String, ObservableList<String>> csv2definition
                = (s) -> FXCollections.observableArrayList(Splitter.on(STR_SEMICOLON).trimResults().splitToList(s));

        final Path path = Paths.get(ParseUtil.decode(source.getPath()));
        URI p = path.toUri();
        URI uri = URI.create( STR_PROTOCOL_JAR + p );

        Map<String, String> env = new HashMap<>();
        env.put(STR_READ, STR_FALSE);
        try ( FileSystem zipfs = FileSystems.newFileSystem( uri, env ) ) {

            Callable<ObservableList<ConditionDeclTableViewModel>> readConditionDecls = () ->
                    Files.readAllLines(zipfs.getPath(String.format(TPL_001_S_D, 1))).stream().map(csv2conditionDecl).collect(MoreCollectors.toObservableList());

            Callable<ObservableList<ActionDeclTableViewModel>> readActionDecls = () ->
                    Files.readAllLines(zipfs.getPath(String.format(TPL_001_S_D, 2))).stream().map(csv2actionDecl).collect(MoreCollectors.toObservableList());

            Callable<ObservableList<ObservableList<String>>> readConditionDefns = () ->
                    Files.readAllLines(zipfs.getPath(String.format(TPL_001_S_D, 3))).stream().map(csv2definition).collect(MoreCollectors.toObservableList());

            Callable<ObservableList<ObservableList<String>>> readActionDefns = () ->
                    Files.readAllLines(zipfs.getPath(String.format(TPL_001_S_D, 4))).stream().map(csv2definition).collect(MoreCollectors.toObservableList());


            Future<ObservableList<ConditionDeclTableViewModel>> cdeclFuture = pool.submit(readConditionDecls);
            Future<ObservableList<ActionDeclTableViewModel>> adeclFuture = pool.submit(readActionDecls);
            Future<ObservableList<ObservableList<String>>> cdefnFuture = pool.submit(readConditionDefns);
            Future<ObservableList<ObservableList<String>>> adefnFuture = pool.submit(readActionDefns);

            return new DtEntity(
                    cdeclFuture.get(1L,TimeUnit.SECONDS),
                    cdefnFuture.get(1L,TimeUnit.SECONDS),
                    adeclFuture.get(1L,TimeUnit.SECONDS),
                    adefnFuture.get(1L,TimeUnit.SECONDS)
            );

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    protected String ifNull(String s, String use) {
        return Strings.isNullOrEmpty(s) ? use : s;
    }

    @Override
    public void write(DtEntity dtEntity, URI target) {

        Function<ConditionDeclTableViewModel, String> conditionDecl2csv = (m) -> m.getModel().asList().stream()
                .reduce((l, r) -> ifNull(l, STR_DASH) + STR_SEMICOLON + ifNull(r, STR_DASH)).get();

        Function<ActionDeclTableViewModel, String> actionDecl2csv = (m) -> m.getModel().asList().stream()
                .reduce((l, r) -> ifNull(l," ") + STR_SEMICOLON + ifNull(r," ")).get();

        Function<ObservableList<String>, String> definitions2csv = (s) -> s.stream()
                .reduce((l, r) -> ifNull(l, STR_DASH) + STR_SEMICOLON + ifNull(r, STR_DASH)).get();


        final Path path = Paths.get(ParseUtil.decode(target.getPath()));
        URI p = path.toUri();
        URI uri = URI.create( STR_PROTOCOL_JAR + p );

        Map<String, String> env = new HashMap<>();
        env.put(STR_CREATE, STR_TRUE);
        try ( FileSystem zipfs = FileSystems.newFileSystem( uri, env ) ) {

            Runnable runnables[] = new Runnable[]{
                    () -> {
                        List<String> lines = dtEntity.getConditionDeclarations().stream().map(conditionDecl2csv).collect(Collectors.toList());
                        try {
                            Files.write(zipfs.getPath(String.format(TPL_001_S_D, 1)), lines, Charset.forName(STR_UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    },
                    () -> {
                        List<String> lines = dtEntity.getActionDeclarations().stream().map(actionDecl2csv).collect(Collectors.toList());
                        try {
                            Files.write(zipfs.getPath(String.format(TPL_001_S_D, 2)), lines, Charset.forName(ZIppedCsvPersistenceStrategy.STR_UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    },
                    () -> {
                        List<String> lines = dtEntity.getConditionDefinitions().stream().map(definitions2csv).collect(Collectors.toList());
                        try {
                            Files.write( zipfs.getPath( String.format(TPL_001_S_D, 3) ), lines, Charset.forName(ZIppedCsvPersistenceStrategy.STR_UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    },
                    () -> {
                        List<String> lines = dtEntity.getActionDefinitions().stream().map(definitions2csv).collect(Collectors.toList());
                        try {
                            Files.write( zipfs.getPath( String.format(TPL_001_S_D, 4) ), lines, Charset.forName(ZIppedCsvPersistenceStrategy.STR_UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    },
            };

            final List<Future<Boolean>> futures = Arrays.stream(runnables).map(r -> pool.submit(r, true)).collect(Collectors.toList());

            futures.forEach(rethrowConsumer(f -> f.get(1L, TimeUnit.SECONDS)));

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
