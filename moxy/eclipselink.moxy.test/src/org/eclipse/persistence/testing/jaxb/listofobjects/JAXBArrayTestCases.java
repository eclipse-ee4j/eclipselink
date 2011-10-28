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
 *     Denise Smith  - October 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class JAXBArrayTestCases extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/arrays.xml";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/arraysNoXsiType.xml";

    public JAXBArrayTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[4];
        classes[0] = char[].class;
        classes[1] = BigDecimal[].class;
        classes[2] = BigInteger[].class;
        classes[3] = QName[].class;
        setClasses(classes);

    }

    protected Object getControlObject() {
        BigInteger[] bigIntegers = new BigInteger[4];
        bigIntegers[0] = new BigInteger("1");
        bigIntegers[1] = new BigInteger("2");
        bigIntegers[2] = new BigInteger("3");
        bigIntegers[3] = new BigInteger("4");

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(bigIntegers);

        return jaxbElement;
    }

    public List<InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/arrays.xsd");
        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        return BigInteger[].class;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }

    public void testConflict() {
        try {
            Class[] classes = new Class[2];
            classes[0] = Integer[].class;
            classes[1] = int[].class;
            JAXBContextFactory.createContext(classes, null);
        } catch(JAXBException e) {
            return;
        }
        fail();
    }

}