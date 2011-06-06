/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.expressions;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

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
    public ReadAllExpressionTest(Class referenceClass, int originalObjectsSize) {
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
            for (Iterator iterator = supportedPlatforms.iterator(); iterator.hasNext();) {
                Class platformClass = (Class)iterator.next();
                if (platformClass.isInstance(platform)) {
                    supported = true;
                }
            }
        } else {
            supported = true;
        }
        if (unsupportedPlatforms != null) {
            for (Iterator iterator = unsupportedPlatforms.iterator(); iterator.hasNext();) {
                Class platformClass = (Class)iterator.next();
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

    protected void setup() {
        if (!isPlatformSupported(getSession().getLogin().getPlatform())) {
            throw new TestWarningException("This expression is not supported on this platform.");
        }

        // Setup the query if not given.
        getQuery(true);

        try {
            if (getName().equals("MultiPlatformTest") && getSession().getLogin().getPlatform().isOracle() && (getAbstractSession().getAccessor().getConnection().getMetaData().getDriverVersion().indexOf("8.1.7") != -1)) {
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

    public void reset() throws Exception {
        super.reset();
        freeHardReferenceToInMemoryObjects();
    }

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

    public void addSupportedPlatform(Class platform) {
        if (supportedPlatforms == null) {
            supportedPlatforms = new Vector();
        }
        supportedPlatforms.addElement(platform);
    }

    public void addUnsupportedPlatform(Class platform) {
        if (unsupportedPlatforms == null) {
            unsupportedPlatforms = new Vector();
        }
        unsupportedPlatforms.addElement(platform);
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
     * @see ExpressionSubSelectTestSuite#addExistsWithBatchAttributeTest
     * @see ExpressionSubSelectTestSuite#addObjectComparisonWithBatchAttributeTest
     * @see ExpressionSubSelectTestSuite#addParallelSelectWithBatchAttributeTest
     * @see ExpressionSubSelectTestSuite#addSubSelectInWithBatchAttributeTest
     * @see ExpressionSubSelectTestSuite#addOneToManyJoin2WithBatchReadTest
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
        Vector phoneNumbers = ((Employee) result.elementAt(0)).getPhoneNumbers();
        ((Employee) result.elementAt(0)).getResponsibilitiesList().size();
        ((Employee) result.elementAt(0)).getProjects().size();
        if ((phoneNumbers == null) || (phoneNumbers.size() == 0)) {
                throw new TestErrorException("The original query was corrupted when made part of a batch query.");
        }
        if (((Employee) result.elementAt(0)).getAddress() == null) {
                throw new TestErrorException("The original query was corrupted when made part of a batch query.");
        }
    }
}
