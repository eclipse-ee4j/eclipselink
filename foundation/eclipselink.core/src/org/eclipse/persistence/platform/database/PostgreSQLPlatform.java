/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * <p><b>Purpose</b>: Provides Postgres specific behaviour.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Native SQL for Date, Time, & Timestamp.
 * <li> Native sequencing.
 * <li> Mapping of class types to database types for the schema framework.
 * <li> Pessimistic locking.
 * <li> Platform specific operators.
 * </ul>
 *
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 */
public class PostgreSQLPlatform extends DatabasePlatform {
    
    public PostgreSQLPlatform() {
        super();
    }
    
    /**
     * Appends a Boolean value. 
     * Refer to : http://www.postgresql.org/docs/8.0/static/datatype-boolean.html
     * In PostgreSQL the following are the values that
     * are value for a boolean field
     * Valid literal values for the "true" state are: 
     *  TRUE, 't', 'true', 'y', 'yes', '1'
     * Valid literal values for the false" state are :
     *  FALSE, 'f', 'false', 'n', 'no', '0'
     *
     * To be consistent with the other data platforms we are using the values 
     * '1' and '0' for true and false states of a boolean field.
     */
    protected void appendBoolean(Boolean bool, Writer writer) throws IOException {
        if (bool.booleanValue()) {
            writer.write("\'1\'");
        } else {
            writer.write("\'0\'");
        }
    }
    
