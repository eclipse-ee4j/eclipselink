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
//     Oracle  - initial API and implementation from Oracle TopLink
//     dmccann - Nov 4/2008 - 1.1 - changed class to interface
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
