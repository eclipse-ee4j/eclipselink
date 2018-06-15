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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.utility.Model;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Describes the core behavior that a policy for a descriptor should
 * contain.
 *
 * @version 10.1.3
 */
public interface MWDescriptorPolicy
    extends MWNode, Model
{
    MWMappingDescriptor getOwningDescriptor();

    void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);

    void dispose();

    MWDescriptorPolicy getPersistedPolicy();

    boolean isActive();
}
