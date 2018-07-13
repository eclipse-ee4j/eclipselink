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
