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
package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;


/**
 * Extend ValueAspectPropertyValueModelAdapter to listen to the
 * "state" of the value in the wrapped value model.
 */
public class ValueStatePropertyValueModelAdapter
    extends ValueAspectPropertyValueModelAdapter
{
    /** Listener that listens to value. */
    protected StateChangeListener valueStateListener;


    // ********** constructors **********

    /**
     * Construct an adapter for the value state.
     */
    public ValueStatePropertyValueModelAdapter(PropertyValueModel valueHolder) {
        super(valueHolder);
    }


    // ********** initialization **********

    protected void initialize() {
        super.initialize();
        this.valueStateListener = this.buildValueStateListener();
    }

    protected StateChangeListener buildValueStateListener() {
        return new StateChangeListener() {
            public void stateChanged(StateChangeEvent e) {
                ValueStatePropertyValueModelAdapter.this.valueAspectChanged();
            }
            public String toString() {
                return "value state listener";
            }
        };
    }


    // ********** behavior **********

    protected void startListeningToValue() {
        ((Model) this.value).addStateChangeListener(this.valueStateListener);
    }

    protected void stopListeningToValue() {
        ((Model) this.value).removeStateChangeListener(this.valueStateListener);
    }

}
