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

/**
 * Interface for use in AdvancedPolicyAction$InterfaceAliasPolicyHolder so
 * the advanced property can be easily manipulated for the two descriptors
 * types that use the policy.
 */
public interface MWInterfaceAliasDescriptor {

    public MWDescriptorPolicy getInterfaceAliasPolicy();

    public void removeInterfaceAliasPolicy();

    public void addInterfaceAliasPolicy();

}
