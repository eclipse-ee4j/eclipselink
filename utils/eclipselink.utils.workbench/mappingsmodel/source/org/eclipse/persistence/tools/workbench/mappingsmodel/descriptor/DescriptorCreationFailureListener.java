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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.EventListener;


/**
 * A "DescriptorCreationFailure" event gets fired whenever a project is unable
 * to create a descriptor for a particular MWClass
 */
public interface DescriptorCreationFailureListener extends EventListener {

    /**
     * This method gets called when a project is unable to create
     * or refresh a descriptor for a given mwClass "external" class repository
     *
     * @param e A DescriptorCreationFailureEvent object describing the
     * event source, the class that did not have a descriptor created,
     * and a resource string key to explain the cause of the failure.
     */
    void descriptorCreationFailure(DescriptorCreationFailureEvent e);
}
