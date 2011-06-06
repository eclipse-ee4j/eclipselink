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
package org.eclipse.persistence.testing.tests.xdb;

import java.util.Vector;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class ReportQueryTest extends TestCase {
    Vector results;

    public ReportQueryTest() {
        setDescription("Tests using a report query to return element values from an XMLType field");
    }

    public void setup() {
    }

    public void reset() {
    }

    public void test() {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(Employee_XML.class, builder);
        rq.addAttribute("degree", builder.get("resume").extractValue("/resume/education/degree/text()"));
        rq.setSelectionCriteria(builder.get("firstName").equal("Frank"));
        results = (Vector)getSession().executeQuery(rq);
    }

    public void verify() {
        ReportQueryResult first = (ReportQueryResult)results.firstElement();
        if (!(first.get("degree").equals("BCS"))) {
            throw new TestErrorException("Wrong Value Returned");
        }
    }
}
