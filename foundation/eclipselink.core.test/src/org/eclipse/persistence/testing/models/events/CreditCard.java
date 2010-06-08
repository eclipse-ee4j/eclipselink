/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.events;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

public class CreditCard {
    public String number;
    public String expiry;

    public CreditCard() {
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.descriptorIsAggregate();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.events.CreditCard.class);

        // RelationalDescriptor properties.
        // Query manager.
        // Event manager.
        // Mappings.
        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("CARD_NUMBER");
        descriptor.addMapping(numberMapping);

        DirectToFieldMapping expiryMapping = new DirectToFieldMapping();
        expiryMapping.setAttributeName("expiry");
        expiryMapping.setFieldName("CARD_EXPIRY");
        descriptor.addMapping(expiryMapping);

        return descriptor;

    }
}
