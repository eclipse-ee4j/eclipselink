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
package org.eclipse.persistence.internal.oxm;

import java.lang.ref.WeakReference;

/**
 *  INTENRAL:
 *  <p><b>Purpose</b>: This class is used for caching objects based on Object
 *  Identity instead of a Primary Key. This class acts as the Primary Key, and
 *  wraps the domain object itself in a weak reference. hashCode and equals
 *  methods are implemented to insure identity is maintained.</p>
 *  @author  mmacivor
 *  @since   10g
 */

public class WeakObjectWrapper {
    protected WeakReference reference;

    public WeakObjectWrapper(Object domainObject) {
        reference = new WeakReference(domainObject);
    }

    public Object getDomainObject() {
        return reference.get();
    }

    public void setDomainObject(Object object) {
        reference = new WeakReference(object);
    }

    public int hashCode() {
        if (getDomainObject() == null) {
            return -1;
        }
        return getDomainObject().hashCode();
    }

    public boolean equals(Object wrapper) {
        if (!(wrapper instanceof WeakObjectWrapper)) {
            return false;
        }
        return getDomainObject() == ((WeakObjectWrapper)wrapper).getDomainObject();
    }
}
