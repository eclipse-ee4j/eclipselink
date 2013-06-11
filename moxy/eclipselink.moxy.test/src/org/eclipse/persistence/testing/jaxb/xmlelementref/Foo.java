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
*    Denise Smith - June 2013
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name ="foo")
public class Foo {

	@XmlElementRef(name = "things", type = JAXBElement.class)
	public List<JAXBElement<List<byte[]>>> things;
	
	public boolean equals(Object obj){
		if(obj instanceof Foo){
			Foo compareObj = (Foo)obj;
			if(things.size() != compareObj.things.size()){
				return false;
			}
			for(int i=0; i<things.size(); i++){
				JAXBElement<List<byte[]>> obj1 = things.get(i);
				JAXBElement<List<byte[]>> obj2 = compareObj.things.get(i);
				if(!obj1.getName().equals(obj2.getName())){
					return false;
				}
				//Uncomment when bug fixed - bug 410240
				//if(!obj1.getDeclaredType().equals(obj2.getDeclaredType())){
				//	return false;  
				//}
				List<byte[]> list1 = obj1.getValue();
				List<byte[]> list2 = obj2.getValue();
				if(list1.size() != list2.size()){
					return false;
				}
				for(int j=0; j<list1.size(); j++){
					byte[] next = list1.get(j);
					byte[] nextCompare = list2.get(j);
					if(!Arrays.equals(next, nextCompare)){
						return false;
					}
				}				
			}
			return true;
		}
		return false;
	}
}
