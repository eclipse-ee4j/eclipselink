/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James - initial impl
 ******************************************************************************/
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
