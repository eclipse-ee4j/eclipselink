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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import javax.swing.ComboBoxModel;

/**
 * This interface allows a client to better control the performance of
 * a combo box model by allowing the client to specify when it is
 * acceptable for the model to "cache" and "uncache" its list of elements.
 * The model may ignore these hints if appropriate.
 */
public interface CachingComboBoxModel extends ComboBoxModel {

    /**
     * Cache the comboBoxModel List.  If you call this, you
     * must make sure to call uncacheList() as well.  Otherwise
     * stale data will be in the ComboBox until cacheList() is
     * called again or uncacheList() is called.
     */
    void cacheList();

    /**
     * Clear the cached list.  Next time the list is needed it will
     * be built when it is not cached.
     */
    void uncacheList();

    /**
     * Check to see if the list is already cached.  This can be used for
     * MouseEvents, since they are not terribly predictable.
     */
    boolean isCached();

}
