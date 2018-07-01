/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 04 November 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.sdo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.impl.ExternalizableDelegator.Resolvable;

public abstract class AbstractExternalizableDelegator implements Externalizable {

    static final long serialVersionUID = 1L;
    transient Resolvable delegate;

    public AbstractExternalizableDelegator() {
        delegate = new SDOResolvable();
    }

    public AbstractExternalizableDelegator(Object target) {
        delegate = new SDOResolvable(target, SDOHelperContext.getHelperContext());
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        delegate.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        delegate.readExternal(in);
    }

    public Object readResolve() throws ObjectStreamException {
        return delegate.readResolve();
    }

}
