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
 * desmith - January 9th/2010- 2.0.1
 * ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringStringToIntegerIntegerMapAdapter extends XmlAdapter<Map<Integer, Integer>, Map<String, String>>{

    public Map<Integer, Integer> marshal(Map<String, String> v) throws Exception {
    	Map<Integer, Integer> theMap = new HashMap<Integer, Integer>();
    	Iterator<Map.Entry<String, String>> iter = v.entrySet().iterator();
    	while(iter.hasNext()){
    		Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
    		String key = entry.getKey();
    		String value = entry.getValue();

    		Integer newKey = getIntegerForString(key);
    		Integer newValue = getIntegerForString(value);
    		
    		theMap.put(newKey, newValue);
    	}
        return theMap;
    }

    public Map<String, String>unmarshal(Map<Integer, Integer> v) throws Exception {
    	Map<String, String> theMap = new HashMap<String, String>();
    	Iterator<Map.Entry<Integer, Integer>> iter = v.entrySet().iterator();
    	while(iter.hasNext()){
    		Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) iter.next();
    		Integer key = entry.getKey();
    		Integer value = entry.getValue();

    		String newKey = getStringForInteger(key);
    		String newValue = getStringForInteger(value);
    		
    		theMap.put(newKey, newValue);
    	}
        return theMap;
    }
    
    private Integer getIntegerForString(String theString){
    	Integer newInteger = 0;

		if(theString.equals("one")){
			newInteger = 1;
		}else if(theString.equals("two")){
			newInteger = 2;
		}else if(theString.equals("three")){
			newInteger = 3;
		}else if(theString.equals("four")){
			newInteger = 4;
		}
		return newInteger;
    }
    
    private String getStringForInteger(Integer theInteger){
    	String theString= "";

		if(theInteger.equals(1)){
			theString = "one";
		}else if(theInteger.equals(2)){
			theString = "two";
		}else if(theInteger.equals(3)){
			theString = "three";
		}else if(theInteger.equals(4)){
			theString = "four";
		}
		return theString;
    }
}
