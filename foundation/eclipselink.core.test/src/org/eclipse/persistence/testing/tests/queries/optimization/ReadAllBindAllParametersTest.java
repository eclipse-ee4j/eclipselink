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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ReadAllBindAllParametersTest extends AutoVerifyTestCase {
    protected boolean shouldBindAllParametersSessionOriginal;
    protected Writer originalWriter;
    protected int originalMessageLogging;
    protected ReadAllQuery query;
    protected Vector v;

    public ReadAllBindAllParametersTest() {
        v = new Vector(2);
        v.addElement(new BigDecimal(1001));
        v.addElement(new BigDecimal(1002));

        setName("ReadAllBindAllParametersTest");
        setDescription("Tests all combinations of shouldBindAllParameters attributes on Session and Query");

    }

    protected void setup() {
        shouldBindAllParametersSessionOriginal = getSession().getPlatform().shouldBindAllParameters();

        originalMessageLogging = getSession().getLogLevel();
        originalWriter = getSession().getLog();

        getSession().setLog(new StringWriter());
        getSession().setLogLevel(SessionLog.FINE);
    }

    protected boolean shouldBind() {
        return (!query.shouldIgnoreBindAllParameters() && query.shouldBindAllParameters()) || (query.shouldIgnoreBindAllParameters() && getSession().getPlatform().shouldBindAllParameters());
    }

    protected boolean wasBound() {
        return getSession().getLog().toString().indexOf("bind =>") != -1;
    }

    protected void test() {
        for (int i = 0; i <= 1; i++) {
            getSession().getPlatform().setShouldBindAllParameters(i != 0);
            for (int j = 0; j <= 2; j++) {
                query = new ReadAllQuery(Employee.class);
                ExpressionBuilder builder = new ExpressionBuilder();
                Vector vExp = new Vector(2);
                vExp.add(builder.getParameter("p1"));
                query.addArgument("p1");
                vExp.add(builder.getParameter("p2"));
                query.addArgument("p2");
                Expression exp = builder.get("id").in(vExp);
                query.setSelectionCriteria(exp);

                switch (j) {
                case 0:
                    // nothing to do - just test the default:
                    // query.bindAllParameters == Undefined
                    break;
                case 1:
                    // query.bindAllParameters == False
                    query.setShouldBindAllParameters(false);
                    break;
                case 2:
                    // query.bindAllParameters == True
                    query.setShouldBindAllParameters(true);
                    break;
                }

                // clear the writer's buffer
                ((StringWriter)getSession().getLog()).getBuffer().setLength(0);
                try {
                    getSession().executeQuery(query, v);
                } catch (DatabaseException e) {
                    throw new TestProblemException("executeQuery threw DatabaseException");
                }
                if (shouldBind() != wasBound()) {
                    return;
                }
            }
        }
    }

    protected void verify() throws Exception {
        if (shouldBind() != wasBound()) {
            String message;
            if (shouldBind()) {
                message = new String("Failed to bind");
            } else {
                message = new String("Wrongfully bound");
            }

            throw new TestErrorException(message);
        }
    }

    public void reset() {
        getSession().getPlatform().setShouldBindAllParameters(shouldBindAllParametersSessionOriginal);

        getSession().setLog(originalWriter);
        getSession().setLogLevel(originalMessageLogging);
    }
}
