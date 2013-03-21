/*******************************************************************************
 * Copyright (c) 2012, 2013 Pervasive Software Inc. All Rights Reserved
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors: 
 *       Peter Lohman - initial implementation
 *     
 ******************************************************************************/  

 /*

    For minimal implementation, compare with:
         C:\PL\JPA\EclipseLink\SVN\org.eclipse.persistence\foundation\org.eclipse.persistence.core\src\org\eclipse\persistence\platform\database\CloudscapePlatform.java
         
    For PVSW data type mapping, see: getColumnClassName():C:\cmsynergy\psql11.20_pnl\psql\comp\sdk\jdbc\pvjdbc2\src\com\pervasive\jdbc\v2\ResultSetMetaData.java         
    
*/
package org.eclipse.persistence.platform.database;

import java.util.*;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

import java.io.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.queries.*;

/** <p><b>Purpose</b>: Provides Pervasive SQL DBMS specific behavior.
* 
* </br></br> 
* Pervasive SQL Platform file <br/>
*  Contributed by: Pervasive Software, Inc.<br/>
*  Contributed under bug: 392109
*  <p/>
* 
* <u><b>Developed on Pervasive PSQL Server 11.30 </b></u>
* <blockquote>
* <li>Eclipselink Core SRG Test passes with known limitations.
* <li>Eclipselink JPA SRG Test passes with known limitations.
* <li>Eclipselink stored procedure tests "CustomSQLTestModel", "StoredProcedureGeneratorModel" pass with known limitations.
* </blockquote>
*  
* <u><p/><b>Limitations</b></u>
* <ul> 
* <li> Updates are not supported on joined queries or queries with group by. 
* <li> The platform method getSelectForUpdateString() currently returns an empty string. This is 
* to avoid avoid joined queries with FOR UPDATE in them, which Pervasive does not support. 
* <li> Columns used in indexes must total no more than 255 bytes in length. 
* <li> Pervasive SQL does not support dynamic parameters in the SELECT list. 
* <li> IDENTITY columns are either 2- or 4-byte integers. Foreign keys referencing such columns must use the same datatypes. 
* 
**/ 


public class PervasivePlatform extends org.eclipse.persistence.platform.database.DatabasePlatform {

    public static final int DEFAULT_CHAR_SIZE = 80;

    //
    // Cloned from AccessPlatform.java
    //
    protected Map<String, Class> buildClassTypes() {
        Map<String, Class> classTypeMapping = super.buildClassTypes();

        // Causes BLOB to translate to LONGVARBINARY(via java.sql.Blob) instead of BINARY (via Byte[])
        classTypeMapping.put("BLOB", java.sql.Blob.class);

        return classTypeMapping;
    }


    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping;

