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
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;

public class DbColumn extends FieldType {

    protected int jdbcType;
    protected String jdbcTypeName;
    protected String pkConstraintName;
    protected boolean unique = false;

    public DbColumn(String columnName) {
        super(columnName);
    }

    public int getJDBCType() {
      return jdbcType;
    }
    public void setJDBCType(int jdbcType) {
      this.jdbcType = jdbcType;
    }

    public String getJDBCTypeName() {
      return jdbcTypeName;
    }
    public void setJDBCTypeName(String jdbcTypeName) {
      this.jdbcTypeName = jdbcTypeName;
    }

    public String getPkConstraintName() {
      return pkConstraintName;
    }
    public void setPkConstraintName(String pkConstraintName) {
      this.pkConstraintName = pkConstraintName;
    }

    public boolean isUnique() {
      return unique;
    }
    public void setUnique(boolean unique) {
      this.unique = unique;
    }

    @Override
    public String toString() {
      //TODO - additional JDBC info should be displayed somehow
      StringBuilder sb = new StringBuilder(super.toString());
      return sb.toString();
    }
}
