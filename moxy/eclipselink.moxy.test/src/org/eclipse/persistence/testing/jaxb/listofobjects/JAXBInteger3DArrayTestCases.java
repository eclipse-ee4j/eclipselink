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
*     bdoughan - April 22/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBInteger3DArrayTestCases extends JAXBListOfObjectsTestCases {

    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integer3DArray.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/integer3DArray.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/integer3DArray.xml";

    public JAXBInteger3DArrayTestCases(String name) throws Exception {
        super(name);
        init();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Integer[][][].class;
        setClasses(classes);
    }


    public List< InputStream> getControlSchemaFiles(){
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/integer3DArray.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected Type getTypeToUnmarshalTo() {
        return Integer[][][].class;
    }

    protected Object getControlObject() {
        int level1Size = 2;
        int level2Size = 3;
        int level3Size = 4;
        int counter = 0;
        int[][][] my3DArray = new int[level1Size][level2Size][level3Size];
        for(int x=0; x<level1Size; x++) {
            for(int y=0; y<level2Size; y++) {
                for(int z=0; z<level3Size; z++) {
                    my3DArray[x][y][z] = counter++;
                }
            }
        }
        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, my3DArray);
        return jaxbElement;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }

}
