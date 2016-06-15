/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

/**
 * Formatter for numbers.
 *
 * @author Roman Grigoriadi
 */
public class JsonbNumberFormatter {

    private final String format;

    private final String locale;


    /**
     * Construct with format string and locale.
     *
     * @param format formatter format
     * @param locale locale
     */
    public JsonbNumberFormatter(String format, String locale) {
        this.format = format;
        this.locale = locale;
    }

    /**
     * Format string to be used either by formatter.
     *
     * @return format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Locale to use with formatter.
     * @return locale
     */
    public String getLocale() {
        return locale;
    }

}
