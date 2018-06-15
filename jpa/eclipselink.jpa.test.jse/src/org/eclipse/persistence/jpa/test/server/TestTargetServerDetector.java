/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/05/2015 Rick Curtis
//       - 455683: Automatically detect target server
//     04/24/2015 Rick Curtis
//       - 465452: Test calling ServerPlatformUtils.createServerPlatform with a null platform class name.
package org.eclipse.persistence.jpa.test.server;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.exceptions.ServerPlatformException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.basic.model.Employee;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformUtils;
import org.eclipse.persistence.platform.server.was.WebSphere_Liberty_Platform;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestTargetServerDetector {
    @Emf(name = "libertyEmf", createTables = DDLGen.NONE, classes = { Employee.class }, properties = { @Property(
            name = "eclipselink.target-server", value = "WebSphere_Liberty") })
    private EntityManagerFactory libertyEmf;

    @Emf(name = "defaultEmf", createTables = DDLGen.NONE, classes = { Employee.class })
    private EntityManagerFactory defaultEmf;

    @Test
    public void test() {
        // Test that if a target-server property is set we honor that setting
        Assert.assertTrue(getPlatform(libertyEmf) instanceof WebSphere_Liberty_Platform);
        // Test that if no target-server property is set we get the default
        // server
        Assert.assertTrue(getPlatform(defaultEmf) instanceof NoServerPlatform);
    }

    @Test
    public void testCreateNullServerPlatform() {
        try {
            ServerPlatformUtils.createServerPlatform(((EntityManagerFactoryImpl) defaultEmf).getServerSession(), null, TestTargetServerDetector.class.getClassLoader());
            Assert.fail("Shouldn't have been able to create a null ServerPlatform");
        } catch (ServerPlatformException spe) {
            // expected
        }
    }

    private ServerPlatform getPlatform(EntityManagerFactory emf) {
        return ((EntityManagerFactoryImpl) emf).getServerSession().getServerPlatform();
    }

}
