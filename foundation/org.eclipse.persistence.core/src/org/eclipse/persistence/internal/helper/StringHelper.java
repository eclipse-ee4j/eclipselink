/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Tomas Kraus - Initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Define any useful {@link String} constants and methods that are missing from the base Java.</p>
 */
public class StringHelper {

    /** Left brace. */
    public static final char LEFT_BRACE = '{';

    /** Right brace. */
    public static final char RIGHT_BRACE = '}';

    /** Left bracket. */
    public static final char LEFT_BRACKET = '(';

    /** Right bracket. */
    public static final char RIGHT_BRACKET = ')';

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

}
