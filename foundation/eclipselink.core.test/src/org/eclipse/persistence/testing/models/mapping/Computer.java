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
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Computer extends Hardware implements Serializable {
    public String description;
    public Boolean isMacintosh;
    public String serialNumber;
    public Monitor monitor;

    public Computer() {
        setSerialNumber(new String());
        setDist("false");
    }

    public static Computer createNew() {
        return new Computer();
    }

    public static Computer example1(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Sun Workstation");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("12876-1762671");
        example.setMonitor(Monitor.example1());
        return example;
    }

    public static Computer example10(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Micron PentiumII 450");
        example.setEmployee(employee);
        example.setSerialNumber("OU812-URABUTLN");
        return example;
    }

    public static Computer example2(Employee employee) {
        Computer example = new Computer();

        example.setDescription("PC 486");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("7657165-12765112");
        example.setMonitor(Monitor.example2());
        return example;
    }

    public static Computer example3(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("76187-1876212-2");
        return example;
    }

    public static Computer example4(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Power Book");
        example.setEmployee(employee);
        example.isMacintosh();
        example.setSerialNumber("187-1987228791");
        return example;
    }

    public static Computer example5(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IMAC");
        example.setEmployee(employee);
        example.isMacintosh();
        example.setSerialNumber("18761-18762817347");
        return example;
    }

    public static Computer example6(Employee employee) {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("8772-2987-28763-123");
        return example;
    }

    public static Computer example7(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Compaq Pentium 200");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("CP18176-187262");
        return example;
    }

    public static Computer example8(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Acer PentiumII 300");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("2876-298769232736");
        return example;
    }

    public static Computer example9(Employee employee) {
        Computer example = new Computer();

        example.setDescription("Micron PentiumII 450");
        example.setEmployee(employee);
        example.notMacintosh();
        example.setSerialNumber("OU812-URABUTLN");
        return example;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int hashCode() {
        return getSerialNumber().hashCode();
    }

    public void isMacintosh() {
        isMacintosh = new Boolean(true);
    }

    public void notMacintosh() {
        isMacintosh = new Boolean(false);
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_COM");

        definition.addPrimaryKeyField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCRIP", String.class, 30);
        definition.addField("EMP_FNAME", String.class, 30);
        definition.addField("EMP_LNAME", String.class, 30);
        definition.addField("SERL_NO", String.class, 30);
        definition.addField("IS_MAC", String.class, 3);
        definition.addField("MON_SER", String.class, 30);
        definition.addForeignKeyConstraint("MonitorRef", "MON_SER", "SERL_NO", "MAP_MON");
        return definition;
    }

    public String toString() {
        return "Computer(" + description + "--" + serialNumber + ")";
    }
}
