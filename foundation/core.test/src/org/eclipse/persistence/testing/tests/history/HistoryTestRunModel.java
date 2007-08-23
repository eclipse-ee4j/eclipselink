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

import org.eclipse.persistence.testing.framework.*;

/**
 * Logically Groups the three History test models into a single package.
 * @author  smcritch
 * @since   10.0.3
 */
public class HistoryTestRunModel extends TestModel {

    public void addTests() {
        addTest(new HistoryTestModel(HistoryTestModel.BASIC));
        if (!getExecutor().isServer) {
            addTest(new HistoryTestModel(HistoryTestModel.PROJECT_XML));
            addTest(new HistoryTestModel(HistoryTestModel.PROJECT_CLASS_GENERATED));
        }
        addTest(new HistoryQualifiedTableTestModel(HistoryQualifiedTableTestModel.BASIC));
    }
}
