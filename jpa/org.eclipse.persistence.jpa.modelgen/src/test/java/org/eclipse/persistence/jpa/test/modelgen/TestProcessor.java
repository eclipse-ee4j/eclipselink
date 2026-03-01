/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     02/17/2018-2.7.2 Lukas Jungmann
//       - 531305: Canonical model generator fails to run on JDK9
package org.eclipse.persistence.jpa.test.modelgen;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
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

public class TestProcessor {

    public TestProcessor() {}

    @BeforeClass
    public static void prepare() throws IOException {
        File testRoot = new File(System.getProperty("run.dir"));
        if (testRoot.exists() && testRoot.isDirectory()) {
            File[] files = testRoot.listFiles();
            if (files != null) {
                for (File testDir : files) {
                    delete(testDir);
                }
            }
        }
    }

    @Test
    public void testProc() throws Exception {
        testProc("testProc3030", PXML30, OXML30);
        testProc("testProc3031", PXML30, OXML31);
    }

    @Test
    public void testGenerateComment() throws Exception {
        testGenerateComment("testGenerateComment3030", PXML30, OXML30);
        testGenerateComment("testGenerateComment3031", PXML30, OXML31);
    }

    @Test
    public void testGenerate() throws Exception {
        testGenerate("testGenerate3030", PXML30, OXML30);
        testGenerate("testGenerate3031", PXML30, OXML31);
    }

    @Test
    public void testTypeUse() throws Exception {
        testTypeUse("testTypeUse3030", PXML30, OXML30);
        testTypeUse("testTypeUse3031", PXML30, OXML31);
    }

    @Test
    public void testAnnotationProcessing() throws Exception {
        String output = testEntity("org.foo.MyEnt", ENTITY, PXML32, OXML32);
        Assert.assertFalse(output.contains("import org.foo"));
        Assert.assertTrue(output.contains("public static volatile EntityType<MyEnt> class_;"));

        Assert.assertTrue(output.contains("public static final String QUERY_FIND_ALL = \"findAll\";"));
        Assert.assertTrue(output.contains("public static final String QUERY_NATIVE_DELETE_ALL = \"native.deleteAll\";"));
        Assert.assertTrue(output.contains("public static final String GRAPH_MY_ENT = \"MyEnt\";"));
        Assert.assertTrue(output.contains("public static final String MAPPING_M_CUSTOM_RESULT = \"m.customResult\";"));

        Assert.assertTrue(output.contains("public static volatile TypedQueryReference<ResultClassType> _findById_;"));

        Assert.assertTrue(output.contains("public static volatile EntityGraph<MyEnt> _MyEnt;"));

        Assert.assertTrue(output.contains("public static final String CUSTOM_ATTRIBUTE = \"customAttribute\";"));
    }

    @Test
    public void testProcessorLoggingOffFromCmdLine() throws Exception {
        verifyLogging("testProcessorLoggingOffFromCmdLine", PXML30, false,
                "-Aeclipselink.logging.level.processor=OFF");
    }

