/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      Tomas Kraus - Initial implementation
package org.eclipse.persistence.logging;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * EclipseLink categories used for logging name space.
 * The EclipseLink categories for the logging name space are:<br>
 * <table summary="">
 * <tr><td>&nbsp;</td><td>ALL</td>            <td>&nbsp;</td><td>= "all"</td></tr>
 * <tr><td>&nbsp;</td><td>CACHE</td>          <td>&nbsp;</td><td>= "cache"</td></tr>
 * <tr><td>&nbsp;</td><td>CONNECTION</td>     <td>&nbsp;</td><td>= "connection"</td></tr>
 * <tr><td>&nbsp;</td><td>DDL</td>            <td>&nbsp;</td><td>= "ddl"</td></tr>
 * <tr><td>&nbsp;</td><td>DMS</td>            <td>&nbsp;</td><td>= "dms"</td></tr>
 * <tr><td>&nbsp;</td><td>EJB</td>            <td>&nbsp;</td><td>= "ejb"</td></tr>
 * <tr><td>&nbsp;</td><td>EJB_OR_METADATA</td><td>&nbsp;</td><td>= "ejb_or_metadata"</td></tr>
 * <tr><td>&nbsp;</td><td>EVENT</td>          <td>&nbsp;</td><td>= "event"</td></tr>
 * <tr><td>&nbsp;</td><td>JPA</td>            <td>&nbsp;</td><td>= "jpa"</td></tr>
 * <tr><td>&nbsp;</td><td>JPARS</td>          <td>&nbsp;</td><td>= "jpars"</td></tr>
 * <tr><td>&nbsp;</td><td>METADATA</td>       <td>&nbsp;</td><td>= "metadata"</td></tr>
 * <tr><td>&nbsp;</td><td>METAMODEL</td>      <td>&nbsp;</td><td>= "metamodel"</td></tr>
 * <tr><td>&nbsp;</td><td>MONITORING</td>     <td>&nbsp;</td><td>= "monitoring"</td></tr>
 * <tr><td>&nbsp;</td><td>MOXY</td>           <td>&nbsp;</td><td>= "moxy"</td></tr>
 * <tr><td>&nbsp;</td><td>PROPAGATION</td>    <td>&nbsp;</td><td>= "propagation"</td></tr>
 * <tr><td>&nbsp;</td><td>PROPERTIES</td>     <td>&nbsp;</td><td>= "properties"</td></tr>
 * <tr><td>&nbsp;</td><td>QUERY</td>          <td>&nbsp;</td><td>= "query"</td></tr>
 * <tr><td>&nbsp;</td><td>SEQUENCING</td>     <td>&nbsp;</td><td>= "sequencing"</td></tr>
 * <tr><td>&nbsp;</td><td>SERVER</td>         <td>&nbsp;</td><td>= "server"</td></tr>
 * <tr><td>&nbsp;</td><td>SQL</td>            <td>&nbsp;</td><td>= "sql"</td></tr>
 * <tr><td>&nbsp;</td><td>THREAD</td>         <td>&nbsp;</td><td>= "thread"</td></tr>
 * <tr><td>&nbsp;</td><td>TRANSACTION</td>    <td>&nbsp;</td><td>= "transaction"</td></tr>
 * <tr><td>&nbsp;</td><td>WEAVER</td>         <td>&nbsp;</td><td>= "weaver"</td></tr>
 * </table>
 */
public enum LogCategory {
    ALL(        (byte)0x00, "all"),
    CACHE(      (byte)0x01, SessionLog.CACHE),
    CONNECTION( (byte)0x02, SessionLog.CONNECTION),
    DDL(        (byte)0x03, SessionLog.DDL),
    DMS(        (byte)0x04, SessionLog.DMS),
    EJB(        (byte)0x05, SessionLog.EJB),
    EVENT(      (byte)0x06, SessionLog.EVENT),
    JPA(        (byte)0x07, SessionLog.JPA),
    JPARS(      (byte)0x08, SessionLog.JPARS),
    METADATA(   (byte)0x09, SessionLog.METADATA),
    METAMODEL(  (byte)0x0A, SessionLog.METAMODEL),
    MISC(       (byte)0x0B, SessionLog.MISC),
    MONITORING( (byte)0x0C, SessionLog.MONITORING),
    MOXY(       (byte)0x0D, SessionLog.MOXY),
    PROCESSOR(  (byte)0x0E, SessionLog.PROCESSOR),
    PROPAGATION((byte)0x0F, SessionLog.PROPAGATION),
    PROPERTIES( (byte)0x10, SessionLog.PROPERTIES),
    QUERY(      (byte)0x11, SessionLog.QUERY),
    SEQUENCING( (byte)0x12, SessionLog.SEQUENCING),
    SERVER(     (byte)0x13, SessionLog.SERVER),
    SQL(        (byte)0x14, SessionLog.SQL),
    TRANSACTION((byte)0x15, SessionLog.TRANSACTION),
    WEAVER(     (byte)0x16, SessionLog.WEAVER),
    THREAD(     (byte)0x17, SessionLog.THREAD);

    /** Logging categories enumeration length. */
    public static final int length = LogCategory.values().length;

    /** Logger name spaces prefix. */
    private static final String NAMESPACE_PREFIX = "eclipselink.logging.";

    /** {@link Map} for {@link String} to {@link LogCategory} case insensitive conversion. */
    private static final Map<String, LogCategory> stringValuesMap = new HashMap<String, LogCategory>(2 * length);

    /** Logger name spaces lookup table. */
    private static final String[] nameSpaces = new String[length];

    /** Logger name spaces lookup table. */
    private static final String[] levelNameSpaces = new String[length];

    static {
        // Initialize String to LogCategory case insensitive lookup Map.
        for (LogCategory category : LogCategory.values()) {
            stringValuesMap.put(category.name.toLowerCase(), category);
        }
        // Initialize logger name spaces lookup table.
        for (LogCategory category : LogCategory.values()) {
            nameSpaces[category.id] = (NAMESPACE_PREFIX + category.name).intern();
            levelNameSpaces[category.id] = (PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + category.name).intern();
        }
    }

    /**
     * Returns {@link LogCategory} object holding the value of the specified {@link String}.
     * @param name The {@link String} to be parsed.
     * @return {@link LogCategory} object holding the value represented by the string argument or {@code null} when
     *         there exists no corresponding {@link LogCategory} object to provided argument value. {@code null} value
     *         of the string argument is converted to {@code ALL}.
     */
    public static final LogCategory toValue(final String name) {
        return name != null  && name.length() > 0 ? stringValuesMap.get(name.toLowerCase()) : ALL;
    }

    /** Logging category ID. Continuous integer sequence starting from 0. */
    private final byte id;

    /** Logging category name. */
    private final String name;

    /**
     * Creates an instance of logging category.
     * @param id   Logging category ID.
     * @param name Logging category name.
     */
    private LogCategory(final byte id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get logging category ID.
     * @return Logging category ID.
     */
    public byte getId() {
        return id;
    }

    /**
     * Get logging category name.
     * @return Logging category name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get logger name space for this logging category.
     * @return Logger name space for this logging category.
     */
    public String getNameSpace() {
        return nameSpaces[id];
    }

    /**
     * Get log level property name for this logging category.
     * @return Log level property name for this logging category.
     */
    public String getLogLevelProperty() {
        return levelNameSpaces[id];
    }

}
