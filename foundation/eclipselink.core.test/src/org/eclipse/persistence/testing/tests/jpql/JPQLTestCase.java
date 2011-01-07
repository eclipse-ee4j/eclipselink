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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Generic EJBQL test case.
 * Executes the EJBQL string and verifies the results.
 */
public class JPQLTestCase extends TransactionalTestCase {
    private String ejbql;
    protected ObjectLevelReadQuery theQuery = null;
    private Class referenceClass;
    private Expression originalExpression = null;
    private Object originalObject;
    private DomainObjectComparer comparer = null;
    private Object returnedObjects;
    public boolean useReportQuery;
    private Vector arguments = null;
    protected Vector supportedPlatforms;
    protected Vector unsupportedPlatforms;

    public JPQLTestCase() {
        super();
        useReportQuery = false;
    }

    public JPQLTestCase(String ejbqlString) {
        this();
        this.ejbql = ejbqlString;
    }

    public JPQLTestCase(ObjectLevelReadQuery theQuery) {
        this();
        this.theQuery = theQuery;
        this.ejbql = (((JPQLCallQueryMechanism)theQuery.getQueryMechanism()).getJPQLCall()).getEjbqlString();
    }

    public JPQLTestCase(String ejbqlString, ObjectLevelReadQuery theQuery) {
        this();
        this.ejbql = ejbqlString;
        this.theQuery = theQuery;
    }

    public JPQLTestCase(String ejbqlString, ObjectLevelReadQuery theQuery, Class theReferenceClass) {
        this();
        this.ejbql = ejbqlString;
        this.theQuery = theQuery;
        this.referenceClass = theReferenceClass;
    }

    public static JPQLTestCase getBaseTestCase() {
        return new BaseTestCase();
    }

    public Vector getAttributeFromAll(String attributeName, Vector objects) {
        ClassDescriptor descriptor = getSession().getClassDescriptor(getReferenceClass());
        DirectToFieldMapping mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName(attributeName);

        Vector attributes = new Vector();
        Object currentObject;
        for (int i = 0; i < objects.size(); i++) {
            currentObject = objects.elementAt(i);
            if (currentObject.getClass() == ReportQueryResult.class) {
                attributes.addElement(((ReportQueryResult)currentObject).get(attributeName));
            } else {
                attributes.addElement(mapping.getAttributeValueFromObject(currentObject));
            }
        }
        return attributes;
    }

    public void reset() {
        // Force the query to be rebuilt each time...
        setQuery(null);
        super.reset();
    }

    /**
     * Return the first employee that has a long enough name for the test.
     * If no match is found throw a warning exception.
     * See bug 223005
     * @param minFirstNameLength
     * @param testName
     * @return
     */
    public Employee getEmployeeWithRequiredNameLength(int minFirstNameLength, String testName) {
    	return getEmployeeWithRequiredNameLength(getSomeEmployees(), minFirstNameLength, testName);
    }
    
    /**
     * Return the first employee that has a long enough name for the test.
     * If no match is found throw a warning exception.
     * See bug 223005
     * @param vectorOfEmployees
     * @param minFirstNameLength
     * @param testName
     * @return
     */
    public Employee getEmployeeWithRequiredNameLength(Vector vectorOfEmployees, int minFirstNameLength, String testName) {
        Employee empMatch = null;
        Vector<Employee> employees = vectorOfEmployees;        
        String firstName;
        StringBuffer partialFirstName;
        
        // Loop through the collection of employees to find one that matches our test requirements
        for(int i=0; i<employees.size();i++) {
            empMatch = employees.get(i);
            firstName = empMatch.getFirstName();
            // Verify length criteria
            if(firstName.length() >= minFirstNameLength) {
                // exit the for loop - return the last empMatch
                i = employees.size();
            }
        }
        
        // If we could not find a proper employee for testing - throw a warning
        if(null == empMatch) {
        	throw new RuntimeException(testName + " Setup Failed: unable to find an Employee with firstName size of at least  " + minFirstNameLength);
        } else {
        	return empMatch;
        }
    }
    
