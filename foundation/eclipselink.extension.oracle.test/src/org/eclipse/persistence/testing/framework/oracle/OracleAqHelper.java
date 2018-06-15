/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/09/2016-2.7 Tomas Kraus
//       - 492077: Use Oracle DB AQ credentials from test properties
package org.eclipse.persistence.testing.framework.oracle;

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;

/**
 * Oracle AQ database setup helper.
 */
public class OracleAqHelper {

    /** Database properties read from test properties {@code db.*}. */
    private static final Map<String, String> DB_PROPERTIES = JUnitTestCaseHelper.getDatabaseProperties();

    /** Default AQ database user if not set as {@code db.user}. */
    private static final String DEFAULT_DB_USER = "aquser";

    /** Default AQ database user if not set as {@code db.pwd}. */
    private static final String DEFAULT_DB_PWD = "aquser";

    /**
     * Get AQ database user.
     * @return Value from {@code db.user} test property or {@code DEFAULT_DB_USER} if not set.
     */
    public static String getAqUser() {
        final String aqUser = DB_PROPERTIES.get(PersistenceUnitProperties.JDBC_USER);
        return aqUser != null ? aqUser : DEFAULT_DB_USER;
    }

    /**
     * Get AQ database password.
     * @return Value from {@code db.pwd} test property or {@code DEFAULT_DB_PWD} if not set.
     */
    public static String getAqPassword() {
        final String aqPwd = DB_PROPERTIES.get(PersistenceUnitProperties.JDBC_PASSWORD);
        return aqPwd != null ? aqPwd : DEFAULT_DB_PWD;
    }

}
