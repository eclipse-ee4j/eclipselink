/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
