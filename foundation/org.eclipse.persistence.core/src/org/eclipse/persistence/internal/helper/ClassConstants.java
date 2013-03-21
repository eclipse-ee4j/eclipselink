/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.util.*;
import java.math.*;
import java.net.URL;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 */
public class ClassConstants extends CoreClassConstants {
    // Java classes
    public static final Class Hashtable_Class = Hashtable.class;
    public static final Class Enumeration_Class = Enumeration.class;
    public static final Class JavaSqlTime_Class = java.sql.Time.class;
    public static final Class JavaSqlDate_Class = java.sql.Date.class;
    public static final Class JavaSqlTimestamp_Class = java.sql.Timestamp.class;
    public static final Class List_Class = List.class;    
    public static final Class Map_Entry_Class = Map.Entry.class;    
    public static final Class Object_Class = Object.class;
    public static final Class SortedSet_Class = SortedSet.class;
    public static final Class URL_Class = URL.class;
    public static final Class Vector_class = Vector.class;
    public static final Class HashSet_class = HashSet.class;
    public static final Class Void_Class = void.class;
    public static final Class PropertyChangeEvent_Class = java.beans.PropertyChangeEvent.class;

    // Eclipselink Classes
    public static final Class Accessor_Class = org.eclipse.persistence.internal.databaseaccess.Accessor.class;
    public static final Class ConversionManager_Class = org.eclipse.persistence.internal.helper.ConversionManager.class;
    public static final Class CursoredStream_Class = org.eclipse.persistence.queries.CursoredStream.class;
    public static final Class DatabaseQuery_Class = org.eclipse.persistence.queries.DatabaseQuery.class;
    public static final Class DatabaseRow_Class = org.eclipse.persistence.internal.sessions.AbstractRecord.class;
    public static final Class OldDescriptorEvent_Class = org.eclipse.persistence.descriptors.DescriptorEvent.class;
    public static final Class DescriptorEvent_Class = org.eclipse.persistence.descriptors.DescriptorEvent.class;
    public static final Class DirectConnector_Class = org.eclipse.persistence.sessions.DirectConnector.class;
    public static final Class Expression_Class = org.eclipse.persistence.expressions.Expression.class;
    public static final Class FunctionExpression_Class = org.eclipse.persistence.internal.expressions.FunctionExpression.class;
    public static final Class ArgumentListFunctionExpression_Class = org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression.class;
    public static final Class IndirectContainer_Class = org.eclipse.persistence.indirection.IndirectContainer.class;
    public static final Class IndirectList_Class = org.eclipse.persistence.indirection.IndirectList.class;
    public static final Class IndirectSet_Class = org.eclipse.persistence.indirection.IndirectSet.class;
    public static final Class IndirectMap_Class = org.eclipse.persistence.indirection.IndirectMap.class;
    public static final Class LogicalExpression_Class = org.eclipse.persistence.internal.expressions.LogicalExpression.class;
    public static final Class PublicInterfaceDatabaseSession_Class = DatabaseSessionImpl.class;
    public static final Class PerformanceProfiler_Class = org.eclipse.persistence.tools.profiler.PerformanceProfiler.class;
    public static final Class PublicInterfaceSession_Class = AbstractSession.class;
    public static final Class QueryKey_Class = org.eclipse.persistence.mappings.querykeys.QueryKey.class;
    public static final Class RelationExpression_Class = org.eclipse.persistence.internal.expressions.RelationExpression.class;
    public static final Class Record_Class = org.eclipse.persistence.sessions.Record.class;
    public static final Class ServerSession_Class = org.eclipse.persistence.sessions.server.ServerSession.class;
    public static final Class SessionsSession_Class = org.eclipse.persistence.sessions.Session.class;
    public static final Class ScrollableCursor_Class = org.eclipse.persistence.queries.ScrollableCursor.class;
    public static final Class ValueHolderInterface_Class = org.eclipse.persistence.indirection.ValueHolderInterface.class;
    public static final Class CollectionChangeEvent_Class = org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent.class;
    public static final Class MapChangeEvent_Class = org.eclipse.persistence.descriptors.changetracking.MapChangeEvent.class;
    public static final Class ChangeTracker_Class = org.eclipse.persistence.descriptors.changetracking.ChangeTracker.class;
    public static final Class WeavedAttributeValueHolderInterface_Class = org.eclipse.persistence.indirection.WeavedAttributeValueHolderInterface.class;
    public static final Class PersistenceWeavedLazy_Class = org.eclipse.persistence.internal.weaving.PersistenceWeavedLazy.class;
    
    // Identity map classes
    public static final Class CacheIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.CacheIdentityMap.class;
    public static final Class FullIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class;
    public static final Class HardCacheWeakIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap.class;
    public static final Class NoIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.NoIdentityMap.class;
    public static final Class SoftCacheWeakIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap.class;
    public static final Class SoftIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.SoftIdentityMap.class;
    public static final Class WeakIdentityMap_Class = org.eclipse.persistence.internal.identitymaps.WeakIdentityMap.class;

    //fetch group class
    public static final Class FetchGroupTracker_class = org.eclipse.persistence.queries.FetchGroupTracker.class;

    // Moved from ConversionManager
    public static final Class ABYTE = Byte[].class;
    public static final Class AOBJECT = Object[].class;
    public static final Class ACHAR = Character[].class;
    public static final Class APBYTE = byte[].class;
    public static final Class APCHAR = char[].class;
    public static final Class BIGDECIMAL = BigDecimal.class;
    public static final Class BIGINTEGER = BigInteger.class;
    public static final Class BOOLEAN = Boolean.class;
    public static final Class BYTE = Byte.class;
    public static final Class CLASS = Class.class;
    public static final Class CHAR = Character.class;
    public static final Class CALENDAR = Calendar.class;
    public static final Class DOUBLE = Double.class;
    public static final Class FLOAT = Float.class;
    public static final Class GREGORIAN_CALENDAR = GregorianCalendar.class;
    public static final Class INTEGER = Integer.class;
    public static final Class LONG = Long.class;
    public static final Class NUMBER = Number.class;
    public static final Class OBJECT = Object.class;
    public static final Class PBOOLEAN = boolean.class;
    public static final Class PBYTE = byte.class;
    public static final Class PCHAR = char.class;
    public static final Class PDOUBLE = double.class;
    public static final Class PFLOAT = float.class;
    public static final Class PINT = int.class;
    public static final Class PLONG = long.class;
    public static final Class PSHORT = short.class;
    public static final Class SHORT = Short.class;
    public static final Class SQLDATE = java.sql.Date.class;
    public static final Class STRING = String.class;
    public static final Class TIME = java.sql.Time.class;
    public static final Class TIMESTAMP = java.sql.Timestamp.class;
    public static final Class UTILDATE = java.util.Date.class;
    public static final Class QNAME = QName.class;
    public static final Class XML_GREGORIAN_CALENDAR = XMLGregorianCalendar.class;
    public static final Class DURATION = Duration.class;    

    //LOB support types
    public static final Class BLOB = java.sql.Blob.class;
    public static final Class CLOB = java.sql.Clob.class;

    //Indication to ConversionManager not to convert classes implementing this interface
    public static final Class NOCONVERSION = NoConversion.class;

    //XML Classes
    public static final Class DOCUMENT = Document.class;
    public static final Class NODE = Node.class;
    
    public ClassConstants() {
    }
}
