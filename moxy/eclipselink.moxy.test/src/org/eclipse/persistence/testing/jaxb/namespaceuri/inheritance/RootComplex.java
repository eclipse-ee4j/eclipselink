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
 *     Oracle - December 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;

@XmlRootElement(name="root", namespace="rootNamespace")
public class RootComplex {

	public BaseType baseTypeThing;
	public List<BaseType > baseTypeList;
	public List objectList;
		
	@XmlElementWrapper(name="choice")
    @XmlElements({@XmlElement(name="integer-choice", type=Integer.class), @XmlElement(name="string-choice", type=String.class),@XmlElement(name="subtype-level2-choice", type=SubTypeLevel2.class), @XmlElement(name="another-package-subtype-choice", type=AnotherPackageSubType.class)})
	public List choiceList;
 	
	public RootComplex(){
		baseTypeList = new ArrayList<BaseType>();
		objectList = new ArrayList();
		choiceList = new ArrayList();
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof RootComplex)){
			return false;
		}
		
		if(baseTypeThing == null){
			return ((RootComplex)obj).baseTypeThing == null;
		}
		if(!baseTypeThing.equals(((RootComplex)obj).baseTypeThing)){
			return false;
		}
		
		if(!compareLists(baseTypeList, ((RootComplex)obj).baseTypeList)){
			return false;
		}
		if(!compareLists(objectList, ((RootComplex)obj).objectList)){
			return false;
		}
		
		if(!compareLists(choiceList, ((RootComplex)obj).choiceList)){
			return false;
		}
	
		return true;
	}
	
	private boolean compareLists(List list1, List list2){
		if(list1.size() != list2.size()){
			return false;
		}
		if(!list1.containsAll(list2)){
			return false;
		}
		if(!list2.containsAll(list1)){
			return false;
		}
		return true;
	}
}
