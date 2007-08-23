/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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