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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This class is used by ReflectiveChangeListener when the requested listener
 * need only implement a single method (i.e. StateChangeListener or
 * PropertyChangeListener).
 */
class SingleMethodReflectiveChangeListener
    extends ReflectiveChangeListener
    implements StateChangeListener, PropertyChangeListener
{

    /** the method we will invoke on the target object */
    private Method method;
    /** cache the number of arguments */
    private boolean methodIsZeroArgument;

    SingleMethodReflectiveChangeListener(Object target, Method method) {
        super(target);
        this.method = method;
        this.methodIsZeroArgument = method.getParameterTypes().length == 0;
    }


    // ********** StateChangeListener implementation **********

    public void stateChanged(StateChangeEvent e) {
        if (this.methodIsZeroArgument) {
            ClassTools.invokeMethod(this.method, this.target, EMPTY_STATE_CHANGE_EVENT_ARRAY);
        } else {
            ClassTools.invokeMethod(this.method, this.target, new StateChangeEvent[] {e});
        }
    }


    // ********** PropertyChangeListener implementation **********

    public void propertyChange(PropertyChangeEvent e) {
        if (this.methodIsZeroArgument) {
            ClassTools.invokeMethod(this.method, this.target, EMPTY_PROPERTY_CHANGE_EVENT_ARRAY);
        } else {
            ClassTools.invokeMethod(this.method, this.target, new PropertyChangeEvent[] {e});
        }
    }

}
