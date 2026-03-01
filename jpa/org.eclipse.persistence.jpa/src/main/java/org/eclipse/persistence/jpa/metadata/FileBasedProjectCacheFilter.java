/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     08/02/2022-4.0.0 Tomas Kraus - 1613: FileBasedProjectCache retrieveProject contains readObject with no data filtering
package org.eclipse.persistence.jpa.metadata;

import java.io.ObjectInputFilter;

import org.eclipse.persistence.sessions.Project;

/**
 * Verify content of FileBasedProjectCache being read with ObjectInputStream.
 */
class FileBasedProjectCacheFilter implements ObjectInputFilter {

    /**
     * Creates an instance of ObjectInputStream data of FileBasedProjectCache verifier.
     */
    FileBasedProjectCacheFilter() {
    }

    // ObjectInputStream data verification entry point.
    // This method is being called for every object instance being deserialized.
    @Override
    public Status checkInput(FilterInfo info) {
        // Skip checks when decision was already made by higher level filter.
        ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
        if (serialFilter != null) {
            ObjectInputFilter.Status status = serialFilter.checkInput(info);
            if (status != ObjectInputFilter.Status.UNDECIDED) {
                // The process-wide filter overrides this filter
                return status;
            }
        }
        // The only allowed instance on top of the structure is Project class
        if (info.serialClass() != null && info.depth() == 1L) {
            if (info.serialClass() != Project.class) {
                return Status.REJECTED;
            }
        }
        return Status.UNDECIDED;
    }

}
