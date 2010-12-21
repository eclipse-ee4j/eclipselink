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
package org.eclipse.persistence.testing.models.aggregate;


/**
 * Used to test shared aggregate inheritance.
 */
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Employee1 {
    private int id;
    private float salary;
    private String name;
    private String status;
    private Address1 address;
    public Address1 businessAddress;

    public Employee1() {
        super();
    }

public static void addToDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor descriptor) {
	org.eclipse.persistence.mappings.TransformationMapping mapping = new org.eclipse.persistence.mappings.TransformationMapping();
	mapping.addFieldTransformation("type", "getType");
	descriptor.addMapping(mapping);
}
    public static Employee1 example1() {
        Employee1 example = new Employee1();
        example.setName("Sami");
        example.setId(1);
        example.setSalary(33000);
        example.setStatus("EclipseLink");
        example.setAddress(HomeAddress.example1());
        example.businessAddress = HomeAddress.example2();

        return example;
    }

    public static Employee1 example2() {
        Employee1 example = new Employee1();
        example.setName("Rick");
        example.setId(3);
        example.setSalary(33000);
        example.setStatus("EclipseLink");
        example.setAddress(WorkingAddress.example2());
        example.businessAddress = HomeAddress.example1();

        return example;
    }

    public static Employee1 example3() {
        Employee1 example = new Employee1();
        example.setName("Rick");
        example.setId(30);
        example.setAddress(null);

        return example;
    }

    public static Employee1 example4() {
        Employee1 example = new Employee1();
        example.setName("Rick");
        example.setId(130);
        example.setSalary(33000);
        example.setStatus("EclipseLink");
        example.setAddress(WorkingAddress.example2());
        example.businessAddress = HomeAddress.example1();

        return example;
    }

    public Address1 getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getSalary() {
        return salary;
    }

    public String getStatus() {
        return status;
    }

    public void setAddress(Address1 thisAddress) {
        address = thisAddress;
    }

    public void setId(int thisId) {
        id = thisId;
    }

    public void setName(String thisName) {
        name = thisName;
    }

    public void setSalary(float thisSalary) {
        salary = thisSalary;
    }

    public void setStatus(String thisStatus) {
        status = thisStatus;
    }

    public static TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("Employee1");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("Name");
        field.setTypeName("VARCHAR");
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("id");
        field1.setTypeName("NUMBER");
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("salary");
        field2.setTypeName("NUMBER");
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("status");
        field3.setTypeName("VARCHAR");
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("APARTMENT_NUMBER");
        field4.setTypeName("NUMBER");
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field5 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field5.setName("BUILDING_NUMBER");
        field5.setTypeName("NUMBER");
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field6 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field6.setName("CITY");
        field6.setTypeName("VARCHAR");
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        tabledefinition.addField(field6);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field7 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field7.setName("COUNTRY");
        field7.setTypeName("VARCHAR");
        field7.setShouldAllowNull(true);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        tabledefinition.addField(field7);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field8 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field8.setName("POSTAL_CODE");
        field8.setTypeName("VARCHAR");
        field8.setShouldAllowNull(true);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        tabledefinition.addField(field8);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field9 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field9.setName("STREET_NAME");
        field9.setTypeName("VARCHAR");
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        tabledefinition.addField(field9);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field10 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field10.setName("TYPE");
        field10.setTypeName("VARCHAR");
        field10.setShouldAllowNull(true);
        field10.setIsPrimaryKey(false);
        field10.setUnique(false);
        field10.setIsIdentity(false);
        tabledefinition.addField(field10);

        // ** second address
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field14 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field14.setName("BAPARTMENT_NUMBER");
        field14.setTypeName("NUMBER");
        field14.setShouldAllowNull(true);
        field14.setIsPrimaryKey(false);
        field14.setUnique(false);
        field14.setIsIdentity(false);
        tabledefinition.addField(field14);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field15 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field15.setName("BBUILDING_NUMBER");
        field15.setTypeName("NUMBER");
        field15.setShouldAllowNull(true);
        field15.setIsPrimaryKey(false);
        field15.setUnique(false);
        field15.setIsIdentity(false);
        tabledefinition.addField(field15);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field16 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field16.setName("BCITY");
        field16.setTypeName("VARCHAR");
        field16.setShouldAllowNull(true);
        field16.setIsPrimaryKey(false);
        field16.setUnique(false);
        field16.setIsIdentity(false);
        tabledefinition.addField(field16);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field17 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field17.setName("BCOUNTRY");
        field17.setTypeName("VARCHAR");
        field17.setShouldAllowNull(true);
        field17.setIsPrimaryKey(false);
        field17.setUnique(false);
        field17.setIsIdentity(false);
        tabledefinition.addField(field17);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field18 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field18.setName("BPOSTAL_CODE");
        field18.setTypeName("VARCHAR");
        field18.setShouldAllowNull(true);
        field18.setIsPrimaryKey(false);
        field18.setUnique(false);
        field18.setIsIdentity(false);
        tabledefinition.addField(field18);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field19 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field19.setName("BSTREET_NAME");
        field19.setTypeName("VARCHAR");
        field19.setShouldAllowNull(true);
        field19.setIsPrimaryKey(false);
        field19.setUnique(false);
        field19.setIsIdentity(false);
        tabledefinition.addField(field19);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field20 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field20.setName("BTYPE");
        field20.setTypeName("VARCHAR");
        field20.setShouldAllowNull(true);
        field20.setIsPrimaryKey(false);
        field20.setUnique(false);
        field20.setIsIdentity(false);
        tabledefinition.addField(field20);

        return tabledefinition;
    }
}
