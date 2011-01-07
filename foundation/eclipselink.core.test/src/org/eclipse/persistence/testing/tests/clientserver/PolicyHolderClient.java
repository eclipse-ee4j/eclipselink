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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.insurance.Policy;

public class PolicyHolderClient extends Client2 {
    public PolicyHolderClient(Server server, Session session, String name) {
        super(server, session, name);
    }

    public void run() {
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression exp = builder.get("policyHolder").get("ssn").equal(1111);
            for (int i = 0; i < 1; i++) {
                Policy policy = (Policy)this.clientSession.readObject(org.eclipse.persistence.testing.models.insurance.Policy.class, exp);
                if ((policy == null) || (policy.getPolicyHolder() == null) || policy.getPolicyHolder().getLastName().equals("")) {
                    throw new TestWarningException("Client/Server dead lock test fails as null is returned");
                }
            }
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
