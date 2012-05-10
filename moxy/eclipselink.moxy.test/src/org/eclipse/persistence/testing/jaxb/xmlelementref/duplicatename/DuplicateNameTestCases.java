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
 *     mmacivor - 2010-03-09 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.duplicatename;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class DuplicateNameTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/duplicatename.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/duplicatename.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelementref/duplicatename.xsd";
    
    public DuplicateNameTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = ObjectFactory.class;
        setClasses(classes);
        

    }

    protected Object getControlObject() {
        BeanB bean = new BeanB();
        bean.value = new ObjectFactory().createBeanBValue();
        
        return bean;
     }
    
    public void testSchemaGen() throws Exception {
        InputStream controlInputStream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        
        List<InputStream> controlSchemas = new ArrayList<InputStream>();        
        controlSchemas.add(controlInputStream);
        testSchemaGen(controlSchemas);
    }      

}

