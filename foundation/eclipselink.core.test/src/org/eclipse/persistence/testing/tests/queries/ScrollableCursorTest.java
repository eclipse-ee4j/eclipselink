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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;

/**
 * Test the scrollable cursor feature by performing a cursor read on the database
 * and comparing the contents to a normal query read.
 */
public class ScrollableCursorTest extends TestCase {
    protected int size;
    protected Vector normalQueryObjects;
    protected Vector cursoredQueryObjects;
    protected Class referenceClass;
    protected Expression joinExpression;
    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public ScrollableCursorTest(Class referenceClass, Expression expression) {
        setReferenceClass(referenceClass);
        setName(getName() + "(" + referenceClass + ")");
        setDescription("This test verifies that the number of objects read in using a scrollable cursor" + 
                       " matches the number of object read in using a normal query");
        joinExpression = expression;
    }

    public Vector getCursoredQueryObjects() {
        return cursoredQueryObjects;
    }

    public Vector getNormalQueryObjects() {
        return normalQueryObjects;
    }

    public Class getReferenceClass() {
        return referenceClass;
    }

    public int getSize() {
        return size;
    }

    public void setCursoredQueryObjects(Vector objects) {
        cursoredQueryObjects = objects;
    }

    public void setNormalQueryObjects(Vector objects) {
        normalQueryObjects = objects;
    }

    public void setReferenceClass(Class aClass) {
        referenceClass = aClass;
    }

    public void setSize(int aSize) {
        size = aSize;
    }

    protected void setup() {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform.");
        }
        TYPE_SCROLL_INSENSITIVE_isSupported = true;
        CONCUR_UPDATABLE_isSupported = true;
        if(getSession().getPlatform().isSQLServer()) {
            // In case either TYPE_SCROLL_INSENSITIVE or CONCUR_UPDATABLE used  
            // MS SQL Server  Version: 9.00.2050;  MS SQL Server 2005 JDBC Driver  Version: 1.2.2828.100 throws exception:
            // com.microsoft.sqlserver.jdbc.SQLServerException: The cursor type/concurrency combination is not supported.
            TYPE_SCROLL_INSENSITIVE_isSupported = false;
            CONCUR_UPDATABLE_isSupported = false;
        }
        setNormalQueryObjects(getSession().readAllObjects(getReferenceClass(), joinExpression));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();
        ScrollableCursor cursor = null;

        try {
            Object databaseObject;

            cursoredQueryObjects = new Vector();

            query.setReferenceClass(getReferenceClass());
            query.setSelectionCriteria(joinExpression);
            if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
                query.useScrollableCursor(2);
            } else {
                ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
                if(!TYPE_SCROLL_INSENSITIVE_isSupported) {
                    policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
                }
                if(!CONCUR_UPDATABLE_isSupported) {
                    policy.setResultSetConcurrency(ScrollableCursorPolicy.CONCUR_READ_ONLY);
                }
                policy.setPageSize(2);
                query.useScrollableCursor(policy);
            }
            cursor = (ScrollableCursor)getSession().executeQuery(query);

            // Test dual cursors and read(int)
            ScrollableCursor cursor2 = (ScrollableCursor)getSession().executeQuery(query);
            try {
                cursor2.next(5);
            } catch (org.eclipse.persistence.exceptions.QueryException ex) {
            } // ignore at end	
            setSize(cursor2.size());
            cursor2.close();
            while (cursor.hasNext()) {
                databaseObject = cursor.next();
                getCursoredQueryObjects().addElement(databaseObject);
            }

            // Test cursor policy		
            ReadAllQuery query3 = new ReadAllQuery(getReferenceClass());
            ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
            policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
            query3.useScrollableCursor(policy);
            ScrollableCursor cursor3 = (ScrollableCursor)getSession().executeQuery(query3);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Verify if number of query objects matches number of cursor objects
     */
    protected void verify() {
        if (getNormalQueryObjects().size() != getCursoredQueryObjects().size()) {
            throw new TestErrorException("The number of streamed objects (" + getCursoredQueryObjects().size() + 
                                         ") does not match the number of objects stored on the database (" + 
                                         getNormalQueryObjects().size() + ") ");
        }

        if (getSize() != getNormalQueryObjects().size())
            throw new TestErrorException("The cursored stream size function is not working properly");

    }

}
