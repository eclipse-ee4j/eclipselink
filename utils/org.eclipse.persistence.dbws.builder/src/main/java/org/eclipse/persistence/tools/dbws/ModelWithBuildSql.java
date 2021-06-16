/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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

    @Override
    public boolean hasBuildSql() {
        if (buildSql != null && buildSql.length() > 0) {
            return true;
        }
        return super.hasBuildSql();
    }
}
