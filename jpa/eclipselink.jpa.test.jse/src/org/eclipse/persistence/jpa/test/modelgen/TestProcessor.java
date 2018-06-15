/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/17/2018-2.7.2 Lukas Jungmann
//       - 531305: Canonical model generator fails to run on JDK9
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                "package org; import javax.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;} interface A {}}");
        TestFO nonEntity = new TestFO("org.NotE",
                "package org; import javax.persistence.Entity; public class NotE extends some.IF { public  NotE() {} @custom.Ann public external.Cls getW() {return new Object();}}");
        TestFO generated8 = new TestFO("org.Gen8",
                "package org; import javax.annotation.Generated; @Generated(\"com.example.Generator\") public class Gen8 { public  Gen8() {} public int getY() {return 42;}}");
        TestFO generated9 = new TestFO("org.Gen9",
                "package org; @javax.annotation.processing.Generated(\"com.example.Generator\") public class Gen9 { public  Gen9() {} public int getZ() {return 9*42;}}");
        CompilationTask task = compiler.getTask(new PrintWriter(System.out), sfm, diagnostics,
                getJavacOptions("-Aeclipselink.logging.level.processor=OFF"), null,
                Arrays.asList(entity, nonEntity, generated8, generated9));
        CanonicalModelProcessor modelProcessor = new CanonicalModelProcessor();
        task.setProcessors(Collections.singleton(modelProcessor));
        task.call();

        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic);
        }
        File outputFile = new File(srcOut, "org/Sample_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).anyMatch(s -> s.contains("@StaticMetamodel(Sample.class)")));
    }

    @Test
    public void testProcessorLoggingOffFromCmdLine() throws Exception {
        verifyLogging("testProcessorLoggingOffFromCmdLine", PXML, false,
                "-Aeclipselink.logging.level.processor=OFF");
    }

    @Test
    public void testGlobalLoggingOffFromCmdLine() throws Exception {
        verifyLogging("testGlobalLoggingOffFromCmdLine", PXML, false,
                "-Aeclipselink.logging.level=OFF");
    }

    @Test
    public void testProcessorLoggingOffFromPU() throws Exception {
        final String pu = buildPU("testProcessorLoggingOffFromPU",
                new Property("eclipselink.logging.level.processor", "OFF"));
        // Turning logging off from PU can't remove messages logged before PU properties are processed.
        verifyLogging("testProcessorLoggingOffFromPU", pu, true);
    }

    @Test
    public void testGlobalLoggingOffFromPU() throws Exception {
        final String pu = buildPU("testGlobalLoggingOffFromPU",
                new Property("eclipselink.logging.level", "OFF"));
        // Turning logging off from PU can't remove messages logged before PU properties are processed.
        verifyLogging("testGlobalLoggingOffFromPU", pu, true);
    }

    @Test
    public void testProcessorLoggingFinestFromCmdLine() throws Exception {
        verifyLogging("testProcessorLoggingFinestFromCmdLine", PXML, true,
                "-Aeclipselink.logging.level.processor=FINEST");
    }

    @Test
    public void testGlobalLoggingFinestFromCmdLine() throws Exception {
        verifyLogging("testGlobalLoggingFinestFromCmdLine", PXML, true,
                "-Aeclipselink.logging.level=FINEST");
    }

    @Test
    public void testProcessorLoggingFinestFromPU() throws Exception {
        final String pu = buildPU("testProcessorLoggingFinestFromPU",
                new Property("eclipselink.logging.level.processor", "FINEST"));
        verifyLogging("testProcessorLoggingFinestFromPU", pu, true);
    }

    @Test
    public void testGlobalLoggingFinestFromPU() throws Exception {
        final String pu = buildPU("testGlobalLoggingFinestFromPU",
                new Property("eclipselink.logging.level", "FINEST"));
        verifyLogging("testGlobalLoggingFinestFromPU", pu, true);
    }

    private List<String> getJavacOptions(String... opts) {
        List<String> result = new ArrayList<String>();
        String systemOpts = System.getProperty("test.junit.jvm.modules");
        if (systemOpts != null && systemOpts.contains("--add-modules")) {
            result.addAll(Arrays.asList(System.getProperty("test.junit.jvm.modules").split(" ")));
        }
        result.add("-proc:only");
        result.add("-Aeclipselink.canonicalmodel.use_static_factory=false");
        result.addAll(Arrays.asList(opts));
        System.out.println("OPTIONS: " + result);
        return result;
    }
    /**
     * Verify logging output suppression
     * @param testName name of the test
     * @param pu persistence unit {@code String}
     * @param whether there should be logging messages in the output or not
     * @param options compiler options
     * @throws Exception
     */
    private void verifyLogging(final String testName, final String pu, final boolean haveMsgs, final String... options) throws Exception {
        File runDir = new File(System.getProperty("run.dir"), testName);
        File srcOut = new File(runDir, "src");
        srcOut.mkdirs();
        File cpDir = new File(runDir, "cp");
        cpDir.mkdirs();
        File pxml = new File(cpDir, "META-INF/persistence.xml");
        pxml.getParentFile().mkdirs();
        try (BufferedWriter writer = Files.newBufferedWriter(pxml.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(pu, 0, pu.length());
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

        CompilationTask task = compiler.getTask(
                new PrintWriter(System.out), sfm, diagnostics, getJavacOptions(options), null,
                Arrays.asList(entity));
        CanonicalModelProcessor modelProcessor = new CanonicalModelProcessor();
        task.setProcessors(Collections.singleton(modelProcessor));
        task.call();

        for ( Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic);
        }
        if (haveMsgs) {
            Assert.assertFalse("Log messages should be generated", diagnostics.getDiagnostics().isEmpty());
        } else {
            Assert.assertTrue("No log message should be generated", diagnostics.getDiagnostics().isEmpty());
        }
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

    private static final String PXML_LOG_BEG =
            "<persistence xmlns=\"http://xmlns.jcp.org/xml/ns/persistence\"\n" +
            "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/persistence\n" +
            "    http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd\"\n" +
            "  version=\"2.2\">\n" +
            "     <persistence-unit name=\"";

    private static final String PXML_LOG_MID =
            "\" transaction-type=\"RESOURCE_LOCAL\">\n" +
            "          <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>\n" +
            "          <exclude-unlisted-classes>false</exclude-unlisted-classes>\n" +
            "          <properties>\n";

    private static final String PXML_LOG_END =
            "          </properties>\n" +
            "     </persistence-unit>\n" +
            "</persistence>";

    /**
     * Simple property holding class.
     */
    private static final class Property {
        private final String name;
        private final String value;
        private Property(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Builds persistence unit with properties.
     * @param name persistence unit name
     * @param properties properties to be added to persistence unit
     * @return persistence unit with specified properties
     */
    private static String buildPU(final String name, Property ... properties) {
        int len = PXML_LOG_BEG.length() + PXML_LOG_MID.length() + PXML_LOG_END.length() + name.length();
        for (Property property : properties) {
            len += property.name.length() + property.value.length() + 43;
        }
        final StringBuilder sb = new StringBuilder(len);
        sb.append(PXML_LOG_BEG);
        sb.append(name);
        sb.append(PXML_LOG_MID);
        for (Property property : properties) {
            sb.append("              <property name=\"");
            sb.append(property.name);
            sb.append("\" value=\"");
            sb.append(property.value);
            sb.append("\"/>\n");
        }
        sb.append(PXML_LOG_END);
        return sb.toString();
    }

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
