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
//     James - initial impl
package org.eclipse.persistence.testing.tests.plsql;

import org.eclipse.persistence.testing.models.plsql.PLSQLXMLSystem;

/**
 * This model tests calling PLSQL stored procedures with PLSQL types.
 */
public class PLSQLXMLTestModel extends PLSQLTestModel {
    public PLSQLXMLTestModel() {
        setDescription("This model tests calling PLSQL stored procedures with PLSQL types from project XML.");
    }

    public void addRequiredSystems() {
        if (!getSession().getLogin().getPlatform().isOracle()) {
            warning("PLSQL is only supported on Oracle.");
        }

        addRequiredSystem(new PLSQLXMLSystem());
    }
}
