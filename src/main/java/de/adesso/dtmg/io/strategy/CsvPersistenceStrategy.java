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

import de.adesso.dtmg.io.DtEntity;
import de.adesso.dtmg.io.PersistenceStrategy;
import sun.net.www.ParseUtil;

import javax.annotation.Nonnull;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * Created by moehler on 01.08.2016.
 */
public class CsvPersistenceStrategy implements PersistenceStrategy<DtEntity> {

    public static final String EXTENSION = "csv";

    @Nonnull
    @Override
    public String extension() {
        return EXTENSION;
    }

    @Override
    public DtEntity read(URL source) {

        return null;

    }

    @Override
    public void write(DtEntity dtEntity, URL target) {
        final String path = ParseUtil.decode(target.getPath());
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw");
             FileOutputStream fos = new FileOutputStream(raf.getFD());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {

            //TextDocument outputOdt = createTextDocument(dtEntity);
            // outputOdt.save(out);

            out.writeObject(dtEntity);
            out.flush();

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
