package org.eclipse.persistence.tools.dbws.oracle;

import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import static org.eclipse.persistence.tools.dbws.Util.InOut.RETURN;

public class PLSQLStoredArgument extends DbStoredArgument {

    protected String plSqlTypeName;
    
    public PLSQLStoredArgument(String name) {
        super(name);
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