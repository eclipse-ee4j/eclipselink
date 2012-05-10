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

import javax.xml.bind.annotation.XmlTransient;

public class ClassBSubClass extends ClassB {
	private String extraString;

	public String getExtraString() {
		return extraString;
	}

	public void setExtraString(String extraString) {
		this.extraString = extraString;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof ClassBSubClass)){
			return false;
		}
		ClassBSubClass classBObj = (ClassBSubClass)obj;
		if(!super.equals(obj)){
			return false;
		}
		if(extraString == null){
			if(classBObj.getExtraString() != null){
				return false;
			}
		}else{
			if(classBObj.getExtraString() == null){
				return false;
			}
			if(!extraString.equals(classBObj.getExtraString())){
				return false;
			}
		}	
		
		return true;
	}
}
