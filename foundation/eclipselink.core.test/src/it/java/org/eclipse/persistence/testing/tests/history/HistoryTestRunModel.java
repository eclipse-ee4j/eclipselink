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
