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
package org.eclipse.persistence.tools.workbench.test.platformsplugin.ui;

import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsplugin.ui.repository.DatabasePlatformRepositoryNode;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;

/**
 *
 */
public class DatabasePlatformTabbedPropertiesPageTest extends AbstractPropertiesPageTest {

    public static void main(String[] args) throws Exception {
        new DatabasePlatformTabbedPropertiesPageTest().exec(args);
    }

    public DatabasePlatformTabbedPropertiesPageTest() {
        super();
    }

    protected ListValueModel nodesModel(DatabasePlatformRepositoryNode reposNode) {
        ListValueModel childrenModel = reposNode.getChildrenModel();
        // add a dummy listener so the models wake up
        childrenModel.addListChangeListener(ListValueModel.VALUE, new ListChangeAdapter());
        return childrenModel;
    }

    private DatabasePlatform currentPlatform() {
        return (DatabasePlatform) this.currentValue();
    }

    protected void print() {
        DatabasePlatform currentPlatform = this.currentPlatform();
        if (currentPlatform == null) {
            return;
        }
        System.out.println("current platform: " + currentPlatform);
        System.out.println("\tshort file name: " + currentPlatform.getShortFileName());
        System.out.println("\truntime platform class name: " + currentPlatform.getRuntimePlatformClassName());
        System.out.println("\tsupports native sequencing: " + currentPlatform.supportsNativeSequencing());
        System.out.println("\tcomment: " + currentPlatform.getComment());
    }

}
