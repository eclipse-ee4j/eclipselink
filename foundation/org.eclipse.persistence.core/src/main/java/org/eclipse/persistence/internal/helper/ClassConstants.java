/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.MapChangeEvent;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.NoIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftIdentityMap;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.DirectConnector;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;
import org.w3c.dom.Document;

import java.beans.PropertyChangeEvent;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;

/**
 * INTERNAL:
 */
@SuppressWarnings({"rawtypes"})
public final class ClassConstants extends CoreClassConstants {
    // Java classes

    public static final Class<Hashtable> Hashtable_Class = Hashtable.class;
    public static final Class<Enumeration> Enumeration_Class = Enumeration.class;
    @Deprecated
    public static final Class<Time> JavaSqlTime_Class = TIME;
    @Deprecated
    public static final Class<Date> JavaSqlDate_Class = SQLDATE;
    @Deprecated
    public static final Class<Timestamp> JavaSqlTimestamp_Class = TIMESTAMP;
    public static final Class<Map.Entry> Map_Entry_Class = Map.Entry.class;
    @Deprecated
    public static final Class<Object> Object_Class = OBJECT;
    public static final Class<SortedSet> SortedSet_Class = SortedSet.class;
    public static final Class<Vector> Vector_class = Vector.class;
    public static final Class<HashSet> HashSet_class = HashSet.class;
    public static final Class<Void> Void_Class = void.class;
    public static final Class<PropertyChangeEvent> PropertyChangeEvent_Class = PropertyChangeEvent.class;

    // Eclipselink Classes
    public static final Class<Accessor> Accessor_Class = Accessor.class;
    public static final Class<ConversionManager> ConversionManager_Class = ConversionManager.class;
    public static final Class<CursoredStream> CursoredStream_Class = CursoredStream.class;
    public static final Class<DatabaseQuery> DatabaseQuery_Class = DatabaseQuery.class;
    public static final Class<AbstractRecord> DatabaseRow_Class = AbstractRecord.class;
    public static final Class<DescriptorEvent> OldDescriptorEvent_Class = DescriptorEvent.class;
    public static final Class<DescriptorEvent> DescriptorEvent_Class = DescriptorEvent.class;
    public static final Class<DirectConnector> DirectConnector_Class = DirectConnector.class;
    public static final Class<Expression> Expression_Class = Expression.class;
    public static final Class<FunctionExpression> FunctionExpression_Class = FunctionExpression.class;
    public static final Class<ArgumentListFunctionExpression> ArgumentListFunctionExpression_Class = ArgumentListFunctionExpression.class;
    public static final Class<IndirectContainer> IndirectContainer_Class = IndirectContainer.class;
    public static final Class<IndirectList> IndirectList_Class = (Class<IndirectList>) IndirectCollectionsFactory.IndirectList_Class;
    public static final Class<IndirectSet> IndirectSet_Class = (Class<IndirectSet>) IndirectCollectionsFactory.IndirectSet_Class;
    public static final Class<IndirectMap> IndirectMap_Class = (Class<IndirectMap>) IndirectCollectionsFactory.IndirectMap_Class;
    public static final Class<LogicalExpression> LogicalExpression_Class = LogicalExpression.class;
    public static final Class<DatabaseSessionImpl> PublicInterfaceDatabaseSession_Class = DatabaseSessionImpl.class;
    public static final Class<PerformanceProfiler> PerformanceProfiler_Class = PerformanceProfiler.class;
    public static final Class<AbstractSession> PublicInterfaceSession_Class = AbstractSession.class;
    public static final Class<QueryKey> QueryKey_Class = QueryKey.class;
    public static final Class<RelationExpression> RelationExpression_Class = RelationExpression.class;
    public static final Class<DataRecord> Record_Class = DataRecord.class;
    public static final Class<ServerSession> ServerSession_Class = ServerSession.class;
    public static final Class<Session> SessionsSession_Class = Session.class;
    public static final Class<ScrollableCursor> ScrollableCursor_Class = ScrollableCursor.class;
    public static final Class<ValueHolderInterface> ValueHolderInterface_Class = ValueHolderInterface.class;
    public static final Class<CollectionChangeEvent> CollectionChangeEvent_Class = CollectionChangeEvent.class;
    public static final Class<MapChangeEvent> MapChangeEvent_Class = MapChangeEvent.class;
    public static final Class<ChangeTracker> ChangeTracker_Class = ChangeTracker.class;
    public static final Class<WeavedAttributeValueHolderInterface> WeavedAttributeValueHolderInterface_Class = WeavedAttributeValueHolderInterface.class;
    public static final Class<PersistenceWeavedLazy> PersistenceWeavedLazy_Class = PersistenceWeavedLazy.class;

    // Identity map classes
    public static final Class<CacheIdentityMap> CacheIdentityMap_Class = CacheIdentityMap.class;
    public static final Class<FullIdentityMap> FullIdentityMap_Class = FullIdentityMap.class;
    public static final Class<HardCacheWeakIdentityMap> HardCacheWeakIdentityMap_Class = HardCacheWeakIdentityMap.class;
    public static final Class<NoIdentityMap> NoIdentityMap_Class = NoIdentityMap.class;
    public static final Class<SoftCacheWeakIdentityMap> SoftCacheWeakIdentityMap_Class = SoftCacheWeakIdentityMap.class;
    public static final Class<SoftIdentityMap> SoftIdentityMap_Class = SoftIdentityMap.class;
    public static final Class<WeakIdentityMap> WeakIdentityMap_Class = WeakIdentityMap.class;

    //fetch group class
    public static final Class<FetchGroupTracker> FetchGroupTracker_class = FetchGroupTracker.class;

    // Moved from ConversionManager
    public static final Class<?> AOBJECT = Object[].class;
    public static final Class<?> ACHAR = Character[].class;
    public static final Class<Instant> TIME_INSTANT = Instant.class;
    public static final Class<LocalDate> TIME_LDATE = LocalDate.class;
    public static final Class<LocalTime> TIME_LTIME = LocalTime.class;
    public static final Class<LocalDateTime> TIME_LDATETIME = LocalDateTime.class;
    public static final Class<OffsetDateTime> TIME_ODATETIME = OffsetDateTime.class;
    public static final Class<OffsetTime> TIME_OTIME = OffsetTime.class;
    public static final Class<Year> TIME_YEAR = Year.class;

    //LOB support types
    public static final Class<Blob> BLOB = Blob.class;
    public static final Class<Clob> CLOB = Clob.class;

    //Indication to ConversionManager not to convert classes implementing this interface
    public static final Class<NoConversion> NOCONVERSION = NoConversion.class;

    //XML Classes
    public static final Class<Document> DOCUMENT = Document.class;


    private ClassConstants() {
        //no instance please
    }
}
