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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;

/**
 * Generic test to check if any results were returned as the result of a expression query.
 * If result vector is empty then throw an exception with specified message.
 * @author Peter O'Blenis
 * @date Feb. 3/99
 */
public class SearchTest extends AutoVerifyTestCase {
    // Expression to test
    private Expression m_expression = null;

    // Error Message
    private String m_szErrorMessage = null;

    public SearchTest() {
        setDescription("Test the wrong query keys are error handled correctly.");
    }

    public void setErrorMessage(String szMsg) {
        m_szErrorMessage = szMsg;
    }

    public void setExpression(Expression expression) {
        m_expression = expression;
    }

    public void test() {
        if ((m_expression == null) || (m_szErrorMessage == null)) {
            throw new RuntimeException("Search Test was invoked without setting all parameters");
        }

        Vector results = getSession().readAllObjects(Employee.class, m_expression);
        if (results.size() < 1) {
            throw new TestErrorException(m_szErrorMessage);
        }
    }
}