        fieldTypeMapping = new Hashtable();
        fieldTypeMapping.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_CHAR_SIZE));            
        // fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(java.math.BigInteger.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(Integer.class, new FieldTypeDefinition("INTEGER", false));
        fieldTypeMapping.put(Long.class, new FieldTypeDefinition("INTEGER", false));        
        fieldTypeMapping.put(Short.class, new FieldTypeDefinition("SMALLINT", false));        
        fieldTypeMapping.put(Byte.class, new FieldTypeDefinition("TINYINT", false));        
        fieldTypeMapping.put(Float.class, new FieldTypeDefinition("REAL", false));          
        fieldTypeMapping.put(Double.class, new FieldTypeDefinition("DOUBLE", false));         
        fieldTypeMapping.put(Character.class, new FieldTypeDefinition("CHAR", 1));
        fieldTypeMapping.put(java.sql.Date.class, new FieldTypeDefinition("DATE", false));              
        fieldTypeMapping.put(java.sql.Time.class, new FieldTypeDefinition("TIME", false));              
        fieldTypeMapping.put(java.sql.Timestamp.class, new FieldTypeDefinition("TIMESTAMP", false));      
        fieldTypeMapping.put(byte[].class, new FieldTypeDefinition("BINARY", DEFAULT_CHAR_SIZE ));   
        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("LONGVARBINARY", false));   
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("CHAR", DEFAULT_CHAR_SIZE));
        fieldTypeMapping.put(Boolean.class, new FieldTypeDefinition("BIT", false));              
        fieldTypeMapping.put(java.sql.Blob.class, new FieldTypeDefinition("LONGVARBINARY", false));
        fieldTypeMapping.put(java.sql.Clob.class, new FieldTypeDefinition("LONGVARCHAR", false));        

        fieldTypeMapping.put(java.math.BigDecimal.class, new FieldTypeDefinition("DECIMAL",38, 0));        // From MySQL
        fieldTypeMapping.put(Number.class, new FieldTypeDefinition("DECIMAL",38,0));                      // From MySQL


        // fieldTypeMapping.put(java.lang.Number.class, new FieldTypeDefinition("BIGINT", false));
        fieldTypeMapping.put(char[].class, new FieldTypeDefinition("LONGVARCHAR", false));
        fieldTypeMapping.put(java.util.Calendar.class, new FieldTypeDefinition("TIMESTAMP"));
        fieldTypeMapping.put(java.util.Date.class, new FieldTypeDefinition("TIMESTAMP"));
   
        return fieldTypeMapping;
    }

    /**
     * 
     * Pervasive uses the INOUT keyword, as opposed to "IN OUT".
     */
    @Override
    public String getInOutputProcedureToken() {
        return "INOUT";
    }
    
    /**
     * Pervasive uses IN prefix for INPUT parameters.
     * 
     */
    @Override
    public String getInputProcedureToken() {
        return "IN";
    }

    /**
     * Pervasive uses ":" as prefix for procedure arguments.
     */
    public String getProcedureArgumentString() {
        return ":";
    }

    /**
     * 
     * Pervasive requires BEGIN in a procedure statement.
     */
    @Override
    public String getProcedureBeginString() {
        return "BEGIN ";
    }

    /**
     * In CREATE PROCEDURE, Pervasive requires brackets after the procedure name, even if there are no arguments.
     */
    public boolean requiresProcedureBrackets() {
        return true;
    }

    /**
     * Pervasive uses CALL or EXECUTE not CALL PROCEDURE or EXECUTE PROCEDURE
     */
    public String getProcedureCallHeader() {
        return "CALL ";
    }

    /**
     * 
     * Pervasive requires END in a procedure statement.
     */
    @Override
    public String getProcedureEndString() {
        return "END";
    }
    

    /**
     * Pervasive uses ":" as prefix for procedure parameters.
     */
    public String getStoredProcedureParameterPrefix() {
        return ":";
    }
    
    
    /**
     * Pervasive requires the OUTPUT keyword for output parameters
     */
    @Override
    public boolean requiresProcedureCallOuputToken() {
        return true;
    }


    protected void initializePlatformOperators() {
        super.initializePlatformOperators();

        addOperator(ExpressionOperator.simpleThreeArgumentFunction(ExpressionOperator.Substring, "SUBSTRING"));
        addOperator(singleArgumentSubstringOperator());
        addOperator(ExpressionOperator.simpleTwoArgumentFunction(ExpressionOperator.Nvl, "ISNULL"));
        addOperator(ExpressionOperator.simpleFunction(ExpressionOperator.Ceil, "CEILING"));
        addOperator(toNumberOperator());
        addOperator(toCharOperator());
        addOperator(toDateOperator());
    }

    /**
     * Cloned from MySQLPlatform.java
     * 
     */

    /**
     * INTERNAL:
     * Pervasive SQL stored procedure calls do not require the argument name be printed in the call string
     * e.g. call MyStoredProc(?) instead of call MyStoredProc(myvariable = ?)
     */
    @Override
    public boolean shouldPrintStoredProcedureArgumentNameInCall(){
        return false;
    }


    protected ExpressionOperator toNumberOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToNumber);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", SQL_NUMERIC)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Cloned from MySQLPlatform.java
     */
    protected ExpressionOperator toDateOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToDate);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", DATETIME)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * Cloned from MySQLPlatform.java
     */
    protected ExpressionOperator toCharOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.ToChar);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", SQL_CHAR)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }

    /**
     * 
     * Cloned from MySQLPlatform.java
     */
    protected ExpressionOperator dateToStringOperator() {
        ExpressionOperator exOperator = new ExpressionOperator();
        exOperator.setType(ExpressionOperator.FunctionOperator);
        exOperator.setSelector(ExpressionOperator.DateToString);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance(2);
        v.addElement("CONVERT(");
        v.addElement(", SQL_CHAR)");
        exOperator.printsAs(v);
        exOperator.bePrefix();
        exOperator.setNodeClass(ClassConstants.FunctionExpression_Class);
        return exOperator;
    }



    /**
     * Answers whether platform is Pervasive
     */
    public boolean isPervasive() {
        return true;
    }

    /**
     * JDBC defines an outer join syntax which many drivers do not support. So we normally avoid it.
     */
    public boolean shouldUseJDBCOuterJoinSyntax() {
        return false; // not sure about this
    }
    
    /** Append the receiver's field 'identity' constraint clause to
    *   a writer.
    *      
    *  Taken from
    *  org.eclipse.persistence\foundation\org.eclipse.persistence.core\src\org\eclipse\persistence\platform\database\AccessPlatform.java
    */
    public void printFieldIdentityClause(Writer writer)	throws ValidationException {
        try {
            writer.write(" IDENTITY");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
    }


    /**
     * Override the default SubstringSingleArg operator. 
     * Cloned from SybasePlatform.java 
     */
    public ExpressionOperator singleArgumentSubstringOperator() {
        ExpressionOperator result = new ExpressionOperator();
        result.setSelector(ExpressionOperator.SubstringSingleArg);
        result.setType(ExpressionOperator.FunctionOperator);
        Vector v = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
        v.addElement("SUBSTRING(");
        v.addElement(",");
        v.addElement(", CHAR_LENGTH(");
        v.addElement("))");
        result.printsAs(v);
        int[] indices = new int[3];
        indices[0] = 0;
        indices[1] = 1;
        indices[2] = 0;

        result.setArgumentIndices(indices);
        result.setNodeClass(ClassConstants.FunctionExpression_Class);
        result.bePrefix();
        return result;
    }




    /**
     *  
     *  Indicates whether the platform supports identity.
     *    
     */
     public boolean supportsIdentity() {
         return true;
     }
    
     // 
     //  Most Temp Table settings cloned from SQLServerPlatform.java
     //    
     /**
      * INTERNAL:
      */
     public boolean supportsLocalTempTables() {
         return true;
     }


     public boolean supportsGlobalTempTables() {
         return true;
     }


     /**
      * INTERNAL:
      */
     protected String getCreateTempTableSqlPrefix() {
         return "CREATE TABLE ";
     }          

     /**
      * INTERNAL:
      */
     public DatabaseTable getTempTableForTable(DatabaseTable table) {
         return new DatabaseTable("#" + table.getName(), table.getTableQualifier(), table.shouldUseDelimiters(), getStartDelimiter(), getEndDelimiter());
     }          


     /** 
     *      
     * Taken from org.eclipse.persistence\foundation\org.eclipse.persistence.core\src\org\eclipse\persistence\platform\database\AccessPlatform.java  
     */
     public void printFieldTypeSize(Writer writer, FieldDefinition field,FieldTypeDefinition fieldType, boolean shouldPrintFieldIdentityClause) throws IOException {
		if (!shouldPrintFieldIdentityClause) {
            // if type requires both precision and scale: NUMERIC, DECIMAL
            if ((fieldType.getName().equals("NUMERIC")) || (fieldType.getName().equals("DECIMAL"))) {
                writer.write(fieldType.getName());
                writer.write("(");
                if (field.getSize() == 0) {
                    writer.write(Integer.valueOf(fieldType.getDefaultSize()).toString());
                } else {
                    writer.write(Integer.valueOf(field.getSize()).toString());
                }
                writer.write(",");
                if (field.getSubSize() != 0) {
                    writer.write(Integer.valueOf(field.getSubSize()).toString());
                } else {
                    writer.write(Integer.valueOf(fieldType.getDefaultSubSize()).toString());
                }
                writer.write(")");
            } else {
                super.printFieldTypeSize(writer, field, fieldType,
                        shouldPrintFieldIdentityClause);
            }
		}
	}

    /**    
     * INTERNAL:
     * Build the identity query for native sequencing.
     *      
     * Taken verbatim from org.eclipse.persistence\foundation\org.eclipse.persistence.core\src\org\eclipse\persistence\platform\database\SQLServerPlatform.java  
     *               
     */
     @Override
     public ValueReadQuery buildSelectQueryForIdentity() {
         ValueReadQuery selectQuery = new ValueReadQuery();
         selectQuery.setSQLString("SELECT @@IDENTITY");
         return selectQuery;
    }

     /**
      * Temporary workaround to avoid joined queries with FOR UPDATE 
      * in them 
      * 
      */
     public String getSelectForUpdateString() {
         return "";
     }




        /**
     * INTERNAL:
     * Indicates whether SELECT DISTINCT ... FOR UPDATE is allowed by the platform (Oracle doesn't allow this).
     */
    public boolean isForUpdateCompatibleWithDistinct() {
        return false;
    }


     /**
     * Setting this to false (cf. Sybase) to work around problem 
     * that unspecified default delete rule is RESTRICT, even when 
     * not allowed due to self-referencing table.  
     */
    @Override
    public boolean supportsDeleteOnCascade() {
        return true;
    }


    /** Attempts to remove FOR UPDATE from queries */
    @Override
    public boolean shouldPrintLockingClauseAfterWhereClause() {
        return false;
    }

        /**
     * INTERNAL:
     * Indicates whether locking clause could be applied to the query that has more than one table
     */
    public boolean supportsLockingQueriesWithMultipleTables() {
        return false;
    }
    

    

}
