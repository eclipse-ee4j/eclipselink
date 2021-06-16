/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    Mapping getMapping();

    /**
     * Gets the value to be marshalled using the specified mapping.
     * @return The value to be marshalled.
     */
    Object getValue();

}
