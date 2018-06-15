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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeSupport;


/**
 * This abstract class provides the infrastructure needed to wrap
 * another property value model, "lazily" listen to it, and propagate
 * its change notifications.
 */
public abstract class PropertyValueModelWrapper
    extends AbstractModel
    implements PropertyValueModel
{

    /** The wrapped property value model. */
    protected PropertyValueModel valueHolder;

    /** A listener that allows us to synch with changes to the wrapped value holder. */
    protected PropertyChangeListener valueChangeListener;


    // ********** constructors **********

    /**
     * Construct a property value model with the specified wrapped
     * property value model. The value holder is required.
     */
    protected PropertyValueModelWrapper(PropertyValueModel valueHolder) {
        super();
        if (valueHolder == null) {
            throw new NullPointerException();
        }
        this.valueHolder = valueHolder;
    }


    // ********** initialization **********

    protected void initialize() {
        super.initialize();
        this.valueChangeListener = this.buildValueChangeListener();
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.AbstractModel#buildDefaultChangeSupport()
     */
    protected ChangeSupport buildDefaultChangeSupport() {
        return new ValueModelChangeSupport(this);
    }

    protected PropertyChangeListener buildValueChangeListener() {
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                PropertyValueModelWrapper.this.valueChanged(e);
            }
            public String toString() {
                return "value change listener";
            }
        };
    }


    // ********** extend change support **********

    /**
     * Extend to start listening to the nested model if necessary.
     * @see Model#addPropertyChangeListener(PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.hasNoPropertyChangeListeners(VALUE)) {
            this.engageValueHolder();
        }
        super.addPropertyChangeListener(listener);
    }

    /**
     * Extend to start listening to the nested model if necessary.
     * @see Model#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyName == VALUE && this.hasNoPropertyChangeListeners(VALUE)) {
            this.engageValueHolder();
        }
        super.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Extend to stop listening to the nested model if necessary.
     * @see Model#removePropertyChangeListener(PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener);
        if (this.hasNoPropertyChangeListeners(VALUE)) {
            this.disengageValueHolder();
        }
    }

    /**
     * Extend to stop listening to the nested model if necessary.
     * @see Model#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.removePropertyChangeListener(propertyName, listener);
        if (propertyName == VALUE && this.hasNoPropertyChangeListeners(VALUE)) {
            this.disengageValueHolder();
        }
    }


    // ********** behavior **********

    /**
     * Begin listening to the value holder.
     */
    protected void engageValueHolder() {
        this.valueHolder.addPropertyChangeListener(VALUE, this.valueChangeListener);
    }

    /**
     * Stop listening to the value holder.
     */
    protected void disengageValueHolder() {
        this.valueHolder.removePropertyChangeListener(VALUE, this.valueChangeListener);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.tools.AbstractModel#toString(StringBuffer)
     */
    public void toString(StringBuffer sb) {
        sb.append(this.valueHolder);
    }


    // ********** property change support **********

    /**
     * The value of the wrapped value holder has changed;
     * propagate the change notification appropriately.
     */
    protected abstract void valueChanged(PropertyChangeEvent e);

}
