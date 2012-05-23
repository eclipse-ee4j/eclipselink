package org.eclipse.persistence.testing.tests.plsqlrecord;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

public class OutputRowSessionEventAdapter extends SessionEventAdapter {

    Object eventResult = null;

    public void outputParametersDetected(SessionEvent event) {
        eventResult = event.getResult();
    }

    public Object getEventResult() {
        return eventResult;
    }
}
