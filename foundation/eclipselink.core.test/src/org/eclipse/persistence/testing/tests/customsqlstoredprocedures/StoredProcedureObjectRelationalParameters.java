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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.sql.Types;
import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.models.insurance.Address;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.testing.framework.*;

/*
 * StoredProcedureObjectRelationalParameters tests Toplinks ability to convert Java objects to/from DB Relational Objects
 * for use in stored procedures.  This also tests passing in the argument type so that null objects can be passed to the
 * stored procedure.  Setting useCustomSQL to true/false switches between using custom sql (SQLCall) and using
 * a StoredProcedureCall for the tests.  
 */
public class StoredProcedureObjectRelationalParameters extends TestCase{
    
    Object result = null;
    Vector results2, results3 = null;
    Address originalAddress;
    Long policyHolderIdToUse = new Long(12345);
    boolean useCustomSQL;
    
    public StoredProcedureObjectRelationalParameters() {
        this.useCustomSQL =false;
    }
    
    public StoredProcedureObjectRelationalParameters(boolean useCustomSQL) {
        this.useCustomSQL = useCustomSQL;
        setName(getName() + " useCustomSQL = " + useCustomSQL);
    }
    
    public void setup() {
        // right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle9())) {
            throw new TestWarningException("This test can only be run in Oracle9");
        }
        try {
            getSession().executeNonSelectingSQL("Delete from PolicyHolders where (ssn = "+policyHolderIdToUse+" )");
        }catch (Exception e){//ignore exceptions
        }
        
