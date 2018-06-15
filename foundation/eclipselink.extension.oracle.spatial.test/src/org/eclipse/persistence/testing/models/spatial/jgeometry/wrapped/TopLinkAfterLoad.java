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
package org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;

public class TopLinkAfterLoad {
    public static void afterLoadWrappedSpatial(ClassDescriptor cd) {
        DirectQueryKey queryKey = new DirectQueryKey();
        queryKey.setField(new DatabaseField("GEOMETRY.GEOM",
                                            "WRAPPED_SPATIAL"));
        queryKey.setName("geometry.geom");
        cd.addQueryKey(queryKey);

        queryKey = new DirectQueryKey();
        queryKey.setField(new DatabaseField("GEOMETRY.ID", "WRAPPED_SPATIAL"));
        queryKey.setName("geometry.id");

        cd.addQueryKey(queryKey);
    }
}
