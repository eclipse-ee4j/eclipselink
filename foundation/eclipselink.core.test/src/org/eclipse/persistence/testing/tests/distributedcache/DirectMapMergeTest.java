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
package org.eclipse.persistence.testing.tests.distributedcache;

import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappingsProject;

public class DirectMapMergeTest extends DistributedCacheMergeTest {
    public DirectMapMergeTest() {
        super();
    }

    protected void modifyCollection(UnitOfWork uow, Object objectToModify) {
        ((DirectMapMappings)objectToModify).directMap.put(new Integer(11), newItemForCollection());
    }

    protected int getCollectionSize(Object rootObject) {
        return ((DirectMapMappings)rootObject).directMap.keySet().size();
    }

    protected Class getRootClass() {
        return DirectMapMappings.class;
    }

    protected Project getNewProject() {
        Project p = new DirectMapMappingsProject();
        DirectMapMapping mapping = (DirectMapMapping)p.getDescriptor(DirectMapMappings.class).getMappingForAttributeName("directMap");
        mapping.setKeyClass(Integer.class);
        return p;
    }

    protected Object buildOriginalObject() {
        DirectMapMappings dm = new DirectMapMappings();
        dm.directMap.put(new Integer("1"), "first value");
        return dm;
    }

    protected Object newItemForCollection() {
        return "testing";
    }
}
