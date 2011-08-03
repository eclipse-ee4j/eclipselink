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
 *     Denise Smith - EclipseLink 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

public class Student extends Person {

	private String studentNumber;	 

    public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}
	
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		}
		boolean equals = super.equals(obj);
		if(!equals){
			return false;
		}
		if(obj instanceof Student){
			Student studentObject = (Student)obj;
		    if(studentNumber == null && studentObject.getStudentNumber()!= null){
		    	return false;
		    }
		    return studentNumber.equals(studentObject.getStudentNumber());
		}
		return false;
	}

}
