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
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmltype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlTypeNameTestCases extends JAXBTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmltype/page.xml";
	private final static String XSD_RESOURCE0 = "org/eclipse/persistence/testing/jaxb/xmltype/page0.xsd";
    private final static String XSD_RESOURCE1 = "org/eclipse/persistence/testing/jaxb/xmltype/page1.xsd";
	private final static String VALUE = "true";

    public XmlTypeNameTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[2];
        classes[0] = GetPageResponse.class;
        classes[1] = A2BCDESomeTest.class; //complex type for this should be "a2BCDESomeTest"
        setClasses(classes);
    }

    protected Object getControlObject() {
        Page returnPage = new Page();
        returnPage.setIsEmailLinkRequired(VALUE);
        GetPageResponse gpr = new GetPageResponse();
        gpr.setReturn(returnPage);
        return gpr;
    }
    
    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is0 = getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE0);
        controlSchemas.add(is0);
        InputStream is1 = getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE1);
        controlSchemas.add(is1);
        super.testSchemaGen(controlSchemas);
    }
}
