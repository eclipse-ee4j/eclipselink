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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;

// This handles the constraint deletion requirements.
public class NLSProjectDeleteTest extends DeleteObjectTest {

    /**
     * ProjectDeleteTest constructor comment.
     */
    public NLSProjectDeleteTest() {
        super();
    }

    /**
     * ProjectDeleteTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public NLSProjectDeleteTest(Object originalObject) {
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
        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("delete from " + appendString + "\u305f_\u304a\u3059\u305f where \u305f\u3064\u305d\u3053_\u3051\u3048 = " + ((org.eclipse.persistence.testing.models.employee.domain.Project)getOriginalObject()).getId()));

    }
}
