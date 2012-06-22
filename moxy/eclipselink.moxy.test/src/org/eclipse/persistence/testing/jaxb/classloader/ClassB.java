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
 * Denise Smith - September 10 /2009
 ******************************************************************************/
 package org.eclipse.persistence.testing.jaxb.classloader;

import org.eclipse.persistence.testing.jaxb.employee.Employee;

public class ClassB {
	public String classBVariable;

	public String getClassBVariable() {
		return classBVariable;
	}

	public void setClassBVariable(String classBVariable) {
		this.classBVariable = classBVariable;
	}
	
	public boolean equals(Object object) {
		ClassB classBObject = ((ClassB)object);
		
		if(classBObject.getClassBVariable()== null && this.getClassBVariable() ==null){
			return true;
		}
		
		if(classBObject.getClassBVariable().equals(this.getClassBVariable())){
			return true;
		}
		return false;
	}
}
