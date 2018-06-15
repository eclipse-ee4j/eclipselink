/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.direct;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ListToStringAdapter extends XmlAdapter<java.lang.String, List<String>> {
    public String marshal(List<String> value) {
        String string = "";
        for(int i = 0; i < value.size(); i++) {
            String next = value.get(i);
            string += next;
            if(i + 1 < value.size()) {
                string +=",";
            }
        }
        return string;
    }

    public List<String> unmarshal(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        ArrayList<String> list = new ArrayList(tokenizer.countTokens());
        while(tokenizer.hasMoreTokens()) {
            String next = tokenizer.nextToken();
            list.add(next);
        }
        return list;
    }

}
