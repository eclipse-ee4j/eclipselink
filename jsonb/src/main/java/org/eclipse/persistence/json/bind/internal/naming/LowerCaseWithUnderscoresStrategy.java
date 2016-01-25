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

package org.eclipse.persistence.json.bind.internal.naming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Grigoriadi
 */
public class LowerCaseWithUnderscoresStrategy implements PropertyNamingStrategy {

    private static final String CAMEL_CASE_REGEX = "([^_])([A-Z])";
    private static final String LOWER_CASE_UNDERSCORE = "([a-z])_([a-z])";

    private static final Pattern LOWER_CASE_UNDERSCORE_PATTERN = Pattern.compile(LOWER_CASE_UNDERSCORE);

    @Override
    public String toJsonPropertyName(String modelPropertyName) {
        return modelPropertyName.replaceAll(CAMEL_CASE_REGEX, "$1_$2").toLowerCase();
    }

    @Override
    public String toModelPropertyName(String jsonPropertyName) {
        final StringBuffer result = new StringBuffer();
        final Matcher m = LOWER_CASE_UNDERSCORE_PATTERN.matcher(jsonPropertyName);
        while (m.find()) {
            m.appendReplacement(result, m.group(1).concat(m.group(2).toUpperCase()));
        }
        m.appendTail(result);
        return result.toString();
    }
}
