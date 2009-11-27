package org.eclipse.persistence.testing.models.jpa.xml.advanced;

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
