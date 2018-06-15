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
package org.eclipse.persistence.testing.tests.history;

import org.eclipse.persistence.testing.framework.TestSystem;

public class HistoryQualifiedTableTestModel extends HistoryTestModel {
    public HistoryQualifiedTableTestModel(int mode) {
        super(mode);
        setName(getName() + "QualifiedTable");
    }

    public void addTests() {
        addTest(new QualifiedTableTest(getAsOfClause()));
    }

    private void configure() throws Exception {
        String user = getSession().getDatasourceLogin().getUserName();
        TestSystem system = new HistoricalQualifiedTableEmployeeSystem(user);

        system.run(getSession());
        buildAsOfClause();
        Thread.sleep(1000);
        depopulate();
        return;
    }

}
