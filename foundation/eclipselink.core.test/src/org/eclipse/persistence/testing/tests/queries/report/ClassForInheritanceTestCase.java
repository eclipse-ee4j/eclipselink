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
 *     May 26, 2009-1.0M6 Chris Delahunt 
 *       - TODO Bug#: Bug Description 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.report;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * @author cdelahun
 *
 */
public class ClassForInheritanceTestCase extends ReportQueryTestCase {

    /* (non-Javadoc)
     * @see org.eclipse.persistence.testing.tests.queries.report.ReportQueryTestCase#buildExpectedResults()
     */
    @Override
    protected void buildExpectedResults() throws Exception {

        Vector projects = getSession().readAllObjects(Project.class);
        Class projClass = null;
        Project proj = null;
        for (Enumeration e = projects.elements(); e.hasMoreElements(); ) {
            proj = (Project)e.nextElement();
            addResult(new Object[] {proj.getClass(), proj}, null );
        }
    }

    protected void setup() throws Exception {
        if (getSession().isRemoteSession()){
            throw new TestWarningException("This Test is not designed to work with RemoteSessions.");
        }
        super.setup();
        ExpressionBuilder builder = new ExpressionBuilder();
        reportQuery = new ReportQuery(Project.class, builder);

        reportQuery.addItem("Type", builder.type());
        reportQuery.addItem("project", builder);
    }

}
