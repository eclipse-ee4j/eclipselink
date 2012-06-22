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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import java.io.Serializable;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.ViewDefinition;

public class Vehicle implements Serializable 
{
	public Number id;
	public ValueHolderInterface owner;
	public Integer passengerCapacity;
	public Vector partNumbers;
public Vehicle() {
	this.owner = new ValueHolder();
	this.partNumbers = new Vector();
}
public void addPartNumber(String aPartNumber) {
	this.partNumbers.addElement(aPartNumber);
}
public void change()
{
	return;	
}
public ValueHolderInterface getOwner()
{
	return this.owner;	
}
/**
 * Return the view for Sybase.
 */

public static ViewDefinition oracleView()
{
	ViewDefinition definition = new ViewDefinition();

	definition.setName("AllVehicles");
	definition.setSelectClause("Select V.*, F.FUEL_CAP, F.FUEL_TYP, B.DESCRIP, C.CDESCRIP"
		+ " from VEHICLE V, FUEL_VEH F, BUS B, CAR C"
		+ " where V.ID = F.ID (+) AND V.ID = B.ID (+) AND V.ID = C.ID (+)");
	
	return definition;
}
public void setOwner(Company ownerCompany)
{
	this.owner.setValue(ownerCompany);	
}
public void setPassengerCapacity(Integer capacity)
{
	this.passengerCapacity = capacity;
}
/**
 * Return the view for Sybase.
 */

public static ViewDefinition sybaseView()
{
	ViewDefinition definition = new ViewDefinition();

	definition.setName("AllVehicles");
	definition.setSelectClause("Select V.*, F.FUEL_CAP, F.FUEL_TYP, B.DESCRIP, C.CDESCRIP"
		+ " from VEHICLE V, FUEL_VEH F, BUS B, CAR C"
		+ " where V.ID *= F.ID AND V.ID *= B.ID AND V.ID *= C.ID");
	
	return definition;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("VEHICLE");

	definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
	definition.addField("TYPE", java.math.BigDecimal.class, 15);
	definition.addField("CAPACITY", Integer.class);
	definition.addField("OWNER_ID", java.math.BigDecimal.class, 15);
	definition.addField("BICY_DES", String.class, 30);	

	return definition;
}
@Override
public String toString()
{
	return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + this.id + ")";
}
}
