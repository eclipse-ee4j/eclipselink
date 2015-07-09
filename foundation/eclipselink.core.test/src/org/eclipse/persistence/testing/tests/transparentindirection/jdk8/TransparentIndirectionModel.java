/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.transparentindirection.jdk8;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.transparentindirection.BidirectionalRelationshipSystem;
import org.eclipse.persistence.testing.models.transparentindirection.CustomIndirectContainerSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectListSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectMapSystem;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectSetSystem;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestSuite;

/**
 * Test transparent indirection.
 */
public class TransparentIndirectionModel extends TestModel {
    public TransparentIndirectionModel() {
        setDescription("Test Transparent Indirection for Java 8");
    }

    @Override
    public void addRequiredSystems() {
        addRequiredSystem(new IndirectListSystem());
        addRequiredSystem(new IndirectMapSystem());
        addRequiredSystem(new IndirectSetSystem());
        addRequiredSystem(new CustomIndirectContainerSystem());
        addRequiredSystem(new BidirectionalRelationshipSystem());
    }

    public static final class X<E> extends IndirectList<E> {}
    public static final class Y<E> extends IndirectSet<E> {}
    public static final class Z<K, V> extends IndirectMap<K, V> {}

    @Override
    public void addTests() {
        addTest(new ZTestSuite(IndirectListTestAPI.class));
        try {
            Class jdk8impl = Class.forName("org.eclipse.persistence.internal.indirection.jdk8.IndirectList");
            addTest(IndirectListTestAPI.getTestSuiteFor(jdk8impl, true));
            addTest(IndirectListTestAPI.getTestSuiteFor(jdk8impl, false));
        } catch (Throwable t) {
            throw new TestWarningException("JDK 8 impl for IndirectList not found.", t);
        }
        addTest(new ZTestSuite(IndirectMapTestAPI.class));
        try {
            Class jdk8impl = Class.forName("org.eclipse.persistence.internal.indirection.jdk8.IndirectMap");
            addTest(IndirectMapTestAPI.getTestSuiteFor(jdk8impl, true));
            addTest(IndirectMapTestAPI.getTestSuiteFor(jdk8impl, false));
        } catch (Throwable t) {
            throw new TestWarningException("JDK 8 impl for IndirectMap not found.", t);
        }
        addTest(new ZTestSuite(IndirectSetTestAPI.class));
        try {
            Class jdk8impl = Class.forName("org.eclipse.persistence.internal.indirection.jdk8.IndirectSet");
            addTest(IndirectSetTestAPI.getTestSuiteFor(jdk8impl, true));
            addTest(IndirectSetTestAPI.getTestSuiteFor(jdk8impl, false));
        } catch (Throwable t) {
            throw new TestWarningException("JDK 8 impl for IndirectSet not found.", t);
        }
    }
    
    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        return new TransparentIndirectionModel();
    }
}
