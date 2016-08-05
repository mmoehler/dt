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

package de.adesso.dtmg.util.zip;

import com.google.common.base.Splitter;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.model.ConditionDecl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ZipFSPUser {

    public static void put() throws Exception {
        final Path path = Paths.get("./csv.dmz");
        URI p = path.toUri();
        URI uri = URI.create( "jar:" + p );

        Map<String, String> env = new HashMap<>();
        env.put( "create", "true" );
        try ( FileSystem zipfs = FileSystems.newFileSystem( uri, env ) ) {
            for (int i = 0; i < 5; i++) {
                Files.write( zipfs.getPath( String.format("csv.%d",(i+1)) ), String.format("Content of csv.%d",i).getBytes() );
           }
        }
    }

    public static void get() throws Exception {
        final Path path = Paths.get("./csv.dmz");
        URI p = path.toUri();
        URI uri = URI.create( "jar:" + p );

        Map<String, String> env = new HashMap<>();
        env.put( "create", "false" );

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

            for (int i = 0; i < 5; i++) {
                Files.lines(zipfs.getPath(String.format("csv.%d", (i + 1)))).forEach(System.out::println);
            }

        }
    }

    public String encodeCSV(ConditionDecl row) {
        return row.asList().stream().reduce("",(l,r) -> ((null==l)?"-":l) + ";" + ((null==r)?"-":r));
    }

    public String encodeCSV(ActionDecl row) {
        return row.asList().stream().reduce("",(l,r) -> ((null==l)?"-":l) + ";" + ((null==r)?"-":r));
    }

    public String encodeCSV(ObservableList<String> row) {
        return row.stream().reduce("",(l,r) -> ((null==l)?"-":l) + ";" + ((null==r)?"-":r));
    }

    ClassPool cp = ClassPool.getDefault();

    public ObservableList<String> decodeCSV(String row, Class clazz) throws NotFoundException {




        return FXCollections.observableArrayList(Splitter.on(';').trimResults().splitToList(row));



    }


    public static void main(String [] args) throws Throwable {
        put();
        get();
    }

}