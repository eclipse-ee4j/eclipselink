/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import java.util.Map;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public class SQLUpdateAllStatementForOracleAnonymousBlock extends SQLModifyStatement {
    protected HashMap tables_databaseFieldsToValues;
    protected HashMap tablesToPrimaryKeyFields;
    protected SQLCall selectCall;
    
    protected static final String varSuffix = "_VAR";
    protected static final String typeSuffix = "_TYPE";
    protected static final String tab = "   ";
    protected static final String dbltab = tab + tab;
    protected static final String trpltab = dbltab + tab;
    
    public void setSelectCall(SQLCall selectCall) {
        this.selectCall = selectCall;
    }
    public SQLCall getSelectCall() {
        return selectCall;
    }
    public void setTablesToPrimaryKeyFields(HashMap tablesToPrimaryKeyFields) {
        this.tablesToPrimaryKeyFields = tablesToPrimaryKeyFields;
    }
    public HashMap getTablesToPrimaryKeyFields() {
        return tablesToPrimaryKeyFields;
    }
    public void setTables_databaseFieldsToValues(HashMap tables_databaseFieldsToValues) {
        this.tables_databaseFieldsToValues = tables_databaseFieldsToValues;
    }
    public HashMap getTables_databaseFieldsToValues() {
        return tables_databaseFieldsToValues;
    }

    /**
     * Append the string containing the SQL insert string for the given table.
     */
    public DatabaseCall buildCall(AbstractSession session) {
        SQLCall call = new SQLCall();
        call.returnNothing();
        
        Writer writer = new CharArrayWriter(100);
        
        Vector mainPrimaryKeys = new Vector();
        mainPrimaryKeys.addAll((Collection)tablesToPrimaryKeyFields.get(table));
        
        Vector allFields = (Vector)mainPrimaryKeys.clone();
        Iterator itDatabaseFieldsToValues = tables_databaseFieldsToValues.values().iterator();
        while(itDatabaseFieldsToValues.hasNext()) {
            Iterator itDatabaseFields = ((HashMap)itDatabaseFieldsToValues.next()).keySet().iterator();
            while(itDatabaseFields.hasNext()) {
                allFields.addElement(itDatabaseFields.next());
            }
        }
        
        try {
            //DECLARE
            writer.write("DECLARE\n");
            
            for(int i=0; i < allFields.size(); i++) {
                writeDeclareTypeAndVar(writer, (DatabaseField)allFields.elementAt(i), session.getPlatform());
            }

            //BEGIN
            writer.write("BEGIN\n");

            //  select t0.emp_id, concat('Even', t0.f_name), t1.salary + 1000 BULK COLLECT into EMPLOYEEE_EMP_ID_VAR, EMPLOYEEE_F_NAME_VAR, SALARY_SALARY_VAR from employee t0, salary t1 where t0.l_name like 'updateEmployeeTestUsingTempTable' and t0.f_name in ('0', '2') and t1.salary = 0 and t0.emp_id = t1.emp_id;
            String selectStr = selectCall.getSQLString();
            int index = selectStr.toUpperCase().indexOf(" FROM ");
            String firstPart = selectStr.substring(0, index);
            String secondPart = selectStr.substring(index, selectStr.length());
            
            writer.write(tab);
            writer.write(firstPart);
            writer.write(" BULK COLLECT INTO ");

            for(int i=0; i < allFields.size(); i++) {
                writeVar(writer, (DatabaseField)allFields.elementAt(i), session.getPlatform());
                if(i < allFields.size() - 1) {
                    writer.write(", ");
                }
            }
            writer.write(secondPart);
            writer.write(";\n");

            call.getParameters().addAll(selectCall.getParameters());
            call.getParameterTypes().addAll(selectCall.getParameterTypes());            

            DatabaseField firstMainPrimaryKey = (DatabaseField)mainPrimaryKeys.firstElement();
            writer.write(tab);
            writer.write("IF ");
            writeVar(writer, firstMainPrimaryKey, session.getPlatform());
            writer.write(".COUNT > 0 THEN\n");
            
            Iterator itEntries = tables_databaseFieldsToValues.entrySet().iterator();
            while(itEntries.hasNext()) {
                writeForAll(writer, firstMainPrimaryKey, session.getPlatform());
                writer.write(trpltab);
                writer.write("UPDATE ");
                Map.Entry entry = (Map.Entry)itEntries.next();
                DatabaseTable t = (DatabaseTable)entry.getKey();
                writer.write(t.getQualifiedNameDelimited(session.getPlatform()));
                writer.write(" SET ");
                HashMap databaseFieldsToValues = (HashMap)entry.getValue();
                int counter = 0;
                Iterator itDatabaseFields = databaseFieldsToValues.keySet().iterator();
                while(itDatabaseFields.hasNext()) {
                    counter++;
                    DatabaseField field = (DatabaseField)itDatabaseFields.next();
                    writer.write(field.getNameDelimited(session.getPlatform()));
                    writer.write(" = ");
                    writeVar(writer, field, session.getPlatform());
                    writer.write("(i)");
                    if(counter < databaseFieldsToValues.size()) {
                        writer.write(", ");
                    }
                }
                
                writer.write(" WHERE ");
                
                Vector tablePrimaryKeys = new Vector();
                tablePrimaryKeys.addAll((Collection)tablesToPrimaryKeyFields.get(t));
                for(int i=0; i < mainPrimaryKeys.size(); i++) {
                    DatabaseField tableField = (DatabaseField)tablePrimaryKeys.elementAt(i);
                    writer.write(tableField.getNameDelimited(session.getPlatform()));
                    writer.write(" = ");
                    DatabaseField mainField = (DatabaseField )mainPrimaryKeys.elementAt(i);
                    writeVar(writer, mainField, session.getPlatform());
                    writer.write("(i)");
                    if(i < mainPrimaryKeys.size()-1) {
                        writer.write(" AND ");
                    } else {
                        writer.write(";\n");
                    }
                }
            }

            writer.write(tab);
            writer.write("END IF;\n");
            
            writer.write(tab);
            DatabaseField outField = new DatabaseField("ROW_COUNT");
            outField.setType(Integer.class);
            call.appendOut(writer, outField);
            writer.write(" := ");
            writeVar(writer, firstMainPrimaryKey, session.getPlatform());
            writer.write(".COUNT;\n");
            
            writer.write("END;");

            call.setSQLString(writer.toString());
            
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
                
        return call;
    }    
    
    protected static void writeUniqueFieldName(Writer writer, DatabaseField field, DatasourcePlatform platform) throws IOException {
        // EMPLOYEE_EMP_ID
        writer.write(field.getTable().getNameDelimited(platform));
        writer.write("_");
        writer.write(field.getNameDelimited(platform));
    }
    
    protected static void writeType(Writer writer, DatabaseField field, DatasourcePlatform platform) throws IOException {
        // EMPLOYEE_EMP_ID_TYPE
        writeUniqueFieldName(writer, field, platform);
        writer.write(typeSuffix);
    }
    
    protected static void writeVar(Writer writer, DatabaseField field, DatasourcePlatform platform) throws IOException {
        // EMPLOYEE_EMP_ID_VAR 
        writeUniqueFieldName(writer, field, platform);
        writer.write(varSuffix);
    }
    
    protected static void writeDeclareTypeAndVar(Writer writer, DatabaseField field, DatasourcePlatform platform) throws IOException {
        //  TYPE EMPLOYEE_EMP_ID_TYPE IS TABLE OF EMPLOYEE.EMP_ID%TYPE;
        writer.write(tab);
        writer.write("TYPE ");
        writeType(writer, field, platform);
        writer.write(" IS TABLE OF ");
        writer.write(field.getQualifiedName());
        writer.write("%TYPE;\n");
        
        //  EMPLOYEE_EMP_ID_VAR EMP_ID_TYPE;
        writer.write(tab);
        writeVar(writer, field, platform);
        writer.write(" ");
        writeType(writer, field, platform);
        writer.write(";\n");
    }

    protected static void writeForAll(Writer writer, DatabaseField field, DatasourcePlatform platform) throws IOException {
        //FORALL i IN EMPLOYEE_EMP_ID_VAR.FIRST..EMPLOYEE_EMP_ID_VAR.LAST
        writer.write(dbltab);
        writer.write("FORALL i IN ");
        writeVar(writer, field, platform);
        writer.write(".FIRST..");
        writeVar(writer, field, platform);
        writer.write(".LAST\n");
    }
}
