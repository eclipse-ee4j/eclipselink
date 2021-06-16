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
