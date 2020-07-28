/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * A custom converter used for testing the TopLink annotation @Converter.
 */
public class CustomSexConverter implements Converter {
    public CustomSexConverter() {}

    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue != null) {
            String sex = (String) dataValue;

            if (sex.equals("M")) {
                return "Male";
            } else {
                return "Female";
            }
        }

        return null;
    }

    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue != null) {
            String sex = (String) objectValue;

            if (sex.equals("Made of testosterone")) {
                return "M";
            } else if (sex.equals("Made of estrogen")) {
                return "F";
            } else if (sex.equals("Male")) {
                return "M";
            } else {
                return "F";
            }
        }

        return null;
    }

    public void initialize(DatabaseMapping mapping, Session session) {}

    public boolean isMutable() {
        return false;
    }
}
