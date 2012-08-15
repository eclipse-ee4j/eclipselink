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
 *     Mike Norman - May 01 2008, created DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

//Javase imports
import java.util.ArrayList;
import java.util.List;

//Oracle DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;

public class TableOperationModel extends OperationModel {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String tablePattern;
    public ArrayList<OperationModel> additionalOperations;
    //cache resolved DatabaseType's
    transient protected List<TableType> dbTables = new ArrayList<TableType>();

    public TableOperationModel() {
        super();
    }

    @Override
    public boolean isTableOperation() {
        return true;
    }

    public String getTablePattern() {
        return tablePattern;
    }
    public void setTablePattern(String tablePattern) {
        this.tablePattern = tablePattern;
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

    public List<TableType> getDbTables() {
        return dbTables;
    }
    public void setDbTables(List<TableType> dbTables) {
        this.dbTables = dbTables;
    }
    
    /**
     * Return the List of additional (nested) operations for this
     * TableOperationModel
     */
    public ArrayList<OperationModel> getAdditionalOperations() {
        if (additionalOperations == null) {
            additionalOperations = new ArrayList<OperationModel>();
        }
        return additionalOperations;
    }

    /**
     * Add an operation to the List of additional (nested) operations
     * for this TableOperationModel
     */
    public void addOperation(OperationModel additionalOperation) {
        getAdditionalOperations().add(additionalOperation);
    }
}