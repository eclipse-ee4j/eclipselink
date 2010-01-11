/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

// Javase imports

// Java extension imports

import java.util.ArrayList;
import java.util.List;

public class DbTable {

    protected String catalog;
    protected String schema;
    protected String name;
    protected String type = "TABLE";
    protected List<DbColumn> columns = new ArrayList<DbColumn>();
    
    public DbTable() {
    }

    public String getCatalog() {
      return catalog;
    }
    public void setCatalog(String catalog) {
      this.catalog = catalog;
    }

    public String getSchema() {
      return schema;
    }
    public void setSchema(String schema) {
      this.schema = schema;
    }

    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }

    public List<DbColumn> getColumns() {
      return columns;
    }
    public void setColumns(ArrayList<DbColumn> columns) {
        this.columns = columns;
    }
    
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(type);
      sb.append(' ');
      if (catalog != null && catalog.length() > 0) {
          sb.append(catalog);
          sb.append('.');
      }
      if (schema != null && schema.length() > 0) {
        sb.append(schema);
        sb.append('.');
      }
      sb.append(name);
      if (columns.size() > 0) {
        sb.append(" (");
        for (DbColumn dbColumn : columns) {
            sb.append("\n\t");
            sb.append(dbColumn);
        }
        sb.append("\n)");
      }
      return sb.toString();
    }
}
