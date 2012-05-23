/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

public class TimestampVersion {
    public java.sql.Timestamp t_id;
    public String versionInfo = "anyInfo";

    public TimestampVersion() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(TimestampVersion.class);
        Vector vector = new Vector();
        vector.addElement("TSAO_LCK");
        descriptor.setTableNames(vector);

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.descriptorIsAggregate();

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("t_id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("TSAO_LCK.AGG_VERSION");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("versionInfo");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("TSAO_LCK.AGG_INFO");
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
