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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;

public class Employee2 implements Serializable {
    private Integer employeeNumber;
    private Integer id;
    private String extraInfo;
    private Integer employeeNumberB;
    private String firstName;

    public Employee2() {
        super();
    }

    public static Employee2 example1() {
        Employee2 example = new Employee2();

        example.setId(new Integer(1));
        example.setEmployeeNumber(new Integer(2));
        example.setEmployeeNumberB(example.getEmployeeNumber());
        example.setExtraInfo("Extra info string");
        example.setFirstName("Jan");

        return example;
    }

    public static Employee2 example2() {
        Employee2 example = new Employee2();

        example.setId(new Integer(4));
        example.setEmployeeNumber(new Integer(8));
        example.setEmployeeNumberB(example.getEmployeeNumber());
        example.setExtraInfo("Whatever");
        example.setFirstName("Ursula");

        return example;
    }

    public Integer getEmployeeNumber() {
        return employeeNumber;
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
     * @return java.lang.Integer
     */
    public Integer getEmployeeNumberB() {
        return employeeNumberB;
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
     * @return java.lang.String
     */
    public String getExtraInfo() {
        return extraInfo;
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
     * @return java.lang.String
     */
    public String getFirstName() {
        return firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setEmployeeNumber(Integer newValue) {
        this.employeeNumber = newValue;
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
     * @param newValue java.lang.Integer
     */
    public void setEmployeeNumberB(Integer newValue) {
        this.employeeNumberB = newValue;
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
     * @param newValue java.lang.String
     */
    public void setExtraInfo(String newValue) {
        this.extraInfo = newValue;
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
     * @param newValue java.lang.String
     */
    public void setFirstName(String newValue) {
        this.firstName = newValue;
    }

    public void setId(Integer newValue) {
        this.id = newValue;
    }

    public String toString() {
        return "Employee2(" + firstName + ")";
    }
}
