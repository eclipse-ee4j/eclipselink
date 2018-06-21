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
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.oxm.mappings.Mapping;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>This class defines an interface that provides information about mappings
 * and values, used to marshal Sequenced objects.
 * <p><b>Responsibilities:</b><ul>
 * <li>Provide access to a Mapping</li>
 * <li>Provide access to the a value to be marshalled for the specific mapping</li>
 * </ul>
 * <p>When marshalling a sequenced Data Object, that Object will provide TopLink OXM with an
 * Ordered list of TopLinkSetting objects. These will be used to marshal the appropriate values
 * in the correct order.
 *
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */
public interface XMLSetting {
    /**
     * @return The TopLink OXM mapping associated with this setting
     */
    public Mapping getMapping();

    /**
     * Gets the value to be marshalled using the specified mapping.
     * @return The value to be marshalled.
     */
    public Object getValue();

}
