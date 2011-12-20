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
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root", namespace="rootNamespace")
public class Root {

	public BaseType baseTypeThing;
	
	public boolean equals(Object obj){
		if(!(obj instanceof Root)){
			return false;
		}
		if(baseTypeThing == null){
			return ((Root)obj).baseTypeThing == null;
		}
		return baseTypeThing.equals(((Root)obj).baseTypeThing);
	}
}
