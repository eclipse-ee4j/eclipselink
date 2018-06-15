/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/17/2008-1.0M6 Guy Pelletier
//       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.tests.jpa;

public class TestingProperties {
    public static final String ORM_TESTING = "orm.testing";

    public static final String JPA_ORM_TESTING = "jpa";
    public static final String ECLIPSELINK_ORM_TESTING = "eclipselink";

    public static String getProperty(String property, String defaultValue) {
        String propertyValue = System.getProperty(property);

        if (propertyValue == null || propertyValue.equals("")) {
            return defaultValue;
        } else {
            return propertyValue;
        }
    }
}

