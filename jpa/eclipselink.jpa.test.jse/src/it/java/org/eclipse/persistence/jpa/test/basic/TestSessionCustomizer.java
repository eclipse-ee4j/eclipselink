/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/23/2015 - Rick Curtis
//       - 462888 : SessionCustomizer instance based configuration
package org.eclipse.persistence.jpa.test.basic;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
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
        try (JpaEntityManagerFactory emf = (JpaEntityManagerFactory) Persistence.createEntityManagerFactory(PU_NAME, props)) {
            emf.createEntityManager();
            Assert.assertTrue(Customizer.staticCustomized);
        }
    }

    @Test
    public void customizerInvoked() {
        Customizer customizerInstance = new Customizer();
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, customizerInstance);
        try (JpaEntityManagerFactory emf = (JpaEntityManagerFactory) Persistence.createEntityManagerFactory(PU_NAME, props)) {
            emf.createEntityManager();
            Assert.assertTrue(customizerInstance.customized);
        }
    }

    @Test
    public void invalidInstance() {
        props.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, new Object());

        try (JpaEntityManagerFactory emf = (JpaEntityManagerFactory) Persistence.createEntityManagerFactory(PU_NAME, props)) {
            emf.createEntityManager();
            Assert.fail();
        } catch (PersistenceException e) {
            Assert.assertTrue(e.toString(), e.getCause() instanceof ClassCastException);
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
