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
package org.eclipse.persistence.testing.models.employee.eis.xmlfile;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.eis.mappings.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.eis.adapters.xmlfile.*;

/**
 * Amends order project with non-MW supported API.
 */
public class EmployeeNSAmendments {

    public static void addToEmployeeDescriptor(ClassDescriptor descriptor) {
        // Common interaction properties.
        descriptor.setProperty(XMLFilePlatform.FILE_NAME, "EMPLOYEE.xml");

        // Insert
        XQueryInteraction insertCall = new XQueryInteraction();
        insertCall.setFunctionName("insert");
        insertCall.setXQueryString("myns:EMPLOYEE");
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Update
        XQueryInteraction updateCall = new XQueryInteraction();
        updateCall.setFunctionName("update");
        updateCall.setXQueryString("myns:EMPLOYEE[myns:EMP_ID='#myns:EMP_ID/text()']");
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Delete
        XQueryInteraction deleteCall = new XQueryInteraction();
        deleteCall.setFunctionName("delete");
        deleteCall.setXQueryString("myns:EMPLOYEE[myns:EMP_ID='#myns:EMP_ID/text()']");
        descriptor.getQueryManager().setDeleteCall(deleteCall);

        // Read object
        XQueryInteraction readObjectCall = new XQueryInteraction();
        readObjectCall.setFunctionName("read");
        readObjectCall.setXQueryString("myns:EMPLOYEE[myns:EMP_ID='#myns:EMP_ID/text()']");
        readObjectCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadObjectCall(readObjectCall);

        // Read all
        XQueryInteraction readAllCall = new XQueryInteraction();
        readAllCall.setFunctionName("read-all");
        readAllCall.setXQueryString("myns:EMPLOYEE");
        readAllCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadAllCall(readAllCall);

        // Interface properties.
        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.Employee.class);

        // EISDescriptor properties.
        descriptor.setSequenceNumberField(new XMLField("myns:EMP_ID/text()"));
        descriptor.setSequenceNumberName("EMP_SEQ");

        EISOneToManyMapping managedEmployeesMapping = (EISOneToManyMapping) descriptor.getMappingForAttributeName("managedEmployees");
        XQueryInteraction managedEmployeesInteraction = new XQueryInteraction();
        managedEmployeesInteraction.setFunctionName("read-managed-employees");
        managedEmployeesInteraction.setProperty("fileName", "EMPLOYEE.xml");
        managedEmployeesInteraction.setXQueryString("myns:EMPLOYEE[myns:MANAGER_ID='#myns:EMP_ID/text()']");
        managedEmployeesInteraction.setOutputResultPath("result");
        managedEmployeesMapping.setSelectionCall(managedEmployeesInteraction);

        EISOneToManyMapping projectsMapping = (EISOneToManyMapping) descriptor.getMappingForAttributeName("projects");
        XQueryInteraction projectsInteraction = new XQueryInteraction();
        projectsInteraction.setFunctionName("read-projects");
        projectsInteraction.setProperty("fileName", "PROJECT.xml");
        projectsInteraction.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        projectsInteraction.setOutputResultPath("result");
        projectsMapping.setSelectionCall(projectsInteraction);
    }

    public static void addToLargeProjectDescriptor(ClassDescriptor descriptor) {
        // Common interaction properties.
        descriptor.setProperty(XMLFilePlatform.FILE_NAME, "PROJECT.xml");

        // Insert
        XQueryInteraction insertCall = new XQueryInteraction();
        insertCall.setFunctionName("insert");
        insertCall.setXQueryString("myns:PROJECT");
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Update
        XQueryInteraction updateCall = new XQueryInteraction();
        updateCall.setFunctionName("update");
        updateCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Delete
        XQueryInteraction deleteCall = new XQueryInteraction();
        deleteCall.setFunctionName("delete");
        deleteCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setDeleteCall(deleteCall);

        // Read object
        XQueryInteraction readObjectCall = new XQueryInteraction();
        readObjectCall.setFunctionName("read");
        readObjectCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        readObjectCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadObjectCall(readObjectCall);

        // Read all
        XQueryInteraction readAllCall = new XQueryInteraction();
        readAllCall.setFunctionName("read-all");
        readAllCall.setXQueryString("myns:PROJECT[@TYPE='L']");
        readAllCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadAllCall(readAllCall);

        // Interface properties.
        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.LargeProject.class);
    }

    public static void addToProjectDescriptor(ClassDescriptor descriptor) {
        // Common interaction properties.
        descriptor.setProperty(XMLFilePlatform.FILE_NAME, "PROJECT.xml");

        // Insert
        XQueryInteraction insertCall = new XQueryInteraction();
        insertCall.setXQueryString("myns:PROJECT");
        insertCall.setFunctionName("insert");
        insertCall.setProperty("fileName", "PROJECT.xml");
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Update
        XQueryInteraction updateCall = new XQueryInteraction();
        updateCall.setFunctionName("update");
        updateCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Delete
        XQueryInteraction deleteCall = new XQueryInteraction();
        deleteCall.setFunctionName("delete");
        deleteCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setDeleteCall(deleteCall);

        // Read object
        XQueryInteraction readObjectCall = new XQueryInteraction();
        readObjectCall.setFunctionName("read");
        readObjectCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        readObjectCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadObjectCall(readObjectCall);

        // Read all
        XQueryInteraction readAllCall = new XQueryInteraction();
        readAllCall.setFunctionName("read-all");
        readAllCall.setXQueryString("myns:PROJECT");
        readAllCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadAllCall(readAllCall);

        // Interface properties.
        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.Project.class);

        // EISDescriptor properties.
        descriptor.setSequenceNumberField(new XMLField("myns:PROJ_ID/text()"));
        descriptor.setSequenceNumberName("PROJ_SEQ");
    }

    public static void addToSmallProjectDescriptor(ClassDescriptor descriptor) {
        // Common interaction properties.
        descriptor.setProperty(XMLFilePlatform.FILE_NAME, "PROJECT.xml");

        // Insert
        XQueryInteraction insertCall = new XQueryInteraction();
        insertCall.setFunctionName("insert");
        insertCall.setXQueryString("myns:PROJECT");
        descriptor.getQueryManager().setInsertCall(insertCall);

        // Update
        XQueryInteraction updateCall = new XQueryInteraction();
        updateCall.setFunctionName("update");
        updateCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Delete
        XQueryInteraction deleteCall = new XQueryInteraction();
        deleteCall.setFunctionName("delete");
        deleteCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        descriptor.getQueryManager().setDeleteCall(deleteCall);

        // Read object
        XQueryInteraction readObjectCall = new XQueryInteraction();
        readObjectCall.setFunctionName("read");
        readObjectCall.setXQueryString("myns:PROJECT[myns:PROJ_ID='#myns:PROJ_ID/text()']");
        readObjectCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadObjectCall(readObjectCall);

        // Read all
        XQueryInteraction readAllCall = new XQueryInteraction();
        readAllCall.setFunctionName("read-all");
        readAllCall.setXQueryString("myns:PROJECT[@TYPE='S']");
        readAllCall.setOutputResultPath("result");
        descriptor.getQueryManager().setReadAllCall(readAllCall);

        // Interface properties.
        descriptor.getInterfacePolicy().addParentInterface(org.eclipse.persistence.testing.models.employee.interfaces.SmallProject.class);
    }
}
