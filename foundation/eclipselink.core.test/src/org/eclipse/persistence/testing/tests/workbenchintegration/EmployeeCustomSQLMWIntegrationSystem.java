/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.testing.tests.customsqlstoredprocedures.EmployeeCustomSQLSystem;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


/**
 *  This test system uses the Employee Custom SQL test system to test the integration between the
 *  Mapping Workbench and the Foundation Library.  in addition, it writes stored 
 *  procedure calls into project XML on which stored procedure WM integration test model runs.
 *  @author Kyle Chen
 */
public class EmployeeCustomSQLMWIntegrationSystem extends EmployeeCustomSQLSystem {

    public static String PROJECT_FILE = "MWIntegrationCustomSQLEmployeeProject";
    public ClassDescriptor employeeDescriptor;

    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public EmployeeCustomSQLMWIntegrationSystem() {
        this(PROJECT_FILE);
    }

    /**
     * Override the constructor for employee system to allow us to read and write XML
     */
    public EmployeeCustomSQLMWIntegrationSystem(String fileName) {
        initializeProject(getInitialProject(), fileName);
    }

    /**
     * This initial project will be written to a Project.xml.
     */
    public org.eclipse.persistence.sessions.Project getInitialProject() {
        org.eclipse.persistence.sessions.Project initialProject = new EmployeeProject();
        employeeDescriptor = 
                initialProject.getDescriptors().get(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        addQueries();
        return initialProject;
    }

    /**
     * Writes the initial project to a Project.xml, read project back from project.xml.
     */
    public void initializeProject(org.eclipse.persistence.sessions.Project initialProject, String fileName) {
        XMLProjectWriter.write(fileName + ".xml", initialProject);
        project = XMLProjectReader.read(fileName + ".xml", getClass().getClassLoader());
    }

    //build your owner stored procedure rather than those inherited if necessary.
    //schema.replaceObject(buildOracleStoredProcedureForProjectXML());
    public void createTables(DatabaseSession session) {
        super.createTables(session);
        org.eclipse.persistence.internal.databaseaccess.DatabasePlatform platform = session.getLogin().getPlatform();
        SchemaManager schema = new SchemaManager((session));

        if (platform.isOracle()) {
        }
    }

    public void addQueries() {
        buildStoredProcedureCallInQueryIntoDescriptor();
        buildUNamedStoredProcedureCallInQueryIntoDescriptor();
        buildNamedQueryWithStoredFunctionIntoDescriptor();
    }

    //Add additional stored procedure call.
    public StoredProcedureDefinition buildOracleStoredProcedureForProjectXML() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        return proc;
    }

    public void buildStoredProcedureCallInQueryIntoDescriptor() {

        StoredProcedureCall call = new StoredProcedureCall();
        call.setProcedureName("StoredProcedure_InOut_Out_In");

        call.addNamedInOutputArgumentValue("P_INOUT", new Integer(100), "P_INOUT_FIELD_NAME", Integer.class);
        call.addNamedOutputArgument("P_OUT", "P_OUT_FIELD_NAME", Integer.class);
        call.addNamedArgumentValue("P_IN", new Integer(1000));

        //Set stored procedure to Named query.
        DataReadQuery dataReadQuery = new DataReadQuery();
        dataReadQuery.setCall(call);

        employeeDescriptor.getQueryManager().addQuery("StoredProcedureCallInDataReadQuery", dataReadQuery);
    }

    public void buildUNamedStoredProcedureCallInQueryIntoDescriptor() {

        StoredProcedureCall unamedcall = new StoredProcedureCall();
        unamedcall.setProcedureName("StoredProcedure_InOut_Out_In");

        unamedcall.addUnamedInOutputArgumentValue(new Integer(100), "P_INOUT_FIELD_NAME", Integer.class);
        unamedcall.addUnamedOutputArgument("P_OUT_FIELD_NAME", Integer.class);
        unamedcall.addUnamedArgumentValue(new Integer(1000));

        //Set stored procedure to Named query
        DataReadQuery unameddataReadQuery = new DataReadQuery();
        unameddataReadQuery.setCall(unamedcall);

        employeeDescriptor.getQueryManager().addQuery("UNamedStoredProcedureCallInDataReadQuery", unameddataReadQuery);
    }
    
    public void buildNamedQueryWithStoredFunctionIntoDescriptor() {

      /*
        StoredFunctionDefinition func = new StoredFunctionDefinition();
        func.setName("StoredFunction_InOut_Out_In");
        func.addInOutputArgument("P_INOUT", Long.class);
        func.addOutputArgument("P_OUT", Long.class);
        func.addArgument("P_IN", Long.class);
        func.setReturnType(Long.class);
        func.addStatement("P_OUT := P_INOUT");
        func.addStatement("P_INOUT := P_IN");
        func.addStatement("RETURN P_OUT");
       */
      DataReadQuery drq = new DataReadQuery();
      drq.setName("StoredFunctionCallInNamedQuery");
      drq.addArgument("P_INOUT", Long.class);
      drq.addArgument("P_IN", Long.class);
      StoredFunctionCall sfc = new StoredFunctionCall();
      sfc.setProcedureName("StoredFunction_InOut_Out_In");
      sfc.addNamedInOutputArgument("P_INOUT", "P_INOUT", Long.class);
      sfc.addNamedOutputArgument("P_OUT", "P_OUT", Long.class);
      sfc.addNamedArgument("P_IN", "P_IN", Long.class);
      sfc.setResult("", Long.class);
      drq.addCall(sfc);
      employeeDescriptor.getQueryManager().addQuery("StoredFunctionCallInNamedQuery", drq);
    }
}
