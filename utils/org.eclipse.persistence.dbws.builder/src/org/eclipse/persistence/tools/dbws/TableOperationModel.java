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

import java.util.ArrayList;

// Javase imports

public class TableOperationModel extends OperationModel {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String tablePattern;
    public ArrayList<OperationModel> additionalOperations;

    public TableOperationModel() {
        super();
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

    @Override
    public boolean isTableOperation() {
        return true;
    }
}
