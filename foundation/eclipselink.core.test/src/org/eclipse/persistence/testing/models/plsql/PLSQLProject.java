package org.eclipse.persistence.testing.models.plsql;

import java.math.BigDecimal;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Project;

/**
 * EclipseLink mapping project for PLSQL data structures.
 * 
 * @author James
 */
public class PLSQLProject extends Project {
    public PLSQLProject() {
        setName("PLSQLProject");
        
        buildAddressDescriptor();
        buildPhoneDescriptor();
        buildEmployeeDescriptor();
    }

    protected PLSQLrecord buildAddressRowTypeRecord() {
        PLSQLrecord record = buildAddressRowTypeDataRecord();
        record.setCompatibleType("PLSQL_P_PLSQL_ADDRESS_REC");
        record.setJavaType(Address.class);
        return record;
    }

    protected PLSQLrecord buildAddressRowTypeDataRecord() {
        PLSQLrecord record = new PLSQLrecord();
        record.setTypeName("PLSQL_ADDRESS%ROWTYPE");
        record.addField("ADDRESS_ID", JDBCTypes.NUMERIC_TYPE, 10, 0);
        record.addField("STREET_NUM", JDBCTypes.NUMERIC_TYPE, 10, 0);
        record.addField("STREET", JDBCTypes.VARCHAR_TYPE, 30);
        record.addField("CITY", JDBCTypes.VARCHAR_TYPE, 30);
        record.addField("STATE", JDBCTypes.VARCHAR_TYPE, 30);
        return record;
    }

    protected PLSQLrecord buildAddressRecord() {
        PLSQLrecord record = new PLSQLrecord();
        record.setTypeName("PLSQL_P.PLSQL_ADDRESS_REC");
        record.setCompatibleType("PLSQL_P_PLSQL_ADDRESS_REC");
        record.addField("ADDRESS_ID", JDBCTypes.NUMERIC_TYPE, 10, 0);
        record.addField("STREET_NUM", JDBCTypes.NUMERIC_TYPE, 10, 0);
        record.addField("STREET", JDBCTypes.VARCHAR_TYPE, 30);
        record.addField("CITY", JDBCTypes.VARCHAR_TYPE, 30);
        record.addField("STATE", JDBCTypes.VARCHAR_TYPE, 30);
        record.setJavaType(Address.class);
        return record;
    }

    protected PLSQLrecord buildPhoneRecord() {
        PLSQLrecord record = new PLSQLrecord();
        record.setTypeName("PLSQL_P.PLSQL_PHONE_REC");
        record.setCompatibleType("PLSQL_P_PLSQL_PHONE_REC");
        record.addField("AREA_CODE", JDBCTypes.VARCHAR_TYPE, 3);
        record.addField("P_NUM", JDBCTypes.VARCHAR_TYPE, 7);
        record.setJavaType(Phone.class);
        return record;
    }
    
    protected DatabaseQuery buildSimpleInQuery() {
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_SIMPLE_IN");
        call.addNamedArgument("P_VARCHAR", JDBCTypes.VARCHAR_TYPE, 30);
        call.addNamedArgument("P_BOOLEAN", OraclePLSQLTypes.PLSQLBoolean);
        call.addNamedArgument("P_BINARY_INTEGER", OraclePLSQLTypes.BinaryInteger);
        call.addNamedArgument("P_DEC", OraclePLSQLTypes.Dec);
        call.addNamedArgument("P_INT", OraclePLSQLTypes.Int);
        call.addNamedArgument("P_NATURAL", OraclePLSQLTypes.Natural);
        call.addNamedArgument("P_NATURALN", OraclePLSQLTypes.NaturalN);
        call.addNamedArgument("P_PLS_INTEGER", OraclePLSQLTypes.PLSQLInteger);
        call.addNamedArgument("P_POSITIVE", OraclePLSQLTypes.Positive);
        call.addNamedArgument("P_POSITIVEN", OraclePLSQLTypes.PositiveN);
        call.addNamedArgument("P_SIGNTYPE", OraclePLSQLTypes.SignType);
        call.addNamedArgument("P_NUMBER", JDBCTypes.NUMERIC_TYPE, 10, 2);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_VARCHAR", String.class);
        query.addArgument("P_BOOLEAN", Integer.class);
        query.addArgument("P_BINARY_INTEGER", Integer.class);
        query.addArgument("P_DEC", Integer.class);
        query.addArgument("P_INT", Integer.class);
        query.addArgument("P_NATURAL", Integer.class);
        query.addArgument("P_NATURALN", Integer.class);
        query.addArgument("P_PLS_INTEGER", Integer.class);
        query.addArgument("P_POSITIVE", Integer.class);
        query.addArgument("P_POSITIVEN", Integer.class);
        query.addArgument("P_SIGNTYPE", BigDecimal.class);
        query.addArgument("P_NUMBER", BigDecimal.class);
        query.setCall(call);
        
        return query;
    }
    
