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

import java.util.Iterator;

/**
 * Inteface describing the base implementation for a descriptor inheritance
 * policy.
 */
public interface MWInheritancePolicy
    extends MWDescriptorPolicy, MWClassIndicatorPolicy.Parent
{
    /** Return the parent in the inheritance hierarchy
     * (NOT the owning descriptor) */
    MWDescriptor getParentDescriptor();

    /** Return those descriptors that are applicable potential choices
     * to be parent descriptors for this inheritance policy */
    Iterator candidateParentDescriptors();

    /**
     * Return the ultimate top of the inheritance hierarchy
     * This method should never return null.  The root
     * is defined as the descriptor in the inerhtance policy that has
     * no parent descriptor set.
     */
    MWDescriptor getRootDescriptor();


    /** Return an iterator of descriptors, each which inherits from the one before,
     * starting with this policy's descriptor and terminating at the root descriptor
     * (or at the point of cyclicity) */
    Iterator descriptorLineage();

    /** Return all descriptors that *directly* branch from this
     * inheritance policy's owning descriptor */
    Iterator childDescriptors();

    /** Return all descriptors that *directly or indirectly*
     * branch from this inheritance policy's owning descriptor */
    Iterator descendentDescriptors();

    boolean isRoot();

    MWClassIndicatorPolicy getClassIndicatorPolicy();

    void buildClassIndicatorValues();

    /** Used to keep up to date with inheritance changes */
    void descriptorInheritanceChanged();

    void parentDescriptorMorphedToAggregate();

    void automap();
}
