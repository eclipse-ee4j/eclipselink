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
package org.eclipse.persistence.tools.workbench.uitools.app;

import java.util.Collection;

/**
 * Extend ValueModel to allow the adding and
 * removing of items in a collection value.
 * Typically the value returned from #getValue()
 * will be an Iterator.
 */
public interface CollectionValueModel extends ValueModel {

    /**
     * Add the specified item to the collection value.
     */
    void addItem(Object item);

    /**
     * Add the specified items to the collection value.
     */
    void addItems(Collection items);

    /**
     * Remove the specified item from the collection value.
     */
    void removeItem(Object item);

    /**
     * Remove the specified items from the collection value.
     */
    void removeItems(Collection items);

    /**
     * Return the size of the collection value.
     */
    int size();

}
