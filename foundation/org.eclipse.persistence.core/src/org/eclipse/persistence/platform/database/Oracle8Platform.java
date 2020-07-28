/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     06/26/2018 - Will Dazey
//       - 532160 : Add support for non-extension OracleXPlatform classes
//     05/06/2019 - Jody Grassel
//       - 547023 : Add LOB Locator support for core Oracle platform.

package org.eclipse.persistence.platform.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.databaseaccess.SimpleAppendCallCustomParameter;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.queries.Call;

/**
 * <p><b>Purpose:</b>
 * Provides Oracle version specific behavior when 
 * org.eclipse.persistence.oracle bundle is not available.
 */
public class Oracle8Platform extends OraclePlatform {
    /**
     * Locator is required for Oracle thin driver to write LOB value exceeds the
     * limits
     */
    protected boolean usesLocatorForLOBWrite = true;

    /** The LOB value limits when the Locator is required for the writing */
    protected int lobValueLimits = 0;

    /**
     * INTERNAL:
     */
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable fieldTypeMapping = super.buildFieldTypes();

        fieldTypeMapping.put(Byte[].class, new FieldTypeDefinition("BLOB", false));
        fieldTypeMapping.put(Character[].class, new FieldTypeDefinition("CLOB", false));

        return fieldTypeMapping;
    }

    /**
     * INTERNAL: Allow for conversion from the Oracle type to the Java type.
     */
    @Override
    public void copyInto(Platform platform) {
        super.copyInto(platform);
        if (!(platform instanceof Oracle8Platform)) {
            return;
        }
        Oracle8Platform oracle8Platform = (Oracle8Platform) platform;
        oracle8Platform.setShouldUseLocatorForLOBWrite(shouldUseLocatorForLOBWrite());
        oracle8Platform.setLobValueLimits(getLobValueLimits());
    }

    /**
     * INTERNAL Used by SQLCall.appendModify(..) If the field should be passed
     * to customModifyInDatabaseCall, retun true, otherwise false. Methods
     * shouldCustomModifyInDatabaseCall and customModifyInDatabaseCall should be
     * kept in sync: shouldCustomModifyInDatabaseCall should return true if and
     * only if the field is handled by customModifyInDatabaseCall.
     */
    @Override
    public boolean shouldUseCustomModifyForCall(DatabaseField field) {
        if (shouldUseLocatorForLOBWrite()) {
            Class type = field.getType();
            if (ClassConstants.BLOB.equals(type) || ClassConstants.CLOB.equals(type)) {
                return true;
            }
        }
        return super.shouldUseCustomModifyForCall(field);
    }

    /**
     * INTERNAL: Return if the LOB value size is larger than the limit, i.e. 4k.
     */
    protected boolean lobValueExceedsLimit(Object value) {
        if (value == null) {
            return false;
        }
        int limit = getLobValueLimits();
        if (value instanceof byte[]) {// blob
            return ((byte[]) value).length >= limit;
        } else if (value instanceof String) {// clob
            return ((String) value).length() >= limit;
        } else {
            return false;
        }
    }

    /**
     * INTERNAL: This method is used to unwrap the oracle connection wrapped by
     * the application server. TopLink needs this unwrapped connection for
     * certain Oracle Specific support. (ie TIMESTAMPTZ, LOB) This is added as a
     * workaround for bug 4565190
     */
    @Override
    public Connection getConnection(AbstractSession session, Connection connection) {
        if (session.getServerPlatform() != null && (session.getLogin()).shouldUseExternalConnectionPooling()) {
            // This is added as a workaround for bug 4460996
            return session.getServerPlatform().unwrapConnection(connection);
        }
        return connection;
    }

    /**
     * INTERNAL Used by SQLCall.translate(..) Typically there is no field
     * translation (and this is default implementation). However on different
     * platforms (Oracle) there are cases such that the values for binding and
     * appending may be different (BLOB, CLOB). In these special cases the
     * method returns a wrapper object which knows whether it should be bound or
     * appended and knows how to do that.
     */
    @Override
    public Object getCustomModifyValueForCall(Call call, Object value, DatabaseField field, boolean shouldBind) {
        Class type = field.getType();
        if (ClassConstants.BLOB.equals(type) || ClassConstants.CLOB.equals(type)) {
            if (value == null) {
                return null;
            }
            Object lobValue = convertToDatabaseType(value);
            if (shouldUseLocatorForLOBWrite() & lobValueExceedsLimit(lobValue)) {
                ((DatabaseCall) call).addContext(field, lobValue);
                if (ClassConstants.BLOB.equals(type)) {
                    if (shouldBind) {
                        lobValue = new byte[1];
                    } else {
                        lobValue = new SimpleAppendCallCustomParameter("empty_blob()");
                    }
                } else {
                    if (shouldBind) {
                        lobValue = new String(" ");
                    } else {
                        lobValue = new SimpleAppendCallCustomParameter("empty_clob()");
                    }
                }
            }
            
            return lobValue;
        }
        return super.getCustomModifyValueForCall(call, value, field, shouldBind);
    }

    
    /**
     * INTERNAL: Write LOB value - only on Oracle8 and up
     */
    @SuppressWarnings("deprecation")
    @Override
    public void writeLOB(DatabaseField field, Object value, ResultSet resultSet, AbstractSession session)
            throws SQLException {
        if (isBlob(field.getType())) {
            // change for 338585 to use getName instead of getNameDelimited
            java.sql.Blob blob = (java.sql.Blob) resultSet.getObject(field.getName());
            blob.setBytes(1, (byte[]) value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_BLOB", Long.valueOf(blob.length()), field.getName());
        } else if (isClob(field.getType())) {
            // change for 338585 to use getName instead of getNameDelimited
            java.sql.Clob clob = (java.sql.Clob) resultSet.getObject(field.getName());
            clob.setString(1, (String) value);
            //impose the localization
            session.log(SessionLog.FINEST, SessionLog.SQL, "write_CLOB", Long.valueOf(clob.length()), field.getName());
        } else {
            // do nothing for now, open to BFILE or NCLOB types
        }
    }

    /**
     * INTERNAL: Used in writeLOB method only to identify a BLOB
     */
    protected boolean isBlob(Class type) {
        return ClassConstants.BLOB.equals(type);
    }

    /**
     * INTERNAL: Used in writeLOB method only to identify a CLOB
     */
    protected boolean isClob(Class type) {
        return ClassConstants.CLOB.equals(type);
    }

    /**
     * INTERNAL: Indicates whether app. server should unwrap connection to use
     * lob locator.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return true;
    }

    /**
     * PUBLIC: Set if the locator is required for the LOB write. The default is
     * true. For Oracle thin driver, the locator is recommended for large size (
     * &gt;4k for Oracle8, &gt;5.9K for Oracle9) BLOB/CLOB value write.
     */
    public void setShouldUseLocatorForLOBWrite(boolean usesLocatorForLOBWrite) {
        this.usesLocatorForLOBWrite = usesLocatorForLOBWrite;
    }

    /**
     * PUBLIC: Return if the locator is required for the LOB write. The default
     * is true. For Oracle thin driver, the locator is recommended for large
     * size ( &gt;4k for Oracle8, &gt;5.9K for Oracle9) BLOB/CLOB value write.
     */
    public boolean shouldUseLocatorForLOBWrite() {
        return usesLocatorForLOBWrite;
    }

    /**
     * PUBLIC: Return the BLOB/CLOB value limits on thin driver. The default
     * value is 0. If usesLocatorForLOBWrite is true, locator will be used in
     * case the lob's size is larger than lobValueLimit.
     */
    public int getLobValueLimits() {
        return lobValueLimits;
    }

    /**
     * PUBLIC: Set the BLOB/CLOB value limits on thin driver. The default value
     * is 0. If usesLocatorForLOBWrite is true, locator will be used in case the
     * lob's size is larger than lobValueLimit.
     */
    public void setLobValueLimits(int lobValueLimits) {
        this.lobValueLimits = lobValueLimits;
    }
}
