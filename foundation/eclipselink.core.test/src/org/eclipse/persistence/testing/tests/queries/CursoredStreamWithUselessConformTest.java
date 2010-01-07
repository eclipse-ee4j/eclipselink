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

/**
 * Test the cursored stream feature by performing a cursor read on the database
 * and comparing the contents to a normal query read.
 */
public class CursoredStreamWithUselessConformTest extends CursoredStreamTest {
    public CursoredStreamWithUselessConformTest(Class referenceClass, Expression expression) {
        super(referenceClass, expression);
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
            //No unit of work, but checking for NPE.  Fixed CR2671
            query.conformResultsInUnitOfWork();
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
}
