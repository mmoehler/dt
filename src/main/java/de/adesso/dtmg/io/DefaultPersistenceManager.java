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

package de.adesso.dtmg.io;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import de.adesso.dtmg.io.strategy.BinaryPersistenceStrategy;
import de.adesso.dtmg.io.strategy.HorizontalAsciiPersistenceStrategy;
import de.adesso.dtmg.io.strategy.VerticalAsciiPersistenceStrategy;
import sun.net.www.ParseUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 04.06.16.
 */
@Singleton
public class DefaultPersistenceManager implements PersistenceManager<DtEntity> {

    private final Map<String, PersistenceStrategy<DtEntity>> strategies = Maps.newHashMap();

    public DefaultPersistenceManager() {
    }

    @PostConstruct
    public void initStrategies() {
        PersistenceStrategy<DtEntity> strategyArray[] = new PersistenceStrategy[]{
                new BinaryPersistenceStrategy(),
                new VerticalAsciiPersistenceStrategy(),
                new HorizontalAsciiPersistenceStrategy()
        };
        Arrays.stream(strategyArray).forEach(p -> strategies.put(p.extension(),p));
    }

    @PreDestroy
    public void releaseStrategies() {
        strategies.clear();
    }

    @Override
    public DtEntity read(final URL source) {
        checkNotNull(source, "Missing Source URL!");
        return detectStrategyAndDo(source, (s) -> s.read(source)  );

    }

    @Override
    public void write(DtEntity dtEntity, URL target) {
        checkNotNull(target, "Missing Target URL!");
        checkNotNull(dtEntity, "Missing Entity to Save!");
        detectStrategyAndDo(target, (s) -> {
            s.write(dtEntity, target);
            return dtEntity;
        });
    }

    private DtEntity detectStrategyAndDo(URL source, Function<PersistenceStrategy<DtEntity>, DtEntity> strategyEvaluation) {
        final String path = ParseUtil.decode(source.getPath());
        String extension = Files.getFileExtension(path);
        PersistenceStrategy<DtEntity> strategy = strategies.get(extension);
        if(null != strategy) {
            return strategyEvaluation.apply(strategy);
        }
        throw new IllegalArgumentException(String.format("No matching persistence strategy for %s!", String.valueOf(source)));
    }


}
