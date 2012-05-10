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
*     dmccann - June 4/2008 - 1.0M8 - Initial implementation
*     dmccann - Nov. 7/2008 - Moved delegate key logic to SDOHelperContext
******************************************************************************/
package org.eclipse.persistence.sdo.helper.delegates;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * Abstract class for SDO helper delegators.  Delegates will shared on an application
 * basis.  This class will return the key to be used to store/retrieve the delegates
 * for a given application. 
 */
public abstract class AbstractHelperDelegator {
    // Hold the context containing all helpers so that we can preserve inter-helper relationships
    protected HelperContext aHelperContext;
    
    /**
     * Return the helperContext that this instance is associated with.
     * This context contains all helpers. If null, the default context
     * is returned. 
     * 
     * @return set helper context or, if null, the default context
     * @see commonj.sdo.helper.HelperContext
     * @see commonj.sdo.impl.HelperProvider
     */
    public HelperContext getHelperContext() {
        if (null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.  This context 
     * will contain all helpers, so inter-helper relationships are preserved.
     *
     * @param helperContext
     * @see commonj.sdo.helper.HelperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
