/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;


/**
 * Ensure sessions xml can correctly load struct converters
 *
 */
public class StructConverterSessionsXMLTest extends AutoVerifyTestCase {

    protected DatabaseSession session = null;

    public static final String SPATIAL_SESSION_NAME = "spatial-session";
    public static final String SPATIAL_SESSIONS_XML_NAME = "org/eclipse/persistence/testing/models/spatial/jgeometry/spatial-sessions.xml";

    public void test() {
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader(SPATIAL_SESSIONS_XML_NAME);

        // log in the session
            session = (DatabaseSession)SessionManager.getManager().getSession(loader, SPATIAL_SESSION_NAME, getClass().getClassLoader(), true, false); // refresh the session  
    }

    public void verify() {
        if (!session.getPlatform().getStructConverters().containsKey("MDSYS.SDO_GEOMETRY")) {
            throw new TestErrorException("Struct Converter not added for MDSYS.SDO_GEOMETRY");
        }
        if (!session.getPlatform().getTypeConverters().containsKey(oracle.spatial.geometry.JGeometry.class)) {
            throw new TestErrorException("Type Converter not added for oracle.spatial.geometry.JGeometry.class");
        }
        // not checking for MY_GEOMETRY struct converter because it is added in the form "<username>.MY_GEOMETRY" and we do not necessarily know what 
        // that is here.
        if (!session.getPlatform().getTypeConverters().containsKey(org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.MyGeometry.class)) {
            throw new TestErrorException("Type Converter not added for org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.MyGeometryConverter.class");
        }
    }

    public void reset() {
        SessionManager.getManager().getSessions().remove(SPATIAL_SESSION_NAME);
    }
}
