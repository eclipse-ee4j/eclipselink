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
package org.eclipse.persistence.tools.workbench.test.mappingsio.legacy;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.test.models.projects.ComplexAggregateProject;

public class BackwardCompatibilityTests60 extends
        BackwardCompatibilityInternalTestCase {

    public static Test suite() {
        return new TestSuite(BackwardCompatibilityTests60.class);
    }

    public BackwardCompatibilityTests60(String name) {
        super(name);
    }

    @Override
    protected MWRelationalProject buildComplexAggregateProject()
            throws Exception {
        return new ComplexAggregateProject().getProject();
    }

    @Override
    protected String version() {
        return "6.0";
    }

}
