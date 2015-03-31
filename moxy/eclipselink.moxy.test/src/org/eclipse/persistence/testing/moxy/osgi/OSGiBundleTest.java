/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.moxy.osgi;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.localRepository;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.repositories;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Tests that all MOXy exported bundles can be properly loaded by OSGi framework.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OSGiBundleTest {

    private static final String PLUGINS_DIR = System.getProperty("moxytest.2.common.plugins.dir");
    private static final String QUALIFIER = System.getProperty("build.qualifier");

    @Inject
    private static BundleContext ctx;

    @Configuration
    public static Option[] config() {
        return options(
                repositories("http://repo1.maven.org/maven2",
                        //                "http://repository.apache.org/content/groups/snapshots-group",
                        //                "https://maven.java.net/content/groups/staging",
                        //                "http://repository.ops4j.org/maven2",
                        //                "http://svn.apache.org/repos/asf/servicemix/m2-repo",
                        //                "http://repository.springsource.com/maven/bundles/release",
                        //                "http://repository.springsource.com/maven/bundles/external",
                        "http://maven.java.net/content/repositories/snapshots/"),
                localRepository(getLocalRepository()),
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").version("4.3.0"),
                //JAXB API
                bundle("file:" + PLUGINS_DIR + "javax.xml.bind_2.2.12.v201410011542.jar"),
                //WS API
                bundle("file:" + PLUGINS_DIR + "javax.ws.rs_1.1.1.v20101004-1200.jar"),
                //EclipseLink bundles
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.moxy_2.7.0." + QUALIFIER + ".jar"),
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.core_2.7.0." + QUALIFIER + ".jar"),

                junitBundles(),
                felix());
    }

    @Test
    public void testInternalJaxb() {
        Class<?> c = loadClass("org.eclipse.persistence.internal.jaxb.AttributeNodeImpl");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testInternalJaxbMany() {
        Class<?> c = loadClass("org.eclipse.persistence.internal.jaxb.many.ArrayValue");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxb() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.JAXBContext");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbAttachment() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.attachment.AttachmentMarshallerAdapter");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbCompiler() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbDynamic() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbDynamicMetadata() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.dynamic.metadata.Metadata");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbJavamodel() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.AnnotationProxy");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbJavamodelOxm() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaClassImpl");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbJavamodelReflection() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.javamodel.reflection.AnnotationHelper");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbMetadata() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.metadata.MetadataSource");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbRs() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.rs.MOXyJsonProvider");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    @Test
    public void testJaxbXmlmodel() {
        Class<?> c = loadClass("org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute");
        assertClassLoadedByBundle(c, "org.eclipse.persistence.moxy");
    }

    private Class<?> loadClass(String className) {
        try {
            return ctx.getBundle().loadClass(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OSGiBundleTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Assert.fail("Cannot find and load class: " + className);
        }
        return null;
    }

    private void assertClassLoadedByBundle(Class<?> c, String bundle) {
        Bundle b = FrameworkUtil.getBundle(c);
        Assert.assertEquals("Class '" + c.getName() + "' was loaded by '"
                + b.getSymbolicName() + "', expected was '" + bundle + "'",
                bundle, b.getSymbolicName());
        Assert.assertEquals("Bundle '" + bundle + "' is not running", Bundle.ACTIVE, b.getState());
    }

    private static String getLocalRepository() {
        String path = System.getProperty("maven.repo.local");
        return (path != null && path.trim().length() > 0)
                ? path
                : System.getProperty("user.home") + File.separator
                    + ".m2" + File.separator
                    + "repository";
    }
}