/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.feature;

/**
 * Test the feature tests without using binding, tests dynamic SQL.
 */

public class FeatureTestModelWithoutBinding extends FeatureTestModel {

    public Boolean origionalBindingState;
    public Boolean origionalStatementCachingState;

    public FeatureTestModelWithoutBinding() {
        setDescription("This model tests selected TopLink features using the employee demo while BindAllParameters turned off.");
    }

    public FeatureTestModelWithoutBinding(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void reset() {
        if (origionalStatementCachingState != null) {
            this.getSession().getPlatform().setShouldCacheAllStatements(this.origionalStatementCachingState.booleanValue());
        }
        if (origionalBindingState != null) {
            this.getSession().getPlatform().setShouldBindAllParameters(this.origionalBindingState.booleanValue());
        }
    }

    public void setup() {
        this.origionalBindingState = new Boolean(this.getSession().getPlatform().shouldBindAllParameters());
        this.origionalStatementCachingState = new Boolean(this.getSession().getPlatform().shouldCacheAllStatements());
        this.getSession().getPlatform().setShouldBindAllParameters(false);
        this.getSession().getPlatform().setShouldCacheAllStatements(false);
    }

}

