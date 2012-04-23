/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas.SetXmlSchemaTestCases;

public class ListOfStringArrayTestCases extends JAXBListOfObjectsTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/listOfStringArray.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/listOfStringArray.json";

    private List<String[]> listOfStringArray;

    public ListOfStringArrayTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Field listOfStringArrayField = PrivilegedAccessHelper.getField(ListOfStringArrayTestCases.class, "listOfStringArray", true);
        Type[] types = new Type[1];
        types[0] = listOfStringArrayField.getGenericType();
        setTypes(types);
    }

    @Override
    protected Object getControlObject() {
        List<String[]> listOfStringArray = new ArrayList<String[]>(2);

        String[] stringArray1 = new String[2];
        stringArray1[0] = "foo";
        stringArray1[1] = "bar";
        listOfStringArray.add(stringArray1);

        String[] stringArray2 = new String[3];
        stringArray2[0] = "A";
        stringArray2[1] = "B";
        stringArray2[2] = "C";
        listOfStringArray.add(stringArray2);
        
        QName qname = new QName("root");
        return new JAXBElement(qname, Object.class, listOfStringArray);
    }


    @Override
    public List<InputStream> getControlSchemaFiles() {
        List<InputStream> controlSchema = new ArrayList<InputStream>(2);
        controlSchema.add(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/listOfStringArray1.xsd"));
        controlSchema.add(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/listOfStringArray2.xsd"));
        return controlSchema;
    }

    @Override
    protected Type getTypeToUnmarshalTo() throws Exception {
        return types[0];
    }

    @Override
    protected String getNoXsiTypeControlResourceName() {
        return null;
    }

}