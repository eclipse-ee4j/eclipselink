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
package org.eclipse.persistence.testing.tests.sessionsxml;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Tests sequencing loading in session xml file that is built and validated against the
 * XML Schema
 * 
 * @author Andrei Ilitchev
 * @version 1.0
 * @date June 11, 2004
 */
public class SessionsXMLSchemaSequencingTest extends AutoVerifyTestCase {
    final int numSessions = 4;
    DatabaseSession[] session = new DatabaseSession[numSessions];
    String[] sessionName = { "NoDefaultSequence_NoSequences", "DefaultSequence_NoSequences", "NoDefaultSequence_Sequences", "DefaultSequence_Sequences" };
    Sequence definedDefaultSequence;
    HashMap definedSequences = new HashMap();

    public SessionsXMLSchemaSequencingTest() {
        setDescription("Test loading of sequencing in session xml against the XML Schema");
        definedDefaultSequence = new TableSequence("", 25, "MY_SEQUENCE", "MY_SEQ_NAME", "MY_SEQ_COUNT");
        definedSequences.put("EMP_SEQ", new DefaultSequence("EMP_SEQ", 30));
        definedSequences.put("PROJ_SEQ", new NativeSequence("PROJ_SEQ", 35));
        definedSequences.put("ADDRESS_SEQ", new UnaryTableSequence("ADDRESS_SEQ", 40));
    }

    public void reset() {
        for (int i = 0; i < numSessions; i++) {
            if (session[i] != null) {
                SessionManager.getManager().getSessions().remove(session[i]);
                session[i] = null;
            }
        }
    }

    protected void setup() throws Exception {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/XMLSchemaSessionSequencing.xml");
        for (int i = 0; i < numSessions; i++) {
            session[i] = (DatabaseSession)SessionManager.getManager().getSession(loader, sessionName[i], getClass().getClassLoader(), false, true);
        }
    }

    protected void verify() {
        for (int i = 0; i < numSessions; i++) {
            boolean isDefaultSequenceDefined = sessionName[i].indexOf("NoDefaultSequence") == -1;
            boolean isSequencesDefined = sessionName[i].indexOf("NoSequences") == -1;
            boolean ok;
            if (isDefaultSequenceDefined) {
                ok = definedDefaultSequence.equals(session[i].getDatasourcePlatform().getDefaultSequence());
            } else {
                ok = !((DatasourcePlatform)session[i].getDatasourcePlatform()).hasDefaultSequence();
            }
            if (!ok) {
                throw new TestErrorException("Session " + sessionName[i] + " has wrong defaultSequence");
            }

            if (isSequencesDefined) {
                Iterator it = definedSequences.values().iterator();
                while (it.hasNext()) {
                    Sequence definedSequence = (Sequence)it.next();
                    Sequence sequence = session[i].getDatasourcePlatform().getSequence(definedSequence.getName());
                    ok = definedSequence.equals(sequence);
                    if (!ok) {
                        throw new TestErrorException("Session " + sessionName[i] + " has wrong " + sequence.getName() + " sequence");
                    }
                }
            } else {
                ok = session[i].getDatasourcePlatform().getSequences() == null || session[i].getDatasourcePlatform().getSequences().isEmpty();
                if (!ok) {
                    throw new TestErrorException("Session " + sessionName[i] + " has sequences, but it's not supposed to.");
                }
            }
        }
    }
}
