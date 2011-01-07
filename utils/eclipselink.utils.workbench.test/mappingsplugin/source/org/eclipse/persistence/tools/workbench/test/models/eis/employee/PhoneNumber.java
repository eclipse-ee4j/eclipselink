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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.eis.employee;

import java.io.Serializable;

public class PhoneNumber 
	implements Serializable 
{
	/** Direct Mapping */
	private String type;
	
	/** Direct Mapping (typed) */
	private Object areaCode;
	
	/** Direct Mapping (typed) */
	private Object number;
	
	
	public PhoneNumber() {
		super();
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Object getAreaCode() {
		return this.areaCode;
	}
	
	public void setAreaCode(Object areaCode) {
		this.areaCode = areaCode;
	}
	
	public Object getNumber() {
		return this.number;
	}
	
	public void setNumber(Object number) {
		this.number = number;
	}
}