    protected DatabaseQuery buildSimpleInOutQuery() {
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_SIMPLE_INOUT");
        call.addNamedInOutputArgument("P_VARCHAR", JDBCTypes.VARCHAR_TYPE, 30);
        call.addNamedInOutputArgument("P_BOOLEAN", OraclePLSQLTypes.PLSQLBoolean);
        call.addNamedInOutputArgument("P_BINARY_INTEGER", OraclePLSQLTypes.BinaryInteger);
        call.addNamedInOutputArgument("P_DEC", OraclePLSQLTypes.Dec);
        call.addNamedInOutputArgument("P_INT", OraclePLSQLTypes.Int);
        call.addNamedInOutputArgument("P_NATURAL", OraclePLSQLTypes.Natural);
        call.addNamedInOutputArgument("P_NATURALN", OraclePLSQLTypes.NaturalN);
        call.addNamedInOutputArgument("P_PLS_INTEGER", OraclePLSQLTypes.PLSQLInteger);
        call.addNamedInOutputArgument("P_POSITIVE", OraclePLSQLTypes.Positive);
        call.addNamedInOutputArgument("P_POSITIVEN", OraclePLSQLTypes.PositiveN);
        call.addNamedInOutputArgument("P_SIGNTYPE", OraclePLSQLTypes.SignType);
        call.addNamedInOutputArgument("P_NUMBER", JDBCTypes.NUMERIC_TYPE, 10, 2);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_VARCHAR", String.class);
        query.addArgument("P_BOOLEAN", Integer.class);
        query.addArgument("P_BINARY_INTEGER", Integer.class);
        query.addArgument("P_DEC", Integer.class);
        query.addArgument("P_INT", Integer.class);
        query.addArgument("P_NATURAL", Integer.class);
        query.addArgument("P_NATURALN", Integer.class);
        query.addArgument("P_PLS_INTEGER", Integer.class);
        query.addArgument("P_POSITIVE", Integer.class);
        query.addArgument("P_POSITIVEN", Integer.class);
        query.addArgument("P_SIGNTYPE", BigDecimal.class);
        query.addArgument("P_NUMBER", BigDecimal.class);
        query.setCall(call);
        
        return query;
    }
    
    protected DatabaseQuery buildSimpleOutQuery() {
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_SIMPLE_OUT");
        call.addNamedOutputArgument("P_VARCHAR", JDBCTypes.VARCHAR_TYPE, 30);
        call.addNamedOutputArgument("P_BOOLEAN", OraclePLSQLTypes.PLSQLBoolean);
        call.addNamedOutputArgument("P_BINARY_INTEGER", OraclePLSQLTypes.BinaryInteger);
        call.addNamedOutputArgument("P_DEC", OraclePLSQLTypes.Dec);
        call.addNamedOutputArgument("P_INT", OraclePLSQLTypes.Int);
        call.addNamedOutputArgument("P_NATURAL", OraclePLSQLTypes.Natural);
        // TODO: NATURALN not working for outputs, get conversion error?
        //call.addNamedOutputArgument("P_NATURALN", OraclePLSQLTypes.NaturalN);
        call.addNamedOutputArgument("P_PLS_INTEGER", OraclePLSQLTypes.PLSQLInteger);
        call.addNamedOutputArgument("P_POSITIVE", OraclePLSQLTypes.Positive);
        //call.addNamedOutputArgument("P_POSITIVEN", OraclePLSQLTypes.PositiveN);
        call.addNamedOutputArgument("P_SIGNTYPE", OraclePLSQLTypes.SignType);
        call.addNamedOutputArgument("P_NUMBER", JDBCTypes.NUMERIC_TYPE, 10, 2);
        DataModifyQuery query = new DataModifyQuery();
        query.setCall(call);
        
        return query;
    }
    
