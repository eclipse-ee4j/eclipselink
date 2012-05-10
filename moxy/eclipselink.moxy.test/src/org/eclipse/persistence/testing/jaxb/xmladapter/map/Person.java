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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="theRoot")
public class Person {
    public String name;
    @XmlJavaTypeAdapter(MyObjectAdapter.class)
    public Map<Integer, String> mapTest;
	
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Integer, String> getMapTest() {
		return mapTest;
	}
	public void setMapTest(Map<Integer, String> mapTest) {
		this.mapTest = mapTest;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Person){
			Person personObject = (Person)obj;
			if(!name.equals(personObject.getName())){
				return false;
			}
			if(mapTest.size() != personObject.getMapTest().size()){
				return false;
			}
			Set<Entry<Integer, String>> entries = mapTest.entrySet();
			
			Iterator<Entry<Integer, String>> iter = entries.iterator();
			while(iter.hasNext()){
				Entry<Integer, String> next = iter.next();
				Object value = personObject.getMapTest().get(next.getKey());
				if(value == null || !next.getValue().equals(value)){
					return false;
				}
			}
			
			return true;
		}else{
			return false;
		}
	}
}
