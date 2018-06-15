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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.mapping;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToManyMapping;
import org.eclipse.persistence.tools.workbench.test.models.projects.CrimeSceneProject;


public class MWOneToManyMappingTests extends TestCase {

    public static Test suite() {
        return new TestSuite(MWOneToManyMappingTests.class);
    }

    public MWOneToManyMappingTests(String name) {
        super(name);
    }

    public void testDescriptorRemovedAndMappingUpdated() {
        CrimeSceneProject crimeSceneProject = new CrimeSceneProject();
        MWDescriptor pieceOfEvidenceDescriptor = crimeSceneProject.getPieceOfEvidenceDescriptor();

        MWOneToManyMapping evidenceMapping = crimeSceneProject.getEvidenceMappingInCrimeScene();

        crimeSceneProject.getProject().removeDescriptor(pieceOfEvidenceDescriptor);

        assertTrue("Mapping's reference descriptor was not set to null", evidenceMapping.getReferenceDescriptor() == null);
    }

}
