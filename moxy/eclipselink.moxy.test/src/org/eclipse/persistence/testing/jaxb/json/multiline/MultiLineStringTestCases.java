/**
 * *****************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 which
 * accompanies this distribution. The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html and the Eclipse Distribution
 * License is available at http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: Martin Grebac - 2.6
 * ****************************************************************************
 */
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
