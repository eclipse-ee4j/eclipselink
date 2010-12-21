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

// EclipseLink imports
import static org.eclipse.persistence.tools.dbws.Util.InOut;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class DbStoredArgument {

    protected String name;
    protected int jdbcType;
    protected String jdbcTypeName;
    protected int precision;
    protected int scale;
    protected short radix;
    protected boolean nullable;
    protected InOut inOut = IN; // args are IN by default
    protected int seq = 0; // oracle-specific info

    public DbStoredArgument(String name) {
        super();
        this.name = name;
    }

    public boolean isPLSQLArgument() {
        return false;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    public void setJdbcTypeName(String jdbcTypeName) {
        this.jdbcTypeName = jdbcTypeName;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
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

    public String getName() {
        return name;
    }

    public short getRadix() {
        return radix;
    }
    public void setRadix(short radix) {
        this.radix = radix;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getName() != null) {
            sb.append(getName());
            sb.append(' ');
        }
        if (inOut != RETURN) {
            sb.append(inOut);
            sb.append(' ');
        }
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
        return sb.toString();
    }

    public InOut getInOut() {
        return inOut;
    }

    public void setInOut(InOut inOut) {
        this.inOut = inOut;
    }

}
