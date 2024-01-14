/*
 * Copyright (c) 2015, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     ljungmann - initial implementation
//     06/206/2018-2.7.2 Tomas Kraus
//       - 535250: Test meta-annotations with dependency cycle
package org.eclipse.persistence.testing.tests.jpa22.metadata;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.samples.annotations.CycleA;
import org.eclipse.samples.annotations.CycleB;
import org.eclipse.samples.annotations.CycleSelf;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MetadataASMFactoryTest extends JUnitTestCase {
    
    public MetadataASMFactoryTest() {
    }

    public MetadataASMFactoryTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MetadataASMFactoryTest");
        suite.addTest(new MetadataASMFactoryTest("testMetadataAnnotations"));
        suite.addTest(new MetadataASMFactoryTest("testAnnotationsWithCycle"));
        suite.addTest(new MetadataASMFactoryTest("testAnnotationsWithPrimitiveCycle"));
        suite.addTest(new MetadataASMFactoryTest("testReadClassBuildByUknownJDK"));
        return suite;
    }

    public void testMetadataAnnotations() {
        MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), MetadataASMFactoryTest.class.getClassLoader());
        MetadataClass metadataClass = fact.getMetadataClass(Employee.class.getName());
        MetadataAnnotation annotation = metadataClass.getAnnotation("jakarta.persistence.Entity");
        Assert.assertNotNull(annotation);
        Assert.assertTrue(PersistenceUnitProcessor.isEntity(metadataClass));
        Assert.assertNotNull(PersistenceUnitProcessor.getEntityAnnotation(metadataClass));

        annotation = metadataClass.getAnnotation("jakarta.persistence.EntityListeners");
        Assert.assertNotNull(annotation);
    }

    /**
     * Check meta-annotations graph with cycle A -{@literal >} B -{@literal >} C -{@literal >} A
     */
    public void testAnnotationsWithCycle() {
        try {
            MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), MetadataASMFactoryTest.class.getClassLoader());
            MetadataClass metadataClass = fact.getMetadataClass(CycleA.class.getName());
            MetadataAnnotation annotation = metadataClass.getAnnotation("org.eclipse.samples.annotations.CycleC");
            metadataClass = fact.getMetadataClass(CycleB.class.getName());
            annotation = metadataClass.getAnnotation("org.eclipse.samples.annotations.CycleA");
        } catch (StackOverflowError e) {
            fail("Stack overflow while processing cycle in meta-annotations");
        }
    }

    /**
     * Check meta-annotations graph with primitive cycle Self -{@literal >} Self
     */
    public void testAnnotationsWithPrimitiveCycle() {
        try {
            MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), MetadataASMFactoryTest.class.getClassLoader());
            MetadataClass metadataClass = fact.getMetadataClass(CycleSelf.class.getName());
            MetadataAnnotation annotation = metadataClass.getAnnotation("org.eclipse.samples.annotations.CycleSelf");
        } catch (StackOverflowError e) {
            fail("Stack overflow while processing cycle in meta-annotations");
        }
    }

    public void testReadClassBuildByUknownJDK() {
        SessionLog log = AbstractSessionLog.getLog();
        try {
            LW tracker = new LW();
            AbstractSessionLog.setLog(tracker);
            ClassLoader cl = new EmployeeLoader(MetadataASMFactoryTest.class.getClassLoader());
            MetadataAsmFactory maf = new MetadataAsmFactory(new MetadataLogger(null), cl);
            MetadataClass employee = maf.getMetadataClass("org.eclipse.persistence.testing.tests.jpa22.metadata.Employee");
            //Check existence of the log message
            Assert.assertNotNull(tracker.msg);
            //Check, that at least some metadata are available
            Assert.assertNotNull(employee);
            //jakarta.persistence.Entity annotation can't be read as class is build by unknown JDK
            Assert.assertNull(employee.getAnnotation("jakarta.persistence.Entity"));
        } finally {
            AbstractSessionLog.setLog(log);
        }
    }

    private static class EmployeeLoader extends ClassLoader {
        private final ClassLoader parent;

        public EmployeeLoader(ClassLoader parent) {
            super(parent);
            this.parent = parent;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            if ("org/eclipse/persistence/testing/tests/jpa22/metadata/Employee.class".equals(name)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] cls = new byte[4096];
                try (InputStream is = MetadataASMFactoryTest.class.getResourceAsStream("Employee.class");) {
                    is.read(cls);
                    baos.write(cls, 0, 5);
                    // change class version to some random high enough
                    // value to trigger the fallback
                    baos.write(99);
                    baos.write(99);
                    baos.write(cls, 7, cls.length - 8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new ByteArrayInputStream(baos.toByteArray());
            }
            return super.getResourceAsStream(name);
        }
    }

    private static class LW extends DefaultSessionLog {
        private String msg = null;
        @Override
        public synchronized void log(SessionLogEntry entry) {
            if (SEVERE == entry.getLevel()) {
                msg = entry.getMessage();
            }
            super.log(entry);
        }
    }
}
