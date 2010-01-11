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
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//EclipseLink imports
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_ARGUMENTS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_COLL_TYPES;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_METHOD_PARAMS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_METHOD_RESULTS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_OBJECTS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_QUEUE_TABLES;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_SYNONYMS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_TYPES;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_TYPE_ATTRS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.ALL_TYPE_METHODS;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.USER_ARGUMENTS;

public class ViewRowFactory extends AbstractViewRow implements ViewRow {

    private transient Map<String, Field> m_fieldCache;

    public ViewRowFactory() {
        m_fieldCache = new HashMap<String, Field>();
    }

    /*
     * Create a new ViewRow
     */
    public static ViewRow createViewRow(String view, String[] columns, ResultSet rs) throws SQLException {
        ViewRow vr = null;
        if (columns.length == 1) {
            vr = new SingleColumnViewRow(rs);
        }
        else if (view.equalsIgnoreCase(ALL_ARGUMENTS)) {
            vr = new AllArguments(rs);
        }
        else if (view.equalsIgnoreCase(USER_ARGUMENTS)) {
            vr = new UserArguments(rs);
        }
        else if (view.equalsIgnoreCase(ALL_COLL_TYPES)) {
            vr = new AllCollTypes(rs);
        }
        else if (view.equalsIgnoreCase(ALL_TYPES)) {
            vr = new AllTypes(rs);
        }
        else if (view.equalsIgnoreCase(ALL_TYPE_METHODS)) {
            vr = new AllTypeMethods(rs);
        }
        else if (view.equalsIgnoreCase(ALL_TYPE_ATTRS)) {
            vr = new AllTypeAttrs(rs);
        }
        else if (view.equalsIgnoreCase(ALL_METHOD_RESULTS)) {
            vr = new AllMethodResults(rs);
        }
        else if (view.equalsIgnoreCase(ALL_METHOD_PARAMS)) {
            vr = new AllMethodParams(rs);
        }
        else if (view.equalsIgnoreCase(ALL_OBJECTS)) {
            vr = new AllObjects(rs);
        }
        else if (view.equalsIgnoreCase(ALL_QUEUE_TABLES)) {
            vr = new AllQueueTables(rs);
        }
        else if (view.equalsIgnoreCase(ALL_SYNONYMS)) {
            vr = new AllSynonyms(rs);
        }
        else {
            throw new SQLException("View cache does not support " + view);
        }
        return vr;
    }

    /*
     * Determine the list of attribute for projection
     */
    public static String getProject(String view, String[] columns) {
        String[] projectList = null;
        if (columns.length > 0) {
            projectList = columns;
        }
        else if (view.equalsIgnoreCase(ALL_ARGUMENTS)) {
            projectList = AllArguments.getProjectList();
        }
        else if (view.equalsIgnoreCase(USER_ARGUMENTS)) {
            projectList = UserArguments.getProjectList();
        }
        if (projectList == null) {
            return "*";
        }
        String project = "";
        for (int i = 0; i < projectList.length; i++) {
            project += projectList[i];
            if (i < projectList.length - 1) {
                project += ", ";
            }
        }
        return project;
    }

    public static boolean hasSequence(String view) {
        return view.equalsIgnoreCase(USER_ARGUMENTS)
            || view.equalsIgnoreCase(ALL_ARGUMENTS);
    }

    public static boolean hasPosition(String view) {
        return view.equalsIgnoreCase(USER_ARGUMENTS)
            || view.equalsIgnoreCase(ALL_ARGUMENTS);
    }

    public boolean equals(String key, Object value) {
        boolean eq = false;
        try {
            // for serialization
            if (m_fieldCache == null) {
                m_fieldCache = new HashMap<String, Field>();
            }
            Field field = (Field)m_fieldCache.get(key);
            if (field == null) {
                Class<?> cls = getClass();
                Field[] fields = cls.getFields();
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().equalsIgnoreCase(key)) {
                        field = fields[i];
                        m_fieldCache.put(key, field);
                    }
                }
            }
            if (field == null) {
                System.err.println("ERROR: " + getClass().getName() + " does not have field " + key);
            }
            Object fieldValue = field.get(this);
            if (key.equals("OWNER")) {
                eq = fieldValue == null || fieldValue.equals(value);
            }
            else if (value == null) {
                eq = fieldValue == null || fieldValue.equals("");
            }
            else if (value.equals("NOT NULL")) {
                eq = fieldValue != null && !fieldValue.equals("");
            }
            else if (fieldValue == null) {
                eq = false;
            }
            else {
                eq = value.equals(fieldValue);
                if (!eq && !(value instanceof String)) {
                    eq = value.toString().equals(fieldValue.toString());
                }
            }
        }
        catch (SecurityException e) {
            // ignore
        }
        catch (IllegalAccessException e) {
            // ignore
        }
        return eq;
    }

}