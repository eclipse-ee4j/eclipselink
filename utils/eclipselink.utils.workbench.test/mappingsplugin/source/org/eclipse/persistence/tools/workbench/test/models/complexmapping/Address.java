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

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address implements Serializable {
	public Number id;
	public String location;
	public Employee employee;
	public String province;

/**
 * This method was created by a SmartGuide.
 */
public Address ( ) 
{
	}
public Object copy() {
	return new Address();
}
public static Address example1()
{
	Address example = new Address();
//please keep the province in capitals	
	example.setLocation("OTTAWA");
	example.setProvince("ONTARIO");
	return example;
}
public static Address example2()
{
	Address example = new Address();
//Please keep the province in capitals 	
	example.setLocation("Montreal");
	example.setProvince("QUEBEC");
	return example;
}
public Employee getEmployee()
{
		return this.employee; 
}
public String getProvince()
{
		return this.province; 
}
public String getProvinceFromObject()
{
	String province = "";
	if (getProvince() == null) {
		return null;
	}	
	
	if (getProvince().equals("ONTARIO"))
		province = "ON";
	if (getProvince().equals("QUEBEC"))
		province = "QUE";
	return province;
}
public String getProvinceFromRow(Record row, Session aSession)
{
	String code = (String) row.get("PROVINCE");
	String provinceString = null;
	this.setEmployee((Employee)aSession.readObject(Employee.class));
	
	if (code == "ON")
		provinceString = new String("ONTARIO");
	if (code == "QUE")
		provinceString = new String("QUEBEC");
	return provinceString;
}

public void postBuild(DescriptorEvent event) {

}
public void postClone(DescriptorEvent event) {

}
public void postMerge(DescriptorEvent event) {

}

public void setEmployee(Employee anEmployee)
{
	this.employee = anEmployee; 
}
public void setLocation(String location)
{
	this.location = location; 
}
public void setProvince(String province)
{
	this.province = province; 
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition() 
{
	TableDefinition definition = new TableDefinition();

	definition.setName("MAP_ADD");

	definition.addIdentityField("A_ID", java.math.BigDecimal.class, 15);
	definition.addField("LOCATION", String.class, 15);
	definition.addField("PROVINCE", String.class, 3);
		return definition;
}
}
