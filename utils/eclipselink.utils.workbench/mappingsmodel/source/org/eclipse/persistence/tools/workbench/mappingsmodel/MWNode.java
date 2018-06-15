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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public interface MWNode
    extends Node
{
    // ********** convenience methods **********

    MWProject getProject();

    MWNode getMWParent();

    MWClassRepository getRepository();

    MWClass typeNamed(String typeName);

    MWClass typeFor(Class javaClass);


    // ********** model synchronization support **********

    void mappingReplaced(MWMapping oldMapping, MWMapping newMapping);

    void descriptorReplaced(MWDescriptor oldDescriptor, MWDescriptor newDescriptor);

    /**
     * Used as a performance tuning when unmapping descriptors instead
     * of calling #nodeRemoved(Node) for every mapping in the descriptor.
     */
    void descriptorUnmapped(Collection mappings);


    // ********** I/O event methods **********

    void resolveClassHandles();

    void resolveDescriptorHandles();

    void resolveMetadataHandles();

    void resolveColumnHandles();

    void resolveReferenceHandles();

    void resolveMethodHandles();

    void postProjectBuild();


    // ********** legacy methods **********



}
