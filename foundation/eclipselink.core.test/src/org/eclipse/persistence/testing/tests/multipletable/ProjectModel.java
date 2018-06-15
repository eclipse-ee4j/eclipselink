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
package org.eclipse.persistence.testing.tests.multipletable;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.multipletable.ProjectSystem;

public class ProjectModel extends TestModel {
    public ProjectModel() {
        setDescription("this tests that multiple table joins work.  See expression tests");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new ProjectSystem());
    }
}
