/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - March 2, 2010
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MyObjectAdapter extends XmlAdapter<MyObject, HashMap<Integer, String>> {

	public MyObject marshal(HashMap<Integer, String> arg0) throws Exception {		
		MyObject myObject = new MyObject();
		
		Iterator<Entry<Integer, String>> iter = arg0.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, String> next = iter.next();			
			myObject.getKeys().add(next.getKey().toString());
			myObject.getValues().add(next.getValue());
		}
		Collections.sort(myObject.getKeys());
		Collections.sort(myObject.getValues());
		return myObject;
	}

	public HashMap<Integer, String> unmarshal(MyObject arg0) throws Exception {
		HashMap theMap = new HashMap<Integer, String>();
		List<String> keys = arg0.getKeys();
		List<String> values = arg0.getValues();
		for(int i=0; i<keys.size(); i++){
			Integer theInteger = Integer.parseInt(keys.get(i));
			theMap.put(theInteger, values.get(i));
		}
		return theMap;
	}

}
