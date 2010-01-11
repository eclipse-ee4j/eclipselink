/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
        return originalObject.toString();
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
