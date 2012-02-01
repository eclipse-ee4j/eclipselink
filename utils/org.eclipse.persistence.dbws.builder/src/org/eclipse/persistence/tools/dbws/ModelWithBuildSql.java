/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - Jan 27 2012, refactor hierarchy of OperationModel so
 *     that both ProcedureOperationModel and SQLOperationModel have 'buildSql'
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

public class ModelWithBuildSql extends OperationModel {

    protected String buildSql;

    public ModelWithBuildSql() {
    }

    public String getBuildSql() {
        return buildSql;
    }
    public void setBuildSql(String buildSql) {
        if (buildSql != null && buildSql.length() > 0) {
            this.buildSql = buildSql;
            setIsSimpleXMLFormat(false);
        }
        else {
            // clears build SQL string; back to simple XML
            this.buildSql = null;
            setIsSimpleXMLFormat(true);
        }
    }

    public boolean hasBuildSql() {
        if (buildSql != null && buildSql.length() > 0) {
            return true;
        }
        return false;
    }
}