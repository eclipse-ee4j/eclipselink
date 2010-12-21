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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws.oracle;

import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class PLSQLStoredArgument extends DbStoredArgument {

    protected String plSqlTypeName;

    public PLSQLStoredArgument(String name) {
        super(name);
    }

    public boolean isPLSQLArgument() {
        return true;
    }

    public String getPlSqlTypeName() {
        return plSqlTypeName;
    }
    public void setPlSqlTypeName(String plSqlTypeName) {
        this.plSqlTypeName = plSqlTypeName;
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
        sb.append(plSqlTypeName);
        sb.append('[');
        sb.append(jdbcTypeName);
        sb.append(']');
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

}