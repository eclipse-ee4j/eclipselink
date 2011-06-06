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
 *     Denise Smith - November 11, 2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.multiplepackage.packagea;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rootA", namespace="namespaceA")
public class ClassA {
	@XmlElement(name="id")
	public int id;
	
	public String toString(){
		return "ClassA: " + id;
	}

	public boolean equals(Object object) {
		ClassA obj = ((ClassA)object);
		if(obj.id != this.id){
			return false;
		}		
		return true;
	}
}
