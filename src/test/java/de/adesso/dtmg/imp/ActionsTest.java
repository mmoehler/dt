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

package de.adesso.dtmg.imp;

import com.codepoetics.protonpack.StreamUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import de.adesso.dtmg.functions.MoreCollectors;
import de.adesso.dtmg.model.ActionDecl;
import de.adesso.dtmg.ui.action.ActionDeclTableViewModel;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by mmoehler on 16.07.16.
 */
public class ActionsTest {

    public static final String ACT_INDICATORS = "X";

    @Test
    public void testImportActions() throws Exception {

        File ff = new File(".");
        String s = ff.getAbsolutePath();
        System.out.println("s = " + s);

        File f = new File("src/main/java/de/adesso/dtmg/imp/Actions.java");
        assertThat(f.exists(), is(TRUE));

        CompilationUnit cu = null;
        try(FileInputStream fis = new FileInputStream(f)) {
            cu = JavaParser.parse(fis);

            TypeDeclaration typeDeclaration = cu.getTypes().get(0);
            ObservableList<ActionDeclTableViewModel> list = StreamUtils.zipWithIndex(typeDeclaration.getMembers().stream()
                    .filter(m -> m instanceof MethodDeclaration)
                    .map(m -> ((MethodDeclaration) m).getName()))
                    .map(i -> new ActionDecl(String.valueOf(i.getIndex()), i.getValue(), ACT_INDICATORS))
                    .map(ActionDeclTableViewModel::new)
                    .collect(MoreCollectors.toObservableList());

            list.forEach(System.out::println);

        }
    }


}
