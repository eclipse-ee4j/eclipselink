/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.adapter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class JsonMapAdapterTestCases extends JSONMarshalUnmarshalTestCases {
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/adapter/adapter.json";

    public JsonMapAdapterTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{RootObject.class});     
    }

    protected Object getControlObject() {
        RootObject ro = new RootObject();
        ro.setTitle("title");
        ro.setData(new HashMap<String, Object>());
        ro.getData().put("foo1", "bar1");
        ro.getData().put("foo2", "bar2");

        return ro;
    }
    
    public Map getProperties(){
        Map props = new HashMap();
        props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "");
        return props;
    }
        
}