        originalAddress = new org.eclipse.persistence.testing.models.insurance.Address();
        originalAddress.setCountry("Canada");
        originalAddress.setCity("Ottawa");
        originalAddress.setState("On");
        originalAddress.setStreet("mystreet");
        originalAddress.setZipCode("Zip");
    }
    
    
    public void test() {
        Vector args = new Vector();
        args.addElement(policyHolderIdToUse);//ssn
        args.addElement(null);//occupation
        args.addElement(new Character('M'));//sex
        args.addElement("Chris");//firstName
        args.addElement(null);//birthDate
        args.addElement("Random");//lastName
        
        args.addElement(originalAddress);//address
        args.addElement(null);//childrenNames
        args.addElement(null);//phones
        
        //testing that an exception is not thrown, using IN parameters exclusively
        result = getSession().executeQuery(getInsertQuery(), args);
        
        //using OUT parameters exclusively, verifying policyHolder was created
        Vector args2 = new Vector();
        args2.addElement(policyHolderIdToUse);//ssn
        results2 = (Vector)getSession().executeQuery(getReadQuery(), args2);
        
        //using IN OUT parameters exclusively, deleting a policyholder
        results3 = (Vector)getSession().executeQuery(getDeleteQuery(), args);
    }
    
    public void verify() {
        if ((results2.size()!=1) &&(results3.size()!=1)){
            throw new TestErrorException("Collections returned did not contain expected number of results");
        }   
        Address addressRead = ((Address)((DatabaseRecord)results2.get(0)).get("address"));
        if (!originalAddress.getStreet().equals(addressRead.getStreet())){
            throw new TestErrorException("Address in did not equal the address returned out by SProc_Read_PHolders");
        }
        Address addressRemoved = ((Address)((DatabaseRecord)results3.get(0)).get("address"));
        if (!originalAddress.getStreet().equals(addressRemoved.getStreet())){
            throw new TestErrorException("Address in did not equal the address returned out by SProc_Delete_PHolders");
        }
    }
    
    public DatabaseCall getInsertCall(){
        DatabaseCall call = null;
        if (useCustomSQL){
            String sqlString = "BEGIN SProc_Insert_PHolders(#ssn, #occupation, #sex, "+
                "#firstName, #birthDate, #lastName, #address, #childrenNames, #phones); END;";
            SQLCall sqlcall = new SQLCall(sqlString);
            //most are not needed unless null it to be passed in
            sqlcall.setCustomSQLArgumentType("ssn", Long.class);
            sqlcall.setCustomSQLArgumentType("occupation", String.class); 
            sqlcall.setCustomSQLArgumentType("sex", Character.class);
            sqlcall.setCustomSQLArgumentType("firstName", String.class);
            sqlcall.setCustomSQLArgumentType("birthDate", java.sql.Date.class);
            sqlcall.setCustomSQLArgumentType("lastName", String.class);
            //address arg type isn't needed to convert Address to a Struct
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE");
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE");
            call = sqlcall;
        }else {
            StoredProcedureCall spcall = new StoredProcedureCall();
            spcall.setProcedureName("SProc_Insert_PHolders");
            spcall.addUnamedArgument("ssn", Long.class);
            spcall.addUnamedArgument("occupation", String.class);
            spcall.addUnamedArgument("sex", Character.class);
            spcall.addUnamedArgument("firstName", String.class);
            spcall.addUnamedArgument("birthDate", java.sql.Date.class);
            spcall.addUnamedArgument("lastName", String.class);        
            //address arg type isn't needed to convert Address to a Struct
            spcall.addUnamedArgument("address", Types.STRUCT, "ADDRESS_TYPE");
            //test just passing in array objects (or nulls) 
            spcall.addUnamedArgument("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            spcall.addUnamedArgument("phones", Types.ARRAY, "PHONELIST_TYPE");
            call = spcall;
        }
        return call;
    }
    
    public DatabaseQuery getInsertQuery(){
        DataModifyQuery query = new DataModifyQuery();

        query.setShouldBindAllParameters(true);
        query.bindAllParameters();
        query.setCall(getInsertCall());
        query.addArgument("ssn");
        query.addArgument("occupation");
        query.addArgument("sex");
        query.addArgument("firstName");
        query.addArgument("birthDate");
        query.addArgument("lastName");
        
        query.addArgument("address");
        query.addArgument("childrenNames");
        query.addArgument("phones");
        
        return query;
    }
    
    public DatabaseCall getReadCall(){
        DatabaseCall call = null;
        if (useCustomSQL){
            String sqlString = "BEGIN SProc_Read_PHolders(####ssn, ###occupation, ###sex, "+
                "###firstName, ###birthDate, ###lastName, ###address, ###childrenNames, ###phones); END;";
            SQLCall sqlcall = new SQLCall(sqlString);
            sqlcall.setCustomSQLArgumentType("ssn", Long.class);
            sqlcall.setCustomSQLArgumentType("occupation", String.class); 
            sqlcall.setCustomSQLArgumentType("sex", Character.class);
            sqlcall.setCustomSQLArgumentType("firstName", String.class);
            sqlcall.setCustomSQLArgumentType("birthDate", java.sql.Date.class);
            sqlcall.setCustomSQLArgumentType("lastName", String.class);
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE", Address.class);
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE");
            call = sqlcall;
        }else{
            StoredProcedureCall spcall = new StoredProcedureCall();
            spcall.setProcedureName("SProc_Read_PHolders");
            spcall.addUnamedInOutputArgument("ssn", Long.class);
            spcall.addUnamedOutputArgument("occupation", String.class);
            spcall.addUnamedOutputArgument("sex", Character.class);
            spcall.addUnamedOutputArgument("firstName", String.class);
            spcall.addUnamedOutputArgument("birthDate", java.sql.Date.class);
            spcall.addUnamedOutputArgument("lastName", String.class);
            spcall.addUnamedOutputArgument("address", Types.STRUCT, "ADDRESS_TYPE", Address.class);
            spcall.addUnamedOutputArgument("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            spcall.addUnamedOutputArgument("phones", Types.ARRAY, "PHONELIST_TYPE");
            call = spcall;
        }
        return call;
    }
    
    public DatabaseQuery getReadQuery(){
        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(true);
        query.bindAllParameters();
        query.setCall(getReadCall());
        query.addArgument("ssn");
        
        return query;
    }
    
    public DatabaseCall getDeleteCall(){
        DatabaseCall call = null;
        if (useCustomSQL){
            String sqlString = "BEGIN SProc_Delete_PHolders(####ssn, ####occupation, ####sex, "+
                "####firstName, ####birthDate, ####lastName, ####address, ####childrenNames, ####phones); END;";
            SQLCall sqlcall = new SQLCall(sqlString);
            sqlcall.setCustomSQLArgumentType("ssn", Long.class);
            sqlcall.setCustomSQLArgumentType("occupation", String.class); 
            sqlcall.setCustomSQLArgumentType("sex", Character.class);
            sqlcall.setCustomSQLArgumentType("firstName", String.class);
            sqlcall.setCustomSQLArgumentType("birthDate", java.sql.Date.class);
            sqlcall.setCustomSQLArgumentType("lastName", String.class);
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE", Address.class);
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE");
            call = sqlcall;
        }else{
            StoredProcedureCall sqlcall = new StoredProcedureCall();
            sqlcall.setProcedureName("SProc_Delete_PHolders");
            sqlcall.addUnamedInOutputArgument("ssn", Long.class);
            sqlcall.addUnamedInOutputArgument("occupation", String.class);
            sqlcall.addUnamedInOutputArgument("sex", Character.class);
            sqlcall.addUnamedInOutputArgument("firstName", String.class);
            sqlcall.addUnamedInOutputArgument("birthDate", java.sql.Date.class);
            sqlcall.addUnamedInOutputArgument("lastName", String.class);
            
            sqlcall.addUnamedInOutputArgument("address", "address", Types.STRUCT, "ADDRESS_TYPE", Address.class);
            sqlcall.addUnamedInOutputArgument("childrenNames", "childrenNames", Types.ARRAY, "NAMELIST_TYPE", Vector.class);
            sqlcall.addUnamedInOutputArgument("phones", "phones", Types.ARRAY, "PHONELIST_TYPE");
            call = sqlcall;
        }
        return call;
    }
    
    public DatabaseQuery getDeleteQuery(){
        DataReadQuery query3 = new DataReadQuery();
        query3.setShouldBindAllParameters(true);
        query3.bindAllParameters();
        query3.setCall(getDeleteCall());
        query3.addArgument("ssn");
        query3.addArgument("occupation");
        query3.addArgument("sex");
        query3.addArgument("firstName");
        query3.addArgument("birthDate");
        query3.addArgument("lastName");
        
        query3.addArgument("address");
        query3.addArgument("childrenNames");
        query3.addArgument("phones");
        return query3;
    }
}
