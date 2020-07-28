/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - Jan 27 2012, refactor hierarchy of OperationModel so
//     that both ProcedureOperationModel and SQLOperationModel have 'buildSql'
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
        return super.hasBuildSql();
    }
}
