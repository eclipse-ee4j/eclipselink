/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.copypolicy;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CustomCopyPolicyMetadata;
import org.eclipse.persistence.jpa.config.CopyPolicy;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CopyPolicyImpl extends MetadataImpl<CustomCopyPolicyMetadata> implements CopyPolicy {

    public CopyPolicyImpl() {
        super(new CustomCopyPolicyMetadata());
    }

    @Override
    public CopyPolicy setCopyPolicyClassName(String copyPolicyClassName) {
        getMetadata().setCopyPolicyClassName(copyPolicyClassName);
        return this;
    }

}
