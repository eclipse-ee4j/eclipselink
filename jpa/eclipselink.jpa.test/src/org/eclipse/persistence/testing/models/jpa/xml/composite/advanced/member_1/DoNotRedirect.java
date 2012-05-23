/*******************************************************************************
 * Copyright (c) 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/19/2009-2.0 Gordon Yorke 
 *       - 239825: XML configuration for Interceptors and Default redirectors
 ******************************************************************************/ 
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
