/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility;

import java.beans.PropertyChangeListener;

import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;


/**
 * Interface to be implemented by models that notify listeners of
 * changes to bound state, properties, collections, lists, and/or trees.
 */
public interface Model {

    void addStateChangeListener(StateChangeListener listener);

    void removeStateChangeListener(StateChangeListener listener);

    void addPropertyChangeListener(PropertyChangeListener listener);
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void addCollectionChangeListener(CollectionChangeListener listener);
    void addCollectionChangeListener(String collectionName, CollectionChangeListener listener);

    void removeCollectionChangeListener(CollectionChangeListener listener);
    void removeCollectionChangeListener(String collectionName, CollectionChangeListener listener);

    void addListChangeListener(ListChangeListener listener);
    void addListChangeListener(String listName, ListChangeListener listener);

    void removeListChangeListener(ListChangeListener listener);
    void removeListChangeListener(String listName, ListChangeListener listener);

    void addTreeChangeListener(TreeChangeListener listener);
    void addTreeChangeListener(String treeName, TreeChangeListener listener);

    void removeTreeChangeListener(TreeChangeListener listener);
    void removeTreeChangeListener(String treeName, TreeChangeListener listener);

}
