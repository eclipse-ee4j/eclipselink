/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - January 11/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlEnum via eclipselink-oxm.xml
 *
 */
public class XmlEnumQualifiedTestCases extends JAXBWithJSONTestCases{
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/game.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/game.json";
    /**
     * This is the preferred (and only) constructor.
     *
     * @param name
     * @throws Exception
     */
    public XmlEnumQualifiedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Game.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

     public Map getProperties(){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/eclipselink-oxm-game-qualified.xml");

            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum", new StreamSource(inputStream));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

            return properties;
        }

        public void testSchemaGen() throws Exception{
            List controlSchemas = new ArrayList();
            InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/game-schema.xsd");
            controlSchemas.add(is);
            super.testSchemaGen(controlSchemas);

        }

        public void testInstanceDocValidation() {
            InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlenum/game-schema.xsd");
            StreamSource schemaSource = new StreamSource(schema);

            InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            String result = validateAgainstSchema(instanceDocStream, schemaSource);
            assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        }

        @Override
        protected Object getControlObject() {
            // setup control objects
            Game gameCtrl = new Game();
            gameCtrl.card = Card.DIAMONDS;
            gameCtrl.coin = Coin.DIME;
            return gameCtrl;
        }
}
