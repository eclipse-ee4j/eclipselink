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
package org.eclipse.persistence.testing.models.mapping;

import java.util.*;
import java.io.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.expressions.*;

public class Employee1 implements Serializable {
    private int id;
    private String firstName;
    private String lastName;
    private String sex;
    private String city;
    private String country;
    private String province;
    private int addressId_E;
    private int addressId_A;
    private java.math.BigDecimal countryId_C;
    private java.math.BigDecimal countryId_A;

    /**
     * Employee1 constructor comment.
     */
    public Employee1() {
        super();
    }

    /**
     * Amend the Employee1 descriptor with a join expression to join the multiple tables in the database.
     */
    public static void amendEmployee1WithFKInfo(ClassDescriptor descriptor) {
        // Setup the join from the address table to the country employee table to the address table by specifying the FK info to 
        // the descriptor.
        // Set the foreign key info from the address table to the country table. 
        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_EMP.ADDR_ID", "MUL_ADDR.ADDR_ID");
        descriptor.addForeignKeyFieldNameForMultipleTable("MUL_ADDR.CNTRY_ID", "MUL_CTRY.CNTRY_ID");
    }

    /**
     * Amend the Employee1 descriptor with a join expression to join the multiple tables in the database.
     */
    public static void amendEmployee1WithFKInfo(DatabaseSessionImpl session) {
        ClassDescriptor descriptor = new LegacyTestProject().getDescriptors().get(Employee1.class);
        amendEmployee1WithFKInfo(descriptor);
        // Insert new descriptor.
        session.addDescriptor(descriptor);
        session.initializeDescriptors();
    }

    /**
     * Amend the Employee1 descriptor with a join expression to join the multiple tables in the database.
     */
    public static void amendEmployee1WithJoinOnly(DatabaseSessionImpl aSession) {
        ClassDescriptor descriptor = new LegacyTestProject().getDescriptors().get(Employee1.class);

        // Setup the join from the employee table to the address table using a custom join expression and 
        // specifying the table insert order.
        ExpressionBuilder builder = new ExpressionBuilder();
        descriptor.getQueryManager().setMultipleTableJoinExpression(builder.getField("MUL_EMP.ADDR_ID").equal(builder.getField("MUL_ADDR.ADDR_ID")).and(builder.getField("MUL_ADDR.CNTRY_ID").equal(builder.getField("MUL_CTRY.CNTRY_ID"))));

        // Insert new descriptor.
        aSession.addDescriptor(descriptor);
        aSession.initializeDescriptors();
    }

    /**
     * Amend the Employee1 descriptor with a join expression to join the multiple tables in the database.
     */
    public static void amendEmployee1WithJoinWithInsert(ClassDescriptor descriptor) {
        // Setup the join from the employee table to the address table using a custom join expression and 
        // specifying the table insert order.
        ExpressionBuilder builder = new ExpressionBuilder();
        descriptor.getQueryManager().setMultipleTableJoinExpression(builder.getField("MUL_EMP.ADDR_ID").equal(builder.getField("MUL_ADDR.ADDR_ID")).and(builder.getField("MUL_ADDR.CNTRY_ID").equal(builder.getField("MUL_CTRY.CNTRY_ID"))));

        Vector tables = new Vector(3);
        tables.addElement(new DatabaseTable("MUL_CTRY"));
        tables.addElement(new DatabaseTable("MUL_ADDR"));
        tables.addElement(new DatabaseTable("MUL_EMP"));
        ((RelationalDescriptor) descriptor).setMultipleTableInsertOrder(tables);
    }

    /**
     * Amend the Employee1 descriptor with a join expression to join the multiple tables in the database.
     */
    public static void amendEmployee1WithJoinWithInsert(DatabaseSessionImpl aSession) {
        ClassDescriptor descriptor = new LegacyTestProject().getDescriptors().get(Employee1.class);

        amendEmployee1WithJoinWithInsert(descriptor);

        // Insert new descriptor.
        aSession.addDescriptor(descriptor);
        aSession.initializeDescriptors();
    }

