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
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address implements Serializable  {
	public Number id;
	public String address;


public static Address example1()
{
	Address example = new Address();
	
	example.setAddress("1-1129 Meadowlands, Ottawa");
	return example;
}
public static Address example2()
{
	Address example = new Address();
	
	example.setAddress("2-1120 Meadowlands, Ottawa");
	return example;
}
public static Address example3()
{
	Address example = new Address();
	
	example.setAddress("3-1130 Meadowlands, Ottawa");
	return example;
}
public static Address example4()
{
	Address example = new Address();
	
	example.setAddress("4-1130 Meadowlands, Ottawa");
	return example;
}
public static Address example5()
{
	Address example = new Address();
	
	example.setAddress("5-1130 Meadowlands, Ottawa");
	return example;
}
public static Address example6()
{
	Address example = new Address();
	
	example.setAddress("6-1130 Meadowlands, Ottawa");
	return example;
}
public static Address example7()
{
	Address example = new Address();
	
	example.setAddress("Address Changed");
	return example;
}
public void setAddress(String anAddress)
{
	this.address = anAddress;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("AGG_ADD");

	definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
	definition.addField("ADDRESS", String.class, 30);

	return definition;
}
}
