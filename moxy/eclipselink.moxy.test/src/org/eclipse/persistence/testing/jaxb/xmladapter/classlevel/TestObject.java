/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 10 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="testObject")
public class TestObject {
	private ClassB classBObject;
	private List<ClassB> classBCollection;
	private ClassBSubClass subClassObject;
	@XmlJavaTypeAdapter(ClassCtoClassBAdapter.class)
	public ClassB anotherClassBObject;
	
	public TestObject(){
		classBCollection = new ArrayList<ClassB>();
	}
	
	public ClassB getClassBObject() {
		return classBObject;
	}

	public void setClassBObject(ClassB classBObject) {
		this.classBObject = classBObject;
	}		

	public List<ClassB> getClassBCollection() {
		return classBCollection;
	}

	public void setClassBCollection(List<ClassB> classBCollection) {
		this.classBCollection = classBCollection;
	}
	
	public boolean equals(Object theObject){
		if(!(theObject instanceof TestObject)){
			return false;
		}
		if(!getClassBObject().equals(((TestObject)theObject).getClassBObject())){
			return false;
		}
		if(!getSubClassObject().equals(((TestObject)theObject).getSubClassObject())){
			return false;
		}
		
		if(getClassBCollection().size() != ((TestObject)theObject).getClassBCollection().size()){
			return false;
		}
		for(int i=0;i<getClassBCollection().size(); i++){
			if(!getClassBCollection().get(i).equals(((TestObject)theObject).getClassBCollection().get(i))){
				return false;
			}
		}
		
		return true;
	}

	public ClassBSubClass getSubClassObject() {
		return subClassObject;
	}

	public void setSubClassObject(ClassBSubClass subClassObject) {
		this.subClassObject = subClassObject;
	}
	
}
