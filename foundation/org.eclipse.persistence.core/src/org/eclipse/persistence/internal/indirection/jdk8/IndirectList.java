/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.indirection.jdk8;

import java.util.Collection;

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectList}.
 *
 * @author Lukas Jungmann
 * @deprecated Use {@link org.eclipse.persistence.indirection.IndirectList} instead.
 */
@Deprecated
public class IndirectList<E> extends org.eclipse.persistence.indirection.IndirectList<E> {

    public IndirectList() {
        super();
    }

    public IndirectList(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectList(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public IndirectList(Collection<? extends E> vector) {
        super(vector);
    }

}
