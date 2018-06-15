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
package org.eclipse.persistence.tools.workbench.utility;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;


/**
 * Null implementation that never notifies any listeners of any changes
 * because its value never changes.
 */
public class NullModel
    implements Model, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;


    /**
     * Default constructor.
     */
    public NullModel() {
        super();
    }


    // ********** Model implementation **********

    /**
     * @see Model#addStateChangeListener(org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener)
     */
    public void addStateChangeListener(StateChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeStateChangeListener(org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener)
     */
    public void removeStateChangeListener(StateChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addPropertyChangeListener(String, java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removePropertyChangeListener(String, java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addCollectionChangeListener(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener)
     */
    public void addCollectionChangeListener(CollectionChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addCollectionChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener)
     */
    public void addCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeCollectionChangeListener(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener)
     */
    public void removeCollectionChangeListener(CollectionChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeCollectionChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener)
     */
    public void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addListChangeListener(org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener)
     */
    public void addListChangeListener(ListChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addListChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener)
     */
    public void addListChangeListener(String listName, ListChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeListChangeListener(org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener)
     */
    public void removeListChangeListener(ListChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeListChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener)
     */
    public void removeListChangeListener(String listName, ListChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addTreeChangeListener(org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener)
     */
    public void addTreeChangeListener(TreeChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#addTreeChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener)
     */
    public void addTreeChangeListener(String treeName, TreeChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeTreeChangeListener(org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener)
     */
    public void removeTreeChangeListener(TreeChangeListener listener) {
        // ignore listeners - nothing ever changes
    }

    /**
     * @see Model#removeTreeChangeListener(String, org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener)
     */
    public void removeTreeChangeListener(String treeName, TreeChangeListener listener) {
        // ignore listeners - nothing ever changes
    }


    // ********** Object overrides **********

    /**
     * @see Object#clone()
     */
    public synchronized Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
