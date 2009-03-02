/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
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

import java.io.Externalizable;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class RowsCacheEntry implements Externalizable {
    private String m_view;
    private String[] m_selects;
    private String[] m_keys;
    private Object[] m_values;
    private ArrayList m_rows;

    public RowsCacheEntry() {
        // for serialization
    }

    public RowsCacheEntry(String view, String[] selects, String[] keys, Object[] values,
        ArrayList rows) {
        m_view = view;
        m_keys = keys;
        m_values = values;
        m_rows = rows;
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

    public ArrayList getRows() {
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
                    /*
                     * String[] toKeys = to.getKeys(); HashSet toHash = new HashSet(); HashSet mHash
                     * = new HashSet(); for (int i=0; i<toKeys; i++) { toHash.add(toKeys[i]); } for
                     * (int i=0; i<m_keys; i++) { mHash.add(m_keys[i]); if
                     * (!toHash.contains(m_keys[i])) { compatible = false; } } if (compatible) {
                     * ArrayList diffKeys = new ArrayList(); ArrayList diffValues = new ArrayList();
                     * for (int i=0; i<toKeys; i++) { if (!mHash.contains(toKeys[i])) {
                     * diffKeys.add(toKeys[i]); diffValues.add(to.getValues()[i]); } } diff = new
                     * RowsCacheEntry( m_view, (String[]) diffKeys.toArray(new String[0]),
                     * diffValues.toArray(new Object[0]), m_rows); }
                     */

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
                    ArrayList keys = new ArrayList();
                    ArrayList values = new ArrayList();
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
                        diff = new RowsCacheEntry(m_view, m_selects, (String[])keys
                            .toArray(new String[0]), values.toArray(new Object[0]), m_rows);
                    }
                }
            }
        }
        return diff;
    }

    public void readExternal(java.io.ObjectInput in) throws IOException, ClassNotFoundException {
        m_view = (String)in.readObject();
        m_selects = (String[])in.readObject();
        m_keys = (String[])in.readObject();
        m_values = (Object[])in.readObject();
        // in.readObject(new Integer(m_values.length));
        // for (int i=0; i<m_values.length; i++) {
        // in.readObject(m_values[i]);
        // }
        int rowsSize = ((Integer)in.readObject()).intValue();
        m_rows = new ArrayList(rowsSize);
        for (int i = 0; i < rowsSize; i++) {
            m_rows.add(in.readObject());
        }
    }

    public void writeExternal(java.io.ObjectOutput out) throws IOException {
        out.writeObject(m_view);
        out.writeObject(m_selects);
        out.writeObject(m_keys);
        out.writeObject(m_values);
        // out.writeObject(new Integer(m_values.length));
        // for (int i=0; i<m_values.length; i++) {
        // out.writeObject(m_values[i]);
        // }
        out.writeObject(new Integer(m_rows.size()));
        for (int i = 0; i < m_rows.size(); i++) {
            out.writeObject(m_rows.get(i));
        }
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
