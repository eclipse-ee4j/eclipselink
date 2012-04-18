/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SuperClassOverrideTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/superclassoverride/control.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/superclassoverride/control.json";
    
    public SuperClassOverrideTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{SubClass.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        SubClass subClass = new SubClass();
        subClass.setMyProperty("My Value");
        
        return subClass;
    }
    
    @Override
    protected Map getProperties() {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/superclassoverride/eclipselink-oxm.xml");

        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        return properties;    
    }
}
