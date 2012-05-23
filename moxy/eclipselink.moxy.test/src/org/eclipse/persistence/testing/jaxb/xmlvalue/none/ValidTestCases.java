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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ValidTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/none/input.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvalue/none/input.json";

    public ValidTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {ValidChild.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
    protected ValidChild getControlObject() {
        ValidChild child = new ValidChild();
        child.setParentAttributeProperty("PARENT");
        child.setChildProperty("CHILD");
        Map anyAttributes = new HashMap();
        anyAttributes.put(new QName("anyAttr"), "something");
        child.setAnyAttributes(anyAttributes);
        return child;
    }
    
    public void testSchemaGen() throws Exception{
		InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlvalue/none/validChild.xsd");		
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);		
		this.testSchemaGen(controlSchemas);
	}

}