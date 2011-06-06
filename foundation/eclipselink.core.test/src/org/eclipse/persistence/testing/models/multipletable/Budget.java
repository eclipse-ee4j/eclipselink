/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.multipletable;

import java.math.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.*;

public class Budget {
    public double amount;
    public String currency;
    public java.math.BigDecimal id;

    public Budget() {
        this(0.0);
    }

    public Budget(double amount) {
        super();
        this.amount = amount;
        currency = "CDN";
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(Budget.class);
        descriptor.setTableName("BUDGET");
        descriptor.addPrimaryKeyFieldName("BUDGET.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberName("BUDG_SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("currency");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("BUDGET.CUR");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("amount");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("BUDGET.AMNT");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping3 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("id");
        directtofieldmapping3.setIsReadOnly(false);
        directtofieldmapping3.setFieldName("BUDGET.ID");
        descriptor.addMapping(directtofieldmapping3);

        return descriptor;
    }

    /**
     * Return a platform independant definition of the LPROJECT database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("BUDGET");
        definition.addIdentityField("ID", BigInteger.class, 15);
        definition.addField("CUR", String.class, 3);
        definition.addField("AMNT", Double.class, 15, 2);

        return definition;
    }
}
