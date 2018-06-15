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
//     Denise Smith - 2011-08-25
package org.eclipse.persistence.testing.jaxb.json.attribute;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class JSONAttributePrefixOnMarshallerTestCases extends JSONAttributePrefixOnContextTestCases {

    public JSONAttributePrefixOnMarshallerTestCases(String name) throws Exception {
        super(name);

    }
    public void setUp() throws Exception{
        super.setUp();
        //this should override the properties set on the context in getProperties (called during inherited c-tor setClasses call)
        try{
            jsonMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
            jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        }catch (PropertyException e){
            e.printStackTrace();
            fail("an error occurred during setup");
        }
    }

    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "CONTEXT_PREFIX");
        return props;
    }


    @Override
    public void testJSONSchemaGen() throws Exception{
    }

}
