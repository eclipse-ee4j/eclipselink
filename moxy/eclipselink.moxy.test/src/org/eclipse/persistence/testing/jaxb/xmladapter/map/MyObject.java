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
//     Denise Smith - March 2, 2010
package org.eclipse.persistence.testing.jaxb.xmladapter.map;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlList;

public class MyObject {
    @XmlList
    List<String> keys;
    @XmlList
    List<String> values;

    public MyObject(){
        keys = new ArrayList<String>();
        values = new ArrayList<String>();
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
