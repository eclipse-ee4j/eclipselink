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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.mappings.*;

public class AnyObjectKeepUnkownAsElementProject extends Project {

    public AnyObjectKeepUnkownAsElementProject() {
        this.addDescriptor(buildRootDescriptor());
    }

    public ClassDescriptor buildRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RootKeepAsElement.class);
        descriptor.setDefaultRootElement("root");

        XMLAnyObjectMapping mapping = new XMLAnyObjectMapping();
        mapping.setAttributeName("t1");
        mapping.setGetMethodName("getT1");
        mapping.setSetMethodName("setT1");
        mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        descriptor.addMapping(mapping);

        return descriptor;
    }

}
