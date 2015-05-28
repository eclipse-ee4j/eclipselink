/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.cache;

import org.eclipse.persistence.testing.framework.TestModel;

/**
 * This model tests advanced processing related classes.
 */
public class AdvancedProcessingTestModel extends TestModel {

    public AdvancedProcessingTestModel() {
        setDescription("This model tests advanced processing related classes.");
    }

    public AdvancedProcessingTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addTests() {
        addTest(new AdvancedProcessingTestSuite(isSRG));
    }

}
