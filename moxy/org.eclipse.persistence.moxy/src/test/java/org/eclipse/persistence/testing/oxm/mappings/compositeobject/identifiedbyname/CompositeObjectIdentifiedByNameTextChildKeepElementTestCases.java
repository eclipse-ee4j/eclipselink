/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
