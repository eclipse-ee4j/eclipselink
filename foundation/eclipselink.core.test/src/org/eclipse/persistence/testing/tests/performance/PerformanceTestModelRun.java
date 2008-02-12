/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance;

import org.eclipse.persistence.testing.framework.*;

/**
 * Runs performance model 3 times.
 */
public class PerformanceTestModelRun extends TestModel {
    public PerformanceTestModelRun() {
        setDescription("Runs performance model 3 times.");
    }

    public void addTests() {
        addTest(new PerformanceTestModel());
        addTest(new PerformanceTestModel());
        addTest(new PerformanceTestModel());
    }
}