/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

public class PolicyClientNonLock extends Client2 {
    public PolicyClientNonLock(Server server, Session session, String name) {
        super(server, session, name);
    }

    public void run() {
        try {
            ExpressionBuilder builder = new ExpressionBuilder();
            Expression exp = builder.anyOf("policies").get("policyNumber").equal(555);
            for (int i = 0; i < 1; i++) {
                PolicyHolder holder = (PolicyHolder)this.clientSession.readObject(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class, exp);
                if ((holder == null) || holder.getFirstName().equals("") || (holder.getPolicies() == null) || holder.getAddress().getCity().equals("")) {
                    throw new TestWarningException("Client/Server dead lock test fails as null/default attribuet value is retruned");
                }
            }
        } catch (Exception exception) {
            this.server.errorOccured = true;
            exception.printStackTrace(System.out);
        }
    }
}
