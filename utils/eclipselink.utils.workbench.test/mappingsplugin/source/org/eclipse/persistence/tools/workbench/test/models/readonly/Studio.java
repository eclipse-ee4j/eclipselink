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
import org.eclipse.persistence.mappings.OneToOneMapping;

public class Studio {
	public String name;
	public String owner;
	public Address address;
// Studio descriptor

public static ClassDescriptor descriptor() {
	ClassDescriptor descriptor = new ClassDescriptor();

	descriptor.setJavaClass(Studio.class);
	descriptor.setTableName("RO_MOVIE");

	descriptor.descriptorIsAggregate();

	descriptor.addDirectMapping("name", "STD_NAME");
	descriptor.addDirectMapping("owner", "STD_OWN");

	OneToOneMapping addressMapping = new OneToOneMapping();
	addressMapping.setAttributeName("address");
	addressMapping.setReferenceClass(Address.class);
	addressMapping.setForeignKeyFieldName("STD_ADD");
	addressMapping.privateOwnedRelationship();
	addressMapping.dontUseIndirection();
	descriptor.addMapping(addressMapping);
	
	return descriptor;
}
public static Studio example1() {
	Studio example = new Studio();

	example.setName("Lucasfilm, Ltd.");
	example.setOwner("George Lucas");
	example.setAddress(Address.example1());

	return example;
}
public static Studio example2() {
	Studio example = new Studio();

	example.setName("Columbia Pictures Corporation");
	example.setOwner("Christopher Columbus");
	example.setAddress(Address.example2());

	return example;
}
public static Studio example3() {
	Studio example = new Studio();

	example.setName("DreamWorks SKG");
	example.setOwner("Steven Speilberg");
	example.setAddress(Address.example3());

	return example;
}
public static Studio example4() {
	Studio example = new Studio();

	example.setName("Tall & Short Studio");
	example.setOwner("Mutt & Jeff");
	example.setAddress(Address.example4());

	return example;
}
public static Studio example5() {
	Studio example = new Studio();

	example.setName("FFC Productions");
	example.setOwner("Francis Ford Coppola");
	example.setAddress(Address.example5());

	return example;
}
public Address getAddress() {
	return this.address;
}
public String getName() {
	return this.name;
}
public String getOwner() {
	return this.owner;
}
public void setAddress(Address address) {
	this.address = address;
}
public void setName(String name) {
	this.name = name;
}
public void setOwner(String owner) {
	this.owner = owner;
}
@Override
public String toString() {
	return new String("Studio: " + getName());
}
}
