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

// Javase imports

// Java extension imports

public class DbColumn {

    protected String name;
    protected int ordinalPosition;
    protected int jdbcType;
    protected String jdbcTypeName;
    protected int precision;
    protected int scale;
    protected boolean nullable;
    protected boolean pk = false;
    protected String pkConstraintName;
    protected boolean unique = false;

    public DbColumn() {
    }

    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }

    public int getOrdinalPosition() {
      return ordinalPosition;
    }
    public void setOrdinalPosition(int ordinalPosition) {
      this.ordinalPosition = ordinalPosition;
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

    public int getPrecision() {
      return precision;
    }
    public void setPrecision(int size) {
      this.precision = size;
    }

    public int getScale() {
      return scale;
    }
    public void setScale(int scale) {
      this.scale = scale;
    }

    public boolean isNullable() {
      return nullable;
    }
    public void setNullable(boolean nullable) {
      this.nullable = nullable;
    }

    public boolean isPK() {
      return pk;
    }
    public void setPK(boolean pk) {
      this.pk = pk;
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
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" ");
        sb.append(jdbcTypeName);
        if (precision > 0) {
          sb.append('(');
          sb.append(precision);
          if (scale > 0) {
            sb.append(',');
            sb.append(scale);
          }
          sb.append(')');
        }
        if (!nullable) {
            sb.append(" NOT NULL");
        }
        if (unique) {
          sb.append(" (UNIQUE)");
        }
        if (pkConstraintName != null && pkConstraintName.length() > 0) {
          sb.append(" CONSTRAINT ");
          sb.append(pkConstraintName);
        }
        if (pk) {
          sb.append(" PRIMARY KEY");
        }
        return sb.toString();
    }
}
