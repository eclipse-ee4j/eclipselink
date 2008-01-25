/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.currency;

import org.eclipse.persistence.tools.workbench.test.models.employee.Employee;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public class Currency
{
	private String unit;
	private float value;
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getUnit() {
	return unit;
}
/**
 * 
 * @return float
 */
public float getValue() {
	return value;
}
/**
 * 
 * @param newUnit java.lang.String
 */
public void setUnit(java.lang.String newUnit) {
	unit = newUnit;
}
/**
 * 
 * @param newValue float
 */
public void setValue(float newValue) {
	value = newValue;
}


public static void addToDescriptor(ClassDescriptor descriptor) {
	
}

public Employee buildEmployee() {
	return new Employee();
}
}
