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

package de.adesso.dtmg.export;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import de.adesso.dtmg.export.odf.OdtExportStrategy;
import de.adesso.dtmg.io.DtEntity;
import sun.net.www.ParseUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 04.06.16.
 */
@Singleton
public class DefaultExportManager implements ExportManager<DtEntity> {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private final Map<String, ExportStrategy<DtEntity>> strategies = Maps.newHashMap();

    public DefaultExportManager() {
    }

    @PostConstruct
    public void initStrategies() {
        ExportStrategy<DtEntity> strategyArray[] = new ExportStrategy[]{
                new OdtExportStrategy()
        };
        Arrays.stream(strategyArray).forEach(p -> strategies.put(p.extension(), p));
    }

    @PreDestroy
    public void releaseStrategies() {
        strategies.clear();
        pool.shutdownNow();
    }

    @Override
    public void export(DtEntity dtEntity, URI target) {
        checkNotNull(target, "Missing Target URL!");
        checkNotNull(dtEntity, "Missing Entity to Export!");
        detectStrategyAndDo(target, (s) -> {
            s.export(dtEntity, target);
            return dtEntity;
        });
    }

    private DtEntity detectStrategyAndDo(URI source, Function<ExportStrategy<DtEntity>, DtEntity> strategyEvaluation) {
        final String path = ParseUtil.decode(source.getPath());
        String extension = Files.getFileExtension(path);
        ExportStrategy<DtEntity> strategy = strategies.get(extension);
        if (null != strategy) {
            return strategyEvaluation.apply(strategy);
        }
        throw new IllegalArgumentException(String.format("No matching export strategy for %s!", String.valueOf(source)));
    }


}
