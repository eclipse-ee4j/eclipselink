/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.optimisticlocking;

import org.eclipse.persistence.descriptors.RelationalDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ObjectVersion {
    public java.math.BigDecimal id;
    public String versionInfo = "anyInfo";

    /**
     * Version constructor comment.
     */
    public ObjectVersion() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(ObjectVersion.class);
        List<String> vector = new ArrayList<>();
        vector.add("AO_LCK");
        descriptor.setTableNames(vector);

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.descriptorIsAggregate();

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("AO_LCK.AGG_VERSION");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("versionInfo");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("AO_LCK.AGG_INFO");
        descriptor.addMapping(directtofieldmapping2);

        return descriptor;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String newValue) {
        this.versionInfo = newValue;
    }
}
