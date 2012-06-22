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
