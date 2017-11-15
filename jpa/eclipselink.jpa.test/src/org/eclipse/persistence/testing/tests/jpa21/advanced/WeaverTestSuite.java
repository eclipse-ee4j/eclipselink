/*******************************************************************************
 * Copyright (c) 2015, 2017  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Lukas - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.Arrays;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.WovenMS;
import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WeaverTestSuite extends JUnitTestCase {

    public WeaverTestSuite(String name) {
        super(name);
        setPuName("pu-with-mappedsuperclass");
    }

    @Override
    public String getPersistenceUnitName() {
        return "pu-with-mappedsuperclass";
    }


    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("WeaverTestSuite");

        suite.addTest(new WeaverTestSuite("testMappedSuperclassWeaving"));

        return suite;
    }

    //bug #466271 - @MappedSuperclass with no implementations should be woven
    public void testMappedSuperclassWeaving() {
        EntityManagerFactory emf = getEntityManagerFactory();
        ManagedType<WovenMS> managedType = emf.getMetamodel().managedType(WovenMS.class);
        Class<WovenMS> javaClass = emf.getMetamodel().managedType(WovenMS.class).getJavaType();
        Assert.assertTrue(Arrays.asList(javaClass.getInterfaces()).contains(PersistenceEntity.class));
    }
}
