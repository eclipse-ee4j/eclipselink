/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.events.AboutToInsertMultiTableObject;
import org.eclipse.persistence.testing.models.events.AboutToInsertSingleTableObject;
import org.eclipse.persistence.testing.framework.*;

/***
    This suite tests the event hook features
***/
public class EventHookTestSuite extends TestSuite {
    public EventHookTestSuite() {
        setName("Event Hook Test Suite");
        setDescription("This suite tests the pre and post delete/insert/update/write event hook features.");
    }

    public EventHookTestSuite(boolean isSRG) {
        super(isSRG);
        setName("Event Hook Test Suite");
        setDescription("This suite tests the pre and post delete/insert/update/write event hook features.");
    }

    public void addTests() {
        addSRGTests();
        //Add new tests here, if any.
        addTest(new PostCalculateUOWChangeSetEventTest());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(new UpdateEventHookTest());
        addTest(new InsertEventHookTest());
        addTest(new DeleteEventHookTest());
        addTest(new WriteEventHookTest());
        addTest(new RefreshEventHookTest());
        addTest(new BuildEventHookTest());
        addTest(new BuildOnRefreshEventHookTest());
        addTest(new CloneAndMergeEventHookTest());
        addTest(new CloneEventOnIsolatedSessionTest());
        addTest(new SessionEventTestCase());
        addTest(new SingleTableAboutToInsertTest(new AboutToInsertSingleTableObject(), false));
        addTest(new MultipleTableAboutToInsertTest(new AboutToInsertMultiTableObject(), false));
        addTest(new PreInsertModifyChangeSetTest());
        addTest(new ObjectChangeSetUpdateAttributeTest());
        addTest(new ObjectChangeSetEventTest());
        addTest(new UpdateAttributeTest());
    }
}
