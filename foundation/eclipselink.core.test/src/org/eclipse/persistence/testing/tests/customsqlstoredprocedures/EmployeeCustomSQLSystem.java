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
 *     Markus KARG - SQL Anywhere now using WATCOM-SQL instead of Transact-SQL.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class EmployeeCustomSQLSystem extends EmployeeSystem {
    public void addDescriptors(DatabaseSession session) {
        session.logout();
        super.addDescriptors(session);
        setCustomSQL(session);

        // Force re-initialize.
        session.login();
    }

    public StoredProcedureDefinition buildOracleDeleteProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Delete_Employee");
        proc.addArgument("P_EMP_ID", Long.class);
        proc.addStatement("Delete FROM SALARY where EMP_ID = P_EMP_ID");
        proc.addStatement("Delete FROM EMPLOYEE where EMP_ID = P_EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildOracleInsertProcedure() {
        // Assume no identity.
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Insert_Employee");
        proc.addArgument("P_EMP_ID", Long.class);
        proc.addArgument("P_SALARY", Integer.class);
        proc.addArgument("P_END_DATE", java.sql.Date.class);
        proc.addArgument("P_MANAGER_ID", Long.class);
        proc.addArgument("P_START_DATE", java.sql.Date.class);
        proc.addArgument("P_F_NAME", String.class, 40);
        proc.addArgument("P_L_NAME", String.class, 40);
        proc.addArgument("P_GENDER", String.class, 1);
        proc.addArgument("P_ADDR_ID", Long.class);
        proc.addArgument("P_VERSION", Long.class);
        proc.addArgument("P_START_TIME", java.sql.Time.class);
        proc.addArgument("P_END_TIME", java.sql.Time.class);
        proc.addStatement("Insert INTO EMPLOYEE (EMP_ID, END_DATE, MANAGER_ID, START_DATE, F_NAME, L_NAME, GENDER, ADDR_ID, VERSION, START_TIME, END_TIME) " + "VALUES (P_EMP_ID, P_END_DATE, P_MANAGER_ID, P_START_DATE, P_F_NAME, P_L_NAME, P_GENDER, P_ADDR_ID, P_VERSION, P_START_TIME, P_END_TIME)");
        proc.addStatement("Insert INTO SALARY (SALARY, EMP_ID) VALUES (P_SALARY, P_EMP_ID)");
        return proc;
    }

    public PackageDefinition buildOraclePackage() {
        PackageDefinition types = new PackageDefinition();
        types.setName("Cursor_Type");
        types.addStatement("Type Any_Cursor is REF CURSOR");
        return types;
    }

    public StoredProcedureDefinition buildOracleReadAllProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_All_Employees");
        proc.addOutputArgument("RESULT_CURSOR", "CURSOR_TYPE.ANY_CURSOR");
        proc.addStatement("OPEN RESULT_CURSOR FOR Select E.*, S.* from EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildOracleReadObjectProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_Employee");
        proc.addArgument("P_EMP_ID", Long.class);
        proc.addOutputArgument("RESULT_CURSOR", "CURSOR_TYPE.ANY_CURSOR");
        proc.addStatement("OPEN RESULT_CURSOR FOR Select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID AND E.EMP_ID = P_EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildOracleStoredProcedureInOutPut() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("StoredProcedure_InOutput");
        proc.addInOutputArgument("P_EMP_ID", Long.class);
        proc.addInOutputArgument("P_F_NAME", String.class);
        proc.addStatement("P_EMP_ID := P_EMP_ID");
        proc.addStatement("P_F_NAME := P_F_NAME");
        return proc;
    }

    public StoredProcedureDefinition buildOracleStoredProcedureTimestamp() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("StoredProcedure_Timestamp");
        proc.addInOutputArgument("CURRENT_DATE", java.sql.Timestamp.class);
        proc.addStatement("CURRENT_DATE := CURRENT_DATE");
        return proc;
    }

    public StoredProcedureDefinition buildOracleStoredProcedureInOutOutIn() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("StoredProcedure_InOut_Out_In");
        proc.addInOutputArgument("P_INOUT", Long.class);
        proc.addOutputArgument("P_OUT", Long.class);
        proc.addArgument("P_IN", Long.class);
        proc.addStatement("P_OUT := P_INOUT");
        proc.addStatement("P_INOUT := P_IN");
        return proc;
    }

    public StoredProcedureDefinition buildOracleStoredProcedureARRAY() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("StoredProcedure_ARRAY");
        proc.addOutputArgument("P_OUT", "TEST_STRING_ARRAY");
        proc.addStatement("P_OUT := TEST_STRING_ARRAY(10)");
        return proc;
    }
    
    public static VarrayDefinition buildVARRAYTypeDefinition() {
		VarrayDefinition definition = new VarrayDefinition();

		definition.setName("TEST_STRING_ARRAY");
		definition.setSize(20);
		definition.setType(String.class);
		definition.setTypeSize(30);

		return definition;
	}

    public StoredFunctionDefinition buildOracleStoredFunctionInOutOutIn() {
        StoredFunctionDefinition func = new StoredFunctionDefinition();
        func.setName("StoredFunction_InOut_Out_In");
        func.addInOutputArgument("P_INOUT", Long.class);
        func.addOutputArgument("P_OUT", Long.class);
        func.addArgument("P_IN", Long.class);
        func.setReturnType(Long.class);
        func.addStatement("P_OUT := P_INOUT");
        func.addStatement("P_INOUT := P_IN");
        func.addStatement("RETURN P_OUT");
        return func;
    }

    public StoredProcedureDefinition buildOracleUpdateProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Update_Employee");
        proc.addArgument("P_EMP_ID", Long.class);
        proc.addArgument("P_SALARY", Integer.class);
        proc.addArgument("P_END_DATE", java.sql.Date.class);
        proc.addArgument("P_MANAGER_ID", Long.class);
        proc.addArgument("P_START_DATE", java.sql.Date.class);
        proc.addArgument("P_F_NAME", String.class, 40);
        proc.addArgument("P_L_NAME", String.class, 40);
        proc.addArgument("P_GENDER", String.class, 1);
        proc.addArgument("P_ADDR_ID", Long.class);
        proc.addArgument("P_START_TIME", java.sql.Time.class);
        proc.addArgument("P_END_TIME", java.sql.Time.class);
        proc.addOutputArgument("O_ERROR_CODE", Long.class);
        proc.addStatement("Update SALARY set SALARY = P_SALARY WHERE (EMP_ID = P_EMP_ID)");
        proc.addStatement("Update EMPLOYEE set END_DATE = P_END_DATE, MANAGER_ID = P_MANAGER_ID, " + "START_DATE = P_START_DATE, F_NAME = P_F_NAME, L_NAME = P_L_NAME, GENDER = P_GENDER, ADDR_ID = P_ADDR_ID " + "WHERE (EMP_ID = P_EMP_ID)");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerDeleteProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Delete_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addStatement("Delete FROM SALARY where EMP_ID = @EMP_ID");
        proc.addStatement("Delete FROM EMPLOYEE where EMP_ID = @EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerInsertProcedure() {
        // Assume no identity.
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Insert_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addArgument("SALARY", Integer.class);
        proc.addArgument("END_DATE", java.sql.Date.class);
        proc.addArgument("MANAGER_ID", Long.class);
        proc.addArgument("START_DATE", java.sql.Date.class);
        proc.addArgument("F_NAME", String.class, 40);
        proc.addArgument("L_NAME", String.class, 40);
        proc.addArgument("GENDER", String.class, 1);
        proc.addArgument("ADDR_ID", Long.class);
        proc.addOutputArgument("VERSION", Long.class);
        proc.addArgument("START_TIME", java.sql.Time.class);
        proc.addArgument("END_TIME", java.sql.Time.class);
        proc.addStatement("Insert INTO EMPLOYEE (EMP_ID, END_DATE, MANAGER_ID, START_DATE, F_NAME, L_NAME, GENDER, ADDR_ID, VERSION, START_TIME, END_TIME) " + "VALUES (@EMP_ID, @END_DATE, @MANAGER_ID, @START_DATE, @F_NAME, @L_NAME, @GENDER, @ADDR_ID, @VERSION, @START_TIME, @END_TIME)");
        proc.addStatement("Insert INTO SALARY (SALARY, EMP_ID) VALUES (@SALARY, @EMP_ID)");
        proc.addStatement("SELECT @VERSION = 952");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerReadAllProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_All_Employees");
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerReadObjectProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID AND E.EMP_ID = @EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerSelectWithOutputAndResultSetProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Select_Output_and_ResultSet");
        proc.addArgument("ARG1", Long.class);
        proc.addOutputArgument("VERSION", Long.class);
        proc.addStatement("SELECT @VERSION = 23");
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID AND E.F_NAME = 'Bob'");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerSelectWithOutputProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Select_Employee_using_Output");
        proc.addArgument("ARG1", Long.class);
        proc.addOutputArgument("VERSION", Long.class);
        proc.addStatement("SELECT @VERSION = 23");
        return proc;
    }

    public StoredProcedureDefinition buildSQLServerUpdateProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Update_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addArgument("SALARY", Integer.class);
        proc.addArgument("END_DATE", java.sql.Date.class);
        proc.addArgument("MANAGER_ID", Long.class);
        proc.addArgument("START_DATE", java.sql.Date.class);
        proc.addArgument("F_NAME", String.class, 40);
        proc.addArgument("L_NAME", String.class, 40);
        proc.addArgument("GENDER", String.class, 1);
        proc.addArgument("ADDR_ID", Long.class);
        proc.addArgument("VERSION", Long.class);
        proc.addArgument("START_TIME", java.sql.Time.class);
        proc.addArgument("END_TIME", java.sql.Time.class);
        proc.addStatement("Update SALARY set SALARY = @SALARY WHERE (EMP_ID = @EMP_ID)");
        proc.addStatement("Update EMPLOYEE set END_DATE = @END_DATE, MANAGER_ID = @MANAGER_ID, " + "START_DATE = @START_DATE, F_NAME = @F_NAME, L_NAME = @L_NAME, GENDER = @GENDER, ADDR_ID = @ADDR_ID, " + "VERSION = @VERSION + 1 WHERE ((EMP_ID = @EMP_ID) AND (VERSION = @VERSION))");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseDeleteProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Delete_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addStatement("Delete FROM SALARY where EMP_ID = @EMP_ID");
        proc.addStatement("Delete FROM EMPLOYEE where EMP_ID = @EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseInsertProcedure() {
        // Assume no identity.
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Insert_Employee");

        proc.addArgument("EMP_ID", Long.class);
        proc.addArgument("SALARY", Integer.class);
        proc.addArgument("END_DATE", java.sql.Date.class);
        proc.addArgument("MANAGER_ID", Long.class);
        proc.addArgument("START_DATE", java.sql.Date.class);
        proc.addArgument("F_NAME", String.class, 40);
        proc.addArgument("L_NAME", String.class, 40);
        proc.addArgument("GENDER", String.class, 1);
        proc.addArgument("ADDR_ID", Long.class);
        proc.addArgument("START_TIME", java.sql.Time.class);
        proc.addArgument("END_TIME", java.sql.Time.class);
        proc.addArgument("VERSION", Long.class);
        proc.addOutputArgument("OUT_VERSION", Long.class);

        proc.addStatement("Insert INTO EMPLOYEE (EMP_ID, END_DATE, MANAGER_ID, START_DATE, F_NAME, L_NAME, GENDER, ADDR_ID, VERSION, START_TIME, END_TIME) " + "VALUES (@EMP_ID, @END_DATE, @MANAGER_ID, @START_DATE, @F_NAME, @L_NAME, @GENDER, @ADDR_ID, @VERSION, @START_TIME, @END_TIME)");
        proc.addStatement("Insert INTO SALARY (SALARY, EMP_ID) VALUES (@SALARY, @EMP_ID)");
        proc.addStatement("Select @OUT_VERSION = 952");

        return proc;
    }

    public StoredProcedureDefinition buildSybaseReadAllProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_All_Employees");
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseReadObjectProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Read_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID AND E.EMP_ID = @EMP_ID");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseSelectWithOutputAndResultSetProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Select_Output_and_ResultSet");
        proc.addArgument("ARG1", Long.class);
        proc.addOutputArgument("VERSION", Long.class);
        proc.addStatement("SELECT @VERSION = 23");
        proc.addStatement("Select E.*, S.* from EMPLOYEE E, SALARY S where E.EMP_ID = S.EMP_ID AND E.F_NAME = 'Bob'");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseWithoutParametersProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Rise_All_Salaries");
        proc.addStatement("Update SALARY set SALARY = SALARY * 1.1");
        return proc;
    }

    public StoredProcedureDefinition buildSybaseUpdateProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("Update_Employee");
        proc.addArgument("EMP_ID", Long.class);
        proc.addArgument("SALARY", Integer.class);
        proc.addArgument("END_DATE", java.sql.Date.class);
        proc.addArgument("MANAGER_ID", Long.class);
        proc.addArgument("START_DATE", java.sql.Date.class);
        proc.addArgument("F_NAME", String.class, 40);
        proc.addArgument("L_NAME", String.class, 40);
        proc.addArgument("GENDER", String.class, 1);
        proc.addArgument("ADDR_ID", Long.class);
        proc.addArgument("VERSION", Long.class);
        proc.addArgument("START_TIME", java.sql.Time.class);
        proc.addArgument("END_TIME", java.sql.Time.class);
        proc.addStatement("Update SALARY set SALARY = @SALARY WHERE (EMP_ID = @EMP_ID)");
        proc.addStatement("Update EMPLOYEE set END_DATE = @END_DATE, MANAGER_ID = @MANAGER_ID, " + "START_DATE = @START_DATE, F_NAME = @F_NAME, L_NAME = @L_NAME, GENDER = @GENDER, ADDR_ID = @ADDR_ID, " + "VERSION = @VERSION + 1 WHERE ((EMP_ID = @EMP_ID) AND (VERSION = @VERSION))");
        return proc;
    }
    
    private static StoredProcedureDefinition buildSQLAnywhereDeleteProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Delete_Employee");
        procedure.addArgument("_EMP_ID", Long.class);
        procedure.addStatement("DELETE FROM SALARY WHERE EMP_ID = _EMP_ID");
        procedure.addStatement("DELETE FROM EMPLOYEE WHERE EMP_ID = _EMP_ID");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereInsertProcedure() {
        // Assume no identity.
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Insert_Employee");
        procedure.addArgument("_EMP_ID", Long.class);
        procedure.addArgument("_SALARY", Integer.class);
        procedure.addArgument("_END_DATE", java.sql.Date.class);
        procedure.addArgument("_MANAGER_ID", Long.class);
        procedure.addArgument("_START_DATE", java.sql.Date.class);
        procedure.addArgument("_F_NAME", String.class, 40);
        procedure.addArgument("_L_NAME", String.class, 40);
        procedure.addArgument("_GENDER", String.class, 1);
        procedure.addArgument("_ADDR_ID", Long.class);
        procedure.addArgument("_START_TIME", java.sql.Time.class);
        procedure.addArgument("_END_TIME", java.sql.Time.class);
        procedure.addArgument("_VERSION", Long.class);
        procedure.addOutputArgument("_OUT_VERSION", Long.class);
        procedure.addStatement("INSERT INTO EMPLOYEE (EMP_ID, END_DATE, MANAGER_ID, START_DATE, F_NAME, L_NAME, GENDER, ADDR_ID, VERSION, START_TIME, END_TIME) VALUES (_EMP_ID, _END_DATE, _MANAGER_ID, _START_DATE, _F_NAME, _L_NAME, _GENDER, _ADDR_ID, _VERSION, _START_TIME, _END_TIME)");
        procedure.addStatement("INSERT INTO SALARY (SALARY, EMP_ID) VALUES (_SALARY, _EMP_ID)");
        procedure.addStatement("SET _OUT_VERSION = 952");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereReadAllProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Read_All_Employees");
        procedure.addStatement("SELECT E.*, S.* FROM EMPLOYEE E JOIN SALARY S ON E.EMP_ID = S.EMP_ID");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereReadObjectProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Read_Employee");
        procedure.addArgument("_EMP_ID", Long.class);
        procedure.addStatement("SELECT E.*, S.* FROM EMPLOYEE E JOIN SALARY S ON E.EMP_ID = S.EMP_ID WHERE E.EMP_ID = _EMP_ID");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereSelectWithOutputAndResultSetProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Select_Output_and_ResultSet");
        procedure.addArgument("ARG1", Long.class);
        procedure.addOutputArgument("VERSION", Long.class);
        procedure.addStatement("SET VERSION = 23");
        procedure.addStatement("SELECT E.*, S.* FROM EMPLOYEE E JOIN SALARY S ON E.EMP_ID = S.EMP_ID WHERE E.F_NAME = 'Bob'");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereWithoutParametersProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Rise_All_Salaries");
        procedure.addStatement("UPDATE SALARY SET SALARY = SALARY * 1.1");
        return procedure;
    }

    private static StoredProcedureDefinition buildSQLAnywhereUpdateProcedure() {
        final StoredProcedureDefinition procedure = new StoredProcedureDefinition();
        procedure.setName("Update_Employee");
        procedure.addArgument("_EMP_ID", Long.class);
        procedure.addArgument("_SALARY", Integer.class);
        procedure.addArgument("_END_DATE", java.sql.Date.class);
        procedure.addArgument("_MANAGER_ID", Long.class);
        procedure.addArgument("_START_DATE", java.sql.Date.class);
        procedure.addArgument("_F_NAME", String.class, 40);
        procedure.addArgument("_L_NAME", String.class, 40);
        procedure.addArgument("_GENDER", String.class, 1);
        procedure.addArgument("_ADDR_ID", Long.class);
        procedure.addArgument("_VERSION", Long.class);
        procedure.addArgument("_START_TIME", java.sql.Time.class);
        procedure.addArgument("_END_TIME", java.sql.Time.class);
        procedure.addStatement("UPDATE SALARY SET SALARY = _SALARY WHERE EMP_ID = _EMP_ID");
        procedure.addStatement("UPDATE EMPLOYEE SET END_DATE = _END_DATE, MANAGER_ID = _MANAGER_ID, START_DATE = _START_DATE, F_NAME = _F_NAME, L_NAME = _L_NAME, GENDER = _GENDER, ADDR_ID = _ADDR_ID, VERSION = _VERSION + 1 WHERE EMP_ID = _EMP_ID AND VERSION = _VERSION");
        return procedure;
    }
    
    public StoredProcedureDefinition buildDB2SelectWithOutputAndResultSetProcedure() {
        StoredProcedureDefinition proc = new StoredProcedureDefinition();
        proc.setName("OUT_RES_TEST");
        proc.addOutputArgument("OUT1", Integer.class);
        proc.addOutputArgument("OUT2", Integer.class);
        proc.addOutputArgument("OUT3", Integer.class);
        proc.addStatement("DECLARE C2 CURSOR WITH RETURN FOR SELECT F_NAME FROM EMPLOYEE");
        // DB2 driver seems to have an issue if you assign a value to outputs... so leave as null for now.
        //proc.addStatement("SELECT MAX(SALARY) INTO OUT1 FROM SALARY");
        //proc.addStatement("SELECT MAX(SALARY) INTO OUT2 FROM SALARY");
        //proc.addStatement("SELECT MAX(SALARY) INTO OUT3 FROM SALARY");
        proc.addStatement("OPEN C2");
        return proc;
    }
    
    /**
     * Also creates the procs.
     */
    public void createTables(DatabaseSession session) {
        super.createTables(session);
        org.eclipse.persistence.internal.databaseaccess.DatabasePlatform platform = session.getLogin().getPlatform();
        SchemaManager schema = new SchemaManager((session));

        if (platform.isSQLServer()) {
            schema.replaceObject(buildSQLServerDeleteProcedure());
            schema.replaceObject(buildSQLServerReadAllProcedure());
            schema.replaceObject(buildSQLServerReadObjectProcedure());
            schema.replaceObject(buildSQLServerInsertProcedure());
            schema.replaceObject(buildSQLServerUpdateProcedure());
            schema.replaceObject(buildSQLServerSelectWithOutputProcedure());
            schema.replaceObject(buildSQLServerSelectWithOutputAndResultSetProcedure());
        }
        if (platform.isSybase()) {
            session.getLogin().handleTransactionsManuallyForSybaseJConnect();
            schema.replaceObject(buildSybaseDeleteProcedure());
            schema.replaceObject(buildSybaseReadAllProcedure());
            schema.replaceObject(buildSybaseReadObjectProcedure());
            schema.replaceObject(buildSybaseInsertProcedure());
            schema.replaceObject(buildSybaseUpdateProcedure());
            schema.replaceObject(buildSybaseSelectWithOutputAndResultSetProcedure());
            schema.replaceObject(buildSybaseWithoutParametersProcedure());
        }

        if (platform.isSQLAnywhere()) {
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereDeleteProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereReadAllProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereReadObjectProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereInsertProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereUpdateProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereSelectWithOutputAndResultSetProcedure());
            schema.replaceObject(EmployeeCustomSQLSystem.buildSQLAnywhereWithoutParametersProcedure());
        }

        if (platform.isOracle()) {
            schema.replaceObject(buildOracleStoredProcedureInOutPut());
            schema.replaceObject(buildOracleStoredProcedureInOutOutIn());
            schema.replaceObject(buildOracleStoredProcedureTimestamp());
            schema.replaceObject(buildOracleStoredProcedureARRAY());
            schema.replaceObject(buildVARRAYTypeDefinition());
            schema.replaceObject(buildOracleStoredFunctionInOutOutIn());
            schema.replaceObject(buildOraclePackage());
            schema.replaceObject(buildOracleDeleteProcedure());
            schema.replaceObject(buildOracleReadAllProcedure());
            schema.replaceObject(buildOracleReadObjectProcedure());
            schema.replaceObject(buildOracleInsertProcedure());
            schema.replaceObject(buildOracleUpdateProcedure());
        }
        if (platform.isDB2()) {
            schema.replaceObject(buildDB2SelectWithOutputAndResultSetProcedure());
        }
    }

    protected void setCommonSQL(Session session) {
        ClassDescriptor empDescriptor = session.getDescriptor(Employee.class);
        empDescriptor.getQueryManager().setDoesExistSQLString("select EMP_ID FROM EMPLOYEE WHERE EMP_ID = #EMP_ID");

        OneToOneMapping managerMapping = (OneToOneMapping)empDescriptor.getMappingForAttributeName("manager");
        managerMapping.setSelectionSQLString("select * FROM EMPLOYEE WHERE EMP_ID = #MANAGER_ID");
        OneToOneMapping oneToOne = (OneToOneMapping)empDescriptor.getMappingForAttributeName("address");
        oneToOne.setSelectionSQLString("select * FROM ADDRESS WHERE ADDRESS_ID = #ADDR_ID");

        OneToManyMapping oneToMany = (OneToManyMapping)empDescriptor.getMappingForAttributeName("managedEmployees");
        oneToMany.setSelectionSQLString("select E.*, S.* FROM EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID AND E.MANAGER_ID = #EMP_ID");

        DirectCollectionMapping directCollection = (DirectCollectionMapping)empDescriptor.getMappingForAttributeName("responsibilitiesList");
        directCollection.setSelectionSQLString("select DESCRIP FROM RESPONS WHERE EMP_ID = #EMP_ID");
        directCollection.setDeleteAllSQLString("delete FROM RESPONS WHERE EMP_ID = #EMP_ID");
        directCollection.setInsertSQLString("insert into RESPONS (EMP_ID, DESCRIP) values (#EMP_ID, #DESCRIP)");

        ManyToManyMapping manyToMany = (ManyToManyMapping)empDescriptor.getMappingForAttributeName("projects");
        manyToMany.setDeleteAllSQLString("delete FROM PROJ_EMP WHERE EMP_ID = #EMP_ID");
        manyToMany.setDeleteSQLString("delete FROM PROJ_EMP WHERE EMP_ID = #EMP_ID AND PROJ_ID = #PROJ_ID");
        manyToMany.setInsertSQLString("insert into PROJ_EMP (EMP_ID, PROJ_ID) values (#EMP_ID, #PROJ_ID)");

        ClassDescriptor projectDescriptor = session.getDescriptor(Project.class);
        projectDescriptor.getQueryManager().setDoesExistSQLString("select PROJ_ID FROM PROJECT WHERE PROJ_ID = #PROJ_ID");
        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        deleteQuery.addCall(new SQLCall("delete FROM PROJECT WHERE PROJ_ID = #PROJ_ID"));
        deleteQuery.addCall(new SQLCall("delete FROM LPROJECT WHERE PROJ_ID = #PROJ_ID"));
        projectDescriptor.getQueryManager().setDeleteQuery(deleteQuery);

        // Must disable locking for project delete to work.
        projectDescriptor.setOptimisticLockingPolicy(null);
        ClassDescriptor smallProjectDescriptor = session.getDescriptor(SmallProject.class);
        smallProjectDescriptor.setOptimisticLockingPolicy(null);
        ClassDescriptor largeProjectDescriptor = session.getDescriptor(LargeProject.class);
        largeProjectDescriptor.setOptimisticLockingPolicy(null);
    }

    protected void setCustomSQL(Session session) {
        setCommonSQL(session);
        if (session.getLogin().getPlatform().isSybase()) {
            setSybaseSQL(session);
        } else if (session.getLogin().getPlatform().isSQLAnywhere()) {
            EmployeeCustomSQLSystem.setSQLAnywhereSQL(session);
        } else if (session.getLogin().getPlatform().isSQLServer()) {
            setSQLServerSQL(session);
        } else if (session.getLogin().isAnyOracleJDBCDriver()) {// Require output cursor support.
            setOracleSQL(session);
        } else {
            ClassDescriptor empDescriptor = session.getDescriptor(new Employee());
            empDescriptor.getQueryManager().setReadObjectSQLString("select E.*, S.* FROM EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID AND E.EMP_ID = #EMP_ID");
            empDescriptor.getQueryManager().setReadAllSQLString("select E.*, S.* FROM EMPLOYEE E, SALARY S WHERE E.EMP_ID = S.EMP_ID");
            ManyToManyMapping manyToMany = (ManyToManyMapping)empDescriptor.getMappingForAttributeName("projects");
            ReadAllQuery readQuery = new ReadAllQuery();
            readQuery.addCall(new SQLCall("select P.*, L.* FROM LPROJECT L, PROJECT P, PROJ_EMP PE WHERE ((P.PROJ_ID = L.PROJ_ID) AND (PE.EMP_ID = #EMP_ID) AND (P.PROJ_ID = PE.PROJ_ID))"));
            readQuery.addCall(new SQLCall("select P.* FROM PROJECT P, PROJ_EMP PE WHERE (PE.EMP_ID = #EMP_ID) AND (P.PROJ_ID = PE.PROJ_ID) AND (P.PROJ_TYPE = 'S')"));
            manyToMany.setCustomSelectionQuery(readQuery);
            UpdateObjectQuery updateQuery = new UpdateObjectQuery();
            updateQuery.addCall(new SQLCall("update EMPLOYEE SET END_DATE = #END_DATE, MANAGER_ID = #MANAGER_ID, START_DATE = #START_DATE, F_NAME = #F_NAME, L_NAME = #L_NAME, GENDER = #GENDER, ADDR_ID = #ADDR_ID, VERSION = ##VERSION WHERE ((EMP_ID = #EMP_ID) AND (VERSION = #VERSION))"));
            updateQuery.addCall(new SQLCall("update SALARY SET SALARY = #SALARY WHERE (EMP_ID = #EMP_ID)"));
            empDescriptor.getQueryManager().setUpdateQuery(updateQuery);
            InsertObjectQuery insertQuery = new InsertObjectQuery();
            insertQuery.addCall(new SQLCall("insert INTO EMPLOYEE (END_DATE, EMP_ID, MANAGER_ID, START_DATE, F_NAME, L_NAME, GENDER, ADDR_ID, VERSION, START_TIME, END_TIME) VALUES (#END_DATE, #EMP_ID, #MANAGER_ID, #START_DATE, #F_NAME, #L_NAME, #GENDER, #ADDR_ID, #VERSION, #START_TIME, #END_TIME)"));
            insertQuery.addCall(new SQLCall("insert INTO SALARY (SALARY, EMP_ID) VALUES (#SALARY, #EMP_ID)"));
            empDescriptor.getQueryManager().setInsertQuery(insertQuery);
            DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
            deleteQuery.addCall(new SQLCall("delete FROM EMPLOYEE WHERE EMP_ID = #EMP_ID"));
            deleteQuery.addCall(new SQLCall("delete FROM SALARY WHERE EMP_ID = #EMP_ID"));
            empDescriptor.getQueryManager().setDeleteQuery(deleteQuery);
        }
    }

    protected void setOracleSQL(Session session) {
        ClassDescriptor empDescriptor = session.getDescriptor(new Employee());
        StoredProcedureCall call;

        // Currently the rowcount does not work, so disable locking.
        empDescriptor.setOptimisticLockingPolicy(null);
        ReadObjectQuery readQuery = new ReadObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_Employee");
        call.addNamedArgument("P_EMP_ID", "EMP_ID");
        call.useNamedCursorOutputAsResultSet("RESULT_CURSOR");
        readQuery.setCall(call);
        empDescriptor.getQueryManager().setReadObjectQuery(readQuery);
        ReadAllQuery readAllQuery = new ReadAllQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_All_Employees");
        call.useNamedCursorOutputAsResultSet("RESULT_CURSOR");
        readAllQuery.setCall(call);
        empDescriptor.getQueryManager().setReadAllQuery(readAllQuery);
        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Delete_Employee");
        call.addNamedArgument("P_EMP_ID", "EMP_ID");
        deleteQuery.setCall(call);
        empDescriptor.getQueryManager().setDeleteQuery(deleteQuery);
        InsertObjectQuery insertQuery = new InsertObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Insert_Employee");
        call.addNamedArgument("P_EMP_ID", "EMP_ID");
        call.addNamedArgument("P_SALARY", "SALARY");
        call.addNamedArgument("P_END_DATE", "END_DATE");
        call.addNamedArgument("P_MANAGER_ID", "MANAGER_ID");
        call.addNamedArgument("P_START_DATE", "START_DATE");
        call.addNamedArgument("P_F_NAME", "F_NAME");
        call.addNamedArgument("P_L_NAME", "L_NAME");
        call.addNamedArgument("P_GENDER", "GENDER");
        call.addNamedArgument("P_ADDR_ID", "ADDR_ID");
        call.addNamedArgument("P_VERSION", "VERSION");
        call.addNamedArgument("P_START_TIME", "START_TIME");
        call.addNamedArgument("P_END_TIME", "END_TIME");
        insertQuery.setCall(call);
        empDescriptor.getQueryManager().setInsertQuery(insertQuery);
        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        call = new StoredProcedureCall();
        call.setUsesBinding(true);
        call.setShouldCacheStatement(true);
        call.setProcedureName("Update_Employee");
        call.addNamedArgument("P_EMP_ID", "EMP_ID");
        call.addNamedArgument("P_SALARY", "SALARY");
        call.addNamedArgument("P_END_DATE", "END_DATE");
        call.addNamedArgument("P_MANAGER_ID", "MANAGER_ID");
        call.addNamedArgument("P_START_DATE", "START_DATE");
        call.addNamedArgument("P_F_NAME", "F_NAME");
        call.addNamedArgument("P_L_NAME", "L_NAME");
        call.addNamedArgument("P_GENDER", "GENDER");
        call.addNamedArgument("P_ADDR_ID", "ADDR_ID");
        call.addNamedArgument("P_START_TIME", "START_TIME");
        call.addNamedArgument("P_END_TIME", "END_TIME");
        call.addNamedOutputArgument("O_ERROR_CODE", "O_ERROR_CODE", Long.class);
        updateQuery.setCall(call);
        empDescriptor.getQueryManager().setUpdateQuery(updateQuery);
        ManyToManyMapping manyToMany = (ManyToManyMapping)empDescriptor.getMappingForAttributeName("projects");
        manyToMany.setSelectionSQLString("select P.*, L.* FROM LPROJECT L, PROJECT P, PROJ_EMP PE WHERE ((P.PROJ_ID = L.PROJ_ID (+)) AND (PE.EMP_ID = #EMP_ID) AND (P.PROJ_ID = PE.PROJ_ID))");
    }

    protected void setSQLServerSQL(Session session) {
        ClassDescriptor empDescriptor = session.getDescriptor(new Employee());
        StoredProcedureCall call;

        // Currently the rowcount does not work, so disable locking.
        empDescriptor.setOptimisticLockingPolicy(null);
        session.getLogin().getPlatform().setUsesNativeSQL(true);

        ReadObjectQuery readQuery = new ReadObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_Employee");
        call.addNamedArgument("EMP_ID");
        call.setReturnsResultSet(true);
        readQuery.setCall(call);
        empDescriptor.getQueryManager().setReadObjectQuery(readQuery);

        ReadAllQuery readAllQuery = new ReadAllQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_All_Employees");
        call.setReturnsResultSet(true);
        readAllQuery.setCall(call);
        empDescriptor.getQueryManager().setReadAllQuery(readAllQuery);

        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Delete_Employee");
        call.addNamedArgument("EMP_ID");
        deleteQuery.setCall(call);
        empDescriptor.getQueryManager().setDeleteQuery(deleteQuery);

        InsertObjectQuery insertQuery = new InsertObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Insert_Employee");
        call.setUsesBinding(true);
        call.setShouldCacheStatement(true);
        call.addNamedArgument("EMP_ID");
        call.addNamedArgument("SALARY");
        call.addNamedArgument("END_DATE");
        call.addNamedArgument("MANAGER_ID");
        call.addNamedArgument("START_DATE");
        call.addNamedArgument("F_NAME");
        call.addNamedArgument("L_NAME");
        call.addNamedArgument("GENDER");
        call.addNamedArgument("ADDR_ID");
        //call.addNamedArgument("VERSION");
        call.addNamedOutputArgument("VERSION", "EMPLOYEE.VERSION", java.math.BigDecimal.class);
        call.addNamedArgument("START_TIME");
        call.addNamedArgument("END_TIME");
        insertQuery.setCall(call);
        empDescriptor.getQueryManager().setInsertQuery(insertQuery);

        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Update_Employee");
        call.addNamedArgument("EMP_ID");
        call.addNamedArgument("SALARY");
        call.addNamedArgument("END_DATE");
        call.addNamedArgument("MANAGER_ID");
        call.addNamedArgument("START_DATE");
        call.addNamedArgument("F_NAME");
        call.addNamedArgument("L_NAME");
        call.addNamedArgument("GENDER");
        call.addNamedArgument("ADDR_ID");
        call.addNamedArgument("VERSION");
        call.addNamedArgument("START_TIME");
        call.addNamedArgument("END_TIME");
        updateQuery.setCall(call);
        empDescriptor.getQueryManager().setUpdateQuery(updateQuery);

        ManyToManyMapping manyToMany = (ManyToManyMapping)empDescriptor.getMappingForAttributeName("projects");
        manyToMany.setSelectionSQLString("select P.*, L.* FROM PROJ_EMP PE, PROJECT P LEFT OUTER JOIN LPROJECT L ON (L.PROJ_ID = P.PROJ_ID) WHERE ((PE.EMP_ID = #EMP_ID) AND (P.PROJ_ID = PE.PROJ_ID))");
    }

    protected void setSybaseSQL(Session session) {
        ClassDescriptor empDescriptor = session.getDescriptor(new Employee());
        StoredProcedureCall call;

        ReadObjectQuery readQuery = new ReadObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_Employee");
        call.addNamedArgument("EMP_ID");
        call.setReturnsResultSet(true);
        readQuery.setCall(call);
        empDescriptor.getQueryManager().setReadObjectQuery(readQuery);

        ReadAllQuery readAllQuery = new ReadAllQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Read_All_Employees");
        call.setReturnsResultSet(true);
        readAllQuery.setCall(call);
        empDescriptor.getQueryManager().setReadAllQuery(readAllQuery);

        DeleteObjectQuery deleteQuery = new DeleteObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Delete_Employee");
        call.addNamedArgument("EMP_ID");
        deleteQuery.setCall(call);
        empDescriptor.getQueryManager().setDeleteQuery(deleteQuery);

        InsertObjectQuery insertQuery = new InsertObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Insert_Employee");
        call.setUsesBinding(true);
        call.setShouldCacheStatement(true);
        call.addNamedArgument("EMP_ID");
        call.addNamedArgument("SALARY");
        call.addNamedArgument("END_DATE");
        call.addNamedArgument("MANAGER_ID");
        call.addNamedArgument("START_DATE");
        call.addNamedArgument("F_NAME");
        call.addNamedArgument("L_NAME");
        call.addNamedArgument("GENDER");
        call.addNamedArgument("ADDR_ID");
        call.addNamedArgument("VERSION");
        call.addNamedArgument("START_TIME");
        call.addNamedArgument("END_TIME");
        call.addNamedInOutputArgumentValue("OUT_VERSION", new Long(0), "EMPLOYEE.VERSION", Long.class);
        insertQuery.setCall(call);
        empDescriptor.getQueryManager().setInsertQuery(insertQuery);

        UpdateObjectQuery updateQuery = new UpdateObjectQuery();
        call = new StoredProcedureCall();
        call.setProcedureName("Update_Employee");
        call.addNamedArgument("EMP_ID");
        call.addNamedArgument("SALARY");
        call.addNamedArgument("END_DATE");
        call.addNamedArgument("MANAGER_ID");
        call.addNamedArgument("START_DATE");
        call.addNamedArgument("F_NAME");
        call.addNamedArgument("L_NAME");
        call.addNamedArgument("GENDER");
        call.addNamedArgument("ADDR_ID");
        call.addNamedArgument("VERSION");
        call.addNamedArgument("START_TIME");
        call.addNamedArgument("END_TIME");
        updateQuery.setCall(call);
        empDescriptor.getQueryManager().setUpdateQuery(updateQuery);

        ManyToManyMapping manyToMany = (ManyToManyMapping)empDescriptor.getMappingForAttributeName("projects");
        manyToMany.setSelectionSQLString("select P.*, L.* FROM PROJ_EMP PE, PROJECT P LEFT OUTER JOIN LPROJECT L ON (L.PROJ_ID = P.PROJ_ID) WHERE ((PE.EMP_ID = #EMP_ID) AND (P.PROJ_ID = PE.PROJ_ID))");
    }

    private static void setSQLAnywhereSQL(final Session session) {
        final ClassDescriptor employeeDescriptor = session.getDescriptor(new Employee());

        final StoredProcedureCall readEmployeeCall = new StoredProcedureCall();
        readEmployeeCall.setProcedureName("Read_Employee");
        readEmployeeCall.addNamedArgument("_EMP_ID", "EMP_ID");
        readEmployeeCall.setReturnsResultSet(true);
        employeeDescriptor.getQueryManager().setReadObjectQuery(new ReadObjectQuery(readEmployeeCall));

        final StoredProcedureCall readAllEmployeesCall = new StoredProcedureCall();
        readAllEmployeesCall.setProcedureName("Read_All_Employees");
        readAllEmployeesCall.setReturnsResultSet(true);
        employeeDescriptor.getQueryManager().setReadAllQuery(new ReadAllQuery(readAllEmployeesCall));

        final StoredProcedureCall deleteEmployeeCall = new StoredProcedureCall();
        deleteEmployeeCall.setProcedureName("Delete_Employee");
        deleteEmployeeCall.addNamedArgument("_EMP_ID", "EMP_ID");
        employeeDescriptor.getQueryManager().setDeleteQuery(new DeleteObjectQuery(deleteEmployeeCall));

        final StoredProcedureCall insertEmployeeCall = new StoredProcedureCall();
        insertEmployeeCall.setProcedureName("Insert_Employee");
        insertEmployeeCall.setUsesBinding(true);
        insertEmployeeCall.setShouldCacheStatement(true);
        insertEmployeeCall.addNamedArgument("_EMP_ID", "EMP_ID");
        insertEmployeeCall.addNamedArgument("_SALARY", "SALARY");
        insertEmployeeCall.addNamedArgument("_END_DATE", "END_DATE");
        insertEmployeeCall.addNamedArgument("_MANAGER_ID", "MANAGER_ID");
        insertEmployeeCall.addNamedArgument("_START_DATE", "START_DATE");
        insertEmployeeCall.addNamedArgument("_F_NAME", "F_NAME");
        insertEmployeeCall.addNamedArgument("_L_NAME", "L_NAME");
        insertEmployeeCall.addNamedArgument("_GENDER", "GENDER");
        insertEmployeeCall.addNamedArgument("_ADDR_ID", "ADDR_ID");
//        insertEmployeeCall.addNamedArgument("_VERSION", "VERSION");
        insertEmployeeCall.addNamedArgument("_START_TIME", "START_TIME");
        insertEmployeeCall.addNamedArgument("_END_TIME", "END_TIME");
        // The order of the arguments shouldn't matter because they are named,
        // but SQLAnywhere 10 for some reason can't handle named parameters
        // (In JPA tests:
        // CALL SProc_Read_InOut(address_id_v = ? , street_v = ? )
        // bind => [17 => ADDRESS_ID, => STREET]
        // fails with java.sql.SQLException: [Sybase][ODBC Driver]Invalid parameter type
        // Until that fixed, naming is switched off
        // (SQLAnywherePlatform.shouldPrintStoredProcedureArgumentNameInCall() returns false),
        // and therefore arguments should be passed exactly in the same order as parameters defined in the sp.
        // After this is fixed (m.b. in SQLAnywhere 11?) the order of the attributes should be returned to original
        // (where it does NOT follow the order of sp parameters).
        insertEmployeeCall.addNamedArgument("_VERSION", "VERSION");
        insertEmployeeCall.addNamedInOutputArgumentValue("_OUT_VERSION", new Long(0), "EMPLOYEE.VERSION", Long.class);
        employeeDescriptor.getQueryManager().setInsertQuery(new InsertObjectQuery(insertEmployeeCall));

        final StoredProcedureCall updateEmployeeCall = new StoredProcedureCall();
        updateEmployeeCall.setProcedureName("Update_Employee");
        updateEmployeeCall.addNamedArgument("_EMP_ID", "EMP_ID");
        updateEmployeeCall.addNamedArgument("_SALARY", "SALARY");
        updateEmployeeCall.addNamedArgument("_END_DATE", "END_DATE");
        updateEmployeeCall.addNamedArgument("_MANAGER_ID", "MANAGER_ID");
        updateEmployeeCall.addNamedArgument("_START_DATE", "START_DATE");
        updateEmployeeCall.addNamedArgument("_F_NAME", "F_NAME");
        updateEmployeeCall.addNamedArgument("_L_NAME", "L_NAME");
        updateEmployeeCall.addNamedArgument("_GENDER", "GENDER");
        updateEmployeeCall.addNamedArgument("_ADDR_ID", "ADDR_ID");
        updateEmployeeCall.addNamedArgument("_VERSION", "VERSION");
        updateEmployeeCall.addNamedArgument("_START_TIME", "START_TIME");
        updateEmployeeCall.addNamedArgument("_END_TIME", "END_TIME");
        employeeDescriptor.getQueryManager().setUpdateQuery(new UpdateObjectQuery(updateEmployeeCall));

        final ManyToManyMapping manyToMany = (ManyToManyMapping) employeeDescriptor.getMappingForAttributeName("projects");
        manyToMany.setSelectionSQLString("SELECT P.*, L.* FROM PROJ_EMP PE JOIN PROJECT P ON PE.PROJ_ID = P.PROJ_ID LEFT OUTER JOIN LPROJECT L ON P.PROJ_ID = L.PROJ_ID WHERE PE.EMP_ID = #EMP_ID");
    }

}
