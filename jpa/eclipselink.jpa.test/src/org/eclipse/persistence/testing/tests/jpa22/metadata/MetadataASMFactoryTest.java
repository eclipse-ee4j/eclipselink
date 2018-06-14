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
 *              ljungmann - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa22.metadata;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.samples.RecursiveAnnotation;
import org.eclipse.samples.RecursiveAnnotation2;

import org.junit.Assert;

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
        suite.addTest(new MetadataASMFactoryTest("testRecursiveMetadataAnnotations"));
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

    public void testRecursiveMetadataAnnotations() {
        MetadataAsmFactory fact = new MetadataAsmFactory(new MetadataLogger(null), RecursiveAnnotation.class.getClassLoader());
        MetadataClass metadataClass = fact.getMetadataClass(RecursiveAnnotation.class.getName());
        MetadataClass metadataClass2 = fact.getMetadataClass(RecursiveAnnotation2.class.getName());
        MetadataAnnotation annotation = metadataClass.getAnnotation("org.eclipse.samples.RecursiveAnnotation");
        Assert.assertNotNull(annotation);
    }

}
