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
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

//javase imports
import java.sql.ResultSet;

public class SingleColumnViewRow extends AbstractViewRow implements ViewRow {

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

    @Override
    public boolean isSingleColumnViewRow() {
        return false;
    }

}