/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.copying;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.sessions.*;

/**
 * <p><b>Purpose</b>: Creates a copy through creating a new instance.
 */
public class InstantiationCopyPolicy extends AbstractCopyPolicy {
    public InstantiationCopyPolicy() {
        super();
    }

    public Object buildClone(Object domainObject, Session session) throws DescriptorException {
        return getDescriptor().getObjectBuilder().buildNewInstance();
    }

    public boolean buildsNewInstance() {
        return true;
    }

    public String toString() {
        return Helper.getShortClassName(this) + "()";
    }
}