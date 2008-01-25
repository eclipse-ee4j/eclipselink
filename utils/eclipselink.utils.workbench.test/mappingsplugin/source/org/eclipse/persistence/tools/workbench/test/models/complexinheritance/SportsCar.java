/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

public class SportsCar extends Car {
public static Car example1()
{
	SportsCar example = new SportsCar();
	
	example.setPassengerCapacity(new Integer(2));
	example.setFuelCapacity(new Integer(60));
	example.setDescription("Corvet");
	example.setFuelType("Disel");	
	return example;
}
}
