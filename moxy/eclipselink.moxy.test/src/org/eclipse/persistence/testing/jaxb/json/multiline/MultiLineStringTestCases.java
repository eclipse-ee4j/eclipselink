/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//    Martin Grebac - 2.6
package org.eclipse.persistence.testing.jaxb.json.multiline;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class MultiLineStringTestCases extends JSONMarshalUnmarshalTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/multiline/json.json";

    public MultiLineStringTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{MultiBean.class});
    }

    protected Object getControlObject() {
        MultiBean tb = new MultiBean();
        tb.setName("abcrr\n" + "\n" + "d");
        return tb;
    }

}