    /**
     * INTERNAL:
     * Initialize any platform-specific operators
     */
    protected void initializePlatformOperators() {
        super.initializePlatformOperators();
        addOperator(ExpressionOperator.simpleLogicalNoParens(ExpressionOperator.Concat, "||"));
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "NULLIF"));
        addOperator(operatorLocate());
    }


    /**
     * INTERNAL:
     * This method returns the query to select the timestamp from the server
     * for Derby.
     */
    public ValueReadQuery getTimestampQuery() {
        if (timestampQuery == null) {
            timestampQuery = new ValueReadQuery();
            timestampQuery.setSQLString("SELECT NOW()");
        }
        return timestampQuery;

    }


    /**
     * This method is used to print the output parameter token when stored
     * procedures are called
     */
    public String getInOutputProcedureToken() {
        return "OUT";
    }

    /**
     * This is required in the construction of the stored procedures with
     * output parameters
     */
    public boolean shouldPrintOutputTokenAtStart() {
        //TODO: Check with the reviewer where this is used
        return false;
    }

    /**
     * INTERNAL:
     * Answers whether platform is Derby
     */
    public boolean isPostgreSQL() {
        return true;
    }

    /**
     * INTERNAL:
     */
    protected String getCreateTempTableSqlSuffix() {
        // http://pgsqld.active-venture.com/sql-createtable.html
        return " ON COMMIT PRESERVE ROWS";
    }

    public boolean supportsNativeSequenceNumbers() {
        return true;
    }

    /**
     *  INTERNAL:
     *    Indicates whether NativeSequence should retrieve
     *  sequence value after the object has been inserted into the db
     *  This method is to be used *ONLY* by sequencing classes
     */
    public boolean shouldNativeSequenceAcquireValueAfterInsert() {
        return true;
    }


    /**
     * INTERNAL:
     * Build the identity query for native sequencing.
     */
    public ValueReadQuery buildSelectQueryForNativeSequence(String seqName, Integer size) {
        ValueReadQuery selectQuery = new ValueReadQuery(); 
        selectQuery.setSQLString("select currval(\'"  + seqName + "\')"); 
        return selectQuery;
    }


    /**
     * INTERNAL:
     */
     protected String getCreateTempTableSqlBodyForTable(DatabaseTable table) {
        // returning null includes fields of the table in body
        // see javadoc of DatabasePlatform#getCreateTempTableSqlBodyForTable(DataBaseTable)
        // for details
        return null;
     }

    /**
     * INTERNAL:
     * Append the receiver's field 'identity' constraint clause to a writer
     */
    public void printFieldIdentityClause(Writer writer) throws ValidationException {
        try {            
            writer.write(" SERIAL");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }

    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = new Hashtable();

        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BOOLEAN", false));

        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("FLOAT"));
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("FLOAT", false));
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("SMALLINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL",38));
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL",38));

        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", 255));
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BYTEA"));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("TEXT"));
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BYTEA"));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("TEXT"));
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("BYTEA"));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("TEXT"));        

        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));

        return fieldTypeMapping;
    }  
    
    /**
     * INTERNAL:
     * Override the default locate operator
     */
    protected ExpressionOperator operatorLocate() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.Locate);
        Vector v = new Vector(3);
        v.addElement("STRPOS(");
        v.addElement(", ");
        v.addElement(")");
        result.printsAs(v);
        result.bePrefix();
        result.setNodeClass(RelationExpression.class);
        return result;
    }


    /**
     * INTERNAL:
     */
    public boolean supportsGlobalTempTables() {
        return true;
    }

    /**
     * INTERNAL:
     */
    protected String getCreateTempTableSqlPrefix() {
        return "CREATE GLOBAL TEMPORARY TABLE ";
    }
                
    /**
     * INTERNAL:
     * returns the maximum number of characters that can be used in a field
     * name on this platform.
     */
    public int getMaxFieldNameSize() {
        // The system uses no more than NAMEDATALEN-1 characters of an identifier; longer names can be written in commands, 
        //    but they will be truncated. By default, NAMEDATALEN is 64 so the maximum identifier length is 63 (but at the time PostgreSQL 
        //    is built, NAMEDATALEN can be changed in src/include/postgres_ext.h).
        // http://www.postgresql.org/docs/7.3/interactive/sql-syntax.html#SQL-SYNTAX-IDENTIFIERS
        return 63;
    }

    // http://www.postgresql.org/docs/8.1/interactive/plpgsql-declarations.html
    /**
     * INTERNAL:
     * Used for sp calls.
     */
    public String getProcedureBeginString() {
        return "AS $$  BEGIN ";
    }

    /**
     * INTERNAL:
     * Used for sp calls.
     */
    public String getProcedureEndString() {
        return "; END ; $$ LANGUAGE plpgsql;";
    }

    /**
     * INTERNAL:
     * Used for sp calls.
     */
    public String getProcedureCallHeader() {
        return "EXECUTE ";
    }
    
    /**
     * INTERNAL
     * Used for stored function calls.
     */
    public String getAssignmentString() {
        return ":= ";
    }    
    
    public void printFieldTypeSize(Writer writer, FieldDefinition field, 
            FieldTypeDefinition fieldType, AbstractSession session, String qualifiedFieldName) throws IOException {
        if(!shouldAcquireSequenceValueAfterInsert(session,  qualifiedFieldName)) {
            writer.write(fieldType.getName());
            if ((fieldType.isSizeAllowed()) && ((field.getSize() != 0) || (fieldType.isSizeRequired()))) {
                writer.write("(");
                if (field.getSize() == 0) {
                    writer.write(new Integer(fieldType.getDefaultSize()).toString());
                } else {
                    writer.write(new Integer(field.getSize()).toString());
                }
                if (field.getSubSize() != 0) {
                    writer.write(",");
                    writer.write(new Integer(field.getSubSize()).toString());
                } else if (fieldType.getDefaultSubSize() != 0) {
                    writer.write(",");
                    writer.write(new Integer(fieldType.getDefaultSubSize()).toString());
                }
                writer.write(")");
            }
        }
    }
    
    public void printFieldUnique(Writer writer,  boolean isUnique, 
            AbstractSession session, String qualifiedFieldName) throws IOException {
       if(!shouldAcquireSequenceValueAfterInsert(session,  qualifiedFieldName)) {
            if (isUnique) {
                if (supportsPrimaryKeyConstraint()) {
                    writer.write(" UNIQUE");
                }
            }
       }
    }

    /**
     * JDBC defines and outer join syntax, many drivers do not support this. So we normally avoid it.
     */
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false;
    }    
     
    /**
     * Set a primitive parameter.
     * Postgres jdbc client driver give problem when doing a setObject() for wrapper types.
     * Ideally this code should be in the DatabasePlatform so that all platforms can use
     * this. 
     */        
    protected void setPrimitiveParameterValue(final PreparedStatement statement, final int index, 
            final Object parameter) throws SQLException {
       if (parameter instanceof Number) {
            Number number = (Number) parameter;
            if (number instanceof Integer) {
                statement.setInt(index, number.intValue());
            } else if (number instanceof Long) {
                statement.setLong(index, number.longValue());
            } else if (number instanceof Short) {
                statement.setShort(index, number.shortValue());
            } else if (number instanceof Byte) {
                statement.setByte(index, number.byteValue());
            } else if (number instanceof Double) {
                statement.setDouble(index, number.doubleValue());
            } else if (number instanceof Float) {
                statement.setFloat(index, number.floatValue());
            } else if (number instanceof BigDecimal) {
                statement.setBigDecimal(index, (BigDecimal) number);
            } else if (number instanceof BigInteger) {
                statement.setBigDecimal(index, new BigDecimal((BigInteger) number));
            } else {
                statement.setObject(index, parameter);
            }
        } else if (parameter instanceof String) {
            statement.setString(index, (String)parameter);
        } else if (parameter instanceof Boolean) {
            statement.setBoolean(index, ((Boolean) parameter).booleanValue());
        } else {
            statement.setObject(index, parameter);
        }           
    }   

    protected String getDBSequenceName(String tableName, String pkFieldName) {
        StringBuffer seqName = new StringBuffer(tableName);
        seqName.append("_").append(pkFieldName).append("_seq");
        return seqName.toString();
    }  
    
     /**
     * INTERNAL:
     * Platform specific sequencing initialization.
     * This internal method should only be called by SequencingManager.
     * 
     * For PostgreSQL database for id fields that are defined as "SERIAL" the database creates
     * an implicit sequence. The name of the sequence provided by the database is of the form
     * <table_name>_<id_field_name>_seq.
     * As part of the insert statement we need to get the correct sequence to be able to get
     * the next value. This code ensures that we have our internal structures pointing to the
     * correct sequence name for the runtime code to work correctly. 
     * The current version of postgresql allows only 1 SERIAL field to be defined per table.
     */
    public void platformSpecificSequencingInitialization(DatabaseSession session) {
        Boolean isDefaultSequenceNative = null;
        
        // We need to iterate over all the entities  
        Iterator iterator = session.getProject().getDescriptors().values().iterator();
        Sequence sequence;
        HashSet toBeRemoved = new HashSet();
        
        while (iterator.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();

            // Ensure that the entity uses sequencing
            if (descriptor.usesSequenceNumbers()) {
                String currSequenceName = descriptor.getSequenceNumberName();
                sequence = getSequence(currSequenceName);

                boolean shouldVerifySequenceName;

                boolean usesDefaultSequence = sequence == null || sequence instanceof DefaultSequence;
                if(usesDefaultSequence) {
                    // is defaultSequence is a NativeSequence?
                    if(isDefaultSequenceNative == null) {
                        isDefaultSequenceNative = new Boolean(getDefaultSequence() instanceof NativeSequence);
                    }
                    shouldVerifySequenceName = isDefaultSequenceNative.booleanValue();
                } else {
                    shouldVerifySequenceName = sequence instanceof NativeSequence;
                }

                if(shouldVerifySequenceName) {                                        
                    DatabaseTable tbl = (DatabaseTable)descriptor.getTables().firstElement();
                    String tableName = tbl.getQualifiedName();                                       
                    String pkFieldName = descriptor.getSequenceNumberField().getName();
                    String seqName = getDBSequenceName(tableName, pkFieldName);
                    if(!currSequenceName.equals(seqName)) {
                        descriptor.setSequenceNumberName(seqName);
                        if(sequence != null) {
                            Sequence newSequence = (Sequence)sequence.clone();
                            newSequence.setName(seqName);
                            addSequence(newSequence);
                            toBeRemoved.add(currSequenceName);
                        }
                    }
                }
            }
        }
        Iterator itToBeRemoved = toBeRemoved.iterator();
        while(itToBeRemoved.hasNext()) {
            removeSequence((String)itToBeRemoved.next());
        }
    }
   
}
