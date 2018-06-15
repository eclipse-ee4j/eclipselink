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
package org.eclipse.persistence.testing.models.plsql;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.workbenchintegration.WorkbenchIntegrationSystemHelper;

public class PLSQLXMLSystem extends PLSQLSystem {
    public PLSQLXMLSystem() {
        this.project = new PLSQLProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (this.project == null) {
            this.project = new PLSQLProject();
        }
        this.project = WorkbenchIntegrationSystemHelper.buildProjectXML(this.project, "plsql-project");
        session.addDescriptors(this.project);
    }
}
