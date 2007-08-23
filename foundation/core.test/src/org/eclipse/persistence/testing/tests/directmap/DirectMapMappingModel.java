/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappingsSystem;

/**
 * Testing model for DirectMapMapping tests
 *
 * @auther Guy Pelletier
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