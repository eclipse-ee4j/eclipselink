/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.Collection;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;

public final class MWLegacyUnmappedMapping extends MWMapping {

    private MWLegacyUnmappedMapping() {
        super();
    }

    public MWLegacyUnmappedMapping(MWMappingDescriptor parent, MWClassAttribute attribute, String name) {
        super(parent, attribute, name);
    }

    protected void addChildrenTo(List children) {
    }


    protected void initializeOn(MWMapping newMapping) {

    }

    public void addWrittenFieldsTo(Collection writtenFields) {
    }

    protected DatabaseMapping buildRuntimeMapping() {
        return null;
    }
}
