/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - January 13/2010 - 2.0.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.typemappinginfo.rootelement;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.TypeMappingInfo.ElementScope;
import org.eclipse.persistence.testing.jaxb.typemappinginfo.TypeMappingInfoWithJSONTestCases;

public class RootFromAnnotationTestCases extends TypeMappingInfoWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/rootelement/Annotation.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/rootelement/Annotation.json";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/typemappinginfo/rootelement/Annotation.xsd";

    private static final String ANNOTATION_NAMESPACE_URI = null;
    private static final String ANNOTATION_LOCAL_NAME = "annotation-address";
    private static final String CONTROL_STREET = "123 A St.";

    public RootFromAnnotationTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        TypeMappingInfo[] typeMappingInfos = new TypeMappingInfo[1];
        TypeMappingInfo addressTypeMappingInfo = new TypeMappingInfo();
        addressTypeMappingInfo.setType(Address.class);
        addressTypeMappingInfo.setElementScope(ElementScope.Local);
        addressTypeMappingInfo.setAnnotations(new Annotation[0]);
        typeMappingInfos[0] = addressTypeMappingInfo;
        setTypeMappingInfos(typeMappingInfos);
    }

    @Override
    protected Address getControlObject() {
        Address address = new Address();
        address.setStreet(CONTROL_STREET);
        return address;
    }

    @Override
    public Object getReadControlObject() {
        return new JAXBElement<Address>(new QName(ANNOTATION_NAMESPACE_URI, ANNOTATION_LOCAL_NAME), Address.class, getControlObject());
    }

    @Override
    public Map<String, InputStream> getControlSchemaFiles() {
        Map<String, InputStream> schemas = new HashMap<String, InputStream>(1);
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        schemas.put("", instream);
        return schemas;
    }

}
