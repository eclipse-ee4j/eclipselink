/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.indirection.jdk8;

import java.util.Map;

/**
 * Java SE 8 additions to {@link org.eclipse.persistence.indirection.IndirectMap}.
 *
 * @author Lukas Jungmann
 * @deprecated Use {@link org.eclipse.persistence.indirection.IndirectMap} instead.
 */
@Deprecated
public class IndirectMap<K, V> extends org.eclipse.persistence.indirection.IndirectMap<K, V> {

    public IndirectMap() {
        super();
    }

    public IndirectMap(int initialCapacity) {
        super(initialCapacity);
    }

    public IndirectMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public IndirectMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
}
