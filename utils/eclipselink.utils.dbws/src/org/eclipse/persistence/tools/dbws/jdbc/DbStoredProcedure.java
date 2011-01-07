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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws.jdbc;

// Javase imports
import java.util.ArrayList;
import java.util.List;

// Java extension imports

public class DbStoredProcedure {

    protected String catalog;
    protected String schema;
    protected String name;
    protected List<DbStoredArgument> arguments = new ArrayList<DbStoredArgument>();
    protected int overload = 0; // oracle-specific info

    public DbStoredProcedure(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public List<DbStoredArgument> getArguments() {
        return arguments;
    }

    public boolean isFunction() {
        return false;
    }

    public int getOverload() {
        return overload;
    }
    public void setOverload(int overload) {
        this.overload = overload;
    }

    public String getStoredType() {
        return "PROCEDURE";
    }

    public boolean matches(String catalog, String schema, String name, boolean isOracle,
        boolean catalogMatchDontCare) {

        // return true if all 3 match, sorta

        boolean catalogMatch =
            this.catalog == null ?
                // for Oracle, catalog matching is 'dont-care' only if null
                (isOracle ? true :
                // other platforms: null has to match null
               (catalog == null))
                : this.catalog.equals(catalog);
        // but catalogDontCare trumps!
        if (catalogMatchDontCare) {
            catalogMatch = true;
        }
        boolean schemaMatch =
            // either they are both null or they match
            this.schema == null ? (schema == null)
                : this.schema.equals(schema);
        boolean nameMatch =
            // either they are both null or they match
            this.name == null ? (name == null)
                : this.name.equals(name);
        return catalogMatch && schemaMatch && nameMatch;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (overload > 0) {
        sb.append('(');
        sb.append(overload);
        sb.append(')');
      }
      sb.append(getStoredType());
      sb.append(' ');
      if (schema != null && schema.length() > 0) {
          sb.append(schema);
          sb.append('.');
      }
      if (catalog != null && catalog.length() > 0) {
          sb.append(catalog);
          sb.append('.');
      }
      sb.append(name);
      if (arguments.size() > 0) {
        sb.append(" (");
        for (DbStoredArgument arg : arguments) {
            sb.append("\n\t");
            sb.append(arg);
        }
        sb.append("\n)");
      }
      else {
        sb.append("()\n");
      }
      return sb.toString();
    }
}
