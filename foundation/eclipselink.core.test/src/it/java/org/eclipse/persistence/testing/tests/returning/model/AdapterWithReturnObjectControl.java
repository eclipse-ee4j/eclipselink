/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.returning.model;

import java.util.Enumeration;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.tests.returning.ProjectAndDatabaseAdapter;

public abstract class AdapterWithReturnObjectControl implements ProjectAndDatabaseAdapter, ReturnObjectControl {

    @Override
    public boolean isOriginalSetupRequired() {
        return false;
    }

    @Override
    public abstract void updateProject(Project project, Session session);

    @Override
    public abstract void updateDatabase(Session session);

    @Override
    public Object getObjectForInsert(Session session, Object objectToInsert) {
        ClassDescriptor desc = session.getClassDescriptor(objectToInsert);
        DataRecord rowToInsert = desc.getObjectBuilder().buildRow(objectToInsert, (AbstractSession)session, WriteType.INSERT);
        DataRecord rowReturn = getRowForInsert(rowToInsert);
        if (rowReturn != null && !rowReturn.isEmpty()) {
            DataRecord row = new DatabaseRecord(rowToInsert.size());
            row.putAll(rowToInsert);
            row.putAll(rowReturn);
            return readObjectFromRow(session, desc, row);
        } else {
            return objectToInsert;
        }
    }

    @Override
    public Object getObjectForUpdate(Session session, Object objectToUpdateBeforeChange, Object objectToUpdateAfterChange, boolean useUOW) {
        ClassDescriptor desc = session.getClassDescriptor(objectToUpdateBeforeChange);
        DataRecord rowBeforeChange = desc.getObjectBuilder().buildRow(objectToUpdateBeforeChange, (AbstractSession)session, WriteType.UPDATE);
        DataRecord rowAfterChange = desc.getObjectBuilder().buildRow(objectToUpdateAfterChange, (AbstractSession)session, WriteType.UPDATE);
        DataRecord rowChange = new DatabaseRecord();
        getChange(rowChange, session, objectToUpdateBeforeChange, objectToUpdateAfterChange, desc, useUOW, WriteType.UPDATE);
        DataRecord rowReturn = getRowForUpdate(rowChange);
        if (rowReturn != null && !rowReturn.isEmpty()) {
            DataRecord row = new DatabaseRecord(rowAfterChange.size());
            row.putAll(rowAfterChange);
            row.putAll(rowReturn);
            return readObjectFromRow(session, desc, row);
        } else {
            return objectToUpdateAfterChange;
        }
    }

    public void getChange(DataRecord row, Session session, Object object1, Object object2, ClassDescriptor desc, boolean useUOW, WriteType writeType) {
        for (Enumeration<DatabaseMapping> mappings = desc.getMappings().elements(); mappings.hasMoreElements(); ) {
            DatabaseMapping mapping = mappings.nextElement();
            if (!mapping.isReadOnly()) {
                getChange(row, mapping, session, object1, object2, useUOW, writeType);
            }
        }
    }

    public void getChange(DataRecord row, DatabaseMapping mapping, Session session, Object object1, Object object2, boolean useUOW, WriteType writeType) {
        if (mapping.isAggregateObjectMapping()) {
            Object aggregate1 = mapping.getAttributeValueFromObject(object1);
            Object aggregate2 = mapping.getAttributeValueFromObject(object2);
            if (aggregate1 == null && aggregate2 == null) {
                if (!useUOW) {
                    mapping.writeFromObjectIntoRow(object2, (DatabaseRecord)row, (AbstractSession)session, writeType);
                }
            } else if (aggregate1 != null && aggregate2 != null && aggregate1.getClass().equals(aggregate2.getClass())) {
                ClassDescriptor desc = mapping.getReferenceDescriptor();
                getChange(row, session, aggregate1, aggregate2, desc, useUOW, writeType);
            } else {
                mapping.writeFromObjectIntoRow(object2, (DatabaseRecord)row, (AbstractSession)session, writeType);
            }
        } else {
            DatabaseRecord row1 = new DatabaseRecord();
            DatabaseRecord row2 = new DatabaseRecord();
            mapping.writeFromObjectIntoRow(object1, row1, (AbstractSession)session, writeType);
            mapping.writeFromObjectIntoRow(object2, row2, (AbstractSession)session, writeType);

            for (int i = 0; i < row1.size(); i++) {
                DatabaseField field = row1.getFields().elementAt(i);
                Object valueBefore = row1.getValues().elementAt(i);
                Object valueAfter = row2.get(field);
                boolean changed;
                if (valueAfter == null) {
                    changed = valueBefore != null;
                } else {
                    changed = !valueAfter.equals(valueBefore);
                }
                if (changed) {
                    row.put(field, valueAfter);
                }
            }
        }
    }

    protected Object readObjectFromRow(Session session, ClassDescriptor desc, DataRecord row) {
        if (desc.hasInheritance()) {
            Class newClass = desc.getInheritancePolicy().classFromRow((DatabaseRecord)row, (AbstractSession)session);
            desc = session.getClassDescriptor(newClass);
        }
        Object object = desc.getObjectBuilder().buildNewInstance();
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSession((AbstractSession)session);
        for (Enumeration<DatabaseMapping> mappings = desc.getMappings().elements(); mappings.hasMoreElements(); ) {
            DatabaseMapping mapping = mappings.nextElement();
            mapping.readFromRowIntoObject((DatabaseRecord)row, query.getJoinedAttributeManager(), object, null, query, query.getSession(), true);
        }
        return object;
    }

    protected DataRecord getRowForInsert(DataRecord rowToInsert) {
        return null;
    }

    protected DataRecord getRowForUpdate(DataRecord rowChange) {
        return null;
    }
}