    public static Employee1 example1() {
        Employee1 example = new Employee1();

        example.setId(1);
        example.setFirstName("Davis");
        example.setLastName("Vadis");
        example.setSex("male");
        example.setAddressId_A(2);
        example.setAddressId_E(2);
        example.setCity("Ottawa");
        example.setProvince("Ontario");
        example.setCountry("Canada");
        example.setCountryId_A(new java.math.BigDecimal(20));
        example.setCountryId_C(new java.math.BigDecimal(20));
        return example;
    }

    public static Employee1 example2() {
        Employee1 example = new Employee1();
        example.setId(2);
        example.setFirstName("Tracy");
        example.setLastName("Chapman");
        example.setSex("male");
        example.setAddressId_A(4);
        example.setAddressId_E(4);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setCountryId_A(new java.math.BigDecimal(40));
        example.setCountryId_C(new java.math.BigDecimal(40));
        return example;
    }

    public static Employee1 example3() {
        Employee1 example = new Employee1();

        example.setId(3);
        example.setFirstName("Farag");
        example.setLastName("Shaeer");
        example.setSex("male");
        example.setAddressId_A(6);
        example.setAddressId_E(6);
        example.setCity("Ottawa");
        example.setCountry("Canada");
        example.setCountryId_A(new java.math.BigDecimal(60));
        example.setCountryId_C(new java.math.BigDecimal(60));
        return example;

    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public int getAddressId_A() {
        return addressId_A;
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public int getAddressId_E() {
        return addressId_E;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getCity() {
        return city;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getCountry() {
        return country;
    }

    /**
     * * PUBLIC INTERNAL:
     *  * OneLineSummary.
     *  * DetailsOfMethodIfNecessary
     *  *
     *  * @returns TextDescribingWhatTheMethodReturns
     *  * @see #something
     *  * @see #somethingElse
     *  * @return java.lang.Class
     *
     * @return java.math.BigDecimal
     */
    public java.math.BigDecimal getCountryId_A() {
        return countryId_A;
    }

    /**
     * * PUBLIC INTERNAL:
     *  * OneLineSummary.
     *  * DetailsOfMethodIfNecessary
     *  *
     *  * @returns TextDescribingWhatTheMethodReturns
     *  * @see #something
     *  * @see #somethingElse
     *  * @return java.lang.Class
     *
     * @return java.math.BigDecimal
     */
    public java.math.BigDecimal getCountryId_C() {
        return countryId_C;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getProvince() {
        return province;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getSex() {
        return sex;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setAddressId_A(int value) {
        addressId_A = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setAddressId_E(int value) {
        addressId_E = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setCity(String value) {
        city = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setCountry(String value) {
        country = value;
    }

    /**
     * * PUBLIC INTERNAL:
     *  * OneLineSummary.
     *  * DetailsOfMethodIfNecessary
     *  *
     *  * @returns TextDescribingWhatTheMethodReturns
     *  * @see #something
     *  * @see #somethingElse
     *  * @return java.lang.Class
     *
     * @param newValue java.math.BigDecimal
     */
    public void setCountryId_A(java.math.BigDecimal newValue) {
        this.countryId_A = newValue;
    }

    /**
     * * PUBLIC INTERNAL:
     *  * OneLineSummary.
     *  * DetailsOfMethodIfNecessary
     *  *
     *  * @returns TextDescribingWhatTheMethodReturns
     *  * @see #something
     *  * @see #somethingElse
     *  * @return java.lang.Class
     *
     * @param newValue java.math.BigDecimal
     */
    public void setCountryId_C(java.math.BigDecimal newValue) {
        this.countryId_C = newValue;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setFirstName(String value) {
        firstName = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setId(int value) {
        id = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setLastName(String value) {
        lastName = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setProvince(String value) {
        province = value;
    }

    /**
     * This method was created in VisualAge.
     * @param value java.lang.String
     */
    public void setSex(String value) {
        sex = value;
    }

    public String toString() {
        return "Employee1(" + firstName + " " + lastName + ")";
    }
}
