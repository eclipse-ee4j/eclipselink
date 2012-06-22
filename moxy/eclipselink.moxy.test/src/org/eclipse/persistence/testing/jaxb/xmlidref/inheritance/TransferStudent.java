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
 *     Denise Smith - EclipseLink 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

public class TransferStudent extends Student{

	private String previousSchool;

	public String getPreviousSchool() {
		return previousSchool;
	}

	public void setPreviousSchool(String previousSchool) {
		this.previousSchool = previousSchool;
	}
	
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		boolean equals = super.equals(obj);
		if(!equals){
			return false;
		}
		if(obj instanceof TransferStudent){
			TransferStudent studentObject = (TransferStudent)obj;
		    if(previousSchool == null && studentObject.getPreviousSchool()!= null){
		    	return false;
		    }
		    return previousSchool.equals(studentObject.getPreviousSchool());
		}
		return false;
	}

}
