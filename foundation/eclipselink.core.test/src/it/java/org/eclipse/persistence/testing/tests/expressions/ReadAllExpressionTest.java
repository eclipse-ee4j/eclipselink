/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.expressions;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Test reading with expressions.
 */
public class ReadAllExpressionTest extends org.eclipse.persistence.testing.framework.ReadAllTest {
    protected Expression expression;
    protected boolean testBatchAttributesOnEmployee;
    protected boolean supportedInMemory = true;
    protected Vector supportedPlatforms;
    protected Vector unsupportedPlatforms;
    protected Object hardReferenceToInMemoryObjects;

    /**
     * ReadAllExpressionTest constructor comment.
     * @param referenceClass java.lang.Class
     * @param originalObjectsSize int
     */
    public ReadAllExpressionTest(Class<?> referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }

    public Expression getExpression() {
        if ((expression == null) && (getQuery() != null)) {
            expression = getQuery().getSelectionCriteria();
        }
        return expression;
    }

    /**
     * Forces the construction of the query, if it has not
     * been built yet.  Normally the query is built from the
     * expression in setup, but some users might need to configure
     * the test's query beforehand: i.e. to make this an in-memory
     * test.
     */
    public ReadAllQuery getQuery(boolean initializeNow) {
        if (initializeNow && (getQuery() == null)) {
            setQuery(new ReadAllQuery());
            getQuery().setReferenceClass(getReferenceClass());
            getQuery().setSelectionCriteria(getExpression());
        }
        return getQuery();
    }

    /**
     * Answers if this test is only to be run on certain platforms.
     */
    public boolean isPlatformSpecific() {
        return ((supportedPlatforms != null) || (unsupportedPlatforms != null));
    }

    /**
     * A cleaner way to specify which tests run on which
     * platforms.  The old way was to check the name of each individual
     * test.
     */
    public boolean isPlatformSupported(DatabasePlatform platform) {
        boolean supported = false;
        boolean notSupported = false;
        if ((unsupportedPlatforms == null) && (supportedPlatforms == null)) {
            return true;
        }
        if (supportedPlatforms != null) {
            for (Object supportedPlatform : supportedPlatforms) {
                Class<?> platformClass = (Class) supportedPlatform;
                if (platformClass.isInstance(platform)) {
                    supported = true;
                }
            }
        } else {
            supported = true;
        }
        if (unsupportedPlatforms != null) {
            for (Object unsupportedPlatform : unsupportedPlatforms) {
                Class<?> platformClass = (Class) unsupportedPlatform;
                if (platformClass.isInstance(platform)) {
                    notSupported = true;
                }
            }
        }
        return supported && (!notSupported);
    }

    public void setExpression(Expression theExpression) {
        expression = theExpression;
    }

    public void setSupportedInMemory(boolean supportedInMemory) {
        this.supportedInMemory = supportedInMemory;
    }

    @Override
    protected void setup() {
        if (!isPlatformSupported(getSession().getLogin().getPlatform())) {
            throw new TestWarningException("This expression is not supported on this platform.");
        }

        // Setup the query if not given.
        // Revert the query prepare to allow re-running of the test to debug.
        getQuery(true).setIsPrepared(false);

        try {
            if (getName().equals("MultiPlatformTest") && getSession().getLogin().getPlatform().isOracle() && (getAbstractSession().getAccessor().getConnection().getMetaData().getDriverVersion().contains("8.1.7"))) {
                throw new TestWarningException("CASE not supported until Oracle 9i.");
            }
        } catch (java.sql.SQLException e) {}

        if (shouldTestBatchAttributesOnEmployee()) {
            setupBatchAttributes();
        }

        // For testing Expressions executed In-Memory must in effect
        // load the database into cache first.
        // Also keep a hard reference to the objects so they are not
        // garbage collected during the test.
        //
        if (getQuery().shouldCheckCacheOnly()) {
            setHardReferenceToInMemoryObjects(getSession().readAllObjects(getReferenceClass()));
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        freeHardReferenceToInMemoryObjects();
    }

    @Override
    protected void test() {
        if (shouldTestBatchAttributesOnEmployee()) {
            super.test();
            testBatchAttributes();
        } else {
            try {
                super.test();
            } catch (QueryException exception) {
                if ((!supportedInMemory) && (exception.getErrorCode() == QueryException.CANNOT_CONFORM_EXPRESSION)) {
                    throw new TestWarningException("This test is not supported In-Memory");
                } else {
                    throw new TestErrorException("Fatal query exception occurred.", exception);
                }
            }
        }
    }

    public void addSupportedPlatform(Class<?> platform) {
        if (supportedPlatforms == null) {
            supportedPlatforms = new Vector();
        }
        supportedPlatforms.add(platform);
    }

    public void addUnsupportedPlatform(Class<?> platform) {
        if (unsupportedPlatforms == null) {
            unsupportedPlatforms = new Vector();
        }
        unsupportedPlatforms.add(platform);
    }

    public void dontTestBatchAttributesOnEmployee() {
            testBatchAttributesOnEmployee = false;
    }

    public boolean shouldTestBatchAttributesOnEmployee() {
            return testBatchAttributesOnEmployee;
    }

    /**
     * This flag has been added to test adding batch attributes to very
     * complex expressions.  This method assumes that the query is on Employee.
     * (see ExpressionSubSelectTestSuite#addOneToManyJoin2WithBatchReadTest)
     * @see ExpressionSubSelectTestSuite#addExistsWithBatchAttributeTest()
     * @see ExpressionSubSelectTestSuite#addObjectComparisonWithBatchAttributeTest()
     * @see ExpressionSubSelectTestSuite#addParallelSelectWithBatchAttributeTest()
     * @see ExpressionSubSelectTestSuite#addSubSelectInWithBatchAttributeTest()
     */
    public void testBatchAttributesOnEmployee() {
            testBatchAttributesOnEmployee = true;
    }

    protected void freeHardReferenceToInMemoryObjects() {
        this.hardReferenceToInMemoryObjects = null;
    }

    protected void setHardReferenceToInMemoryObjects(Object hardReference) {
        this.hardReferenceToInMemoryObjects = hardReference;
    }

    protected void setupBatchAttributes() {
        getQuery().addBatchReadAttribute("phoneNumbers");
        getQuery().addBatchReadAttribute("address");
        getQuery().addBatchReadAttribute("responsibilitiesList");
        getQuery().addBatchReadAttribute("projects");
    }

    protected void testBatchAttributes() {
        Vector result = (Vector) this.objectsFromDatabase;
        Vector phoneNumbers = ((Employee) result.get(0)).getPhoneNumbers();
        ((Employee) result.get(0)).getResponsibilitiesList().size();
        ((Employee) result.get(0)).getProjects().size();
        if ((phoneNumbers == null) || (phoneNumbers.isEmpty())) {
                throw new TestErrorException("The original query was corrupted when made part of a batch query.");
        }
        if (((Employee) result.get(0)).getAddress() == null) {
                throw new TestErrorException("The original query was corrupted when made part of a batch query.");
        }
    }
}
