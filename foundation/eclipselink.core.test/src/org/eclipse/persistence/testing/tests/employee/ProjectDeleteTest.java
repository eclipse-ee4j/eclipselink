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
package org.eclipse.persistence.testing.tests.employee;

import org.eclipse.persistence.testing.framework.DeleteObjectTest;
import org.eclipse.persistence.sessions.Session;

// This handles the constraint deletion requirements.
public class ProjectDeleteTest extends DeleteObjectTest {

    /**
     * ProjectDeleteTest constructor comment.
     */
    public ProjectDeleteTest() {
        super();
    }

    /**
     * ProjectDeleteTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public ProjectDeleteTest(Object originalObject) {
        super(originalObject);
    }

    protected void setup() {
        super.setup();
        // CR2114; Project.class passed as an argument
        String appendString = getAbstractSession().getPlatform(org.eclipse.persistence.testing.models.employee.domain.Project.class).getTableQualifier();
        if (appendString.length() != 0) {
            appendString = appendString + ".";
        }

        // Must drop references first to appease constraints.
        Session session = getAbstractSession().getSessionForClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);
        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("delete from " + appendString + "PROJ_EMP where PROJ_ID = " + ((org.eclipse.persistence.testing.models.employee.domain.Project)getOriginalObject()).getId()));
    }
}
