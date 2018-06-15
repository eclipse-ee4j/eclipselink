/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectIdentifiedByNameTextChildKeepElementTestCases extends CompositeObjectIdentifiedByNameTextChildTestCases{

    public CompositeObjectIdentifiedByNameTextChildKeepElementTestCases(            String name) throws Exception {
        super(name);
        Project p = new CompositeObjectIdentifiedByNameTextProject();
        ((XMLCompositeObjectMapping)p.getDescriptor(EmployeeWithObjects.class).getMappingForAttributeName("salary")).setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        setProject(p);
    }

}
