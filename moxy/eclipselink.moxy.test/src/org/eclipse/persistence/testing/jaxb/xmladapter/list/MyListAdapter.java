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
 * dmccann - December 30/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.list;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyListAdapter extends XmlAdapter<Object, List<String>> {
    public static String VAL0 = "00";
    public static String VAL1 = "11";
    public static String VAL2 = "22";
    public static String EMPTY_STR = "";
    public static String SPACE = " ";
    
    public MyListAdapter() {}
	
	public List<String> unmarshal(Object arg0) throws Exception {
	    List<String> list = new ArrayList<String>();
		if (arg0 instanceof Bar) {
		    String idstr = ((Bar)arg0).id;
		    StringTokenizer stok = new StringTokenizer(idstr, SPACE);
		    while (stok.hasMoreTokens()) {
	            list.add(stok.nextToken());
		    }
		}
        return list;
    }

    public Object marshal(List<String> arg0) throws Exception {
        Bar bar = new Bar();
        String id = EMPTY_STR;
        if (arg0.get(0).equals(VAL0)) {
			id += arg0.get(0);
		}
        if (arg0.get(1).equals(VAL1)) {
            id += SPACE + arg0.get(1);
        }
        if (arg0.get(2).equals(VAL2)) {
            id += SPACE + arg0.get(2);
        }
        bar.id = id;
        return bar;
    }
}