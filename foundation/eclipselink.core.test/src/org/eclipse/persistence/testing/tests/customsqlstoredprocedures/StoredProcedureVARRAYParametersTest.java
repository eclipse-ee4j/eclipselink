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

import java.sql.Struct;
import java.sql.Types;
import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.insurance.Phone;

/**
 * This tests using Vectors in stored procedures (through storedprocedurecalls or custom sql) for
 * ARRAY objects using IN, OUT and INOUT parameter types.  Also verifies that if no conversion 
 * is specified for a Struct object, a Struct will be returned.  
 */
public class StoredProcedureVARRAYParametersTest extends StoredProcedureObjectRelationalParameters {
    
    Vector phoneNumbers, childrenNames = null;
    
    public StoredProcedureVARRAYParametersTest() {
        super();
    }
    
    public StoredProcedureVARRAYParametersTest(boolean useCustomSQL) {
        super(useCustomSQL);
    }
    
    public void setup() {
        super.setup();
    }
    
    public void test() {
        Vector args = new Vector();

        childrenNames = new Vector();
        childrenNames.add("Mary");
        childrenNames.add("Sue");
        
        phoneNumbers= new Vector();
        phoneNumbers.add(Phone.example2());
        phoneNumbers.add(Phone.example3());
        
        
        args.addElement(policyHolderIdToUse);//ssn
        args.addElement(null);//occupation
        args.addElement(null);//sex
        args.addElement("Peter");//firstName
        args.addElement(null);//birthDate
        args.addElement("Griffin");//lastName
        
        args.addElement(originalAddress);//address
        args.addElement(childrenNames);//childrenNames
        args.addElement(phoneNumbers);//phones
        
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
        //check that a Struct was returned for address
        Object addressOut = ((DatabaseRecord)results2.get(0)).get("address");
        if ((addressOut==null)||(! (addressOut instanceof Struct))){
            throw new TestErrorException("Address returned by SProc_Read_PHolders was null or not a Struct :"+addressOut);
        }
        Object addressInOut = ((DatabaseRecord)results2.get(0)).get("address");
        if ((addressInOut==null)||(! (addressInOut instanceof Struct))){
            throw new TestErrorException("Address returned by SProc_Delete_PHolders was null or not a Struct "+addressInOut);
        }
        
        
        Vector childrenRead = ((Vector)((DatabaseRecord)results2.get(0)).get("childrenNames"));
        if ( (childrenNames.size() != childrenRead.size()) && 
                (!childrenNames.get(0).equals(childrenRead.get(0))) ){
            throw new TestErrorException("First Child's Name did not match what was returned out by SProc_Read_PHolders");
        }
        childrenRead = ((Vector)((DatabaseRecord)results3.get(0)).get("childrenNames"));
        if ( (childrenNames.size() != childrenRead.size()) && 
                (!childrenNames.get(0).equals(childrenRead.get(0))) ){
            throw new TestErrorException("First Child's Name did not match what was returned out by SProc_Delete_PHolders");
        }
        
        Vector phonesRead = ((Vector)((DatabaseRecord)results2.get(0)).get("phones"));
        if ( (phoneNumbers.size() != phonesRead.size()) && 
                (!phoneNumbers.get(0).equals(childrenRead.get(0))) ){
            throw new TestErrorException("First phone did not match what was returned out by SProc_Read_PHolders");
        }
        
        phonesRead = ((Vector)((DatabaseRecord)results3.get(0)).get("phones"));
        if ( (phoneNumbers.size() != phonesRead.size()) && 
                (!phoneNumbers.get(0).equals(phonesRead.get(0))) ){
            throw new TestErrorException("First phone did not match what was returned out by SProc_Delete_PHolders");
        }
    }
    
    
    public DatabaseCall getInsertCall(){
        DatabaseCall call = null;
        
        ObjectRelationalDatabaseField ordf = new ObjectRelationalDatabaseField("");
        ordf.setSqlType(Types.STRUCT);
        ordf.setSqlTypeName("PHONE_TYPE");
        ordf.setType(Phone.class);
        
        if (useCustomSQL){
            String sqlString = "BEGIN SProc_Insert_PHolders(#ssn, #occupation, #sex, "+
                "#firstName, #birthDate, #lastName, #address, #childrenNames, #phones); END;";
            SQLCall sqlcall = new SQLCall(sqlString);
            sqlcall.setCustomSQLArgumentType("ssn", Long.class);
            sqlcall.setCustomSQLArgumentType("occupation", String.class);
            sqlcall.setCustomSQLArgumentType("sex", Character.class);
            sqlcall.setCustomSQLArgumentType("firstName", String.class);
            sqlcall.setCustomSQLArgumentType("birthDate", java.sql.Date.class);
            sqlcall.setCustomSQLArgumentType("lastName", String.class);
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE");
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE", ordf);
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
            
            spcall.addUnamedArgument("address", Types.STRUCT, "ADDRESS_TYPE");//Address.class
            spcall.addUnamedArgument("childrenNames", Types.ARRAY, "NAMELIST_TYPE");
            spcall.addUnamedArgument("phones", Types.ARRAY, "PHONELIST_TYPE", ordf);
            call = spcall;
        }
        return call;
    }
    
    public DatabaseCall getReadCall(){
        DatabaseCall call = null;
        ObjectRelationalDatabaseField ordf = new ObjectRelationalDatabaseField("");
        ordf.setSqlType(Types.STRUCT);
        ordf.setSqlTypeName("PHONE_TYPE");
        ordf.setType(Phone.class);
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
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE");
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE", Vector.class);
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE", Vector.class, ordf);
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
            spcall.addUnamedOutputArgument("address", Types.STRUCT, "ADDRESS_TYPE");
            spcall.addUnamedOutputArgument("childrenNames", Types.ARRAY, "NAMELIST_TYPE", Vector.class);
            spcall.addUnamedOutputArgument("phones", Types.ARRAY, "PHONELIST_TYPE", Vector.class, ordf);
            call = spcall;
        }
        return call;
    }
    
    public DatabaseCall getDeleteCall(){
        DatabaseCall call = null;
        ObjectRelationalDatabaseField ordf = new ObjectRelationalDatabaseField("");
        ordf.setSqlType(Types.STRUCT);
        ordf.setSqlTypeName("PHONE_TYPE");
        ordf.setType(Phone.class);
        
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
            sqlcall.setCustomSQLArgumentType("address", Types.STRUCT, "ADDRESS_TYPE");
            sqlcall.setCustomSQLArgumentType("childrenNames", Types.ARRAY, "NAMELIST_TYPE", Vector.class);
            sqlcall.setCustomSQLArgumentType("phones", Types.ARRAY, "PHONELIST_TYPE", Vector.class, ordf);
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

            sqlcall.addUnamedInOutputArgument("address", "address", Types.STRUCT, "ADDRESS_TYPE");
            sqlcall.addUnamedInOutputArgument("childrenNames", "childrenNames", Types.ARRAY, "NAMELIST_TYPE", Vector.class);
            sqlcall.addUnamedInOutputArgument("phones", "phones", Types.ARRAY, "PHONELIST_TYPE", Vector.class, ordf);
            call = sqlcall;
        }
        return call;
    }
}
