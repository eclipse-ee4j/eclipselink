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
//     03/23/2015 - Rick Curtis
//       - 462888 : SessionCustomizer instance based configuration
package org.eclipse.persistence.jpa.test.basic;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.sessions.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestSessionCustomizer {
    private static final String PU_NAME = "static-small-pu";
    Map<String, Object> props;

    @Before
    public void before() {
        props = new HashMap<String, Object>();
        Customizer.staticCustomized = false;
    }

    @Test
    public void stringCustomizerInvoked() {
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, Customizer.class.getName());
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, props);
        try {
            emf.createEntityManager();
            Assert.assertTrue(Customizer.staticCustomized);
        } finally {
            emf.close();
        }
    }

    @Test
    public void customizerInvoked() {
        Customizer customizerInstance = new Customizer();
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizerInstance);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, props);
        try {
            emf.createEntityManager();
            Assert.assertTrue(customizerInstance.customized);
        } finally {
            emf.close();
        }
    }

    @Test
    public void invalidInstance() {
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, new Object());

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU_NAME, props);
        try {
            emf.createEntityManager();
            Assert.fail();
        } catch (PersistenceException e) {
            Assert.assertTrue(e.toString(), e.getCause() instanceof ClassCastException);
        } finally {
            if (emf != null) {
                emf.close();
            }
        }
    }

    public static class Customizer implements SessionCustomizer {
        static boolean staticCustomized = false;
        boolean customized = false;
        Session session;

        @Override
        public void customize(Session session) throws Exception {
            this.session = session;
            customized = true;
            staticCustomized = true;
        }
    }
}
