/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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