    protected DatabaseQuery buildAddressInOutObjectQuery() {
        PLSQLrecord record = buildAddressRowTypeRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_INOUT");
        call.addNamedInOutputArgument("P_ADDRESS", record);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("P_ADDRESS", Address.class);
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildAddressInOutDataQuery() {
        PLSQLrecord record = buildAddressRowTypeDataRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_INOUT_DATA");
        call.addNamedInOutputArgument("P_ADDRESS", record);
        call.addNamedInOutputArgument("P_LOCAL", JDBCTypes.VARCHAR_TYPE, 30);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("ADDRESS_ID", BigDecimal.class);
        query.addArgument("STREET_NUM", Integer.class);
        query.addArgument("STREET", String.class);
        query.addArgument("CITY", String.class);
        query.addArgument("STATE", String.class);
        query.addArgument("P_LOCAL", String.class);
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildAddressInObjectQuery() {
        PLSQLrecord record = buildAddressRowTypeRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_IN");
        call.addNamedArgument("P_ADDRESS", record);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_ADDRESS", Address.class);
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildAddressInDataQuery() {
        PLSQLrecord record = buildAddressRowTypeDataRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_IN_DATA");
        call.addNamedArgument("P_LOCAL", JDBCTypes.VARCHAR_TYPE, 30);
        call.addNamedArgument("P_ADDRESS", record);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("ADDRESS_ID", BigDecimal.class);
        query.addArgument("STREET_NUM", Integer.class);
        query.addArgument("STREET", String.class);
        query.addArgument("CITY", String.class);
        query.addArgument("STATE", String.class);
        query.addArgument("P_LOCAL", String.class);
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildAddressOutObjectQuery() {
        PLSQLrecord record = buildAddressRowTypeRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_INOUT");
        call.addNamedOutputArgument("P_ADDRESS", record);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildBadAddressOutObjectQuery() {
        PLSQLrecord record = buildAddressRowTypeRecord();
        record.setTypeName(null);

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_INOUT");
        call.addNamedOutputArgument("P_ADDRESS", record);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;
    }
    
    protected DatabaseQuery buildAddressOutDataQuery() {
        PLSQLrecord record = buildAddressRowTypeDataRecord();

        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_ADDRESS_INOUT_DATA");
        call.addNamedOutputArgument("P_ADDRESS", record);
        call.addNamedOutputArgument("P_LOCAL", JDBCTypes.VARCHAR_TYPE, 30);
        DataReadQuery query = new DataReadQuery();
        query.dontMaintainCache();
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildCityListInQuery() {
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_CITY_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_CITY_LIST");
    	collection.setNestedType(JDBCTypes.VARCHAR_TYPE);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_CITY_LIST_IN");
        call.addNamedArgument("P_CITY_LIST", collection);
        call.addNamedArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_CITY_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildCityListOutQuery() {
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_CITY_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_CITY_LIST");
    	collection.setNestedType(JDBCTypes.VARCHAR_TYPE);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_CITY_LIST_OUT");
        call.addNamedOutputArgument("P_CITY_LIST", collection);
        call.addNamedOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;        
    }
    
    protected DatabaseQuery buildCityListInOutQuery() {
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_CITY_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_CITY_LIST");
    	collection.setNestedType(JDBCTypes.VARCHAR_TYPE);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_CITY_LIST_INOUT");
        call.addNamedInOutputArgument("P_CITY_LIST", collection);
        call.addNamedInOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("P_CITY_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }

    protected DatabaseQuery buildAddressListInQuery() {
        PLSQLrecord record = buildAddressRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_ADDRESS_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_ADDRESS_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_ADDRESS_LIST_IN");
        call.addNamedArgument("P_ADDRESS_LIST", collection);
        call.addNamedArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_ADDRESS_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }

    protected DatabaseQuery buildMissingTypeAddressListOutQuery() {
        PLSQLrecord record = buildAddressRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_ADDRESS_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_ADDRESS_LIST_OUT");
        call.addNamedOutputArgument("P_ADDRESS_LIST", collection);
        call.addNamedOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;
    }
    
    protected DatabaseQuery buildAddressListOutQuery() {
        PLSQLrecord record = buildAddressRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_ADDRESS_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_ADDRESS_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_ADDRESS_LIST_OUT");
        call.addNamedOutputArgument("P_ADDRESS_LIST", collection);
        call.addNamedOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;
    }

    protected DatabaseQuery buildAddressListInOutQuery() {
        PLSQLrecord record = buildAddressRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_ADDRESS_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_ADDRESS_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_ADDRESS_LIST_INOUT");
        call.addNamedInOutputArgument("P_ADDRESS_LIST", collection);
        call.addNamedInOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("P_ADDRESS_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }

    protected PLSQLrecord buildEmployeeRecord() {        
        PLSQLrecord record = new PLSQLrecord();
        record.setTypeName("PLSQL_P.PLSQL_EMP_REC");
        record.setCompatibleType("PLSQL_P_PLSQL_EMP_REC");
        record.addField("EMP_ID", JDBCTypes.NUMERIC_TYPE, 10, 0);
        record.addField("NAME", JDBCTypes.VARCHAR_TYPE, 30);
        record.addField("ACTIVE", OraclePLSQLTypes.PLSQLBoolean, 30);
        record.addField("ADDRESS", buildAddressRecord());
        record.setJavaType(Employee.class);

    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_PHONE_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_PHONE_LIST");
    	collection.setNestedType(buildPhoneRecord());
    	
        record.addField("PHONES", collection);
        
        return record;
    }
        
    protected DatabaseQuery buildEmployeeListInQuery() {
        PLSQLrecord record = buildEmployeeRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_EMP_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_EMP_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_LIST_IN");
        call.addNamedArgument("P_EMP_LIST", collection);
        call.addNamedArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_EMP_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }
        
    protected DatabaseQuery buildEmployeeListOutQuery() {
        PLSQLrecord record = buildEmployeeRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_EMP_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_EMP_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_LIST_OUT");
        call.addNamedOutputArgument("P_EMP_LIST", collection);
        call.addNamedOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;        
    }
        
    protected DatabaseQuery buildEmployeeListInOutQuery() {
        PLSQLrecord record = buildEmployeeRecord();
        
    	PLSQLCollection collection = new PLSQLCollection();
    	collection.setTypeName("PLSQL_P.PLSQL_EMP_LIST");
    	collection.setCompatibleType("PLSQL_P_PLSQL_EMP_LIST");
    	collection.setNestedType(record);
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_LIST_INOUT");
        call.addNamedInOutputArgument("P_EMP_LIST", collection);
        call.addNamedInOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("P_EMP_LIST", java.sql.Array.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }
        
    protected DatabaseQuery buildEmployeeInQuery() {
        PLSQLrecord record = buildEmployeeRecord();
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_IN");
        call.addNamedArgument("P_EMP", record);
        call.addNamedArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("P_EMP", Employee.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }
        
    protected DatabaseQuery buildEmployeeOutQuery() {
        PLSQLrecord record = buildEmployeeRecord();
            	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_OUT");
        call.addNamedOutputArgument("P_EMP", record);
        call.addNamedOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);

        return query;        
    }
        
    protected DatabaseQuery buildEmployeeInOutQuery() {
        PLSQLrecord record = buildEmployeeRecord();
    	
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("PLSQL_P.PLSQL_EMP_INOUT");
        call.addNamedInOutputArgument("P_EMP", record);
        call.addNamedInOutputArgument("P_CITY", JDBCTypes.VARCHAR_TYPE);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("P_EMP", Employee.class);
        query.addArgument("P_CITY", String.class);
        query.setCall(call);

        return query;        
    }
    
    protected void buildAddressDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.setTableName("PLSQL_ADDRESS");
        descriptor.setStructureName("PLSQL_P_PLSQL_ADDRESS_REC");
        descriptor.descriptorIsAggregate();

        descriptor.getQueryManager().addQuery("SimpleIn", buildSimpleInQuery());
        descriptor.getQueryManager().addQuery("SimpleOut", buildSimpleOutQuery());
        descriptor.getQueryManager().addQuery("SimpleInOut", buildSimpleInOutQuery());
        descriptor.getQueryManager().addQuery("AddressInOutObject", buildAddressInOutObjectQuery());
        descriptor.getQueryManager().addQuery("AddressInOutData", buildAddressInOutDataQuery());
        descriptor.getQueryManager().addQuery("AddressOutObject", buildAddressOutObjectQuery());
        descriptor.getQueryManager().addQuery("AddressOutData", buildAddressOutDataQuery());
        descriptor.getQueryManager().addQuery("AddressInObject", buildAddressInObjectQuery());
        descriptor.getQueryManager().addQuery("AddressInData", buildAddressInDataQuery());
        descriptor.getQueryManager().addQuery("CityListIn", buildCityListInQuery());
        descriptor.getQueryManager().addQuery("CityListOut", buildCityListOutQuery());
        descriptor.getQueryManager().addQuery("CityListInOut", buildCityListInOutQuery());
        descriptor.getQueryManager().addQuery("AddressListIn", buildAddressListInQuery());
        descriptor.getQueryManager().addQuery("AddressListOut", buildAddressListOutQuery());
        descriptor.getQueryManager().addQuery("AddressListInOut", buildAddressListInOutQuery());
        descriptor.getQueryManager().addQuery("BadAddressOut", buildBadAddressOutObjectQuery());
        descriptor.getQueryManager().addQuery("MissingTypeAddressListOut", buildMissingTypeAddressListOutQuery());
        
        descriptor.addFieldOrdering("ADDRESS_ID");
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("ADDRESS_ID");
        descriptor.addMapping(idMapping);

        descriptor.addFieldOrdering("STREET_NUM");
        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("STREET_NUM");
        descriptor.addMapping(numberMapping);

        descriptor.addFieldOrdering("STREET");
        DirectToFieldMapping streetMapping = new DirectToFieldMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setFieldName("STREET");
        descriptor.addMapping(streetMapping);

        descriptor.addFieldOrdering("CITY");
        DirectToFieldMapping cityMapping = new DirectToFieldMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setFieldName("CITY");
        descriptor.addMapping(cityMapping);

        descriptor.addFieldOrdering("STATE");
        DirectToFieldMapping stateMapping = new DirectToFieldMapping();
        stateMapping.setAttributeName("state");
        stateMapping.setFieldName("STATE");
        descriptor.addMapping(stateMapping);
        
        addDescriptor(descriptor);
    }

    
    protected void buildPhoneDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Phone.class);
        descriptor.setStructureName("PLSQL_P_PLSQL_PHONE_REC");
        descriptor.descriptorIsAggregate();
        
        descriptor.addFieldOrdering("AREA_CODE");
        DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
        areaCodeMapping.setAttributeName("areaCode");
        areaCodeMapping.setFieldName("AREA_CODE");
        descriptor.addMapping(areaCodeMapping);

        descriptor.addFieldOrdering("P_NUM");
        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("P_NUM");
        descriptor.addMapping(numberMapping);
        
        addDescriptor(descriptor);
    }
    
    protected void buildEmployeeDescriptor() {
        ObjectRelationalDataTypeDescriptor descriptor = new ObjectRelationalDataTypeDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setTableName("PLSQL_EMP");
        descriptor.setStructureName("PLSQL_P_PLSQL_EMP_REC");
        descriptor.descriptorIsAggregate();
        
        descriptor.getQueryManager().addQuery("EmployeeListIn", buildEmployeeListInQuery());
        descriptor.getQueryManager().addQuery("EmployeeListOut", buildEmployeeListOutQuery());
        descriptor.getQueryManager().addQuery("EmployeeListInOut", buildEmployeeListInOutQuery());
        descriptor.getQueryManager().addQuery("EmployeeInObject", buildEmployeeInQuery());
        descriptor.getQueryManager().addQuery("EmployeeOutObject", buildEmployeeOutQuery());
        descriptor.getQueryManager().addQuery("EmployeeInOutObject", buildEmployeeInOutQuery());
        
        descriptor.addFieldOrdering("EMP_ID");
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMP_ID");
        descriptor.addMapping(idMapping);

        descriptor.addFieldOrdering("NAME");
        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("NAME");
        descriptor.addMapping(nameMapping);

        descriptor.addFieldOrdering("ACTIVE");
        DirectToFieldMapping activeMapping = new DirectToFieldMapping();
        activeMapping.setAttributeName("active");
        activeMapping.setFieldName("ACTIVE");
        descriptor.addMapping(activeMapping);

        descriptor.addFieldOrdering("ADDRESS");
        StructureMapping addressMapping = new StructureMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setFieldName("ADDRESS");
        addressMapping.setReferenceClass(Address.class);
        descriptor.addMapping(addressMapping);

        descriptor.addFieldOrdering("PHONES");
        ObjectArrayMapping phonesMapping = new ObjectArrayMapping();
        phonesMapping.setStructureName("PLSQL_P_PLSQL_PHONE_LIST");
        phonesMapping.setAttributeName("phones");
        phonesMapping.setFieldName("PHONES");
        phonesMapping.setReferenceClass(Phone.class);
        descriptor.addMapping(phonesMapping);
        
        addDescriptor(descriptor);
    }
}
