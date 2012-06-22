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
package org.eclipse.persistence.tools.workbench.test.models.complexmapping;

import java.io.Serializable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Monitor extends Hardware implements Serializable {
	public String brand;
	public int size;
	public String serialNumber;
	public Computer computer;
public Monitor() {
	setSerialNumber(new String());
	setDist("false");
}
	public static void addToDescriptor(ClassDescriptor des){
	org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey parentKey = new org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey();
	parentKey.setName("computerKey");
	parentKey.setReferenceClass(Computer.class);
	org.eclipse.persistence.expressions.ExpressionBuilder parentBuilder = new org.eclipse.persistence.expressions.ExpressionBuilder();
	parentKey.setJoinCriteria(parentBuilder.getField("MAP_COM.MON_SER").equal(parentBuilder.getParameter("MAP_MON.SERL_NO")));
	des.addQueryKey(parentKey);
	}
@Override
public boolean equals(Object monitor)
{
	return getBrand().equals(((Monitor)monitor).getBrand());
}
public static Monitor example1() {
	Monitor example = new Monitor();
	
	example.setBrand("Daewoo");
	example.setSize(15);
	example.setSerialNumber("119383-12983-H11");
	return example;
}
public static Monitor example2() {
	Monitor example = new Monitor();
	
	example.setBrand("MAG Innovision");
	example.setSize(17);
	example.setSerialNumber("268551-127611223");
	return example;
}
public static Monitor example3() {
	Monitor example = new Monitor();
	
	example.setBrand("Sony");
	example.setSize(17);
	example.setSerialNumber("28376HSI-182J-11WWQ");
	return example;
}
public static Monitor example4() {
	Monitor example = new Monitor();
	
	example.setBrand("Daewoo");
	example.setSize(19);
	example.setSerialNumber("423234-21551-T24");
	return example;
}
public static Monitor example5() {
	Monitor example = new Monitor();
	
	example.setBrand("Sony");
	example.setSize(21);
	example.setSerialNumber("37628TEY-153G-53EER");
	return example;
}
public static Monitor example6() {
	Monitor example = new Monitor();
	
	example.setBrand("Viewsonic");
	example.setSize(15);
	example.setSerialNumber("12874-128762");
	return example;
}
public static Monitor example7() {
	Monitor example = new Monitor();
	
	example.setBrand("Sony");
	example.setSize(19);
	example.setSerialNumber("847564-126GYG");
	return example;
}
public static Monitor example8() {
	Monitor example = new Monitor();
	
	example.setBrand("Acer");
	example.setSize(19);
	example.setSerialNumber("8763-823762");
	return example;
}
public static Monitor example9() {
	Monitor example = new Monitor();
	
	example.setBrand("Micron SVGA");
	example.setSize(21);
	example.setSerialNumber("2861-192872781-12");
	return example;
}
public String getBrand() {
	return brand;
}
public Computer getComputer() {
	return computer;
}
public String getSerialNumber() {
	return serialNumber;
}
public int getSize() {
	return size;
}
@Override
public int hashCode() {
	return getSerialNumber().hashCode();
}
public void setBrand(String brand) {
	this.brand = brand;
}
public void setComputer(Computer computer) {
	this.computer = computer;
}
public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
}
public void setSize(int size) {
	this.size = size;
}
public static TableDefinition tableDefinition() {
	TableDefinition definition = new TableDefinition();

	definition.setName("MAP_MON");

	definition.addIdentityField("ID",java.math.BigDecimal.class, 15);
	definition.addField("MSIZE", Integer.class);
	definition.addField("BRAND", String.class, 30);
	definition.addField("SERL_NO", String.class, 30);
	definition.addField("COM_SER", String.class, 30);
	definition.addForeignKeyConstraint("ComputerRef", "COM_SER", "SERL_NO", "MAP_COM");
	return definition;
}
}
