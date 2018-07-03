/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Tomas Kraus - Initial API and implementation
package org.eclipse.persistence.internal.helper;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Define any useful {@link String} constants and methods that are missing from the base Java.</p>
 */
public class StringHelper {

    /** Horizontal tab. */
    public static final char TAB = '\t';

    /** Line feed. */
    public static final char LF = '\n';

    /** Form feed */
    public static final char FF = '\f';

    /** Carriage return */
    public static final char CR = '\r';

    /** Space. */
    public static final char SPACE = ' ';

    /** Left brace. */
    public static final char LEFT_BRACE = '{';

    /** Right brace. */
    public static final char RIGHT_BRACE = '}';

    /** Left bracket. */
    public static final char LEFT_BRACKET = '(';

    /** Right bracket. */
    public static final char RIGHT_BRACKET = ')';

    /** Dot. */
    public static final char DOT = '.';

    /** Vertical bar. */
    public static final char VERTICAL_BAR = '|';

    /** Vertical bar. */
    public static final char QUESTION_MARK = '?';

    /** Empty {@link String}. */
    public static final String EMPTY_STRING = "";

    /** <code>"null"</code> {@link String}. */
    public static final String NULL_STRING = "null";

    /**
     * Never return null but replace it with <code>"null"</code> {@link String}.
     * @param str String to be checked for null and eventually replaced with
     *            <code>"null"</code> {@link String}.
     * @return Provided string when not null or <code>"null"</code> {@link String}.
     */
    public static final String nonNullString(String str) {
        return str != null ? str : NULL_STRING;
    }

    /**
     * Checks if {@link String} is {@code null}, is empty ({@code ""}) or contains white spaces only.
     * @param str {@link String}  to be checked.
     * @return Value of {@code true} if @link String} is {@code null}, is empty ({@code ""}) or contains white spaces 
     *         only or {@code false} otherwise
     */
    public static final boolean isBlank(final String str) {
        return str == null || str.trim().length() == 0;
    }
}
