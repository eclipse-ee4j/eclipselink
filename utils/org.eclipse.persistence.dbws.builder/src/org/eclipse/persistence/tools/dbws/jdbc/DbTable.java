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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
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