    public void setup() {
        if (!isPlatformSupported(getSession().getLogin().getPlatform())) {
            throw new TestWarningException("This EJBQL is not supported on this platform.");
        }

        if (getReferenceClass() == null) {
            setReferenceClass(Employee.class);
        }
        getQuery().setEJBQLString(getEjbqlString());
    }

    public void test() throws Exception {
        getSession().logMessage("Running EJBQL -> " + getEjbqlString());
        executeEJBQLQuery();
    }

    //lowest level execute that can be subclassed if required	
    public void executeEJBQLQuery() throws Exception {
        if (hasArguments()) {
            addArgumentNamesToQuery();
            setReturnedObjects(getSession().executeQuery(getQuery(), getArguments()));
        } else {
            setReturnedObjects(getSession().executeQuery(getQuery()));
        }
    }

    //add the "1", "2", ... to the query for each parameter
    private void addArgumentNamesToQuery() {
        int argumentIndex = 1;
        while (argumentIndex <= getArguments().size()) {
            getQuery().addArgument(String.valueOf(argumentIndex));
            ++argumentIndex;
        }
    }

    public void verify() throws Exception {
        if (!getComparer().compareObjects(getOriginalObject(), getReturnedObjects())) {
            throw new TestErrorException(getName() + " Verify Failed:" + getOriginalObject() + " != " + getReturnedObjects());
        }
    }

    /**
     * Return some projects to be used for querying
     */
    public Vector getSomeProjects() {
        return getSession().readAllObjects(Project.class);
    }

    /*
     * Return all the addresses to be used for querying
     */
    public Vector getSomeAddresses() {
        return getSession().readAllObjects(Address.class);
    }

    /*
     * Return some employees to be used for querying
     */
    public Vector getSomeEmployees() {
        return getSession().readAllObjects(Employee.class);
    }

    /**
     * Set the ejbql string to the passed value
     * @param theEjbqlString
     */
    public void setEjbqlString(String theEjbqlString) {
        this.ejbql = theEjbqlString;
    }

    /**
     * Return the ejbqlString
     */
    public String getEjbqlString() {
        return ejbql;
    }

    public ObjectLevelReadQuery getQuery() {
        if (theQuery == null) {
            if (shouldUseReportQuery()) {
                setQuery(buildReportQuery());
            } else {
                setQuery(new ReadAllQuery());
            }
            getQuery().setEJBQLString(getEjbqlString());
        }
        return theQuery;
    }

    public ReportQuery buildReportQuery() {
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.dontRetrievePrimaryKeys();
        reportQuery.returnSingleAttribute();
        return reportQuery;
    }

    public void setQuery(ObjectLevelReadQuery theQuery) {
        this.theQuery = theQuery;
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public void setReferenceClass(Class theClass) {
        this.referenceClass = theClass;
    }

    public Object getReturnedObjects() {
        return returnedObjects;
    }

    public void setReturnedObjects(Object theReturnedObjects) {
        returnedObjects = theReturnedObjects;
    }

    public DomainObjectComparer getComparer() {
        if (comparer == null) {
            setComparer(new DomainObjectComparer());
            getComparer().setSession(getSession());
        }
        return comparer;
    }

    public Expression getOriginalObjectExpression() {
        return originalExpression;
    }

    public void setOriginalObjectExpression(Expression theOriginalExpression) {
        originalExpression = theOriginalExpression;
    }

    public void setComparer(DomainObjectComparer theComparer) {
        comparer = theComparer;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public void setOriginalOject(Object theObject) {
        originalObject = theObject;
    }

    public void useReportQuery() {
        useReportQuery = true;
    }

    public boolean shouldUseReportQuery() {
        return getUseReportQuery();
    }

    public boolean getUseReportQuery() {
        return useReportQuery;
    }

    public void setUseReportQuery(boolean useReportQuery) {
        this.useReportQuery = useReportQuery;
    }

    //get and set for arguments to the EJBQL query
    public Vector getArguments() {
        return arguments;
    }

    public void setArguments(Vector newArguments) {
        arguments = newArguments;
    }

    public boolean hasArguments() {
        return getArguments() != null;
    }

    /**
     * A clean way to specify which tests run on which platforms.
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
}
