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
//     Denise Smith - September 2011 - 2.4
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class IncludeRootTrueWithXMLRootElementTestCases extends IncludeRootFalseWithXMLRootElementTestCases{

    public IncludeRootTrueWithXMLRootElementTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE_WITH_ROOT);
        setWriteControlJSON(JSON_RESOURCE_WITH_ROOT);
        setClasses(new Class[]{AddressWithRootElement.class});
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
      }

    @Override
    public Class getUnmarshalClass(){
        return Address.class;
    }

    public Object getReadControlObject() {
        QName name = new QName("addressWithRootElement");
        JAXBElement jbe = new JAXBElement<AddressWithRootElement>(name, AddressWithRootElement.class, (AddressWithRootElement)getControlObject());
        return jbe;
    }

    public void testJSONSchemaGeneration() throws Exception{
      //not yet supported
    }
}
