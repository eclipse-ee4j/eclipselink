/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.types;

import org.eclipse.persistence.testing.framework.*;

/**
 * Custom WriteObjectTests that will allow the TypeTester object to pass through the calls to:
 * test(), setup(), & verify(). This ius required so the tests can customize according to the platform.
 */
public class WriteTypeObjectTest extends WriteObjectTest {

    /** The following allows individual tests to be run with or without using native sql. */
    protected boolean useNativeSQL = false;
    protected boolean didUseNativeSQL = false;

    public WriteTypeObjectTest(TypeTester originalObject) {
        super(originalObject);
        // Need to set shouldMutate to false as the mutator method causes problems
        // with tests that pass in null values and field length tests.
        super.setTestShouldMutate(false);
    }

    public String getName() {
        return "WriteTypeObjectTest(" + originalObject.getClass() + ")";
    }

    protected Object getObjectFromDatabase() {
        return this.objectFromDatabase;
    }

    protected void setup() {
        if (shouldUseNativeSQL()) {
            if (!getSession().getLogin().shouldUseNativeSQL()) {
                setDidUseNativeSQL(true);
                getAbstractSession().getAccessor().disconnect(getAbstractSession());
                getSession().getLogin().setUsesNativeSQL(true);
                getAbstractSession().getAccessor().connect(getSession().getLogin(), getAbstractSession());
            }
        }
        ((TypeTester)this.originalObject).setup(getSession());
        super.setup();
    }

    protected void superTest() {
        super.test();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void superVerify() throws TestErrorException, TestWarningException {
        super.verify();
    }

    protected void test() {
        ((TypeTester)this.originalObject).test(this);
    }

    public String toString() {
        return originalObject.toString();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() throws TestErrorException, TestWarningException {
        ((TypeTester)this.originalObject).verify(this);
    }

    /**
     * Was native sql used for this test only?
     */
    protected boolean didUseNativeSQL() {
        return didUseNativeSQL;
    }

    public void reset() {
        super.reset();

        if (didUseNativeSQL()) {
            getAbstractSession().getAccessor().disconnect(getAbstractSession());
            getSession().getLogin().setUsesNativeSQL(false);
            getAbstractSession().getAccessor().connect(getSession().getLogin(), getAbstractSession());
        }
    }

    /**
     * Notify the test that the session's usesNativeSQL setting needs to be
     * reset after the test.
     */
    protected void setDidUseNativeSQL(boolean value) {
        didUseNativeSQL = value;
    }

    /**
     * Allows one to set usesNativeSQL to true on a test by test basis.
     */
    public void setUseNativeSQL(boolean value) {
        useNativeSQL = value;
    }

    public boolean shouldUseNativeSQL() {
        return useNativeSQL;
    }
}
