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
import org.eclipse.persistence.internal.jpa.metadata.MetadataDynamicClassWriter;
import org.eclipse.persistence.internal.jpa.rs.dynamic.RestDynamicClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestCollectionAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.rs.weaving.RestReferenceAdapterV2ClassWriter;

/**
 * This custom ClassLoader provides support for dynamically generating classes
 * within an JPA-RS EclipseLink application using byte codes created using a
 * {@link DynamicClassWriter}. A DynamicClassLoader requires a parent or
 * delegate class-loader which is provided to the constructor. This delegate
 * class loader handles the lookup and storage of all created classes.
 *
 * @since EclipseLink 3.0
 */
public class DynamicRestClassLoader extends DynamicClassLoader {

    public DynamicRestClassLoader(ClassLoader delegate) {
        super(delegate);
    }

    public DynamicRestClassLoader(ClassLoader delegate, DynamicClassWriter writer) {
        super(delegate, writer);
    }

    public void createDynamicAdapter(String className) {
        // Reference adapter for JPARS version < 2.0
        RestAdapterClassWriter restAdapter = new RestAdapterClassWriter(className);
        addClass(restAdapter.getClassName(), restAdapter);
    }

    public void createDynamicCollectionAdapter(String className) {
        // Collection adapter for JPARS version >= 2.0
        RestCollectionAdapterClassWriter restCollectionAdapter = new RestCollectionAdapterClassWriter(className);
        addClass(restCollectionAdapter.getClassName(), restCollectionAdapter);
    }

    public void createDynamicReferenceAdapter(String className) {
        // Reference adapter for JPARS version >= 2.0
        RestReferenceAdapterV2ClassWriter restReferenceAdapterV2 = new RestReferenceAdapterV2ClassWriter(className);
        addClass(restReferenceAdapterV2.getClassName(), restReferenceAdapterV2);
    }

    @Override
    public Class<?> createDynamicClass(String className, DynamicClassWriter writer) {
        DynamicClassWriter w = writer;
        if (MetadataDynamicClassWriter.class.isAssignableFrom(writer.getClass())) {
            w = new RestDynamicClassWriter(MetadataDynamicClassWriter.class.cast(writer));
        }
        return super.createDynamicClass(className, w);
    }


}
