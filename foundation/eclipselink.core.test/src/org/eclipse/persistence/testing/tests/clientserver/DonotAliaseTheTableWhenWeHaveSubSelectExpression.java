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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class DonotAliaseTheTableWhenWeHaveSubSelectExpression extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Server server;

    public DonotAliaseTheTableWhenWeHaveSubSelectExpression() {
        setDescription("DonotAliaseTheTableWhenWeHaveSubSelectExpression");
    }

    public void reset() {
    }

    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
    }

    public void test() {
        Session cs = server.serverSession.acquireClientSession();

        ReadAllQuery query = new ReadAllQuery(EmployeeForClientServerSession.class);
        ExpressionBuilder raqb = new ExpressionBuilder(EmployeeForClientServerSession.class);

        ExpressionBuilder rqb = new ExpressionBuilder();
        ReportQuery rq = new ReportQuery(PhoneNumber.class, rqb);
        Expression exp = rqb.get("id").equal(raqb.get("id"));
        rq.setSelectionCriteria(exp);
        rq.addAttribute("id");

        Expression expression = raqb.get("id").in(rq);
        query.setSelectionCriteria(expression);

        cs.executeQuery(query);
    }

    public void verify() {
    }
}
