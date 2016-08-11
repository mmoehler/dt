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

package de.adesso.dtmg.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by moehler on 11.08.2016.
 */
public class PackageDependencyCheck {
    public static final String IMPORT_STATIC = "import static";
    public static final String IMPORT = "import";
    public static final String UTF_8 = "utf8";
    public static final String PACKAGE = "package";
    Multimap<String, String> adjacencyList = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

    public void scan(File f) {
        checkNotNull(f, "Missing File!!");
        if (f.isDirectory()) {
            final File[] files = f.listFiles();
            Arrays.stream(files).forEach(this::scan);
        } else {
            if(f.getName().endsWith(".java"))
                scanImports(f);
        }
    }

    private void scanImports(File f) {
        checkNotNull(f, "Missing File!!");
        final List<String> lines;
        try {
            lines = Files.lines(Paths.get(f.toURI()), Charset.forName(UTF_8)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException("Trouble with: " + f.getAbsolutePath());
        }

        final Optional<String> packageMame = lines.stream()
                .filter(l -> l.startsWith(PACKAGE)).findFirst();

        if(packageMame.isPresent()) {
            String tmp = packageMame.get();
            final String ps = extractPackageName(PACKAGE, tmp.substring(0,tmp.length()-1));
            lines.stream()
                    .filter(l -> l.startsWith(IMPORT))
                    .map(k -> {
                        if (k.startsWith(IMPORT_STATIC)) {
                            return extractPackageName(IMPORT_STATIC,k);
                        }
                        return extractPackageName(IMPORT,k);
                    })
                    .forEach(m -> adjacencyList.put(m,ps));
        }
    }

    private String extractPackageName(String prefix, String s) {
        String tmp = s.substring(prefix.length(), s.lastIndexOf('.')).trim();
        final Optional<String> ret = Splitter.on('.').splitToList(tmp).stream().filter(t -> !Character.isUpperCase(t.charAt(0)))
                .reduce((l, r) -> l + '.' + r);
        System.err.println(ret.get());
        return ret.get();
    }

    public static void main(String[] args) {
        String path = "C:\\cygwin64\\home\\moehler\\m2-projects\\dt\\src\\main\\java\\de\\adesso\\dtmg";
        final PackageDependencyCheck check = new PackageDependencyCheck();
        check.scan(new File(path));
        Dump.dumpSimpleDot(check.adjacencyList, (v) -> v.startsWith("de.adesso"), (v) -> v.startsWith("de.adesso"));

    }


}
