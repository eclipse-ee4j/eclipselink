/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ListToStringAdapter extends XmlAdapter<String, List<String>> {

    @Override
    public String marshal(List<String> v) throws Exception {
        return "marshalled string value";
    }

    @Override
    public List<String> unmarshal(String v) throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.add("String1");
        list.add("String2");
        return list;
    }

}
