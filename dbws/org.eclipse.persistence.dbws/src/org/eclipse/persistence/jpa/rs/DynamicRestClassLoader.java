/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.rs;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicClassWriter;
import org.eclipse.persistence.internal.jpa.rs.dynamic.RestClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestCollectionAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestReferenceAdapterV2ClassWriter;

public class DynamicRestClassLoader extends DynamicClassLoader {

    public DynamicRestClassLoader(ClassLoader delegate) {
        this(delegate, new RestClassWriter());
    }

    public DynamicRestClassLoader(ClassLoader delegate, DynamicClassWriter writer) {
        super(delegate, writer);
    }

    public void createDynamicAdapter(String javaClassName) {
        // Reference adapter for JPARS version < 2.0
        RestAdapterClassWriter restAdapter = new RestAdapterClassWriter(javaClassName);
        addClass(restAdapter.getClassName(), restAdapter);
    }

    public void createDynamicCollectionAdapter(String javaClassName) {
        // Collection adapter for JPARS version >= 2.0
        RestCollectionAdapterClassWriter restCollectionAdapter = new RestCollectionAdapterClassWriter(javaClassName);
        addClass(restCollectionAdapter.getClassName(), restCollectionAdapter);
    }

    public void createDynamicReferenceAdapter(String javaClassName) {
        // Reference adapter for JPARS version >= 2.0
        RestReferenceAdapterV2ClassWriter restReferenceAdapterV2 = new RestReferenceAdapterV2ClassWriter(javaClassName);
        addClass(restReferenceAdapterV2.getClassName(), restReferenceAdapterV2);
    }
}
