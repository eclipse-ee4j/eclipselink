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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappingsSystem;

/**
 * Testing model for DirectMapMapping tests
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class DirectMapMappingModel extends TestModel {
    public DirectMapMappingModel() {
        setDescription("This model tests DirectMapMappings in Toplink.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new DirectMapMappingsSystem());
    }

    public void addTests() {
        addTest(new MergeChangeSetWithDirectMapMappingTest());
        addTest(new MergeChangeSetWithIndirectDirectMapMappingTest());
        addTest(new DirectMapUnitOfWorkTest());
        addTest(new DirectMapMappingDeleteTest());
        addTest(new DirectMapMappingBatchReadTest());
        addTest(new DirectMapMappingIndirectionTest());
        addTest(new DirectMapMappingsSerializedConverterTestCase());
        addTest(new DirectMapMappingHashMapTest());
    }
}
