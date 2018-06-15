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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.platformsplugin.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * no new datatypes in R2
 */
public class Oracle10gR2Tests extends Oracle10gTests {

    public static Test suite() {
        return new TestSuite(Oracle10gR2Tests.class);
    }

    public Oracle10gR2Tests(String name) {
        super(name);
    }

    /**
     * the Oracle 10.2.0.3.0 server in Ottawa
     */
    protected String serverName() {
        return "tlsvrdb3.ca.oracle.com";
    }

    protected String expectedVersionNumber() {
        return "10.2.0.3.0";
    }

}
