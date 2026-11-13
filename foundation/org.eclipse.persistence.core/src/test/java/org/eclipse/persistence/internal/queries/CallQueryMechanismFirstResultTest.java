/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.queries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.List;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.factories.EclipseLinkObjectPersistenceRuntimeXMLProject;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CallQueryMechanismFirstResultTest {

    @Test
    void untouchedOffsetPreservesDirectCallOffset() {
        assertDirectCallOffset(new DataReadQuery());
    }

    @Test
    void maximumDoesNotTakeOwnershipOfDirectCallOffset() {
        DataReadQuery query = new DataReadQuery();
        query.setMaxRows(7);
        assertTrue(query.shouldCloneCall());
        assertDirectCallOffset(query);
    }

    @Test
    void timeoutDoesNotTakeOwnershipOfDirectCallOffset() {
        DataReadQuery query = new DataReadQuery();
        query.setQueryTimeout(3);
        assertTrue(query.shouldCloneCall());
        assertDirectCallOffset(query);
    }

    @Test
    void explicitZeroOverridesDirectOffsetWithoutChangingCursorSettings() {
        for (boolean scrollable : List.of(false, true)) {
            DataReadQuery query = new DataReadQuery();
            query.setFirstResult(0);
            SQLCall call = offsetCall();
            call.setIsResultSetScrollable(scrollable);
            call.setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
            call.setResultSetConcurrency(ResultSet.CONCUR_UPDATABLE);

            new CallQueryMechanism(query, call).configureDatabaseCall(call);

            assertEquals(0, call.getFirstResult());
            assertEquals(scrollable, call.isResultSetScrollable());
            assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, call.getResultSetType());
            assertEquals(ResultSet.CONCUR_UPDATABLE, call.getResultSetConcurrency());
        }
    }

    @Test
    void positiveOffsetRetainsCursorSetupAfterReset() {
        DataReadQuery query = new DataReadQuery();
        SQLCall call = offsetCall();
        CallQueryMechanism mechanism = new CallQueryMechanism(query, call);
        query.setFirstResult(2);
        mechanism.configureDatabaseCall(call);
        assertEquals(2, call.getFirstResult());
        assertScrollableReadOnly(call);

        query.setFirstResult(0);
        mechanism.configureDatabaseCall(call);
        assertEquals(0, call.getFirstResult());
        assertScrollableReadOnly(call);
    }

    @Test
    void firstExplicitZeroRepreparesAnImplicitZeroQuery() {
        SQLCall original = offsetCall();
        DataReadQuery query = new DataReadQuery(original);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        query.prepareCall(session, new DatabaseRecord());
        assertTrue(query.isPrepared());
        assertEquals(4, query.getCall().getFirstResult());

        query.setFirstResult(0);
        assertFalse(query.isPrepared());
        query.prepareCall(session, new DatabaseRecord());

        assertTrue(query.isPrepared());
        assertEquals(0, query.getCall().getFirstResult());
        assertNotSame(original, query.getCall());
        assertEquals(4, original.getFirstResult());
    }

    @Test
    void repeatedExplicitZeroDoesNotReprepare() {
        DataReadQuery query = new DataReadQuery();
        query.setFirstResult(0);
        query.setIsPrepared(true);
        query.setFirstResult(0);
        assertTrue(query.isPrepared());
        query.setFirstResult(1);
        assertFalse(query.isPrepared());
    }

    @Test
    void sqlHandledOffsetDoesNotRequireRepreparation() {
        SQLCall call = offsetCall();
        call.setIgnoreFirstRowSetting(true);
        DataReadQuery query = new DataReadQuery(call);
        query.setIsPrepared(true);
        query.setFirstResult(0);
        assertTrue(query.isPrepared());
        assertTrue(query.isFirstResultSet());
        query.setFirstResult(2);
        assertTrue(query.isPrepared());
    }

    @Test
    void firstAndMaximumIgnoreFlagsRemainIndependent() {
        for (boolean ignoreFirst : List.of(false, true)) {
            for (boolean ignoreMaximum : List.of(false, true)) {
                DataReadQuery query = new DataReadQuery();
                query.setFirstResult(0);
                query.setMaxRows(7);
                query.setFetchSize(11);
                SQLCall call = offsetCall();
                call.setMaxRows(9);
                call.setIgnoreFirstRowSetting(ignoreFirst);
                call.setIgnoreMaxResultsSetting(ignoreMaximum);

                new CallQueryMechanism(query, call).configureDatabaseCall(call);

                assertEquals(ignoreFirst ? 4 : 0, call.getFirstResult());
                assertEquals(ignoreMaximum ? 9 : 7, call.getMaxRows());
                assertEquals(11, call.getResultSetFetchSize());
            }
        }
    }

    @Test
    void protectedNonzeroOffsetStillTransfersWithoutSetterIntent() {
        FieldOffsetQuery query = new FieldOffsetQuery();
        query.firstResultDirectly(2);
        assertFalse(query.isFirstResultSet());
        SQLCall call = offsetCall();
        new CallQueryMechanism(query, call).configureDatabaseCall(call);
        assertEquals(2, call.getFirstResult());
        assertScrollableReadOnly(call);
    }

    @Test
    void clonePreservesOffsetOwnershipWithoutChangingSourceCall() {
        for (boolean explicit : List.of(false, true)) {
            DataReadQuery source = new DataReadQuery(offsetCall());
            if (explicit) {
                source.setFirstResult(0);
            }
            DataReadQuery copy = (DataReadQuery) source.clone();
            assertEquals(explicit, copy.isFirstResultSet());
            copy.prepareCall(new DatabaseSessionImpl(new DatabaseLogin()), new DatabaseRecord());
            assertEquals(explicit ? 0 : 4, copy.getCall().getFirstResult());
            assertEquals(4, source.getCall().getFirstResult());
            copy.setFirstResult(2);
            assertEquals(0, source.getFirstResult());
        }
    }

    @Test
    void copyFromQueryPreservesAndClearsOffsetOwnership() {
        for (boolean explicit : List.of(false, true)) {
            DataReadQuery source = new DataReadQuery(offsetCall());
            if (explicit) {
                source.setFirstResult(0);
            }
            DataReadQuery copy = new DataReadQuery();
            copy.setFirstResult(2);
            copy.copyFromQuery(source);

            assertEquals(explicit, copy.isFirstResultSet());
            assertEquals(0, copy.getFirstResult());
            copy.prepareCall(new DatabaseSessionImpl(new DatabaseLogin()), new DatabaseRecord());
            assertEquals(explicit ? 0 : 4, copy.getCall().getFirstResult());
            assertEquals(4, source.getCall().getFirstResult());
        }
    }

    @Test
    void preparationReuseRetainsReceivingQueryOffsetOwnership() {
        for (boolean explicit : List.of(false, true)) {
            DataReadQuery source = new DataReadQuery(offsetCall());
            source.setFirstResult(2);
            DataReadQuery receiver = new DataReadQuery();
            if (explicit) {
                receiver.setFirstResult(0);
            }
            receiver.prepareFromQuery(source);

            assertEquals(explicit, receiver.isFirstResultSet());
            assertEquals(0, receiver.getFirstResult());
            receiver.prepareCall(new DatabaseSessionImpl(new DatabaseLogin()), new DatabaseRecord());
            assertEquals(explicit ? 0 : 4, receiver.getCall().getFirstResult());
            assertEquals(2, source.getFirstResult());
        }
    }

    @Test
    void multipleCallsPreserveUntouchedOffsetsAndIsolateExplicitZero() {
        DataReadQuery query = new DataReadQuery();
        query.setSession(new DatabaseSessionImpl(new DatabaseLogin()));
        SQLCall first = offsetCall();
        SQLCall second = offsetCall();
        second.setFirstResult(6);
        query.setCall(first);
        query.addCall(second);
        CallQueryMechanism mechanism = (CallQueryMechanism) query.getQueryMechanism();
        mechanism.prepareCall();
        assertEquals(4, ((DatabaseCall) mechanism.getCalls().get(0)).getFirstResult());
        assertEquals(6, ((DatabaseCall) mechanism.getCalls().get(1)).getFirstResult());

        query.setFirstResult(0);
        mechanism.prepareCall();
        assertEquals(0, ((DatabaseCall) mechanism.getCalls().get(0)).getFirstResult());
        assertEquals(0, ((DatabaseCall) mechanism.getCalls().get(1)).getFirstResult());
        assertNotSame(first, mechanism.getCalls().get(0));
        assertNotSame(second, mechanism.getCalls().get(1));
        assertEquals(4, first.getFirstResult());
        assertEquals(6, second.getFirstResult());
    }

    @Test
    void serializationRetainsOwnershipAndBaselineReadQueryIdentity() throws Exception {
        assertEquals(-1076104486010025988L, ObjectStreamClass.lookup(ReadQuery.class).getSerialVersionUID());
        for (boolean explicit : List.of(false, true)) {
            DataReadQuery source = new DataReadQuery(offsetCall());
            if (explicit) {
                source.setFirstResult(0);
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
                output.writeObject(source);
            }
            DataReadQuery restored;
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                restored = (DataReadQuery) input.readObject();
            }
            assertEquals(explicit, restored.isFirstResultSet());
            new CallQueryMechanism(restored).configureDatabaseCall(restored.getCall());
            assertEquals(explicit ? 0 : 4, restored.getCall().getFirstResult());
        }
    }

    @Test
    void xmlOmittedFirstResultPreservesDirectCallOffset() {
        assertConfiguredXmlOffset(readXmlQuery(false), 1);
    }

    @Test
    void xmlExplicitZeroOverridesDirectCallOffset() {
        assertConfiguredXmlOffset(readXmlQuery(true), 0);
    }

    @Test
    void freshReadAllQueryPreservesDirectCallOffset() {
        ReadAllQuery query = new ReadAllQuery(XmlOffsetRecord.class);
        query.setSQLString("SELECT ID FROM OFFSET_RECORD ORDER BY ID");
        assertEquals(0, query.getFirstResult());
        assertConfiguredXmlOffset(query, 1);
    }

    @Test
    void xmlRoundTripsPreserveOffsetOwnership() {
        for (boolean readAll : List.of(false, true)) {
            for (int first : List.of(-1, 0, 2)) {
                ObjectLevelReadQuery query = readAll ? new ReadAllQuery(XmlOffsetRecord.class)
                        : new ReadObjectQuery(XmlOffsetRecord.class);
                query.setSQLString("SELECT ID FROM OFFSET_RECORD ORDER BY ID");
                if (first >= 0) {
                    query.setFirstResult(first);
                }
                for (int round = 0; round < 2; round++) {
                    String xml = writeXmlQuery(query);
                    if (first < 0) {
                        assertFalse(xml.contains("first-result"));
                    } else {
                        assertTrue(xml.contains("first-result>" + first + "</"));
                    }
                    query = readXmlQuery(xml);
                    assertEquals(first < 0 ? 0 : first, query.getFirstResult());
                    assertEquals(first >= 0, query.isFirstResultSet());
                    assertConfiguredXmlOffset(query, first < 0 ? 1 : first);
                }
            }
        }
    }

    @Test
    void xmlAccessorPreservesMarkerlessNonzeroOffsets() {
        XMLDirectMapping mapping = firstResultXmlMapping();
        FieldOffsetQuery query = new FieldOffsetQuery();
        assertNull(mapping.getAttributeAccessor().getAttributeValueFromObject(query));
        query.firstResultDirectly(2);
        assertFalse(query.isFirstResultSet());
        assertEquals(2, mapping.getAttributeAccessor().getAttributeValueFromObject(query));
        query.setFirstResult(0);
        assertEquals(0, mapping.getAttributeAccessor().getAttributeValueFromObject(query));
    }

    @Test
    void xmlMappingPreservesEmptyElementDefaultWithoutAssigningAbsentElements() {
        XMLDirectMapping mapping = firstResultXmlMapping();
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        ReadAllQuery query = new ReadAllQuery(XmlOffsetRecord.class);
        assertTrue(mapping.getNullPolicy().isNullRepresentedByEmptyNode());
        mapping.setAttributeValueInObject(query, mapping.getAttributeValue(XMLRecord.noEntry, session, null));
        assertFalse(query.isFirstResultSet());
        Object empty = mapping.getAttributeValue("", session, null);
        assertEquals(0, empty);
        mapping.setAttributeValueInObject(query, empty);
        assertEquals(0, query.getFirstResult());
        assertTrue(query.isFirstResultSet());
    }

    private XMLDirectMapping firstResultXmlMapping() {
        return (XMLDirectMapping) new EclipseLinkObjectPersistenceRuntimeXMLProject()
                .getDescriptor(ReadQuery.class).getMappingForAttributeName("firstResult");
    }

    private String writeXmlQuery(ObjectLevelReadQuery query) {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(XmlOffsetRecord.class);
        descriptor.setTableName("OFFSET_RECORD");
        descriptor.addPrimaryKeyFieldName("ID");
        descriptor.addDirectMapping("id", "ID");
        descriptor.getQueryManager().addQuery("xmlOffset", query);
        Project project = new Project();
        project.addDescriptor(descriptor);
        StringWriter writer = new StringWriter();
        XMLProjectWriter.write(project, writer);
        return writer.toString();
    }

    private ReadAllQuery readXmlQuery(boolean explicitZero) {
        ReadAllQuery query = new ReadAllQuery(XmlOffsetRecord.class);
        query.setSQLString("SELECT ID FROM OFFSET_RECORD ORDER BY ID");
        // Write a nondefault value so the fixture can distinguish an omitted element from an explicit zero.
        query.setFirstResult(2);
        String xml = writeXmlQuery(query);
        assertTrue(xml.contains("first-result>2</"));
        xml = explicitZero ? xml.replace("first-result>2</", "first-result>0</")
                : xml.replaceAll("<[^<>]*first-result>2</[^<>]*first-result>", "");
        assertEquals(explicitZero, xml.contains("first-result"));
        ReadAllQuery restored = (ReadAllQuery) readXmlQuery(xml);
        assertEquals(0, restored.getFirstResult());
        return restored;
    }

    private ObjectLevelReadQuery readXmlQuery(String xml) {
        Project restored = XMLProjectReader.read(new StringReader(xml), getClass().getClassLoader());
        return (ObjectLevelReadQuery) restored.getDescriptor(XmlOffsetRecord.class).getQueryManager().getQuery("xmlOffset");
    }

    private void assertConfiguredXmlOffset(ReadQuery query, int expected) {
        DatabaseCall call = query.getCall();
        call.setFirstResult(1);
        call.setIsResultSetScrollable(true);
        call.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
        call.setResultSetConcurrency(ResultSet.CONCUR_READ_ONLY);
        new CallQueryMechanism(query, call).configureDatabaseCall(call);
        assertEquals(expected, call.getFirstResult(), "loaded query offset=" + query.getFirstResult());
    }

    private void assertDirectCallOffset(DataReadQuery query) {
        SQLCall call = offsetCall();
        new CallQueryMechanism(query, call).configureDatabaseCall(call);
        assertEquals(0, query.getFirstResult());
        assertEquals(4, call.getFirstResult());
    }

    private SQLCall offsetCall() {
        SQLCall call = new SQLCall("SELECT ID FROM OFFSET_RECORD");
        call.setFirstResult(4);
        return call;
    }

    private void assertScrollableReadOnly(DatabaseCall call) {
        assertTrue(call.isResultSetScrollable());
        assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, call.getResultSetType());
        assertEquals(ResultSet.CONCUR_READ_ONLY, call.getResultSetConcurrency());
    }

    private static class FieldOffsetQuery extends DataReadQuery {
        void firstResultDirectly(int offset) {
            firstResult = offset;
        }
    }

    public static class XmlOffsetRecord {
        public int id;
    }
}
