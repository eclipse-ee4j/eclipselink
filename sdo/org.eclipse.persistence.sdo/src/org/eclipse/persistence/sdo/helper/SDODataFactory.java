/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle  - initial API and implementation from Oracle TopLink
 *     dmccann - Nov 4/2008 - 1.1 - changed class to interface
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;

/**
 * <p><b>Purpose</b>: The implementation of commonj.sdo.helper.DataFactory</p>
 */
public interface SDODataFactory extends DataFactory {
    /**
     * INTERNAL:
     * Return the helperContext that this instance is associated with.
     * @return
     */
    public HelperContext getHelperContext();

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.
     *
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext);
}
