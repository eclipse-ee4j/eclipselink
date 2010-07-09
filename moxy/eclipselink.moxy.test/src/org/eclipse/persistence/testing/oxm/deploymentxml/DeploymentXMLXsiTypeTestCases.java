/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.deploymentxml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.Document;

public class DeploymentXMLXsiTypeTestCases extends XMLTestCase {
    public DeploymentXMLXsiTypeTestCases(String name) {
        super(name);
    }
    
    public void testDeploymentXmlConversion() {
       XMLProjectReader reader = new XMLProjectReader();
       XMLParser parser = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLParser();
       InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deploymentxml/db-adapter-toplink-mapping-file.xml");
       Project proj = reader.read(new InputStreamReader(stream));
       
       Document controlDoc = parser.parse(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deploymentxml/control_doc.xml"));
       
       StringWriter writer = new StringWriter();
       new XMLProjectWriter().write(proj, writer);
       
       Document testDoc = parser.parse(new StringReader(writer.toString()));
       
       super.assertXMLIdentical(controlDoc, testDoc);
    }

}
