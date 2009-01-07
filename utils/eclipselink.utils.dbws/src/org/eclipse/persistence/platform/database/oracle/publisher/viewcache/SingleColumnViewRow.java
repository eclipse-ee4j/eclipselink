package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

public class SingleColumnViewRow implements ViewRow {
    public String m_value;

    public SingleColumnViewRow(ResultSet rs) throws java.sql.SQLException {
        m_value = rs.getString(1);
    }

    public String toString() {
        return m_value;
    }

    public String getValue() {
        return m_value;
    }

    public boolean equals(String view, Object row) {
        if (row instanceof SingleColumnViewRow) {
            SingleColumnViewRow singlecol = (SingleColumnViewRow)row;
            return m_value == null && singlecol.getValue() == null || m_value != null
                && m_value.equals(singlecol.getValue());
        }
        return false;
    }
}
