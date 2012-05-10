/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.returning.model;

import java.math.*;

import java.util.*;

import java.io.StringWriter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.SQLCall;

// This adapter requires project of type ReturningProject");
public class AdapterForReturningProject extends AdapterWithReturnObjectControl {

    static class InsertInfo {
        InsertInfo(boolean isReadOnly, boolean isSequence, boolean overrideNullOnly, Object value) {
            this.isReadOnly = isReadOnly;
            this.isSequence = isSequence;
            this.overrideNullOnly = overrideNullOnly;
            this.value = value;
        }
        boolean isReadOnly;
        boolean isSequence;
        boolean overrideNullOnly;
        Object value;
    }

    static class UpdateInfo {
        UpdateInfo(boolean overrideNullOnly, Object value) {
            this.overrideNullOnly = overrideNullOnly;
            this.value = value;
        }
        boolean overrideNullOnly;
        Object value;
    }

    Hashtable insertInfos = new Hashtable();
    Hashtable updateInfos = new Hashtable();

    public void addInsert(String qualifiedName, Object value) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        insertInfos.put(field, new InsertInfo(false, false, false, value));
    }

    public void addInsert(String qualifiedName, Object value, boolean overrideNullOnly) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        insertInfos.put(field, new InsertInfo(false, false, overrideNullOnly, value));
    }

    public void addInsertReadOnly(String qualifiedName, Object value) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        insertInfos.put(field, new InsertInfo(false, false, false, value));
    }

    public void addInsertSequence(String qualifiedName) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        insertInfos.put(field, new InsertInfo(false, true, false, new BigDecimal(0)));
    }

    public void addInsertSequenceReadOnly(String qualifiedName) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        insertInfos.put(field, new InsertInfo(true, true, false, new BigDecimal(0)));
    }

    public void addUpdate(String qualifiedName, Object value) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        updateInfos.put(field, new UpdateInfo(false, value));
    }

    public void addUpdate(String qualifiedName, Object value, boolean overrideNullOnly) {
        DatabaseField field = new DatabaseField(qualifiedName);
        field.setType(java.math.BigDecimal.class);
        updateInfos.put(field, new UpdateInfo(overrideNullOnly, value));
    }

    public void updateProject(Project project, Session session) {
        ClassDescriptor desc = project.getClassDescriptor(Class1.class);
        if (!desc.hasReturningPolicy()) {
            desc.setReturningPolicy(new ReturningPolicy());
        }
        Enumeration insertFields = insertInfos.keys();
        while (insertFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)insertFields.nextElement();
            InsertInfo info = (InsertInfo)insertInfos.get(field);
            if (info.isReadOnly) {
                desc.getReturningPolicy().addFieldForInsertReturnOnly(field);
            } else {
                desc.getReturningPolicy().addFieldForInsert(field);
            }
        }
        Enumeration updateFields = updateInfos.keys();
        while (updateFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)updateFields.nextElement();
            UpdateInfo info = (UpdateInfo)updateInfos.get(field);
            desc.getReturningPolicy().addFieldForUpdate(field);
        }
    }

    public void updateDatabase(Session session) {
        createSequence(session);
        createInsertTrigger(session);
        createUpdateTriggers(session);
    }

    protected void createSequence(Session session) {
        Enumeration insertFields = insertInfos.keys();
        while (insertFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)insertFields.nextElement();
            InsertInfo info = (InsertInfo)insertInfos.get(field);
            if (info.isSequence) {
                String seqName = field.getTableName() + "_" + field.getName() + "_SEQ";
                try {
                    session.executeNonSelectingCall(new SQLCall("DROP SEQUENCE " + seqName));
                } catch (Exception e) {
                }
                session.executeNonSelectingCall(new SQLCall("CREATE SEQUENCE " + seqName));
            }
        }
    }

    protected void createInsertTrigger(Session session) {
        StringWriter writer = new StringWriter();
        writer.write("CREATE OR REPLACE TRIGGER RETURNING_TRIGGER_INS BEFORE INSERT ON RETURNING FOR EACH ROW ");
        writer.write("BEGIN\n");

        Enumeration insertFields = insertInfos.keys();
        while (insertFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)insertFields.nextElement();
            InsertInfo info = (InsertInfo)insertInfos.get(field);
            if (info.overrideNullOnly) {
                writer.write("  IF :new." + field.getName() + " IS NULL THEN\n  ");
            }
            if (info.isSequence) {
                String seqName = field.getTableName() + "_" + field.getName() + "_SEQ";
                writer.write("  SELECT " + seqName + ".NEXTVAL INTO :new." + field.getName() + " FROM DUAL;\n");
            } else {
                if (info.value == null) {
                    writer.write("  :new." + field.getName() + " := null;\n");
                } else {
                    writer.write("  :new." + field.getName() + " := " + info.value + ";\n");
                }
            }
            if (info.overrideNullOnly) {
                writer.write("  END IF;\n");
            }
        }
        writer.write("END;");
        String str = writer.toString();
        DataModifyQuery query = new DataModifyQuery(new SQLCall(str));
        query.setShouldBindAllParameters(false);
        session.executeQuery(query);
    }

    protected void createUpdateTriggers(Session session) {
        Enumeration updateFields = updateInfos.keys();
        while (updateFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)updateFields.nextElement();
            UpdateInfo info = (UpdateInfo)updateInfos.get(field);
            createUpdateTrigger(session, field, info);
        }
    }

    protected void createUpdateTrigger(Session session, DatabaseField field, UpdateInfo info) {
        StringWriter writer = new StringWriter();
        writer.write("CREATE OR REPLACE TRIGGER RETURNING_TRIGGER_UPD_" + field.getName() + "  BEFORE UPDATE OF " + field.getName() + " ON RETURNING FOR EACH ROW ");
        writer.write("BEGIN\n");

        if (info.overrideNullOnly) {
            writer.write("  IF :new." + field.getName() + " IS NULL THEN\n  ");
        }
        if (info.value == null) {
            writer.write("  :new." + field.getName() + " := null;\n");
        } else {
            writer.write("  :new." + field.getName() + " := " + info.value + ";\n");
        }
        if (info.overrideNullOnly) {
            writer.write("  END IF;\n");
        }

        writer.write("END;");
        String str = writer.toString();
        DataModifyQuery query = new DataModifyQuery(new SQLCall(str));
        query.setShouldBindAllParameters(false);
        session.executeQuery(query);
    }

    protected Record getRowForInsert(Record rowToInsert) {
        Record row = new DatabaseRecord();
        Enumeration insertFields = insertInfos.keys();
        while (insertFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)insertFields.nextElement();
            InsertInfo info = (InsertInfo)insertInfos.get(field);
            Object valueIn = rowToInsert.get(field);
            if (!info.overrideNullOnly || valueIn == null) {
                row.put(field, info.value);
            }
        }
        return row;
    }

    protected Record getRowForUpdate(Record rowChange) {
        Record row = new DatabaseRecord();
        Enumeration updateFields = updateInfos.keys();
        while (updateFields.hasMoreElements()) {
            DatabaseField field = (DatabaseField)updateFields.nextElement();
            UpdateInfo info = (UpdateInfo)updateInfos.get(field);
            if (rowChange.containsKey(field)) {
                Object valueIn = rowChange.get(field);
                if (!info.overrideNullOnly || valueIn == null) {
                    row.put(field, info.value);
                }
            }
        }
        return row;
    }
}
