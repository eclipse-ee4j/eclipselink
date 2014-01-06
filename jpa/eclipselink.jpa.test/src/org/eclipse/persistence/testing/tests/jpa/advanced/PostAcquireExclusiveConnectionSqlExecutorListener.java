package org.eclipse.persistence.testing.tests.jpa.advanced;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class PostAcquireExclusiveConnectionSqlExecutorListener extends
SessionEventAdapter{
    String sqlToExecute = "SELECT * FROM CMP3_VEGETABLE WHERE VEGETABLE_NAME='nonexistent'";

    public void setSqlToExecute(String sqlString) {
        this.sqlToExecute = sqlString;
    }

    public String getSqlToExecute() {
        return this.sqlToExecute;
    }

    public void postAcquireExclusiveConnection(SessionEvent event) {
        event.getSession().executeSelectingCall(new SQLCall(sqlToExecute));
    }
}
