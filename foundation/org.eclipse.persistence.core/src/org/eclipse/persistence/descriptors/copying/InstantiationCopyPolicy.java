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
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;

/**
 * <p><b>Purpose</b>: This is the default copy policy.
 *
 * It creates a copy by creating a new instance of the object and then using the
 * mappings specified for the object to populate the object.
 */
public class InstantiationCopyPolicy extends AbstractCopyPolicy {
    public InstantiationCopyPolicy() {
        super();
    }

    @Override
    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        return getDescriptor().getObjectBuilder().buildNewInstance();
    }

    @Override
    public boolean buildsNewInstance() {
        return true;
    }

    @Override
    public String toString() {
        return Helper.getShortClassName(this) + "()";
    }
}
