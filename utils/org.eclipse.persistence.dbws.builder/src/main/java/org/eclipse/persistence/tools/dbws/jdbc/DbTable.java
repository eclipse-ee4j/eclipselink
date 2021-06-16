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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.dbws.jdbc;

//javase imports

//java eXtension imports

//DDL parser imports
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;

public class DbTable extends TableType {

    protected String catalog;
    protected String type; // TABLE or VIEW or whatever ...

    public DbTable() {
    }

    public String getCatalog() {
      return catalog;
    }
    public void setCatalog(String catalog) {
      this.catalog = catalog;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
      //TODO - catalog should be displayed somehow
      StringBuilder sb = new StringBuilder(super.toString());
      return sb.toString();
    }
}
