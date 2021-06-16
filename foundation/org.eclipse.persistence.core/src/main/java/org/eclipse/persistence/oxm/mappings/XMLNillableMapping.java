/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;


/**
 * INTERNAL
 * All nillable mappings which can be added to org.eclipse.persistence.oxm.XMLDescriptor must
 * implement this interface.<br>
 * The default policy is AbstractNullPolicy.<br>
 *
 *@see org.eclipse.persistence.oxm.mappings
 */
public interface XMLNillableMapping {

    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
    void setNullPolicy(AbstractNullPolicy aNullPolicy);

    /**
     * Get the AbstractNullPolicy from the Mapping.<br>
     * The default policy is NullPolicy.<br>
     * @return
     */
    AbstractNullPolicy getNullPolicy();
}
