/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import com.sun.tools.xjc.Driver;

import org.eclipse.persistence.jaxb.compiler.Generator;

import javax.tools.Diagnostic;
import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.eclipse.persistence.testing.jaxb.beanvalidation.ContentComparator.equalsXML;

/**
 * Tests generation of JAXB Facets and Bean Validation annotations during Schemagen and XJC, respectively.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class BeanValidationBindingsTestCase extends junit.framework.TestCase {

    private static final String GOLDEN_FILE_RESOURCE_PATH = "org/eclipse/persistence/testing/jaxb/beanvalidation/sgen_xjc/golden_file.xsd";
    private static String PATH_TO_SCHEMA_DIRECTORY;
    private static String GOLDEN_FILE_PATH;
    private static String RICH_SCHEMA_PATH;
    private static String CUSTOMIZED_SCHEMA_PATH;
    private static String GENERATED_SCHEMA_PATH;
    private static String TARGET_PATH;
    private static final int DIRS_TO_ROOT = GOLDEN_FILE_RESOURCE_PATH.length() - GOLDEN_FILE_RESOURCE_PATH.replace("/", "").length();

    public BeanValidationBindingsTestCase() {
        try {
            //context class loader and already exists file path is needed to run this test case from within IDE like Eclipse
            PATH_TO_SCHEMA_DIRECTORY = new File(Thread.currentThread().getContextClassLoader().getResource(GOLDEN_FILE_RESOURCE_PATH).toURI()).getParentFile().getAbsolutePath();
            GOLDEN_FILE_PATH = PATH_TO_SCHEMA_DIRECTORY + "/golden_file.xsd";
            RICH_SCHEMA_PATH = PATH_TO_SCHEMA_DIRECTORY + "/rich_schema.xsd";
            CUSTOMIZED_SCHEMA_PATH = PATH_TO_SCHEMA_DIRECTORY + "/customized_schema.xsd";
            GENERATED_SCHEMA_PATH = PATH_TO_SCHEMA_DIRECTORY + "/schema1.xsd";

            File alreadyExistsFile = new File(Thread.currentThread().getContextClassLoader().getResource(GOLDEN_FILE_RESOURCE_PATH).toURI());
            for (int i = 0; i < DIRS_TO_ROOT + 1; i++) {
                alreadyExistsFile = alreadyExistsFile.getParentFile();
            }

            TARGET_PATH = alreadyExistsFile.getAbsolutePath();
        } catch (URISyntaxException e) {
            fail(String.format("Unable to initialize %s test case", BeanValidationBindingsTestCase.class.getName()));
        }
    }

    // Handles error case where no BV annotations would be generated on class fields and would still pass the equality test.
    boolean annotationsGenerated;

    private String pkg;

    @Override
    public void tearDown() throws Exception {
        assertTrue(deleteDir(new File(pkg)));
        //noinspection ResultOfMethodCallIgnored
        new File(GENERATED_SCHEMA_PATH).delete();
    }

    /**
     * A fancy test demonstrating (and testing) that it is possible to perform
     * a round-trip in a lucky case where the XML Schema is identical to the
     * original golden file.
     * In practice, the XJC/Schemagen transformations guarantee only equality
     * of post round-trip schema with the original, not identity.
     * This test should still pass even if changes are done to our code.
     */
    public void testGoldenFileIdentity() throws Exception {
        pkg = "gf";

        roundTrip(GOLDEN_FILE_PATH, pkg);
        assertTrue(equalsXML(new File(GOLDEN_FILE_PATH), new File(GENERATED_SCHEMA_PATH)));
    }

    /**
     * Tests equality of the original schema and schema after round-trip, with
     * bean validation annotations and facets.
     * Also tests equality of generated Java classes from the original schema
     * and from the schema after round-trip.
     */
    public void testEqualitySchemaAndJava() throws Exception {
        pkg = "rs";

        Class<?>[] cTenured = roundTrip(RICH_SCHEMA_PATH, pkg);

        xjcGenerateJavaSources(GENERATED_SCHEMA_PATH); // Regenerate the sources.
        compileGeneratedSources(createCompileList(pkg));
        Class<?>[] cYoung = loadCompiledClasses(createLoadList(pkg));

        assertTrue(equalsClasses(cTenured, cYoung));
    }

    /**
     * Tests customizations, i.e. facet customizations + custom facets, i.e.
     * Future, Past, AssertTrue, AssertFalse.
     */
    public void testFacetCustomizationsAndCustomFacets() throws Exception {
        pkg = "cs";

        xjcGenerateJavaSourcesWithCustomizations(CUSTOMIZED_SCHEMA_PATH);
        compileGeneratedSources(createCompileList(pkg));
        Class<?> custom = loadCompiledClasses(createLoadList(pkg))[0];

        Field generic = custom.getDeclaredField("generic");
        Size s = generic.getAnnotation(Size.class);
        assertEquals(s.message(), "Hello, world!");
        assertEquals(s.groups()[0], BindingTeam.class);
        assertEquals(s.groups()[1], RocketTeam.class);
        assertEquals(s.max(), 4);

        Pattern p1 = generic.getAnnotation(Pattern.List.class).value()[0];
        Pattern p2 = generic.getAnnotation(Pattern.List.class).value()[1];
        assertEquals(p1.message(), p2.message());
        assertTrue(Arrays.equals(p1.groups(), p2.groups()));
        assertEquals(p1.message(), "Hello.");
        assertEquals(p1.groups()[0], Object.class);
        assertEquals(p1.regexp(), "10");

        Future f = generic.getAnnotation(Future.class);
        assertEquals(f.message(), "Welcome to the Future!");
        assertEquals(f.groups()[0], BindingTeam.class);

        Past p = generic.getAnnotation(Past.class);
        assertEquals(p.message(), "Farewell from the ancestors.");
        assertEquals(p.groups()[0], Ancestors.class);

        AssertTrue at = generic.getAnnotation(AssertTrue.class);
        assertEquals(at.message(), "True fan of the team!");
        assertEquals(at.groups()[0], BindingTeam.class);

        AssertFalse af = generic.getAnnotation(AssertFalse.class);
        assertEquals(af.message(), "false");
        assertEquals(af.groups()[0], Object.class);

        // To test a user custom annotation.
        CustomAnnotation xmlKey = generic.getAnnotation(CustomAnnotation.class);
        assertNotNull(xmlKey);
    }

    /**
     * Tests that the XJC detects all facets and generates their respective
     * annotations correctly.
     */
    public void testAllFacetsAndAnnotations() throws Exception {
        pkg = "rs";

        Class<?>[] c = roundTrip(RICH_SCHEMA_PATH, pkg);
        Class<?> Main = c[0];
        Class<?> Numbers = c[1];
        Class<?> NumberWithHiddenValueAttribute = c[2];
        Class<?> Strings = c[4];

        XmlElement xmlElement;
        Size size;
        DecimalMax decimalMax;
        DecimalMin decimalMin;
        Pattern.List patternList;
        Pattern pattern;

        /* Main.class */
        Field numbers = Main.getDeclaredField("numbers");
        assertNotNull(numbers.getAnnotation(Valid.class));
        assertNotNull(numbers.getAnnotation(NotNull.class));
        xmlElement = numbers.getAnnotation(XmlElement.class);
        assertFalse(xmlElement.nillable());

        Field strings = Main.getDeclaredField("strings");
        size = strings.getAnnotation(Size.class);
        assertTrue(size.min() == 1 && size.max() == 2);
        assertNotNull(strings.getAnnotation(Valid.class));
        assertNotNull(strings.getAnnotation(NotNull.class));
        xmlElement = strings.getAnnotation(XmlElement.class);
        assertFalse(xmlElement.nillable());

        Field unsignedByte = Main.getDeclaredField("unsignedByte");
        decimalMax = unsignedByte.getAnnotation(DecimalMax.class);
        assertEquals(decimalMax.value(), "255");
        assertTrue(decimalMax.inclusive());
        decimalMin = unsignedByte.getAnnotation(DecimalMin.class);
        assertEquals(decimalMin.value(), "0");
        assertTrue(decimalMin.inclusive());

        Field byteArray = Main.getDeclaredField("byteArray");
        size = byteArray.getAnnotation(Size.class);
        assertTrue(size.max() == 18);

        Field someCollection = Main.getDeclaredField("someCollection");
        size = someCollection.getAnnotation(Size.class);
        assertTrue(size.min() == 1);
        assertNotNull(someCollection.getAnnotation(Valid.class));
        assertNotNull(someCollection.getAnnotation(NotNull.class));

        Field optionalElement = Main.getDeclaredField("optionalElement");
        size = optionalElement.getAnnotation(Size.class);
        assertTrue(size.min() == 0);
        assertNotNull(optionalElement.getAnnotation(Valid.class));
        assertNull(optionalElement.getAnnotation(NotNull.class));

        /* Numbers.class */
        Field minInclusive = Numbers.getDeclaredField("minInclusive");
        decimalMin = minInclusive.getAnnotation(DecimalMin.class);
        assertEquals(decimalMin.value(), "1000");
        assertTrue(decimalMin.inclusive());

        Field maxInclusive = Numbers.getDeclaredField("maxInclusive");
        decimalMax = maxInclusive.getAnnotation(DecimalMax.class);
        assertEquals(decimalMax.value(), "1000");
        assertTrue(decimalMax.inclusive());

        Field minExclusive = Numbers.getDeclaredField("minExclusive");
        decimalMin = minExclusive.getAnnotation(DecimalMin.class);
        assertEquals(decimalMin.value(), "0");
        assertFalse(decimalMin.inclusive());

        Field maxExclusive = Numbers.getDeclaredField("maxExclusive");
        decimalMax = maxExclusive.getAnnotation(DecimalMax.class);
        assertEquals(decimalMax.value(), "1000");
        assertFalse(decimalMax.inclusive());

        Field minMaxExclusive = Numbers.getDeclaredField("minMaxExclusive");
        decimalMax = minMaxExclusive.getAnnotation(DecimalMax.class);
        assertEquals(decimalMax.value(), "9223372");
        assertFalse(decimalMax.inclusive());
        decimalMin = minMaxExclusive.getAnnotation(DecimalMin.class);
        assertEquals(decimalMin.value(), "0");
        assertFalse(decimalMin.inclusive());

        /* NumberWithHiddenValueAttribute.class */
        Field value = NumberWithHiddenValueAttribute.getDeclaredField("value");
        assertNotNull(value.getAnnotation(XmlValue.class));
        size = value.getAnnotation(Size.class);
        assertTrue(size.min() == 1 && size.max() == 5);

        Field code = NumberWithHiddenValueAttribute.getDeclaredField("code");
        assertNotNull(code.getAnnotation(NotNull.class));

        Field whatNumber = NumberWithHiddenValueAttribute.getDeclaredField("whatNumber");
        XmlAttribute xmlAttribute = whatNumber.getAnnotation(XmlAttribute.class);
        assertTrue(xmlAttribute.required());

        /* Strings.class */
        Field regexShorthands = Strings.getDeclaredField("regexShorthands");
        patternList = regexShorthands.getAnnotation(Pattern.List.class);
        Pattern[] patterns = patternList.value();
        assertEquals("[:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]", patterns[0].regexp());
        assertEquals("[^:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]", patterns[1].regexp());
        assertEquals("[-.0-9:A-Z_a-z\\u00B7\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u203F\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]", patterns[2].regexp());
        assertEquals("[^-.0-9:A-Z_a-z\\u00B7\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u203F\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]", patterns[3].regexp());
        assertEquals("\\p{Nd}", patterns[4].regexp());
        assertEquals("\\P{Nd}", patterns[5].regexp());
        assertEquals("[\\u0009-\\u000D\\u0020\\u0085\\u00A0\\u1680\\u180E\\u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000]", patterns[6].regexp());
        assertEquals("[^\\u0009-\\u000D\\u0020\\u0085\\u00A0\\u1680\\u180E\\u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000]", patterns[7].regexp());
        assertEquals("[\\u0009\\u0020\\u00A0\\u1680\\u180E\\u2000-\\u200A\\u202F\\u205F\\u3000]", patterns[8].regexp());
        assertEquals("[^\\u0009\\u0020\\u00A0\\u1680\\u180E\\u2000\\u2001-\\u200A\\u202F\\u205F\\u3000]", patterns[9].regexp());
        assertEquals("[^\\u000A-\\u000D\\u0085\\u2028\\u2029]", patterns[10].regexp());
        assertEquals("(?:(?>\\u000D\\u000A)|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029])", patterns[11].regexp());

        Field idType = Strings.getDeclaredField("idType");
        pattern = idType.getAnnotation(Pattern.class);
        assertEquals(pattern.regexp(), "[[:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]-[:]][[-.0-9:A-Z_a-z\\u00B7\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u203F\\u2040\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD]-[:]]*");
        assertTrue(idType.getAnnotation(Size.class).max() == 100);

        Field genericString = Strings.getDeclaredField("genericString");
        assertTrue(genericString.getAnnotation(Size.class).min() == 0);
        assertTrue(genericString.getAnnotation(Size.class).max() == 1024);

        Field maxLength = Strings.getDeclaredField("maxLength");
        assertTrue(maxLength.getAnnotation(Size.class).max() == 1024);

        Field minLength = Strings.getDeclaredField("minLength");
        assertTrue(minLength.getAnnotation(Size.class).min() == 0);
    }

    private Class<?>[] roundTrip(String schemaPath, String pkg) throws Exception {
        xjcGenerateJavaSources(schemaPath);
        compileGeneratedSources(createCompileList(pkg));
        Class<?>[] classes = loadCompiledClasses(createLoadList(pkg));
        generateSchema(classes); // Generates resource/PATH_TO_SCHEMA_DIRECTORY/schema1.xsd.
        return classes;
    }

    public void xjcGenerateJavaSources(String schemaPath) throws Exception {
        Driver.run(new String[] { schemaPath, "-XBeanVal" }, System.out, System.out);
    }

    public void xjcGenerateJavaSourcesWithCustomizations(String schemaPath) throws Exception {
        Driver.run(new String[] { schemaPath, "-extension", "-XBeanVal" }, System.out, System.out);
    }

    private void generateSchema(Class<?>[] classes) throws FileNotFoundException {
        JavaModelImpl javaModel = new JavaModelImpl(Thread.currentThread().getContextClassLoader());
        JavaModelInputImpl modelInput = new JavaModelInputImpl(classes, javaModel);
        modelInput.setFacets(true);
        Generator gen = new Generator(modelInput);
        gen.generateSchemaFiles(PATH_TO_SCHEMA_DIRECTORY, null);
    }

    private void compileGeneratedSources(File... compileList) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<? super JavaFileObject> diag = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fm = compiler.getStandardFileManager(diag, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fm.getJavaFileObjectsFromFiles(Arrays.asList(compileList));
        Iterable<String> options = Arrays.asList("-d", TARGET_PATH);
        JavaCompiler.CompilationTask task = compiler.getTask(new OutputStreamWriter(System.out), fm, diag, options,
                null, compilationUnits);

        if (!task.call()) {
            for (Diagnostic diagnostic : diag.getDiagnostics())
                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            fail("Compilation of generated classes failed. See the diagnostics output.");
        }
    }

    private File[] createCompileList(String pkg) {
        return "cs".equals(pkg)
                ? new File[]{new File(pkg + "/Custom.java"),
                    new File(pkg + "/ObjectFactory.java"),
                    new File(pkg + "/package-info.java")}
                : new File[]{new File(pkg + "/Main.java"),
                    new File(pkg + "/Numbers.java"),
                    new File(pkg + "/NumberWithHiddenValueAttribute.java"),
                    new File(pkg + "/ObjectFactory.java"),
                    new File(pkg + "/package-info.java"),
                    new File(pkg + "/Strings.java")};
    }

    private Class<?>[] loadCompiledClasses(String... loadList) throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class<?>[] loadedClasses = new Class[loadList.length];
        for (int i = 0; i < loadedClasses.length; i++)
            loadedClasses[i] = cl.loadClass(loadList[i]);
        return loadedClasses;
    }

    private String[] createLoadList(String pkg) throws ClassNotFoundException {
        return "cs".equals(pkg)
                ? new String[]{pkg + ".Custom", pkg + ".ObjectFactory" }
                : new String[]{pkg + ".Main", pkg + ".Numbers",
                pkg + ".NumberWithHiddenValueAttribute", pkg + ".ObjectFactory",
                pkg + ".Strings"};
    }

    private boolean equalsClasses(Class<?>[] cTenured, Class<?>[] cYoung) {
        if (cTenured.length != cYoung.length) return false;
        for (int i = 0; i < cTenured.length; i++)
            if (!equalsAnnotations(cTenured[i], cYoung[i])) return false;
        return true;
    }

    private boolean equalsAnnotations(Class<?> c1, Class<?> c2) {
        Field[] f1 = c1.getDeclaredFields();
        Field[] f2 = c2.getDeclaredFields();
        if (f1.length != f2.length) return false;
        for (int i = 0; i<f2.length; i++) {
            Annotation[] a1 = f1[i].getDeclaredAnnotations();
            Annotation[] a2 = f2[i].getDeclaredAnnotations();
            if (!Arrays.equals(a1, a2)) return false;
            // Returns false if no BV annotations were generated.
            if (!annotationsGenerated)
                for (Annotation a : a1)
                    if (a.annotationType().equals(NotNull.class)) {
                        annotationsGenerated = true;
                        break;
                    }
        }
        return annotationsGenerated;
    }

    private static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String child : children)
                deleteDir(new File(file, child));
            return file.delete();
        } else
        // The directory is now empty so delete it
        return file.delete();
    }

    public interface BindingTeam{}
    public @interface RocketTeam{}
    public abstract class Ancestors{}
    @Target({ ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CustomAnnotation {
        String value();
    }

}
