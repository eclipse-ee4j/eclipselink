/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

