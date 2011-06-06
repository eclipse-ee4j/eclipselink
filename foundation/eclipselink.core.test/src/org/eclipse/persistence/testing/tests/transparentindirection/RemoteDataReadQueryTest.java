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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * comment
 */
public class RemoteDataReadQueryTest extends AutoVerifyTestCase {
    Stack stack;

    public RemoteDataReadQueryTest() {
        setDescription("Remote data read queries with various container policies");
    }

    public DataReadQuery buildNewQuery() {
        return new DataReadQuery("select ID, CUSTNAME from ORD");
    }

    /**
     * set up test fixtures:
     *   check session
     */
    protected void setup() {
        if (!getSession().isRemoteSession()) {
            throw new TestProblemException("session should be a remote session");
        }
    }

    public void test() {
        this.testStackContainerPolicy();
        this.testCursoredStreamPolicy();
    }

    /**
     * assume the stack has already been populated by the previous test
     */
    public void testCursoredStreamPolicy() {
        ValueReadQuery sizeQuery = new ValueReadQuery("select count(*) from ORD");

        DataReadQuery query = this.buildNewQuery();
        query.useCursoredStream(5, 5, sizeQuery);

        CursoredStream stream = (CursoredStream)getSession().executeQuery(query);

        // if we get here, we must not have generated a ClassCastException
        int count = 0;
        while (stream.hasMoreElements()) {
            count++;
            Record row = (Record)stream.nextElement();
            if (row.get("CUSTNAME") == null) {
                throw new TestErrorException("missing data");
            }
        }
        if (count != stack.size()) {
            throw new TestErrorException("stream does not match stack - " + "expected: " + stack.size() + " actual: " + count);
        }
    }

    public void testStackContainerPolicy() {
        DataReadQuery query = this.buildNewQuery();
        query.useCollectionClass(Stack.class);

        stack = (Stack)getSession().executeQuery(query);
        // if we get here, we must not have generated a ClassCastException
        Record row = (Record)stack.peek();
        if (row.get("CUSTNAME") == null) {
            throw new TestErrorException("missing data");
        }
    }
}
