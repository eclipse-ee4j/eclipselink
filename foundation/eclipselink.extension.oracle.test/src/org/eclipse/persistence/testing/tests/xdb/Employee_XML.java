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
package org.eclipse.persistence.testing.tests.xdb;

import java.io.*;

import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * A simple business object containing an XML Document
 */
public class Employee_XML implements Serializable {
    public int id;
    public String firstName;
    public String lastName;
    public String gender;
    public Employee_XML manager;
    public Vector managedEmployees;
    public Document resume;
    public String payroll_xml;

    public Employee_XML() {
    }

    public static Document documentFromString(String xmlString) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(xmlString.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            return doc;
        } catch (Exception ex) {
            System.out.println("Unable to create document due to: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static Employee_XML example0() {
        try {
            Employee_XML emp = new Employee_XML();
            emp.firstName = "Bob";
            emp.lastName = "Jones";
            emp.gender = "Male";
            String resume = resume0();
            emp.resume = documentFromString(resume);
            emp.managedEmployees = new Vector();
            emp.payroll_xml = "<payroll><salary>100000</salary><pay-period>bi-monthly</pay-period></payroll>";
            return emp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Employee_XML example1() {
        try {
            Employee_XML emp = new Employee_XML();
            emp.firstName = "Frank";
            emp.lastName = "Cotton";
            emp.gender = "Male";
            String resume = resume1();
            emp.resume = documentFromString(resume);
            emp.payroll_xml = "<payroll><salary>10000</salary><pay-period>weekly</pay-period></payroll>";
            return emp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String resume0() {
        String resume = "<resume>\n";
        resume += "  <first-name>Bob</first-name>\n";
        resume += "   <last-name>Jones</last-name>\n";
        resume += "   <age>45</age>\n";
        resume += "   <education>\n";
        resume += "     <degree>BCS</degree>\n";
        resume += "     <degree>MBA</degree>\n";
        resume += "   </education>\n";
        resume += "</resume>";
        return resume;
    }

    public static String resume1() {
        String resume = "<resume>\n";
        resume += "  <first-name>Frank</first-name>\n";
        resume += "   <last-name>Cotton</last-name>\n";
        resume += "   <age>27</age>\n";
        resume += "   <education>\n";
        resume += "     <degree>BCS</degree>\n";
        resume += "   </education>\n";
        resume += "</resume>";
        return resume;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EMPLOYEE_XML");

        definition.addIdentityField("ID", java.lang.Integer.class);
        definition.addField("FIRST_NAME", String.class);
        definition.addField("LAST_NAME", String.class);
        definition.addField("GENDER", String.class);
        definition.addField("RESUME_XML", Document.class);
        definition.addField("PAYROLL_XML", Document.class);
        definition.addField("MANAGER_ID", java.lang.Integer.class);

        return definition;
    }
}
