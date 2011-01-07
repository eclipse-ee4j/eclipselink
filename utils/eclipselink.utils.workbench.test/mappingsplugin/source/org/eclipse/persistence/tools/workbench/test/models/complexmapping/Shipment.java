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
package org.eclipse.persistence.tools.workbench.test.models.complexmapping;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Shipment implements Serializable, Cloneable
{
	public Timestamp creationTimestamp;
	public int creationTimestampMillis;
	public String quantityShipped;
	public String shipMode;
	public Vector employees;
public Shipment()
{
	this.creationTimestamp = new Timestamp(System.currentTimeMillis());
	try { // Sleep to ensure milliseconds is ok.
		Thread.currentThread().sleep(10);
	} catch (InterruptedException exception) {}	
	this.employees = new Vector();
}
/**
 * NOTE: this should not be needed.  This is only 
 * here because there seems to be a VisualAge bug 
 * that can't reflectively call superclass methods.
 */
 
@Override
public Object clone() {
	try {
		return super.clone();
	} catch (CloneNotSupportedException exception) {
	}
	return null;
}
public static Shipment example1()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "1 ton";
	example.shipMode = "Air";	

	return example;
}
public static Shipment example2()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "2 ton";
	example.shipMode = "Air";
	
	return example;
}
public static Shipment example3()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "3 ton";
	example.shipMode = "Ship";
	
	return example;
}
public static Shipment example4()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "4 ton";
	example.shipMode = "Ship";
	
	return example;
}
public static Shipment example5()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "6 ton";
	example.shipMode = "Plane";
	
	return example;
}
public static Shipment example6()
{
	Shipment example = new Shipment();
	
	example.quantityShipped = "2 kg";
	example.shipMode = "Pony Express";
	
	return example;
}
public void prepareForInsert(Session session) {
	long millis = System.currentTimeMillis();
	this.creationTimestamp = new Timestamp(millis);
	if (session.getPlatform().isAccess() || session.getPlatform().isSQLServer() 
			|| session.getPlatform().isOracle() || session.getPlatform().isSybase()) {
		// Oracle does not support millis, Sybase stores them only within 1-2 millis...
		this.creationTimestampMillis = this.creationTimestamp.getNanos();
		this.creationTimestamp.setNanos(0);
		try {
			Thread.currentThread().sleep(50);
		} catch (InterruptedException exception) {}	
	}	
}
public void removeEmployee(Employee employee) 
{
	this.employees.removeElement(employee);	
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("MAP_SHIP");

	definition.addPrimaryKeyField("SP_TS", Timestamp.class);
	definition.addPrimaryKeyField("SP_TSMIL", Integer.class);
	definition.addField("QUANTITY", String.class, 20);
	definition.addField("SHP_MODE", String.class, 50);
	
	return definition;
}
}
