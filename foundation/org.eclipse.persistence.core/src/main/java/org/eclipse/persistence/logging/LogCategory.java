/*
 * Copyright (c) 2016, 2024 Oracle and/or its affiliates. All rights reserved.
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

/**
 * EclipseLink categories used for logging name spaces.
 * <p>
 * The EclipseLink categories are:<br>
 * <table>
 * <caption>Logging categories</caption>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#CACHE}</td>       <td>&nbsp;</td><td>= {@value SessionLog#CACHE}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#CONNECTION}</td>  <td>&nbsp;</td><td>= {@value SessionLog#CONNECTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#DDL}</td>         <td>&nbsp;</td><td>= {@value SessionLog#DDL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#DMS}</td>         <td>&nbsp;</td><td>= {@value SessionLog#DMS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#EJB}</td>         <td>&nbsp;</td><td>= {@value SessionLog#EJB}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#EVENT}</td>       <td>&nbsp;</td><td>= {@value SessionLog#EVENT}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#JPA}</td>         <td>&nbsp;</td><td>= {@value SessionLog#JPA}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#JPARS}</td>       <td>&nbsp;</td><td>= {@value SessionLog#JPARS}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#METADATA}</td>    <td>&nbsp;</td><td>= {@value SessionLog#METADATA}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#METAMODEL}</td>   <td>&nbsp;</td><td>= {@value SessionLog#METAMODEL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#MONITORING}</td>  <td>&nbsp;</td><td>= {@value SessionLog#MONITORING}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#MOXY}</td>        <td>&nbsp;</td><td>= {@value SessionLog#MOXY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#PROPAGATION}</td> <td>&nbsp;</td><td>= {@value SessionLog#PROPAGATION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#PROPERTIES}</td>  <td>&nbsp;</td><td>= {@value SessionLog#PROPERTIES}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#QUERY}</td>       <td>&nbsp;</td><td>= {@value SessionLog#QUERY}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#SEQUENCING}</td>  <td>&nbsp;</td><td>= {@value SessionLog#SEQUENCING}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#SERVER}</td>      <td>&nbsp;</td><td>= {@value SessionLog#SERVER}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#SQL}</td>         <td>&nbsp;</td><td>= {@value SessionLog#SQL}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#THREAD}</td>      <td>&nbsp;</td><td>= {@value SessionLog#THREAD}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#TRANSACTION}</td> <td>&nbsp;</td><td>= {@value SessionLog#TRANSACTION}</td></tr>
 * <tr><td>&nbsp;</td><td>{@linkplain SessionLog#WEAVER}</td>      <td>&nbsp;</td><td>= {@value SessionLog#WEAVER}</td></tr>
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

    /** {@linkplain Map} for {@linkplain String} to {@linkplain LogCategory} case insensitive conversion. */
    private static final Map<String, LogCategory> stringValuesMap = new HashMap<>(2 * length);

    /** Logger name spaces lookup table. */
    private static final String[] nameSpaces = new String[length];

    /** Logger name spaces lookup table. */
    private static final String[] levelNameSpaces = new String[length];

    static {
        // Initialize String to LogCategory case-insensitive lookup Map.
        for (LogCategory category : LogCategory.values()) {
            stringValuesMap.put(category.name.toLowerCase(), category);
        }
        // Initialize logger name spaces lookup table.
        for (LogCategory category : LogCategory.values()) {
            nameSpaces[category.id] = (NAMESPACE_PREFIX + category.name).intern();
            levelNameSpaces[category.id] = ("eclipselink.logging.level." + category.name).intern();
        }
    }

    /**
     * Returns {@linkplain LogCategory} object holding the value of the specified {@linkplain String}.
     * @param name The {@linkplain String} to be parsed.
     * @return {@linkplain LogCategory} object holding the value represented by the string argument or {@code null} when
     *         there exists no corresponding {@linkplain LogCategory} object to provided argument value. {@code null} value
     *         of the string argument is converted to {@code ALL}.
     */
    public static final LogCategory toValue(final String name) {
        return name != null  && !name.isEmpty() ? stringValuesMap.get(name.toLowerCase()) : ALL;
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
    LogCategory(final byte id, final String name) {
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
