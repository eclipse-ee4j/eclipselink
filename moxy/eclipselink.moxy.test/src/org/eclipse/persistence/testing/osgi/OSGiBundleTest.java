/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

/**
 * Tests that all MOXy exported bundles can be properly loaded by OSGi framework.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 * @since 2.7.0
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OSGiBundleTest {
    // MOXy bundle symbolic name
    private static final String MOXY_BUNDLE_NAME = "org.eclipse.persistence.moxy";

    // MOXy bundle
    private Bundle moxyBundle;

    @Inject
    private static BundleContext ctx;

    @Configuration
    public static Option[] config() {
        return OSGITestHelper.getDefaultOptions();
    }

    @Test
    public void testAsmVersion() {
        Class<?> c = loadClass("org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.asm", "6.1.1.v201804051226");
    }

    @Test
    public void testInternalJaxb() {
        Class<?> c = loadClass("org.eclipse.persistence.internal.jaxb.AttributeNodeImpl");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testInternalJaxbMany() {
        Class<?> c = loadClass("org.eclipse.persistence.internal.jaxb.many.ArrayValue");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxb() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.JAXBContext");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbAttachment() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.attachment.AttachmentMarshallerAdapter");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbCompiler() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbDynamic() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbDynamicMetadata() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.dynamic.metadata.Metadata");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbJavamodel() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.AnnotationProxy");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbJavamodelOxm() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaClassImpl");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbJavamodelReflection() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.reflection.AnnotationHelper");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbMetadata() throws ClassNotFoundException {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.metadata.MetadataSource");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbRs() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.rs.MOXyJsonProvider");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJaxbXmlmodel() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute");
        assertClassLoadedByBundle(c, MOXY_BUNDLE_NAME);
    }

    @Test
    public void testJavaxXmlParsers() {
        Class<?> c = loadClass("javax.xml.parsers.ParserConfigurationException");
        assertClassLoadedBySystemBundle(c);
    }

    @Test
    public void testJavaxNaming() {
        Class<?> c = loadClass("javax.naming.InitialContext");
        assertClassLoadedBySystemBundle(c);
    }

    @Test
    public void testOrgXmlSaxHelpers() {
        Class<?> c = loadClass("org.xml.sax.helpers.DefaultHandler");
        assertClassLoadedBySystemBundle(c);
    }

    /**
     * Loads a class from MOXy bundle. Fails the test if not loaded.
     */
    private Class<?> loadClass(String className) {
        try {
            return getMoxyBundle().loadClass(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OSGiBundleTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Assert.fail("Cannot find and load class: " + className);
        }
        return null;
    }

    /**
     * Returns MOXy bundle ('org.eclipse.persistence.moxy').
     */
    private Bundle getMoxyBundle() {
        if (this.moxyBundle != null) {
            return this.moxyBundle;
        }

        for (Bundle b : ctx.getBundles()) {
            if (b.getSymbolicName().equals(MOXY_BUNDLE_NAME)) {
                this.moxyBundle = b;
                return b;
            }
        }

        return null;
    }

    private void assertClassLoadedBySystemBundle(Class<?> c) {
        Assert.assertNotNull(c);

        // Class is there but loaded from the system bundle
        Assert.assertNull("Class " + c.getName() + " was not loaded from JDK", FrameworkUtil.getBundle(c));
    }

    private void assertClassLoadedByBundle(Class<?> c, String bundle) {
        Assert.assertNotNull(c);
        Bundle b = FrameworkUtil.getBundle(c);
        Assert.assertEquals("Class '" + c.getName() + "' was loaded by '" + b.getSymbolicName() +
                        "', expected was '" + bundle + "'", bundle, b.getSymbolicName());
        Assert.assertEquals("Bundle '" + bundle + "' is not running", Bundle.ACTIVE, b.getState());
    }

    private void assertClassLoadedByBundle(Class<?> c, String bundle, String version) {
        Bundle b = FrameworkUtil.getBundle(c);
        Version v = b.getVersion();
        Assert.assertEquals("Class '" + c.getName() + "' was loaded by '" + b.getSymbolicName() +
                        "', expected was '" + bundle + "'", bundle, b.getSymbolicName());
        Assert.assertEquals("Class '" + c.getName() + "' was loaded by '"  + b.getSymbolicName() + "', version '"
                        + v.toString() + "' expected was '" + bundle + "', version '" + v.toString() + "'.", version, v.toString());
        Assert.assertEquals("Bundle '" + bundle + "' is not running", Bundle.ACTIVE, b.getState());
    }
}
