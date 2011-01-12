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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

//javase imports
import java.util.ArrayList;

public class RowsCacheEntry {

    protected String m_view;
    protected String[] m_selects;
    protected String[] m_keys;
    protected Object[] m_values;
    protected ArrayList<ViewRow> m_rows;

    public RowsCacheEntry() {
        // for serialization
    }

    public RowsCacheEntry(String view, String[] selects, String[] keys, Object[] values,
        ArrayList<ViewRow> rowsV) {
        m_view = view;
        m_keys = keys;
        m_values = values;
        m_rows = rowsV;
        m_selects = selects;
    }

    public String getView() {
        return m_view;
    }

    public String[] getSelects() {
        return m_selects;
    }

    public String[] getKeys() {
        return m_keys;
    }

    public Object[] getValues() {
        return m_values;
    }

    public ArrayList<ViewRow> getRows() {
        return m_rows;
    }

    /**
     * Instance "this" is the cache query
     *
     * @param to
     *            the target query
     * @return ${to} MINUS ${this}
     */
    public RowsCacheEntry compare(RowsCacheEntry to) {
        RowsCacheEntry diff = null;
        if (m_view.equals(to.getView())) {
            if (m_selects.length == 0 || to.getSelects().length == 1) {
                if (m_keys.length < to.getKeys().length) {
                    boolean compatible = true;
                    for (int i = 0; i < m_keys.length; i++) {
                        boolean match = false;
                        for (int j = 0; j < to.getKeys().length; j++) {
                            if (m_keys[i].equals(to.getKeys()[j]) && (i + 1) < m_keys.length
                                && m_keys[i].equals(m_keys[i + 1])
                                && ("" + m_values[i]).equals("" + to.getValues()[j])) {
                                if ((j + 1) < to.getKeys().length
                                    && to.getKeys()[j].equals(to.getKeys()[j + 1])
                                    && ("" + m_values[i + 1]).equals("" + to.getValues()[j + 1])) {
                                    i++;
                                    j++;
                                    match = true;
                                }
                                else {
                                    match = false;
                                    break;
                                }
                            }
                            else if (m_keys[i].equals(to.getKeys()[j])
                                && ("" + m_values[i]).equals("" + to.getValues()[j])) {
                                match = true;
                            }
                        }
                        if (!match) {
                            compatible = false;
                            break;
                        }
                    }
                    ArrayList<String> keys = new ArrayList<String>();
                    ArrayList<Object> values = new ArrayList<Object>();
                    for (int j = 0; j < to.getKeys().length; j++) {
                        boolean match = false;
                        for (int i = 0; i < m_keys.length; i++) {
                            if (m_keys[i].equals(to.getKeys()[j])) {
                                match = true;
                            }
                        }
                        if (!match) {
                            keys.add(to.getKeys()[j]);
                            values.add(to.getValues()[j]);
                        }
                    }
                    if (compatible) {
                        diff = new RowsCacheEntry(m_view, m_selects,
                            keys.toArray(new String[keys.size()]),
                            values.toArray(new Object[values.size()]),
                            m_rows);
                    }
                }
            }
        }
        return diff;
    }

    public String printSummary() {
        String text = "";
        text += "  view: " + m_view + "\n";
        text += "  what: ";
        for (int i = 0; i < m_selects.length; i++) {
            text += m_selects[i] + " ";
        }
        text += "\n";
        text += "  where: ";
        for (int i = 0; i < m_keys.length; i++) {
            text += m_keys[i] + "=" + m_values[i] + " ";
        }
        text += "\n";
        text += "  rows: " + m_rows.size();
        return text;
    }
}