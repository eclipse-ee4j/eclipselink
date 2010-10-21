/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.direct.objectlist;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ObjectListAdapter extends XmlAdapter<String, List<PhoneNumber>> {

    @Override
    public String marshal(List<PhoneNumber> list) throws Exception {
        String string = "";
        for(PhoneNumber phoneNumber : list) {
            string += "," + phoneNumber.value;
        }
        return string.substring(1);
    }

    @Override
    public List<PhoneNumber> unmarshal(String string) throws Exception {
        StringTokenizer st = new StringTokenizer(string, ",");
        List<PhoneNumber> list = new ArrayList<PhoneNumber>();
        while(st.hasMoreTokens()) {
            list.add(new PhoneNumber(st.nextToken()));
        }
        return list;
    }

}