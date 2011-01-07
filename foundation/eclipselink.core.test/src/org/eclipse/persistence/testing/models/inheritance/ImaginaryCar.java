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
package org.eclipse.persistence.testing.models.inheritance;

public class ImaginaryCar extends Car {
/**
 * This method was created in VisualAge.
 */
public ImaginaryCar() {
	super();
}
public static Car example1()
{
	ImaginaryCar example = new ImaginaryCar();
	
	example.setPassengerCapacity(new Integer(2));
	example.setFuelCapacity(new Integer(30));
	example.setDescription("PONTIAC");
	example.setFuelType("Petrol");
	example.addPartNumber("021776RM-b");
	example.addPartNumber("122500JC-s");
	example.addPartNumber("101101BI-n");
	return example;
}
public static Car example2()
{
	ImaginaryCar example = new ImaginaryCar();
	
	example.setPassengerCapacity(new Integer(4));
	example.setFuelCapacity(new Integer(50));
	example.setDescription("TOYOTA");
	example.setFuelType("Petrol");
	example.addPartNumber("021776TT-a");
	example.addPartNumber("122500RF-g");
	example.addPartNumber("101101ML-m");
	return example;
}
public static Car example3()
{
	ImaginaryCar example = new ImaginaryCar();
	
	example.setPassengerCapacity(new Integer(5));
	example.setFuelCapacity(new Integer(60));
	example.setDescription("BMW");
	example.setFuelType("Disel");
	example.addPartNumber("021776KM-k");
	example.addPartNumber("122500MP-k");
	example.addPartNumber("101101MP-d");
	return example;
}
public static Car example4()
{
	Car example = new Car();
	
	example.setPassengerCapacity(new Integer(8));
	example.setFuelCapacity(new Integer(100));
	example.setDescription("Mazda");
	example.setFuelType("Coca-Cola");
	example.addPartNumber("021776KM-k");
	example.addPartNumber("122500MP-k");
	example.addPartNumber("101101MP-d");
	return example;
}

}
