/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/17/2018-2.7.2 Lukas Jungmann
 *       - 531305: Canonical model generator fails to run on JDK9
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.modelgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;

import javax.persistence.Entity;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestProcessor {

    @BeforeClass
    public static void prepare() throws IOException {
        File testRoot = new File(System.getProperty("run.dir"));
        if (testRoot.exists() && testRoot.isDirectory()) {
            for (File testDir: testRoot.listFiles()) {
                delete(testDir);
            }
        }
    }
    
    @Test
    public void testProc() throws Exception {
        File runDir = new File(System.getProperty("run.dir"), "testproc");
        File srcOut = new File(runDir, "src");
        srcOut.mkdirs();
        File cpDir = new File(runDir, "cp");
        cpDir.mkdirs();
        File pxml = new File(cpDir, "META-INF/persistence.xml");
        pxml.getParentFile().mkdirs();
        try (BufferedWriter writer = Files.newBufferedWriter(pxml.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(PXML, 0, PXML.length());
        } catch (IOException x) {
            throw x;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        StandardJavaFileManager sfm = compiler.getStandardFileManager(diagnostics, null, null);
        URL apiUrl = Entity.class.getProtectionDomain().getCodeSource().getLocation();
        sfm.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(new File(apiUrl.getFile()), cpDir));
        sfm.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(srcOut));
        sfm.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(cpDir));

        TestFO entity = new TestFO("org.Sample",
                "package org; import javax.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;}}");
        TestFO nonEntity = new TestFO("org.NotE",
                "package org; import javax.persistence.Entity; public class NotE extends some.IF { public  NotE() {} @custom.Ann public external.Cls getW() {return new Object();}}");
        TestFO generated8 = new TestFO("org.Gen8",
                "package org; import javax.annotation.Generated; @Generated(\"com.example.Generator\") public class Gen8 { public  Gen8() {} public int getY() {return 42;}}");
        TestFO generated9 = new TestFO("org.Gen9",
                "package org; @javax.annotation.processing.Generated(\"com.example.Generator\") public class Gen9 { public  Gen9() {} public int getZ() {return 9*42;}}");
        CompilationTask task = compiler.getTask(new PrintWriter(System.out), sfm, diagnostics,
                Arrays.asList("-proc:only"), null,
                Arrays.asList(entity, nonEntity, generated8, generated9));
        CanonicalModelProcessor modelProcessor = new CanonicalModelProcessor();
        task.setProcessors(Collections.singleton(modelProcessor));
        task.call();
        
        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic);
        }
        Assert.assertTrue("Model file not generated", new File(srcOut, "org/Sample_.java").exists());
    }

    private static class TestFO extends SimpleJavaFileObject {
        private final String text;

        public TestFO(String name, String code) {
            super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.text = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return text;
        }
    }

    private static final String PXML = "<persistence xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\"\n" + 
            "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
            "  xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/persistence\n" + 
            "    http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd\"\n" + 
            "  version=\"2.2\">\n" + 
            "     <persistence-unit name=\"sample-pu\" transaction-type=\"RESOURCE_LOCAL\">\n" + 
            "          <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>\n" + 
            "          <exclude-unlisted-classes>false</exclude-unlisted-classes>\n" + 
            "          <properties>\n" + 
            "          </properties>\n" + 
            "     </persistence-unit>\n" + 
            "</persistence>";
    
    private static void delete(File dir) throws IOException {
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
