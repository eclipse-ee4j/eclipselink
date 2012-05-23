/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.oxm.XMLTestCase;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DeploymentXMLXsiTypeTestCases extends XMLTestCase {
    public DeploymentXMLXsiTypeTestCases(String name) {
        super(name);
    }
    
    public void testDeploymentXmlConversion() {
       XMLProjectReader reader = new XMLProjectReader();
       XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
       XMLParser parser = xmlPlatform.newXMLParser();
       InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/deploymentxml/db-adapter-toplink-mapping-file.xml");
       Project proj = reader.read(new InputStreamReader(stream));
       
       StringWriter writer = new StringWriter();
       new XMLProjectWriter().write(proj, writer);
       

       parser.setNamespaceAware(true);
       parser.setWhitespacePreserving(false);
       String schema = XMLProjectReader.SCHEMA_DIR + XMLProjectReader.ECLIPSELINK_SCHEMA;
       parser.setValidationMode(XMLParser.SCHEMA_VALIDATION);
       URL eclipselinkSchemaURL = getClass().getClassLoader().getResource(schema);
       parser.setEntityResolver(new EntityResolver() {
            
           public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
               if (XMLProjectReader.OPM_SCHEMA.equals(systemId)) {
                   URL url = getClass().getClassLoader().getResource(XMLProjectReader.SCHEMA_DIR + XMLProjectReader.OPM_SCHEMA);
                   if (null == url) {
                       return null;
                   }
                   return new InputSource(url.openStream());
               }
               return null;
           }
       }); 
       parser.setXMLSchema(eclipselinkSchemaURL);
       parser.parse(new StringReader(writer.toString()));
       
    }

}
