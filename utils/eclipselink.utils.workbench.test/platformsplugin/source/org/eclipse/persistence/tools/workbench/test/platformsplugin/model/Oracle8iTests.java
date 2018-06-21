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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 */
public class Oracle8iTests extends OracleTests {

    public static Test suite() {
        return new TestSuite(Oracle8iTests.class);
    }

    public Oracle8iTests(String name) {
        super(name);
    }

    /**
     * the Oracle 8.1.7.4.1 server in Ottawa
     */
    protected String serverName() {
        return "tlsvrdb4.ca.oracle.com";
    }

    protected void appendColumnsToTableDDL(StringBuffer sb) {
        super.appendColumnsToTableDDL(sb);
    }

    protected String platformName() {
        return "Oracle8i";
    }

    protected String expectedVersionNumber() {
        return "8.1.7.4.1";
    }

}
