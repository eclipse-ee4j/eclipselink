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
package org.eclipse.persistence.tools.workbench.test.models.readonly;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address {
	public Number id;
	public String streetAddress;
	public String city;
	public String zipCode;
	public Country country;
// Address descriptor

public static ClassDescriptor descriptor() {
	ClassDescriptor descriptor = new ClassDescriptor();

	descriptor.setJavaClass(Address.class);
	descriptor.setTableName("RO_ADDR");
	descriptor.addPrimaryKeyFieldName("ADD_ID");
	descriptor.setSequenceNumberName("ADD_SEQ");
	descriptor.setSequenceNumberFieldName("ADD_ID");
	
	descriptor.addDirectMapping("id", "ADD_ID");
	descriptor.addDirectMapping("streetAddress", "STREET");
	descriptor.addDirectMapping("city", "CITY");
	descriptor.addDirectMapping("zipCode", "ZIP"); 

	OneToOneMapping countryMapping = new OneToOneMapping();
	countryMapping.setAttributeName("country");
	countryMapping.setReferenceClass(Country.class);
	countryMapping.addForeignKeyFieldName("COUNTRY_ID","COUNTRY_ID");
	countryMapping.dontUseIndirection();
	descriptor.addMapping(countryMapping);

	return descriptor;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null || getClass() != obj.getClass())
		return false;
	Address other = (Address) obj;
	return (getStreet().equals(other.getStreet()) && getCity().equals(other.getCity()) && getZipCode().equals(other.getZipCode()) && getCountry().equals(other.getCountry()));
}
public static Address example1() {
	Address example = new Address();

	example.setStreetAddress("14431 Skywalkwer Ave.");
	example.setCity("San Rafael, California");
	example.setZipCode("92313");
	example.setCountry(Country.canada());
	
	return example;
}
public static Address example2() {
	Address example = new Address();

	example.setStreetAddress("1431 Hollywood Blvd.");
	example.setCity("Hollywood, California");
	example.setZipCode("90212");
	example.setCountry(Country.czech());
	
	return example;
}
public static Address example3() {
	Address example = new Address();

	example.setStreetAddress("1976 Rodeo Dr.");
	example.setCity("Los Angeles, California");
	example.setZipCode("90211");
	example.setCountry(Country.india());
	
	return example;
}
public static Address example4() {
	Address example = new Address();

	example.setStreetAddress("5 Birch St");
	example.setCity("Kitchener, Ontario");
	example.setZipCode("M8K 9D4");
	example.setCountry(Country.russia());

	return example;
}
public static Address example5() {
	Address example = new Address();
	example.setStreetAddress("Any Street");
	example.setCity("Los Angeles, CA");
	example.setZipCode("90144");
	example.setCountry(Country.uk());

	return example;
}
public String getCity() {
	return this.city;
}
/**
 * This method was created in VisualAge.
 * @return org.eclipse.persistence.testing.ReadOnlyModel.Country
 */
public Country getCountry() {
	return this.country;
}
public String getStreet() {
	return this.streetAddress;
}
public String getZipCode() {
	return this.zipCode;
}
public void setCity(String city) {
	this.city = city;
}
/**
 * This method was created in VisualAge.
 * @param newValue org.eclipse.persistence.testing.ReadOnlyModel.Country
 */
public void setCountry(Country newValue) {
	this.country = newValue;
}
public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
}
public void setZipCode(String zipCode) {
	this.zipCode = zipCode;
}
// Address table definition

public static TableDefinition tableDefinition() {
	TableDefinition definition = new TableDefinition();

	definition.setName("RO_ADDR");

	definition.addIdentityField("ADD_ID", java.math.BigDecimal.class, 15);
	definition.addField("STREET", String.class, 30);
	definition.addField("CITY", String.class, 30);
	definition.addField("ZIP", String.class, 10);
	definition.addField("COUNTRY_ID", java.math.BigDecimal.class, 15);
	
	return definition;
}
@Override
public String toString() {
	java.io.StringWriter stringWriter = new java.io.StringWriter();
	java.io.PrintWriter writer = new java.io.PrintWriter(stringWriter);
	writer.print(org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()));
	writer.print("(");
	if (this.streetAddress == null)
		writer.print("null");
	else
		writer.print(this.streetAddress);
	writer.print(",");
	if (this.city == null)
		writer.print("null");
	else
		writer.print(this.city);
	writer.print(",");
	if (this.country == null)
		writer.print("null");
	else
		writer.print(this.country);
	writer.print(",");
	if (this.zipCode == null)
		writer.print("null");
	else
		writer.print(this.zipCode);
	writer.write(")");
	return stringWriter.toString();
}
}
