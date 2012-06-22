/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 06 March 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
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