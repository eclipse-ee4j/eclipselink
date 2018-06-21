/*******************************************************************************
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     ljungmann - initial implementation
 *     06/206/2018-2.7.2 Tomas Kraus
 *       - 535250: Test meta-annotations with dependency cycle
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa22.metadata;

import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.samples.annotations.CycleA;
import org.eclipse.samples.annotations.CycleB;
import org.eclipse.samples.annotations.CycleSelf;
import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestSuite;

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
        return suite;
    }

    public void testMetadataAnnotations() {
        MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), MetadataASMFactoryTest.class.getClassLoader());
        MetadataClass metadataClass = fact.getMetadataClass(Employee.class.getName());
        MetadataAnnotation annotation = metadataClass.getAnnotation("javax.persistence.Entity");
        Assert.assertNotNull(annotation);
        Assert.assertTrue(PersistenceUnitProcessor.isEntity(metadataClass));
        Assert.assertNotNull(PersistenceUnitProcessor.getEntityAnnotation(metadataClass));

        annotation = metadataClass.getAnnotation("javax.persistence.EntityListeners");
        Assert.assertNotNull(annotation);
    }

    /**
     * Check meta-annotations graph with cycle A -> B -> C -> A
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
     * Check meta-annotations graph with primitive cycle Self -> Self
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

}
