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
//     James Sutherland - initial impl
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.List;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.QueryRedirector;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class ArrayQueryRedirector implements QueryRedirector {
    public Object invokeQuery(DatabaseQuery query, Record record, Session session) {
        List result = (List)session.executeQuery(query);
        return result.toArray(new Object[result.size()]);
    }
}
