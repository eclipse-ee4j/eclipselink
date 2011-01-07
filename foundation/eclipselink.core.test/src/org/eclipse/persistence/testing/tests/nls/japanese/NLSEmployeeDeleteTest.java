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
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.DeleteObjectTest;

// This handles the constraint deletion requirements.
public class NLSEmployeeDeleteTest extends DeleteObjectTest {
    public NLSEmployeeDeleteTest() {
        super();
    }

    public NLSEmployeeDeleteTest(Object originalObject) {
        super(originalObject);
    }

    public static void deleteDependencies(AbstractSession session, org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee employee) {
        // CR2114 - following line modified; employee.getClass() passed as argument
        String appendString = session.getPlatform(employee.getClass()).getTableQualifier();
        if (appendString.length() != 0) {
            appendString = appendString + ".";
        }

        Session psession = session.getSessionForClass(org.eclipse.persistence.testing.models.employee.domain.Project.class);

        // Must drop references first to appease constraints.
        psession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + "\u305f\u3064\u305d\u3053 set \u3057\u3048\u3064_\u3051\u3048 = null where \u3057\u3048\u3064_\u3051\u3048 = " + employee.getId()));

        Session esession = session.getSessionForClass(org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
        esession.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update " + appendString + "\u304a\u3059\u305f set \u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048 = null where \u3059\u3042\u305b\u3042\u304d\u304a\u3064_\u3051\u3048 = " + employee.getId()));
    }

    protected void setup() {
        super.setup();
        deleteDependencies(getAbstractSession(), (org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee)getOriginalObject());
    }
}