    @Test
    public void testGlobalLoggingOffFromCmdLine() throws Exception {
        verifyLogging("testGlobalLoggingOffFromCmdLine", PXML30, false,
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
        verifyLogging("testProcessorLoggingFinestFromCmdLine", PXML30, true,
                "-Aeclipselink.logging.level.processor=FINEST");
    }

    @Test
    public void testGlobalLoggingFinestFromCmdLine() throws Exception {
        verifyLogging("testGlobalLoggingFinestFromCmdLine", PXML30, true,
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

    private void testProc(String name, String pxml, String oxml) throws Exception {
        TestFO entity = new TestFO("org.Sample",
                "package org; import jakarta.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;} interface A {}}");
        TestFO nonSC = new TestFO("some.IF",
                "package some; public class IF { public IF() {}}");
        TestFO nonAnn = new TestFO("custom.Ann",
                "package custom; public @interface Ann { }");
        TestFO nonExt = new TestFO("external.Cls",
                "package external; public class Cls { public Cls(){}}");
        TestFO nonEntity = new TestFO("org.NotE",
                "package org; import jakarta.persistence.Entity; public class NotE extends some.IF { public  NotE() {} @custom.Ann public external.Cls getW() {return new Object();}}");
        TestFO generated8 = new TestFO("org.Gen8",
                "package org; import jakarta.annotation.Generated; @Generated(\"com.example.Generator\") public class Gen8 { public  Gen8() {} public int getY() {return 42;}}");
        TestFO generated9 = new TestFO("org.Gen9",
                "package org; @javax.annotation.processing.Generated(\"com.example.Generator\") public class Gen9 { public  Gen9() {} public int getZ() {return 9*42;}}");

        Result result = runProject(name,
                getJavacOptions("-Aeclipselink.logging.level.processor=OFF"),
                Arrays.asList(entity, nonSC, nonAnn, nonExt, nonEntity, generated8, generated9), pxml, oxml);

        File outputFile = new File(result.srcOut, "org/Sample_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).anyMatch(s -> s.contains("@StaticMetamodel(Sample.class)")));
    }

    private void testGenerateComment(String name, String pxml, String oxml) throws Exception {
        TestFO entity = new TestFO("org.Sample",
                "package org; import jakarta.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;} interface A {}}");

        Result result = runProject(name,
                getJavacOptions("-A" + CanonicalModelProperties.CANONICAL_MODEL_GENERATE_COMMENTS + "=false",
                        "-Aeclipselink.logging.level.processor=OFF"),
                Arrays.asList(entity), pxml, oxml);

        File outputFile = new File(result.srcOut, "org/Sample_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).noneMatch(s -> s.contains("comments=")));
        Assert.assertTrue("Compilation failed", result.success);
    }

    private void testGenerate(String name, String pxml, String oxml) throws Exception {
        TestFO entity = new TestFO("org.Sample",
                "package org; import jakarta.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;} interface A {}}");

        Result result = runProject(name,
                getJavacOptions("-A" + CanonicalModelProperties.CANONICAL_MODEL_GENERATE_GENERATED + "=false",
                        "-Aeclipselink.logging.level.processor=OFF"),
                Arrays.asList(entity), pxml, oxml);

        File outputFile = new File(result.srcOut, "org/Sample_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).noneMatch(s -> s.contains("Generated")));
        Assert.assertTrue("Compilation failed", result.success);
    }

    private String testEntity(String name, String template, String pxml, String oxml) throws Exception {
        TestFO entity = new TestFO(name,
                template.replace("$PKG", name.substring(0, name.lastIndexOf('.')))
                        .replace("$NAME", name.substring(name.lastIndexOf('.') + 1)));
        Result result = runProject(name.replace('.', '_'),
                getJavacOptions("-A" + CanonicalModelProperties.CANONICAL_MODEL_GENERATE_GENERATED + "=false",
                        "-Aeclipselink.logging.level.processor=OFF"),
                Arrays.asList(entity), pxml, oxml);

        File outputFile = new File(result.srcOut, name.replace('.', '/') + "_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).noneMatch(s -> s.contains("Generated")));
        Assert.assertTrue("Compilation failed", result.success);
        return Files.readString(outputFile.toPath());
    }

    public void testTypeUse(String name, String pxml, String oxml) throws Exception {
        TestFO entity = new TestFO("org.Ent",
                "package org; @jakarta.persistence.Entity public class Ent { @org.ann.NotNull private byte[] bytes;}");
        TestFO ann = new TestFO("org.ann.NotNull",
                "package org.ann; @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE_USE) public @interface NotNull {}");

        Result result = runProject(name,
                getJavacOptions("-Aeclipselink.logging.level.processor=OFF"),
                Arrays.asList(entity, ann), pxml, oxml);

        File outputFile = new File(result.srcOut, "org/Ent_.java");
        Assert.assertTrue("Model file not generated", outputFile.exists());
        Assert.assertTrue(Files.lines(outputFile.toPath()).noneMatch(s -> s.contains("NotNull")));
        Assert.assertTrue("Compilation failed", result.success);
    }

    private List<String> getJavacOptions(String... opts) {
        List<String> result = new ArrayList<>();
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
     * @param haveMsgs there should be logging messages in the output or not
     * @param options compiler options
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
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager sfm = compiler.getStandardFileManager(diagnostics, null, null);
        URL apiUrl = Entity.class.getProtectionDomain().getCodeSource().getLocation();
        URL generatedUrl = Generated.class.getProtectionDomain().getCodeSource().getLocation();
        sfm.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(new File(apiUrl.getFile()), new File(generatedUrl.getFile()), cpDir));
        sfm.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(srcOut));
        sfm.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(cpDir));

        TestFO entity = new TestFO("org.Sample",
                "package org; import jakarta.persistence.Entity; @Entity public class Sample { public  Sample() {} public int getX() {return 1;}}");

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

    private Result runProject(String name, List<String> options, List<JavaFileObject> sources, String pxmlStr, String oxmlStr) throws Exception {
                File runDir = new File(System.getProperty("run.dir"), name);
        File srcOut = new File(runDir, "src");
        srcOut.mkdirs();
        File cpDir = new File(runDir, "cp");
        cpDir.mkdirs();
        File pxml = new File(cpDir, "META-INF/persistence.xml");
        pxml.getParentFile().mkdirs();
        try (BufferedWriter writer = Files.newBufferedWriter(pxml.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(pxmlStr, 0, pxmlStr.length());
        } catch (IOException x) {
            throw x;
        }
        File oxml = new File(cpDir, "META-INF/orm.xml");
        try (BufferedWriter writer = Files.newBufferedWriter(oxml.toPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(oxmlStr, 0, oxmlStr.length());
        } catch (IOException x) {
            throw x;
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        StandardJavaFileManager sfm = compiler.getStandardFileManager(diagnostics, null, null);
        URL apiUrl = Entity.class.getProtectionDomain().getCodeSource().getLocation();
        URL generatedUrl = Generated.class.getProtectionDomain().getCodeSource().getLocation();
        sfm.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(new File(apiUrl.getFile()), new File(generatedUrl.getFile()), cpDir));
        sfm.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singleton(srcOut));
        sfm.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(cpDir));

        CompilationTask task = compiler.getTask(new PrintWriter(System.out), sfm, diagnostics,
                options, null, sources);
        CanonicalModelProcessor modelProcessor = new CanonicalModelProcessor();
        task.setProcessors(Collections.singleton(modelProcessor));
        boolean result = task.call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic);
            String msg = diagnostic.getMessage(null);
            Assert.assertFalse(msg,
                    msg.contains("The following options were not recognized by any processor:"));
        }
        return new Result(srcOut, cpDir, result);
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

    private static class Result {
        File srcOut, binOut;
        boolean success;
        Result(File srcOut, File binOut, boolean success) {
            this.srcOut = srcOut;
            this.binOut = binOut;
            this.success = success;
        }
    }

    private static final String PXML30 = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
              version="3.0">
                 <persistence-unit name="sample-pu" transaction-type="RESOURCE_LOCAL">
                      <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                      <exclude-unlisted-classes>false</exclude-unlisted-classes>
                      <properties>
                      </properties>
                 </persistence-unit>
            </persistence>""";

    private static final String PXML32 = """
            <persistence xmlns="https://jakarta.ee/xml/ns/persistence"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                https://jakarta.ee/xml/ns/persistence/persistence_3_2.xsd"
              version="3.2">
                 <persistence-unit name="sample-pu" transaction-type="RESOURCE_LOCAL">
                      <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                      <exclude-unlisted-classes>false</exclude-unlisted-classes>
                      <properties>
                      </properties>
                 </persistence-unit>
            </persistence>""";

    private static final String OXML30 = """
            <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm https://jakarta.ee/xml/ns/persistence/orm/orm_3_0.xsd"
                         version="3.0"></entity-mappings>""";

    private static final String OXML31 = """
            <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm https://jakarta.ee/xml/ns/persistence/orm/orm_3_1.xsd"
                         version="3.1"></entity-mappings>""";

    private static final String OXML32 = """
            <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm https://jakarta.ee/xml/ns/persistence/orm/orm_3_2.xsd"
                         version="3.2"></entity-mappings>""";

    private static final String PXML_LOG_BEG =
            """
                    <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                        http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
                      version="2.2">
                         <persistence-unit name=\"""";

    private static final String PXML_LOG_MID =
            """
                    " transaction-type="RESOURCE_LOCAL">
                              <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
                              <exclude-unlisted-classes>false</exclude-unlisted-classes>
                              <properties>
                    """;

    private static final String PXML_LOG_END =
            """
                              </properties>
                         </persistence-unit>
                    </persistence>""";

    private static final String ENTITY =
            """
                package $PKG;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Id;
                import jakarta.persistence.NamedEntityGraph;
                import jakarta.persistence.NamedNativeQuery;
                import jakarta.persistence.NamedQuery;
                import jakarta.persistence.SqlResultSetMapping;
                @Entity
                @NamedQuery(name = "findAll", query="select xy from $NAME xy")
                @NamedQuery(name = "findById", query="select xy from $NAME xy WHERE xy.id = :id", resultClass = ResultClassType.class)
                @NamedNativeQuery(name = "native.deleteAll", query = "DELETE FROM $NAME")
                @NamedNativeQuery(name = "native.deleteById", query = "DELETE FROM $NAME WHERE id = ?1", resultClass = ResultClassType.class)
                @NamedEntityGraph
                @SqlResultSetMapping(name = "m.customResult")
                public class $NAME {
                    @Id
                    public int id;
                    public int customAttribute;
                    public $NAME() {}
                    public int getCustomAttribute() { return customAttribute; }
                    public int setCustomAttribute(int customAttribute) { this.customAttribute = customAttribute; }
                    interface A {}
                };
                class ResultClassType {}
                """;

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
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<>() {
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
