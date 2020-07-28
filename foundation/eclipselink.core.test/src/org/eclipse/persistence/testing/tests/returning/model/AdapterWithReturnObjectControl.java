/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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

    public boolean isOriginalSetupRequired() {
        return false;
    }

    public abstract void updateProject(Project project, Session session);

    public abstract void updateDatabase(Session session);

    public Object getObjectForInsert(Session session, Object objectToInsert) {
        ClassDescriptor desc = session.getClassDescriptor(objectToInsert);
        org.eclipse.persistence.sessions.Record rowToInsert = desc.getObjectBuilder().buildRow(objectToInsert, (AbstractSession)session, WriteType.INSERT);
        org.eclipse.persistence.sessions.Record rowReturn = getRowForInsert(rowToInsert);
        if (rowReturn != null && !rowReturn.isEmpty()) {
            org.eclipse.persistence.sessions.Record row = new DatabaseRecord(rowToInsert.size());
            row.putAll(rowToInsert);
            row.putAll(rowReturn);
            return readObjectFromRow(session, desc, row);
        } else {
            return objectToInsert;
        }
    }

    public Object getObjectForUpdate(Session session, Object objectToUpdateBeforeChange, Object objectToUpdateAfterChange, boolean useUOW) {
        ClassDescriptor desc = session.getClassDescriptor(objectToUpdateBeforeChange);
        org.eclipse.persistence.sessions.Record rowBeforeChange = desc.getObjectBuilder().buildRow(objectToUpdateBeforeChange, (AbstractSession)session, WriteType.UPDATE);
        org.eclipse.persistence.sessions.Record rowAfterChange = desc.getObjectBuilder().buildRow(objectToUpdateAfterChange, (AbstractSession)session, WriteType.UPDATE);
        org.eclipse.persistence.sessions.Record rowChange = new DatabaseRecord();
        getChange(rowChange, session, objectToUpdateBeforeChange, objectToUpdateAfterChange, desc, useUOW, WriteType.UPDATE);
        org.eclipse.persistence.sessions.Record rowReturn = getRowForUpdate(rowChange);
        if (rowReturn != null && !rowReturn.isEmpty()) {
            org.eclipse.persistence.sessions.Record row = new DatabaseRecord(rowAfterChange.size());
            row.putAll(rowAfterChange);
            row.putAll(rowReturn);
            return readObjectFromRow(session, desc, row);
        } else {
            return objectToUpdateAfterChange;
        }
    }

    public void getChange(org.eclipse.persistence.sessions.Record row, Session session, Object object1, Object object2, ClassDescriptor desc, boolean useUOW, WriteType writeType) {
        for (Enumeration mappings = desc.getMappings().elements(); mappings.hasMoreElements(); ) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            if (!mapping.isReadOnly()) {
                getChange(row, mapping, session, object1, object2, useUOW, writeType);
            }
        }
    }

    public void getChange(org.eclipse.persistence.sessions.Record row, DatabaseMapping mapping, Session session, Object object1, Object object2, boolean useUOW, WriteType writeType) {
        if (mapping.isAggregateObjectMapping()) {
            Object aggregate1 = mapping.getAttributeValueFromObject(object1);
            Object aggregate2 = mapping.getAttributeValueFromObject(object2);
            if (aggregate1 == null && aggregate2 == null) {
                if (!useUOW) {
                    mapping.writeFromObjectIntoRow(object2, (DatabaseRecord)row, (AbstractSession)session, writeType);
                }
            } else if (aggregate1 != null && aggregate2 != null && aggregate1.getClass().equals(aggregate2.getClass())) {
                ClassDescriptor desc = ((AggregateObjectMapping)mapping).getReferenceDescriptor();
                getChange(row, session, aggregate1, aggregate2, desc, useUOW, writeType);
            } else {
                mapping.writeFromObjectIntoRow(object2, (DatabaseRecord)row, (AbstractSession)session, writeType);
            }
        } else {
            org.eclipse.persistence.sessions.Record row1 = new DatabaseRecord();
            org.eclipse.persistence.sessions.Record row2 = new DatabaseRecord();
            mapping.writeFromObjectIntoRow(object1, (DatabaseRecord)row1, (AbstractSession)session, writeType);
            mapping.writeFromObjectIntoRow(object2, (DatabaseRecord)row2, (AbstractSession)session, writeType);

            for (int i = 0; i < row1.size(); i++) {
                DatabaseField field = (DatabaseField)((DatabaseRecord)row1).getFields().elementAt(i);
                Object valueBefore = ((DatabaseRecord)row1).getValues().elementAt(i);
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

    protected Object readObjectFromRow(Session session, ClassDescriptor desc, org.eclipse.persistence.sessions.Record row) {
        if (desc.hasInheritance()) {
            Class newClass = desc.getInheritancePolicy().classFromRow((DatabaseRecord)row, (AbstractSession)session);
            desc = session.getClassDescriptor(newClass);
        }
        Object object = desc.getObjectBuilder().buildNewInstance();
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSession((AbstractSession)session);
        for (Enumeration mappings = desc.getMappings().elements(); mappings.hasMoreElements(); ) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
            mapping.readFromRowIntoObject((DatabaseRecord)row, query.getJoinedAttributeManager(), object, null, query, query.getSession(), true);
        }
        return object;
    }

    protected org.eclipse.persistence.sessions.Record getRowForInsert(org.eclipse.persistence.sessions.Record rowToInsert) {
        return null;
    }

    protected org.eclipse.persistence.sessions.Record getRowForUpdate(org.eclipse.persistence.sessions.Record rowChange) {
        return null;
    }
}
