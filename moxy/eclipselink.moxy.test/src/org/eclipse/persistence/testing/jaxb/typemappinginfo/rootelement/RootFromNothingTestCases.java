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
*     bdoughan - January 13/2010 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoTestCases;

public class RootFromNothingTestCases extends TypeMappingInfoTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/rootelement/Nothing.xml";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/rootelement/Nothing.xsd";

    private static final String ADDRESS_NAMESPACE_URI = null;
    private static final String ADDRESS_LOCAL_NAME = null;
    private static final String ANNOTATION_NAMESPACE_URI = null;
    private static final String ANNOTATION_LOCAL_NAME = null;
    private static final String CONTROL_NUMBER = "613-555-1234";

    public RootFromNothingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        TypeMappingInfo[] typeMappingInfos = new TypeMappingInfo[1];
        TypeMappingInfo phoneNumberTypeMappingInfo = new TypeMappingInfo(); 
        phoneNumberTypeMappingInfo.setType(PhoneNumber.class); 
        phoneNumberTypeMappingInfo.setElementScope(ElementScope.Local); 
        phoneNumberTypeMappingInfo.setAnnotations(new Annotation[0]); 
        typeMappingInfos[0] = phoneNumberTypeMappingInfo;
        setTypeMappingInfos(typeMappingInfos);
    }

    @Override
    protected PhoneNumber getControlObject() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(CONTROL_NUMBER);
        return phoneNumber;
    }

    @Override
    public JAXBElement<PhoneNumber> getReadControlObject() {
        return new JAXBElement<PhoneNumber>(new QName("number"), PhoneNumber.class, new PhoneNumber());
    }

    @Override
    public Map<String, InputStream> getControlSchemaFiles() {
        Map<String, InputStream> schemas = new HashMap<String, InputStream>(1);
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        schemas.put("", instream);
        return schemas;
    }

}