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
 *     Mike Norman - May 01 2008, created DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.util.ArrayList;
import java.util.List;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;

public class ProcedureOperationModel extends OperationModel {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String procedurePattern;
    //cache resolved DatabaseType's
    transient protected List<ProcedureType> dbStoredProcedures = new ArrayList<ProcedureType>();

    //TODO - do we really need any of these?
    protected int overload; // Oracle-specific
    protected boolean isAdvancedJDBC = false;
    protected List<DatabaseType[]> argumentTypes = null;
    protected DatabaseType dbStoredFunctionReturnType = null;
    protected boolean hasComplexArguments = false;

    public ProcedureOperationModel() {
        super();
    }

    @Override
    public boolean isProcedureOperation() {
        return true;
    }
    public boolean isPLSQLProcedureOperation() {
        return false;
    }

    public String getCatalogPattern() {
        return catalogPattern;
    }
    public void setCatalogPattern(String catalogPattern) {
        if ("null".equalsIgnoreCase(catalogPattern)) {
            this.catalogPattern = null;
        }
        else {
            this.catalogPattern = catalogPattern;
        }
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }
    public void setSchemaPattern(String schemaPattern) {
        if ("null".equalsIgnoreCase(schemaPattern)) {
            this.schemaPattern = null;
        }
        else {
            this.schemaPattern = schemaPattern;
        }
    }

    public String getProcedurePattern() {
        return procedurePattern;
    }
    public void setProcedurePattern(String procedurePattern) {
        this.procedurePattern = procedurePattern;
    }

    public List<ProcedureType> getDbStoredProcedures() {
        return dbStoredProcedures;
    }
    public void setDbStoredProcedures(List<ProcedureType> dbStoredProcedures) {
        this.dbStoredProcedures = dbStoredProcedures;
    }

    public int getOverload() {
        return overload;
    }
    public void setOverload(int overload) {
        this.overload = overload;
    }

    public boolean isAdvancedJDBCProcedureOperation() {
        return isAdvancedJDBC;
    }
    public void setIsAdvancedJDBCProcedureOperation(boolean isAdvancedJDBC) {
        this.isAdvancedJDBC = isAdvancedJDBC;
    }

    @Override
    public void buildOperation(DBWSBuilder builder) {
        super.buildOperation(builder);
        builder.getBuilderHelper().buildProcedureOperation(this);
    }

    /**
     * Indicates if this ProcedureOperationModel has types set for its
     * stored procedure arguments, i.e. argumentTypes is non-null.
     *
     * @return true if this ProcedureOperationModel has types set for
     *         its stored procedure arguments, false otherwise
     */
    public boolean hasArgumentTypes() {
        return argumentTypes != null;
    }

    /**
     * Return the List of DatabaseType[] entries for this ProcedureOperationModel
     * instance's stored procedure arguments, or null if not set.   It is assumed
     * that each entry in the List corresponds to a  stored procedure at the same
     * index in the dbStoredProcedures List. It is also assumed the each entry in
     * a given DatabaseType[] corresponds to an argument in the associated stored
     * procedure at the same index.
     *
     * @return List of DatabaseType[] entries for this ProcedureOperationModel
     *         instance's stored procedure arguments, or null if not set
     */
    public List<DatabaseType[]> getArgumentTypes() {
        return argumentTypes;
    }

    /**
     * Add to the List of DatabaseType[] entries  for  this  ProcedureOperationModel
     * instance's stored procedures.       It is assumed that each entry in the List
     * corresponds to a stored procedure at the same index in the dbStoredProcedures
     * List. It is also assumed the each entry in a given DatabaseType[] corresponds
     * to an argument in the associated stored procedure at the same index.
     *
     * @param argumentTypes
     */
    public void addArgumentTypes(DatabaseType[] dbTypes) {
        if (argumentTypes == null) {
            argumentTypes = new ArrayList<DatabaseType[]>();
        }
        argumentTypes.add(dbTypes);
    }

    /**
     * Set  the  List of DatabaseType[]  entries  for  this  ProcedureOperationModel
     * instance's stored procedures.       It is assumed that each entry in the List
     * corresponds to a stored procedure at the same index in the dbStoredProcedures
     * List. It is also assumed the each entry in a given DatabaseType[] corresponds
     * to an argument in the associated stored procedure at the same index.
     *
     * @param dbStoredProcedureTypes
     */
    public void setArgumentTypes(List<DatabaseType[]> argumentTypes) {
        this.argumentTypes = argumentTypes;
    }

    /**
     * Get the DatabaseType of the stored function's return argument.
     *
     * @return the stored function's return type
     */
    public DatabaseType getDbStoredFunctionReturnType() {
        return dbStoredFunctionReturnType;
    }

    /**
     * Set the DatabaseType of the stored function's return argument.
     *
     * @param dbStoredFunctionReturnType
     */
    public void setDbStoredFunctionReturnType(DatabaseType dbStoredFunctionReturnType) {
        this.dbStoredFunctionReturnType = dbStoredFunctionReturnType;
    }

    /**
     * Indicates if this ProcedureOperationModel contains advanced Oracle
     * and/or complex PL/SQL arguments.
     */
    public boolean hasComplexArguments() {
        return hasComplexArguments;
    }

    /**
     * Sets the boolean that indicates if this ProcedureOperationModel
     * contains advanced Oracle and/or complex PL/SQL arguments.
     */
    public void setHasComplexArguments(boolean hasComplexArguments) {
        this.hasComplexArguments = hasComplexArguments;
    }
}