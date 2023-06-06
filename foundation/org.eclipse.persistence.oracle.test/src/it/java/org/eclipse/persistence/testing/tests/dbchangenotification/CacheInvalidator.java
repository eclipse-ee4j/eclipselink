/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.dbchangenotification;

import java.util.*;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * This class translates a database change notification message
 * into invalidation of a corresponding object in TopLink cache.
 *
 * The class expects the invalidation notification to be found in the following
 * JMSMessage properties:
 * String property "TABLE" should contain table name;
 * Properties of appropriate type should contain value(s) of PK field(s).
 *
 * Examples based on Employee demo:
 * 1. EMPLOYEE table:
 * getStringProperty("TABLE") == "EMPLOYEE";
 * getObjectProperty("EMP_ID") == value of EMP_ID column for the modified row
 *
 * 2. SALARY table:
 * getStringProperty("TABLE") == "SALARY";
 * getObjectProperty("EMP_ID") == value of EMP_ID column for the modified row
 *
 * 3. PHONE table:
 * getStringProperty("TABLE") == "PHONE";
 * getObjectProperty("EMP_ID") == value of EMP_ID column for the modified row
 * getObjectProperty("TYPE") == value of TYPE column for the modified row
 */
public class

CacheInvalidator {
    // maps a table name to Class mapped to this table
    Hashtable tableNameToClass;
    // maps table name to a vector of primary key fields' names
    Hashtable tableNameToPkFieldNames;

    // Create a CacheInvalidator object that invalidates cache if the changed table
    // is mapped by one of the descriptors of the passed session

    public CacheInvalidator(Session session) {
        // HashSet is used to avoid duplications
        HashSet tableNames = new HashSet();

        // fill out tableNames collection with all tables' names mapped by all descriptors
        Iterator<ClassDescriptor> descriptors = session.getDescriptors().values().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor desc = descriptors.next();

            // Create a Vector containing names of all tables mapped to the descriptor
            Vector descTableNames = desc.getTableNames();
            // Remove schema names (if any) converting "SCHEMA_NAME.TABLE_NAME" to "TABLE_NAME"
            removePrefixFromDatabaseObjectNames(descTableNames);
            // add descTableNames to the collection
            tableNames.addAll(descTableNames);
        }
        // initialize
        initializeWithTableNames(session, tableNames);
    }

    // Create a CacheInvalidator object that invalidates cache if the changed table's name
    // is in tableNames collection
    // and the table is mapped by one of the descriptors of the passed session.
    // Note that the Collection tableNames will be altered - only names
    // of tables not found in descriptors of the passed session will remain.

    public CacheInvalidator(Session session, Collection tableNames) {
        // initialize
        initializeWithTableNames(session, tableNames);
    }

    protected void initializeWithTableNames(Session session, Collection tableNames) {
        tableNameToClass = new Hashtable(tableNames.size());
        tableNameToPkFieldNames = new Hashtable(tableNames.size());

        // pkFieldVectors cached here to avoid calculating it more than once per class
        Hashtable classToPkFieldNames = new Hashtable();
        // loop through the descriptors to fill out tableNameToClass and tableNameToPkFieldNames
        Iterator<ClassDescriptor> descriptors = session.getDescriptors().values().iterator();
        while (descriptors.hasNext() && !tableNames.isEmpty()) {
            ClassDescriptor desc = descriptors.next();

            // Create a Vector containing names of all tables mapped to the descriptor
            Vector descTableNames = desc.getTableNames();

            // bypass descriptors with no tables
            if (descTableNames.isEmpty()) {
                continue;
            }

            // Remove schema names (if any) converting "SCHEMA_NAME.TABLE_NAME" to "TABLE_NAME"
            removePrefixFromDatabaseObjectNames(descTableNames);

            // handle inheritance: table name should be mapped to the base mapped class
            Class<?> baseClass = desc.getJavaClass();
            while (desc.isChildDescriptor()) {
                desc = session.getDescriptor(desc.getInheritancePolicy().getParentClass());
                baseClass = desc.getJavaClass();
            }

            Iterator it = tableNames.iterator();
            while (it.hasNext()) {
                // for each tableName specified by the user
                String tableName = (String)it.next();
                // verify whether the descriptor maps a table with the same name
                if (descTableNames.contains(tableName)) {
                    // map the table name to the baseClass corresponding to the descriptor
                    tableNameToClass.put(tableName, baseClass);

                    // try to obtain cached pkFieldNames Vector corresponding to baseClass
                    Vector pkFieldNames = (Vector)classToPkFieldNames.get(baseClass);
                    if (pkFieldNames == null) {
                        // Create a Vector containing names of all primary key fields
                        pkFieldNames = desc.getPrimaryKeyFieldNames();
                        // Remove table name converting from "TABLE_NAME.FIELD_NAME" to "FIELD_NAME"
                        removePrefixFromDatabaseObjectNames(pkFieldNames);
                        // cache pkFieldNames Vector corresponding to baseClass
                        classToPkFieldNames.put(baseClass, pkFieldNames);
                    }
                    // map the table name to the Vector of names of primary key fields.
                    tableNameToPkFieldNames.put(tableName, pkFieldNames);

                    // the table name is mapped - remove it from the list of table names to be mapped.
                    it.remove();
                }
            }
        }
    }

    // invalidates in tjhe cache the object corresponding to the massage

    public void invalidateObject(Session session, jakarta.jms.Message msg) throws jakarta.jms.JMSException {
        String tableName = msg.getStringProperty("TABLE");
        if (tableName == null) {
            return;
        }
        Class<?> baseClass = (Class)tableNameToClass.get(tableName);
        if (baseClass == null) {
            return;
        }
        Vector pkFieldNames = (Vector)tableNameToPkFieldNames.get(tableName);
        if (pkFieldNames == null) {
            return;
        }

        // create DatabaseRecord corresponding to the message
        DatabaseRecord row = new DatabaseRecord(pkFieldNames.size());
        for (int i = 0; i < pkFieldNames.size(); i++) {
            String fieldName = (String)pkFieldNames.elementAt(i);
            Object value = msg.getObjectProperty(fieldName);
            row.put(fieldName, value);
        }

        // invalidate in TopLink cache the object corresponding to the row and the baseClass
        session.getIdentityMapAccessor().invalidateObject(row, baseClass);
    }

    // converts "SCHEMA_NAME.TABLE_NAME" to "TABLE_NAME"

    protected void removePrefixFromDatabaseObjectNames(Vector names) {
        for (int i = 0; i < names.size(); i++) {
            String qualifiedName = (String)names.elementAt(i);
            String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
            names.setElementAt(name, i);
        }
    }
}
