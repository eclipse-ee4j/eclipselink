/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     11/19/2009-2.0 Gordon Yorke
//       - 239825: XML configuration for Interceptors and Default redirectors
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class DoNotRedirect implements QueryRedirector {

    public Object invokeQuery(DatabaseQuery query, Record arguments, Session session) {
        query.setDoNotRedirect(true);
        return ((AbstractSession)session).executeQuery(query, (AbstractRecord)arguments);
    }

}
