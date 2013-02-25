/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Denise Smith - February 25, 2013
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.interfaces.choice;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ListmyObject", namespace = "someNamespace")
@XmlType(name = "ListmyObject", namespace = "someNamespace")
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {
	
	/*
	 * customer is using a List and specifies the Interface ImyObject that just contains 
	 * getters and setters
	 */
	@XmlElements(value = { @XmlElement(name = "myObject", type = MyObject.class), @XmlElement(name = "otherObject", type = MyOtherObject.class) })
	protected List<MyInterface> myList = new ArrayList<MyInterface>();

	public List<MyInterface> getMyList() {
		return myList;
	}

	public void setMyList(List<MyInterface> m_listMyObject) {
		this.myList = m_listMyObject;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof Root){
			Root compare = (Root)obj;
			return myList.equals(compare.myList);
		}
		return false;
	}
}
