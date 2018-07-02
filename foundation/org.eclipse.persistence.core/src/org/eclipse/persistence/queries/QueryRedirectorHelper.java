/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Gordon Yorke - EclipseLink Interceptors work (ER 219683)
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose:</b>
 * The purpose of this class is to provide the query redirector with some helper methods for
 * general EclipseLink operations (such as checking a query against the cache) which may not
 * occur because the query is being redirected.
 *
 * @author Gordon Yorke
 *
 */
public class QueryRedirectorHelper {

    /**
     * This method will cause EclipseLink to check the EclipseLink cache for the object.  EclipseLink
     * always checks the shared cache before executing a query but because the query has been redirected
     * in this case the cache check has also been redirected.  Through this method the redirector can have
     * EclipseLink check the cache in the normal manner.
     */
    public static Object checkEclipseLinkCache(DatabaseQuery query, Record record, Session session){
        return query.checkEarlyReturn((AbstractSession)session, (AbstractRecord)record);
    }

}
