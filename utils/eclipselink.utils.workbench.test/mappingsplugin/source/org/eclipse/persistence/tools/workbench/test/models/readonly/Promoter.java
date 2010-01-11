/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * This type was created in VisualAge.
 */
public class Promoter {
	public Number id;
	public String name;
	public ValueHolderInterface phoneNumbers;
/**
 * Promoter constructor comment.
 */
public Promoter() {
	super();
}
// Movie descriptor

public static ClassDescriptor descriptor() {
	ClassDescriptor descriptor = new ClassDescriptor();

	descriptor.setJavaClass(Promoter.class);
	descriptor.setTableName("RO_PROMO");
	descriptor.addPrimaryKeyFieldName("PROMO_ID");
	descriptor.setSequenceNumberName("PROMO_SEQ");
	descriptor.setSequenceNumberFieldName("PROMO_ID");
 
	descriptor.addDirectMapping("id", "PROMO_ID");
	descriptor.addDirectMapping("name", "NAME");
/*
	// The promoter operates in a collection of countries.
	org.eclipse.persistence.mappings.OneToManyMapping phoneNumbersMapping = new org.eclipse.persistence.mappings.OneToManyMapping() ;
	phoneNumbersMapping.setAttributeName("phoneNumbers");
	phoneNumbersMapping.setReferenceClass(PhoneNumber.class);
	phoneNumbersMapping.setTargetForeignKeyFieldName("PROMO_ID");
	phoneNumbersMapping.privateOwnedRelationship();
//	phoneNumbersMapping.dontUseIndirection();
	descriptor.addMapping(phoneNumbersMapping);
*/
 

	return descriptor;
}
/**
 * This method was created in VisualAge.
 */
public static Promoter example1() {
	Promoter example = new Promoter();
	example.setName("Promoter 1");

	// Add phone numbers.
/*	Vector numbers = new Vector();
	numbers.addElement(PhoneNumber.example1(example));
	numbers.addElement(PhoneNumber.example2(example));
	example.phoneNumbers = new ValueHolder() ;
	example.phoneNumbers.setValue(numbers);
*/
	return example;
}
/**
 * This method was created in VisualAge.
 */
public static Promoter example2() {
	Promoter example = new Promoter();
	example.setName("Promoter 2");

	return example;
}
/**
 * This method was created in VisualAge.
 */
public static Promoter example3() {
	Promoter example = new Promoter();
	example.setName("Promoter 3");

	return example;
}
/**
 * This method was created in VisualAge.
 */
public static Promoter example4() {
	Promoter example = new Promoter();
	example.setName("Promoter 4");

	return example;
}
/**
 * This method was created in VisualAge.
 */
public static Promoter example5() {
	Promoter example = new Promoter();
	example.setName("Promoter 5");

	return example;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setName(String newValue) {
	this.name = newValue;
}
// Promoter table definition

public static TableDefinition tableDefinition() {
	TableDefinition definition = new TableDefinition();

	definition.setName("RO_PROMO");

	definition.addIdentityField("PROMO_ID", java.math.BigDecimal.class, 15);
	definition.addField("NAME", String.class, 50);

	return definition;
}
@Override
public String toString() {
	return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + "(" + getName() + ") ";
}
}
