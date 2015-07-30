/*******************************************************************************
 * Copyright (c) 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Tomas Kraus - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.logging;

import java.util.HashMap;
import java.util.Map;

// Package visibility because it's used in SLF4J bridge only.
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
 * <tr><td>&nbsp;</td><td>PROPAGATION</td>    <td>&nbsp;</td><td>= "propagation"</td></tr>
 * <tr><td>&nbsp;</td><td>PROPERTIES</td>     <td>&nbsp;</td><td>= "properties"</td></tr>
 * <tr><td>&nbsp;</td><td>QUERY</td>          <td>&nbsp;</td><td>= "query"</td></tr>
 * <tr><td>&nbsp;</td><td>SEQUENCING</td>     <td>&nbsp;</td><td>= "sequencing"</td></tr>
 * <tr><td>&nbsp;</td><td>SERVER</td>         <td>&nbsp;</td><td>= "server"</td></tr>
 * <tr><td>&nbsp;</td><td>SQL</td>            <td>&nbsp;</td><td>= "sql"</td></tr>
 * <tr><td>&nbsp;</td><td>TRANSACTION</td>    <td>&nbsp;</td><td>= "transaction"</td></tr>
 * <tr><td>&nbsp;</td><td>WEAVER</td>         <td>&nbsp;</td><td>= "weaver"</td></tr>
 * </table>
 */
enum LogCategory {
    ALL(        (byte)0x00, "all"),
    CACHE(      (byte)0x01, "cache"),
    CONNECTION( (byte)0x02, "connection"),
    DDL(        (byte)0x03, "ddl"),
    DMS(        (byte)0x04, "dms"),
    EJB(        (byte)0x05, "ejb"),
    EVENT(      (byte)0x06, "event"),
    JPA(        (byte)0x07, "jpa"),
    JPARS(      (byte)0x08, "jpars"),
    METADATA(   (byte)0x09, "metadata"),
    METAMODEL(  (byte)0x0A, "metamodel"),
    MISC(       (byte)0x0B, "misc"),
    MONITORING( (byte)0x0C, "monitoring"),
    PROPAGATION((byte)0x0D, "propagation"),
    PROPERTIES( (byte)0x0E, "properties"),
    QUERY(      (byte)0x0F, "query"),
    SEQUENCING( (byte)0x10, "sequencing"),
    SERVER(     (byte)0x11, "server"),
    SQL(        (byte)0x12, "sql"),
    TRANSACTION((byte)0x13, "transaction"),
    WEAVER(     (byte)0x14, "weaver");

    /** Logging categories enumeration length. */
    public static final int length = LogCategory.values().length;

    /** Logger name spaces prefix. */
    private static final String NAMESPACE_PREFIX = "eclipselink.logging.";

    /** {@link Map} for {@link String} to {@link LogCategory} case insensitive conversion. */
    private static final Map<String, LogCategory> stringValuesMap = new HashMap<String, LogCategory>(2 * length);

    /** Logger name spaces lookup table. */
    private static final String[] nameSpaces = new String[length];

    static {
        // Initialize String to LogCategory case insensitive lookup Map.
        for (LogCategory category : LogCategory.values()) {
            stringValuesMap.put(category.name.toLowerCase(), category);
        }
        // Initialize logger name spaces lookup table.
        for (LogCategory category : LogCategory.values()) {
            nameSpaces[category.id] = NAMESPACE_PREFIX + category.name;
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

    // Used only in logger internal code.
    /**
     * Get logging category ID.
     * @return Logging category ID.
     */
    byte getId() {
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

}
