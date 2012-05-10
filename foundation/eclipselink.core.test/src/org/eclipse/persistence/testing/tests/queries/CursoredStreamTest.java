/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Test the cursored stream feature by performing a cursor read on the database
 * and comparing the contents to a normal query read.
 */
public class

CursoredStreamTest extends AutoVerifyTestCase {
    protected int size;
    protected Vector normalQueryObjects;
    protected Vector cursoredQueryObjects;
    protected Class referenceClass;
    protected Expression joinExpression;

    public CursoredStreamTest(Class referenceClass, Expression expression) {
        setReferenceClass(referenceClass);
        setName(getName() + "(" + referenceClass + ")");
        setDescription("This test verifies that the number of objects read in using a cursored stream" + 
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
        setNormalQueryObjects(getSession().readAllObjects(getReferenceClass(), joinExpression));
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();
        CursoredStream stream = null;

        try {
            Object databaseObject;

            cursoredQueryObjects = new Vector();

            query.setReferenceClass(getReferenceClass());
            query.setSelectionCriteria(joinExpression);
            query.useCursoredStream();
            stream = (CursoredStream)getSession().executeQuery(query);

            // Test dual cursors and read(int)
            CursoredStream stream2 = (CursoredStream)getSession().executeQuery(query);
            try {
                stream2.read(5);
            } catch (org.eclipse.persistence.exceptions.QueryException ex) {
            } // ignore at end	
            setSize(stream2.size());
            stream2.close();
            while (!stream.atEnd()) {
                databaseObject = stream.read();
                getCursoredQueryObjects().addElement(databaseObject);
                stream.releasePrevious();
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Verify if number of query objects matches number of cursor objects
     */
    protected void verify() {
        if (getNormalQueryObjects().size() != getCursoredQueryObjects().size()) {
            throw new TestErrorException("The number of streamed objects does not match the number of objects stored on the database.  Expected: " + 
                                         getNormalQueryObjects().size() + ". Got: " + 
                                         getCursoredQueryObjects().size());
        }

        if (getNormalQueryObjects().size() == 0) {
            throw new TestWarningException("no object with the specified selection criteria was found ");
        }

        if (getSize() != getNormalQueryObjects().size()) {
            throw new TestErrorException("The cursored stream size function is not working properly");
        }

        int first = 0;
        int last = getNormalQueryObjects().size() - 1;
        if (!((AbstractSession)getSession()).compareObjects(getCursoredQueryObjects().elementAt(first), 
                                                            getNormalQueryObjects().elementAt(first))) {
            throw new TestErrorException("The First Objects do not match");
        }
        if (!((AbstractSession)getSession()).compareObjects(getCursoredQueryObjects().elementAt(last), 
                                                            getNormalQueryObjects().elementAt(last))) {
            throw new TestErrorException("The Last Objects do not match");
        }


    }
}
