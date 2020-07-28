/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.copypolicy;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CloneCopyPolicyMetadata;
import org.eclipse.persistence.jpa.config.CloneCopyPolicy;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CloneCopyPolicyImpl extends MetadataImpl<CloneCopyPolicyMetadata> implements CloneCopyPolicy {

    public CloneCopyPolicyImpl() {
        super(new CloneCopyPolicyMetadata());
    }

    public CloneCopyPolicy setMethodName(String methodName) {
        getMetadata().setMethodName(methodName);
        return this;
    }

    public CloneCopyPolicy setWorkingCopyMethodName(String workingCopyMethodName) {
        getMetadata().setWorkingCopyMethodName(workingCopyMethodName);
        return this;
    }

}
