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
import java.io.*;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mmoehler on 02.07.16.
 */
public class BinaryPersistenceStrategy implements PersistenceStrategy<DtEntity> {

    public static final String DTM = "dtm";

    public BinaryPersistenceStrategy() {
    }

    @Nonnull
    @Override
    public String extension() {
        return DTM;
    }

    @Override
    public DtEntity read(URI source) {
        checkNotNull(source, "Missing Source URI!");
        final String path = ParseUtil.decode(source.getPath());
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
             FileInputStream fileInputStream = new FileInputStream(randomAccessFile.getFD());
             ObjectInputStream in = new ObjectInputStream(fileInputStream)) {
            return ((DtEntity) in.readObject());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(DtEntity dtEntity, URI target) {
        checkNotNull(target, "Missing Target URL!");
        checkNotNull(dtEntity, "Missing Entity to Save!");

        final String path = ParseUtil.decode(target.getPath());
        try (RandomAccessFile raf = new RandomAccessFile(path, "rw");
             FileOutputStream fos = new FileOutputStream(raf.getFD());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(dtEntity);
            out.flush();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
