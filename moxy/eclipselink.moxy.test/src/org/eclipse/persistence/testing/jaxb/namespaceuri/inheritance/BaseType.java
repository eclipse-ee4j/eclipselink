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
 *     Oracle - December 2011
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2.AnotherPackageSubType;

@XmlType(namespace = "someNamespace")
@XmlSeeAlso({
	SubType.class,
	AnotherSubType.class,
	SubTypeLevel2.class,
	AnotherPackageSubType.class
})
public class BaseType {
	
	@XmlElement(namespace="uri1")
	public String baseProp;
	
	public boolean equals(Object obj){
		if(!(obj instanceof BaseType)){
			return false;
		}
		if(baseProp == null){
			if(!(((BaseType)obj).baseProp == null)){
				return false;
			}
		}else if(!(baseProp.equals(((BaseType)obj).baseProp))){
			return false;
		}
		return true;
	}
}
