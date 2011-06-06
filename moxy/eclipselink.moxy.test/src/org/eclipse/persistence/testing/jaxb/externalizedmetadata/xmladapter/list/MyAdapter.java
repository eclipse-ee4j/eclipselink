/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class MyAdapter extends XmlAdapter<Object, String> {
    public static String VAL0 = "00";
    public static String VAL1 = "11";
    public static String VAL2 = "22";
    public static String EMPTY_STR = "";

    public MyAdapter() {}
	
	public String unmarshal(Object arg0) throws Exception {
	    String id = EMPTY_STR;
		if (arg0 instanceof Bar) {
			id = ((Bar)arg0).id;
		}
		return id;
    }

    public Object marshal(String arg0) throws Exception {
        Bar bar = new Bar();
        bar.id = arg0;
		return bar;
    }
}