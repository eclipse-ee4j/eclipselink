/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class Employee implements Serializable {
    public String firstName;
    public String lastName;
    public String sex;
    public java.util.Date dateAndTimeOfBirth;
    public java.util.Date joiningDate;
    public ValueHolderInterface policies;
    private ValueHolderInterface designation;
    public JobDescription jobDescription;
    public Hardware computer;
    public Vector shipments;
    public Vector managedEmployees;
    public Employee manager;
    public ValueHolderInterface phoneNumbers;
    public Gender gender;
    public Cubicle cubicle;

    public Employee() {
        policies = new ValueHolder(new Vector());
        designation = new ValueHolder();
        shipments = new Vector();
        managedEmployees = new Vector();
        phoneNumbers = new ValueHolder(new Vector());
    }

    public void addManagedEmployee(Employee employee) {
        this.managedEmployees.addElement(employee);
    }

    public void addPhoneNumber(Phone phoneNumber) {
        this.getPhoneNumbers().addElement(phoneNumber);
    }

    public static void addToDescriptor(ClassDescriptor des) {
        ExpressionBuilder computer = new ExpressionBuilder();
        OneToOneMapping computerMapping = (OneToOneMapping)des.getMappingForAttributeName("computer");
        computerMapping.setSelectionCriteria((computer.getField("MAP_HRW.EMP_FNAME").equal(computer.getParameter(new DatabaseField("MAP_EMP.FNAME")))).and(computer.getField("MAP_HRW.EMP_LNAME").equal(computer.getParameter(new DatabaseField("MAP_EMP.LNAME")))));

        ExpressionBuilder shipment = new ExpressionBuilder();
        Expression linkTable = shipment.getTable("MAP_EMSP");
        ManyToManyMapping shipmentMapping = (ManyToManyMapping)des.getMappingForAttributeName("shipments");
        shipmentMapping.setSelectionCriteria(((((linkTable.getField("MAP_EMSP.EMP_FNAME").equal(shipment.getParameter(new DatabaseField("MAP_EMP.FNAME")))).and(linkTable.getField("MAP_EMSP.EMP_LNAME").equal(shipment.getParameter(new DatabaseField("MAP_EMP.LNAME"))))).and(linkTable.getField("MAP_EMSP.SP_TS").equal(shipment.getField("MAP_SHIP.SP_TS")))).and(linkTable.getField("MAP_EMSP.SP_TSMIL").equal(shipment.getField("MAP_SHIP.SP_TSMIL")))).and(linkTable.getField("MAP_EMSP.STATUS").equal(null)));

        DirectToFieldMapping genderType = new DirectToFieldMapping();
        ObjectTypeConverter genderConverter = new ObjectTypeConverter();
        genderType.setFieldName("GENDER");
        genderType.setAttributeName("gender");
        genderConverter.addConversionValue("M", Male.male);
        genderConverter.addConversionValue("F", Female.female);
        genderConverter.setDefaultAttributeValue(Female.female);
        genderType.setConverter(genderConverter);
        des.addMapping(genderType);
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition employeePhoneJoinTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_EMPH");

        definition.addPrimaryKeyField("FNAME", String.class, 20);
        definition.addPrimaryKeyField("LNAME", String.class, 20);
        definition.addPrimaryKeyField("P_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    public static Employee errorExample1() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Dave";
        example.lastName = "Vadis";
        example.sex = "unknown";
        Calendar c = Calendar.getInstance();
        c.set(1974, 2, 14, 1, 1, 1);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c = Calendar.getInstance();
        c.set(1994, 2, 14);
        example.joiningDate = c.getTime();
        example.designation.setValue("Executive");
        example.setJobDescription(JobDescription.example1());
        example.computer = Computer.example1(example);

        empPolicies.addElement("Health");
        empPolicies.addElement("Tenant");
        example.setPolicies(empPolicies);

        Shipment s1 = Shipment.example1();
        Shipment s2 = Shipment.example2();
        example.shipments.addElement(s1);
        example.shipments.addElement(s2);
        s1.employees.addElement(example);
        s2.employees.addElement(example);

        example.addPhoneNumber(Phone.example1());
        example.addPhoneNumber(Phone.example2());

        return example;
    }

    public static Employee example1() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Dave";
        example.lastName = "Vadis";
        example.sex = "male";
        example.gender = Male.male;
        example.cubicle = Cubicle.example1();

        Calendar c = Calendar.getInstance();
        c.set(1974, 2, 14, 1, 1, 1);//new java.util.Date(74, 2, 14, 1, 1, 1);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();// re-set calendar
        c.set(1994, 2, 14, 1, 1, 1);//new java.util.Date(94, 2, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Executive");
        example.setJobDescription(JobDescription.example1());
        example.computer = Computer.example1(example);

        empPolicies.addElement("Health");
        empPolicies.addElement("Tenant");
        example.setPolicies(empPolicies);

        Shipment s1 = Shipment.example1();
        Shipment s2 = Shipment.example2();
        example.shipments.addElement(s1);
        example.shipments.addElement(s2);
        s1.employees.addElement(example);
        s2.employees.addElement(example);

        example.addPhoneNumber(Phone.example1());
        example.addPhoneNumber(Phone.example2());

        return example;
    }

    public static Employee example2() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Tracy";
        example.lastName = "Chapman";
        example.sex = "female";
        example.gender = Female.female;
        example.cubicle = Cubicle.example2();

        Calendar c = Calendar.getInstance();
        c.set(1975, 1, 19, 2, 2, 2);// new java.util.Date(75, 1, 19, 2, 2, 2);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1995, 3, 14);// new java.util.Date(95, 3, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Non-Executive");
        example.setJobDescription(JobDescription.example2());
        example.computer = Computer.example2(example);

        empPolicies.addElement("Vehicle");
        empPolicies.addElement("Tenant");
        example.setPolicies(empPolicies);

        Shipment s1 = Shipment.example3();
        Shipment s2 = Shipment.example4();
        example.shipments.addElement(s1);
        example.shipments.addElement(s2);
        s1.employees.addElement(example);
        s1.employees.addElement(example);

        example.addPhoneNumber(Phone.example3());

        return example;
    }

    public static Employee example3() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Edward";
        example.lastName = "White";
        example.sex = "male";
        example.gender = Male.male;
        example.cubicle = Cubicle.example1();

        Calendar c = Calendar.getInstance();
        c.set(1971, 11, 24, 3, 3, 3);// new java.util.Date(71, 11, 24, 3, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1995, 3, 14);// new java.util.Date(95, 3, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Non-Executive");
        example.setJobDescription(JobDescription.example3());
        example.computer = Computer.example3(example);

        empPolicies.addElement("Vehicle");
        empPolicies.addElement("House");
        example.setPolicies(empPolicies);

        example.addPhoneNumber(Phone.example4());
        example.addPhoneNumber(Phone.example5());
        return example;
    }

    public static Employee example4() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Graham";
        example.lastName = "Gooch";
        example.sex = "male";
        example.gender = Male.male;
        example.cubicle = Cubicle.example2();

        Calendar c = Calendar.getInstance();
        c.set(1973, 2, 14, 1, 1, 1);// new java.util.Date(73, 2, 14, 1, 1, 1);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1992, 2, 14);// new java.util.Date(92, 2, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Executive");
        example.setJobDescription(JobDescription.example1());
        example.computer = Computer.example4(example);

        empPolicies.addElement("House");
        empPolicies.addElement("Land");
        example.setPolicies(empPolicies);

        Shipment s1 = Shipment.example3();
        Shipment s2 = Shipment.example4();
        example.shipments.addElement(s1);
        example.shipments.addElement(s2);
        s1.employees.addElement(example);
        s2.employees.addElement(example);

        example.addPhoneNumber(Phone.example7());
        example.addPhoneNumber(Phone.example8());

        return example;
    }

    public static Employee example5() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Tracy";
        example.lastName = "Rue";
        example.sex = "female";
        example.gender = Female.female;

        Calendar c = Calendar.getInstance();
        c.set(1975, 1, 19, 2, 2, 2);// new java.util.Date(75, 1, 19, 2, 2, 2);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1992, 2, 14);// new java.util.Date(92, 2, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Non-Executive");
        example.setJobDescription(JobDescription.example2());
        example.computer = Computer.example5(example);

        empPolicies.addElement("Vehicle");
        empPolicies.addElement("Tenant");
        example.setPolicies(empPolicies);

        Shipment s1 = Shipment.example5();
        Shipment s2 = Shipment.example6();
        example.shipments.addElement(s1);
        example.shipments.addElement(s2);
        s1.employees.addElement(example);
        s2.employees.addElement(example);

        example.addPhoneNumber(Phone.example9());

        return example;
    }

    public static Employee example6() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Norman";
        example.lastName = "Louis";
        example.sex = "male";
        example.gender = Male.male;

        Calendar c = Calendar.getInstance();
        c.set(1971, 11, 24, 3, 3, 3);// new java.util.Date(71, 11, 24, 3, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1992, 2, 14);// new java.util.Date(92, 2, 14);
        example.joiningDate = c.getTime();

        example.designation.setValue("Non-Executive");
        example.setJobDescription(JobDescription.example3());
        example.computer = Computer.example6(example);

        empPolicies.addElement("Vehicle");
        empPolicies.addElement("House");
        example.setPolicies(empPolicies);

        example.addPhoneNumber(Phone.example10());
        example.addPhoneNumber(Phone.example11());
        return example;
    }

    public static Employee example7() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Imran";
        example.lastName = "Khan";
        example.sex = "male";
        example.gender = Male.male;

        Calendar c = Calendar.getInstance();
        c.set(1971, 11, 24, 3, 3, 3);// new java.util.Date(71, 11, 24, 3, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1970, 2, 19);// new java.util.Date(70, 2, 19);
        example.joiningDate = c.getTime();

        example.designation.setValue("Executive");
        example.setJobDescription(JobDescription.example3());
        example.computer = Computer.example7(example);

        empPolicies.addElement("Arms");
        empPolicies.addElement("Legs");
        example.setPolicies(empPolicies);

        example.addPhoneNumber(Phone.example13());
        example.addPhoneNumber(Phone.example14());
        return example;
    }

    public static Employee example8() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Margaret";
        example.lastName = "Thatcher";
        example.sex = "female";
        example.gender = Female.female;

        Calendar c = Calendar.getInstance();
        c.set(1971, 11, 24, 3, 3, 3);// new java.util.Date(71, 11, 24, 3, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1901, 1, 1);// new java.util.Date(01,01,01);
        example.joiningDate = c.getTime();

        example.designation.setValue("Non-Executive");
        example.setJobDescription(JobDescription.example4());
        example.computer = Computer.example8(example);

        empPolicies.addElement("House");
        empPolicies.addElement("Vehicle");
        example.setPolicies(empPolicies);

        example.addPhoneNumber(Phone.example15());
        example.addPhoneNumber(Phone.example16());
        return example;
    }

    public static Employee example9() {
        Employee example = new Employee();
        Vector empPolicies = new Vector();

        example.firstName = "Ace";
        example.lastName = "Ventura";
        example.sex = "male";
        example.gender = Male.male;

        Calendar c = Calendar.getInstance();
        c.set(1971, 11, 24, 3, 3, 3);// new java.util.Date(71, 11, 24, 3, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.dateAndTimeOfBirth = c.getTime();
        c.clear();
        c.set(1901, 1, 1);// new java.util.Date(01,01,01);
        example.joiningDate = c.getTime();

        example.designation.setValue("Executive");
        example.setJobDescription(JobDescription.example5());
        example.computer = Computer.example9(example);

        empPolicies.addElement("Pet");
        empPolicies.addElement("Vehicle");
        example.setPolicies(empPolicies);

        example.addPhoneNumber(Phone.example17());
        example.addPhoneNumber(Phone.example18());
        return example;
    }

    public Hardware getComputer() {
        return computer;
    }

    public java.sql.Date getDate() {
        if (this.dateAndTimeOfBirth == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(this.dateAndTimeOfBirth);
            return new java.sql.Date(c.getTime().getTime());
        }
    }

    public ValueHolderInterface getDesignation() {
        return designation;
    }

    public JobDescription getJobDescription() {
        return jobDescription;
    }

    public Vector getManagedEmployees() {
        return managedEmployees;
    }

    public Vector getManagedEmployeesForTOPLink() {
        // test method mutating
        return (Vector)managedEmployees.clone();
    }

    public Employee getManager() {
        return manager;
    }

    public Vector getPhoneNumbers() {
        return (Vector)phoneNumbers.getValue();
    }

    public Vector getPolicies() {
        return (Vector)policies.getValue();
    }

    public Integer getRankFromObject() {
        Integer rank = null;
        if (getDesignation().getValue() == null) {
            return null;
        }

        if (getDesignation().getValue().equals("Executive")) {
            rank = new Integer(1);
        }
        if (getDesignation().getValue().equals("Non-Executive")) {
            rank = new Integer(2);
        }
        return rank;
    }

    public String getRankFromRow(org.eclipse.persistence.sessions.Record row, Session aSession) {
        if (row.get("RANK") == null) {
            return null;
        }
        Integer value = new Integer(((Number)row.get("RANK")).intValue());
        String rank = null;
        Employee employee = new Employee();

        //(Employee) aSession.readObject(Employee.class);
        if (value.intValue() == 1) {
            rank = new String("Executive");
        }
        if (value.intValue() == 2) {
            rank = new String("Non-Executive");
        }
        return rank;
    }

    public Vector getShipments() {
        return shipments;
    }

    public java.sql.Time getTime() {
        if (dateAndTimeOfBirth == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(this.dateAndTimeOfBirth);
        return new java.sql.Time(c.getTime().getTime());
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition joinTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_EMSP");

        definition.addPrimaryKeyField("EMP_FNAME", String.class, 20);
        definition.addPrimaryKeyField("EMP_LNAME", String.class, 20);
        definition.addPrimaryKeyField("SP_TS", java.sql.Timestamp.class);
        definition.addPrimaryKeyField("SP_TSMIL", Integer.class);
        definition.addField("STATUS", Character.class);

        return definition;
    }

    /**
     * This method was created in VisualAge.
     */
    void newMethod() {
    }

    public void removeManagedEmployee(Employee employee) {
        managedEmployees.removeElement(employee);
    }

    public void setComputer(Computer computer) {
        this.computer = computer;
    }

    public void setComputer(Hardware computer) {
        this.computer = computer;
    }

    public java.util.Date setDateAndTime(org.eclipse.persistence.sessions.Record row, org.eclipse.persistence.sessions.Session session) {
        java.sql.Date sqlDateOfBirth = (java.sql.Date)ConversionManager.getDefaultManager().convertObject(row.get("BDAY"), java.sql.Date.class);
        java.sql.Time timeOfBirth = (java.sql.Time)session.getLogin().getPlatform().convertObject(row.get("BTIME"), java.sql.Time.class);
        if ((timeOfBirth == null) || (sqlDateOfBirth == null)) {
            return null;
        }
        Calendar calendarOfBirth = Calendar.getInstance();
        calendarOfBirth.setTime(sqlDateOfBirth);
        Calendar calendarTimeOfBirth = Calendar.getInstance();
        calendarTimeOfBirth.setTime(timeOfBirth);
        calendarOfBirth.set(Calendar.HOUR_OF_DAY, calendarTimeOfBirth.get(Calendar.HOUR_OF_DAY));
        calendarOfBirth.set(Calendar.MINUTE, calendarTimeOfBirth.get(Calendar.MINUTE));
        calendarOfBirth.set(Calendar.SECOND, calendarTimeOfBirth.get(Calendar.SECOND));
        java.util.Date utilDateOfBirth = calendarOfBirth.getTime();

        return utilDateOfBirth;
    }

    public void setDesignation(ValueHolderInterface value) {
        designation = value;
    }

    public void setJobDescription(JobDescription newJobDescription) {
        jobDescription = newJobDescription;
    }

    public void setManagedEmployees(Vector emps) {
        // test method mutating
        managedEmployees = emps;
    }

    public void setManagedEmployeesFromTOPLink(Vector emps) {
        // test method mutating
        managedEmployees = (Vector)emps.clone();
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setPhoneNumbers(Vector v) {
        phoneNumbers.setValue(v);
    }

    public void setPolicies(Vector theVector) {
        policies.setValue(theVector);
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName("MAP_EMP");
        definition.addField("FNAME", String.class, 20);
        definition.addField("LNAME", String.class, 20);
        definition.addField("SEX", String.class, 10);
        definition.addField("BDAY", java.sql.Date.class);
        definition.addField("BTIME", java.sql.Time.class);
        definition.addField("JDAY", java.sql.Date.class);
        definition.addField("RANK", Integer.class);
        definition.addField("GENDER", String.class, 10);

        // The JDESC field will be added after a plaftorm check in
        // MappingSystem.createTables()
        definition.addField("M_FNAME", String.class, 20);
        definition.addField("M_LNAME", String.class, 20);
        definition.addField("C_ID", String.class, 15);
        definition.addField("A_ID", java.math.BigDecimal.class, 15);

        definition.addForeignKeyConstraint("MAP_EMP_MAP_ADD", "A_ID", "A_ID", "MAP_ADD");
        definition.addForeignKeyConstraint("MAP_EMP_MAP_CUB", "C_ID", "C_ID", "MAP_CUB");
        definition.addForeignKeyConstraint("MAP_EMP_MAP_EMP", "M_FNAME,M_LNAME", "FNAME,LNAME", "MAP_EMP");

        return definition;
    }

    public String toString() {
        return "Employee(" + firstName + " " + lastName + ")";
    }
}
