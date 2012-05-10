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
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import java.util.ArrayList;
import java.util.List;

public class ClassC {
	public String classCValue;

	public ClassC(){
	}
	
	public String getClassCValue() {
		return classCValue;
	}

	public void setClassCValue(String classCValue) {
		this.classCValue = classCValue;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof ClassC)){
			return false;
		}
		ClassC classCObj = (ClassC)obj;
		
		if(classCValue == null){
			if(classCObj.getClassCValue() != null){
				return false;
			}
		}else{
			if(classCObj.getClassCValue() == null){
				return false;
			}
			if(!getClassCValue().equals(classCObj.getClassCValue())){
				return false;
			}
		}		
		
		return true;
	}
}
