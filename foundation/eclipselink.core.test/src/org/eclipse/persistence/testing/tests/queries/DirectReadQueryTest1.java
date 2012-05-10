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

/**
 * Test direct read queries with various container policies.
 */
public class DirectReadQueryTest1 extends TestCase {
    Stack stack;

    public DirectReadQueryTest1() {
        setDescription("This tests direct read queries with various container policies.");
    }

    public DirectReadQuery buildNewQuery() {
        return new DirectReadQuery("select L_NAME from EMPLOYEE");
    }

    public void test() {
        this.testStackContainerPolicy();
	this.testCursoredStreamPolicy();
    }

    /**
     * assume the stack has already been populated by the previous test
     */
    public void testCursoredStreamPolicy() {

	ValueReadQuery sizeQuery = new ValueReadQuery("select count(*) from EMPLOYEE");

	DirectReadQuery query = this.buildNewQuery();
	query.useCursoredStream(5, 5, sizeQuery);

	CursoredStream stream = (CursoredStream) getSession().executeQuery(query);
	// if we get here, we must not have generated a ClassCastException

	int count = 0;
	while (stream.hasMoreElements()) {
		count++;
		String lastName = (String) stream.nextElement();
		if (lastName.length() == 0) {
			throw new TestErrorException("missing data");
		}
	}
	if (count != stack.size()) {
		throw new TestErrorException("stream does not match stack - "
				+ "expected: " + stack.size() + " actual: " + count);
	}
    }

    public void testStackContainerPolicy() {
        DirectReadQuery query = this.buildNewQuery();
        query.useCollectionClass(Stack.class);

        stack = (Stack)getSession().executeQuery(query);
        // if we get here, we must not have generated a ClassCastException
        String lastName = (String)stack.peek();
        if (lastName.length() == 0) {
            throw new TestErrorException("missing data");
        }
    }
}
