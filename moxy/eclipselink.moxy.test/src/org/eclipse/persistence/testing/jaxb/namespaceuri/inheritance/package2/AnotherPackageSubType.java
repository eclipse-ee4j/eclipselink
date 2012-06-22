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
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2;

import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.SubType;

@XmlType(namespace = "uri3")
public class AnotherPackageSubType extends SubType{
	public boolean equals(Object obj){
		if(!(obj instanceof AnotherPackageSubType)){
			return false;
		}
		if(!super.equals(obj)){
			return false;
		}
		return true;
	}
}
