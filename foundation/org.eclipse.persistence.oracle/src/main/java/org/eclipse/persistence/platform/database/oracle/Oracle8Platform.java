/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia - added method for releasing temporary LOBs after conversion
package org.eclipse.persistence.platform.database.oracle;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.databaseaccess.SimpleAppendCallCustomParameter;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.Call;

import oracle.jdbc.OracleBlob;
import oracle.jdbc.OracleClob;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;


/**
 * <p><b>Purpose:</b>
 * Supports certain new Oracle 8 data types, and usage of certain Oracle JDBC specific APIs.
 * <p> Supports Oracle thin JDBC driver LOB &gt;4k binding workaround.
 * <p> Creates BLOB and CLOB type for byte[] and char[] for table creation.
 * <p> Supports object-relational data-type creation.
 */
public class Oracle8Platform extends OraclePlatform {

    /** Locator is required for Oracle thin driver to write LOB value exceeds the limits */
    protected boolean usesLocatorForLOBWrite = true;

    /** The LOB value limits when the Locator is required for the writing */
    protected int lobValueLimits = 0;

    /**
     * Default constructor.
     */
    public Oracle8Platform() {
        super();
    }

    /**
     * INTERNAL:
     */
    @Override
    protected Map<Class<?>, FieldDefinition.DatabaseType> buildDatabaseTypes() {
        Map<Class<?>, FieldDefinition.DatabaseType> fieldTypeMapping = super.buildDatabaseTypes();

        fieldTypeMapping.put(Byte[].class, new FieldDefinition.DatabaseType("BLOB", false));
        fieldTypeMapping.put(Character[].class, new FieldDefinition.DatabaseType("CLOB", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL:
     * Allow for conversion from the Oralce type to the Java type.
     */
    @Override
    public void copyInto(Platform platform) {
        super.copyInto(platform);
        if (!(platform instanceof Oracle8Platform oracle8Platform)) {
            return;
        }
        oracle8Platform.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWrite());
        oracle8Platform.setLobValueLimits(getLobValueLimits());
    }

    /**
     * INTERNAL:
     * Return if the LOB value size is larger than the limit, i.e. 4k.
     */
    protected boolean lobValueExceedsLimit(Object value) {
        if (value == null) {
            return false;
        }
        int limit = getLobValueLimits();
        if (value instanceof byte[]) {//blob
            return ((byte[])value).length >= limit;
        } else if (value instanceof String) {//clob
            return ((String)value).length() >= limit;
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * This method is used to unwrap the oracle connection wrapped by
     * the application server.  TopLink needs this unwrapped connection for certain
     * Oracle Specific support. (ie TIMESTAMPTZ, LOB)
     * This is added as a workaround for bug 4565190
     */
    @Override
    public Connection getConnection(AbstractSession session, Connection connection) {
        if (session.getServerPlatform() != null && (session.getLogin()).shouldUseExternalConnectionPooling()){
        // This is added as a workaround for bug 4460996
            return session.getServerPlatform().unwrapConnection(connection);
        }
        return connection;
    }

    /**
     * INTERNAL
     * Used by SQLCall.translate(..)
     * Typically there is no field translation (and this is default implementation).
     * However on different platforms (Oracle) there are cases such that the values for
     * binding and appending may be different (BLOB, CLOB).
     * In these special cases the method returns a wrapper object
     * which knows whether it should be bound or appended and knows how to do that.
     */
    @Override
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind) {
        Class<?> type = field.getType();
        if (ClassConstants.BLOB.equals(type) || ClassConstants.CLOB.equals(type)) {
            if(value == null) {
                return null;
            }
            value = convertToDatabaseType(value);
            if (shouldUseLocatorForLOBWrite()) {
                if (lobValueExceedsLimit(value)) {
                    ((DatabaseCall)call).addContext(field, value);
                    if (ClassConstants.BLOB.equals(type)) {
                        if (shouldBind) {
                            value = new byte[1];
                        } else {
                            value = new SimpleAppendCallCustomParameter("empty_blob()");
                        }
                    } else {
                        if (shouldBind) {
                            value = new String(" ");
                        } else {
                            value = new SimpleAppendCallCustomParameter("empty_clob()");
                        }
                    }
                }
            }
            return value;
        }
        return super.getCustomModifyValueForCall(call, value, field, shouldBind);
    }

    /**
     * INTERNAL
     * Used by SQLCall.appendModify(..)
     * If the field should be passed to customModifyInDatabaseCall, retun true,
     * otherwise false.
     * Methods shouldCustomModifyInDatabaseCall and customModifyInDatabaseCall should be
     * kept in sync: shouldCustomModifyInDatabaseCall should return true if and only if the field
     * is handled by customModifyInDatabaseCall.
     */
    @Override
    public boolean shouldUseCustomModifyForCall(DatabaseField field) {
        if (shouldUseLocatorForLOBWrite()) {
            Class<?> type = field.getType();
            if (ClassConstants.BLOB.equals(type) || ClassConstants.CLOB.equals(type)) {
                return true;
            }
        }
        return super.shouldUseCustomModifyForCall(field);
    }

    /**
     * INTERNAL:
     * Write LOB value - works on Oracle 10 and newer
     */
    @Override
    public void writeLOB(DatabaseField field, Object value, ResultSet resultSet, AbstractSession session) throws SQLException {
        if (isBlob(field.getType())) {
            //change for 338585 to use getName instead of getNameDelimited
            Blob blob = (Blob) resultSet.getObject(field.getName());
            blob.setBytes(1, (byte[]) value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_BLOB", blob.length(), field.getName());
        } else if (isClob(field.getType())) {
            //change for 338585 to use getName instead of getNameDelimited
            Clob clob = (Clob) resultSet.getObject(field.getName());
            clob.setString(1, (String) value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_CLOB", clob.length(), field.getName());
        } else {
            //do nothing for now, open to BFILE or NCLOB types
        }
    }

    /**
     * INTERNAL:
     * Used in writeLOB method only to identify a BLOB
     */
    protected boolean isBlob(Class<?> type) {
        return ClassConstants.BLOB.equals(type);
    }

    /**
     * INTERNAL:
     * Used in writeLOB method only to identify a CLOB
     */
    protected boolean isClob(Class<?> type) {
        return ClassConstants.CLOB.equals(type);
    }

    /**
     * INTERNAL:
     * Indicates whether app. server should unwrap connection
     * to use lob locator.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return true;
    }

    /**
     * PUBLIC:
     * Set if the locator is required for the LOB write. The default is true.
     * For Oracle thin driver, the locator is recommended for large size
     * ( &gt;4k for Oracle8, &gt;5.9K for Oracle9) BLOB/CLOB value write.
     */
    public void setShouldUseLocatorForLOBWrite(boolean usesLocatorForLOBWrite) {
        this.usesLocatorForLOBWrite = usesLocatorForLOBWrite;
    }

    /**
     * PUBLIC:
     * Return if the locator is required for the LOB write. The default is true.
     * For Oracle thin driver, the locator is recommended for large size
     * ( &gt;4k for Oracle8, &gt;5.9K for Oracle9) BLOB/CLOB value write.
     */
    public boolean shouldUseLocatorForLOBWrite() {
        return usesLocatorForLOBWrite;
    }

    /**
     * PUBLIC:
     * Return the BLOB/CLOB value limits on thin driver. The default value is 0.
     * If usesLocatorForLOBWrite is true, locator will be used in case the
     * lob's size is larger than lobValueLimit.
     */
    public int getLobValueLimits() {
        return lobValueLimits;
    }

    /**
     * PUBLIC:
     * Set the BLOB/CLOB value limits on thin driver. The default value is 0.
     * If usesLocatorForLOBWrite is true, locator will be used in case the
     * lob's size is larger than lobValueLimit.
     */
    public void setLobValueLimits(int lobValueLimits) {
        this.lobValueLimits = lobValueLimits;
    }

    /**
     * INTERNAL:
     * Platforms that support java.sql.Array may override this method.
     * @return Array
     */
    @Override
    public Array createArray(String elementDataTypeName, Object[] elements, Connection connection) throws SQLException {
        return new oracle.sql.ARRAY(new oracle.sql.ArrayDescriptor(elementDataTypeName, connection), connection, elements);
    }

    /**
     * INTERNAL:
     * Platforms that support java.sql.Struct may override this method.
     * @return Struct
     */
    @Override
    public Struct createStruct(String structTypeName, Object[] attributes, Connection connection) throws SQLException {
        return new oracle.sql.STRUCT(new oracle.sql.StructDescriptor(structTypeName, connection), connection, attributes);
    }

    /**
     * INTERNAL:
     * Overrides DatabasePlatform method.
     * @return String
     */
    @Override
    public Object getRefValue(Ref ref,Connection connection) throws SQLException {
        ((oracle.sql.REF)ref).setPhysicalConnectionOf(connection);
        return ((oracle.sql.REF)ref).getValue();
    }

    /**
     * INTERNAL:
     * Used by Oracle platforms during reading of ResultSet to free temporary LOBs.
     */
    @Override
    public void freeTemporaryObject(Object value) throws SQLException {
        if (value instanceof OracleClob && ((OracleClob) value).isTemporary()) {
            ((OracleClob) value).free();
        } else if (value instanceof OracleBlob && ((OracleBlob) value).isTemporary()) {
            ((OracleBlob) value).free();
        }
    }
}
