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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectMapProject;


/**
 * Bug 3945357
 * Ensure projectClassGenerator writes correct project class
 * for mappings that map to a Map and use transparent indirection with a non-default collection type.
 */
public class MapPolicyIndirectionTest extends ProjectClassGeneratorResultFileTest {
    public MapPolicyIndirectionTest() {
        super(new IndirectMapProject(),
              ".useMapClass(org.eclipse.persistence.testing.models.directmap.IndirectMapSubclass.class, \"getKey\"");
        setDescription("Test project class generation for Map Container Policy with indirection.");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        ClassDescriptor descriptorToModify =
            project.getDescriptors().get(((IndirectMapProject)project).orderClass());

        ((org.eclipse.persistence.mappings.CollectionMapping)descriptorToModify.getMappingForAttributeName("salesReps")).useTransparentMap("getKey");
        ((org.eclipse.persistence.mappings.CollectionMapping)descriptorToModify.getMappingForAttributeName("salesReps")).useMapClass(org.eclipse.persistence.testing.models.directmap.IndirectMapSubclass.class,
                                                                                                                            "getKey");
    }
}
