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
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import java.io.Serializable;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Company implements Serializable 
{
	public Number id;
	public String name;
	public ValueHolderInterface vehicles;
public Company()
{
	vehicles = new ValueHolder();
}
public static Company example1()
{
	Company example = new Company();
	Vector vehicle = new Vector();

	vehicle.addElement(Bus.example2(example));
	vehicle.addElement(Bicycle.example1(example));
	vehicle.addElement(Bus.example3(example));
	vehicle.addElement(Bus.example4(example));
	vehicle.addElement(NonFueledVehicle.example4(example));
			
	example.setName("TOP");
	example.getVehicles().setValue(vehicle);
	return example;
}
public static Company example2()
{
	Company example = new Company();
	Vector vehicle = new Vector();

	vehicle.addElement(Boat.example1(example));
	vehicle.addElement(Bicycle.example2(example));
	vehicle.addElement(Bus.example3(example));
	vehicle.addElement(FueledVehicle.example1(example));
	vehicle.addElement(NonFueledVehicle.example4(example));
		
	example.setName("ABC");
	example.getVehicles().setValue(vehicle);
	return example;
}
public static Company example3()
{
	Company example = new Company();
	Vector vehicle = new Vector();

	vehicle.addElement(Boat.example1(example));
	vehicle.addElement(Bicycle.example3(example));
	vehicle.addElement(Boat.example2(example));
	vehicle.addElement(Boat.example3(example));	
	vehicle.addElement(NonFueledVehicle.example4(example));
	
	example.setName("XYZ");
	example.getVehicles().setValue(vehicle);
	return example;
}
public ValueHolderInterface getVehicles()
{
	return vehicles;	
}
public void setName(String aName)
{
	name = aName;	
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("COMPANY");

	definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
	definition.addField("NAME", String.class, 30);

	return definition;
}
}
