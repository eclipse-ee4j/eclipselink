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
 *     Denise Smith - November 11, 2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.multiplepackage.packageb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.multiplepackage.packagea.ClassA;

@XmlRootElement(name="rootB", namespace="namespaceB")
public class ClassB {
	@XmlElement(name="id")
	public int id;
	
	public String toString(){
		return "ClassB: " + id;
	}

	public boolean equals(Object object) {
		ClassB obj = ((ClassB)object);
		if(obj.id != this.id){
			return false;
		}		
		return true;
	}
}
