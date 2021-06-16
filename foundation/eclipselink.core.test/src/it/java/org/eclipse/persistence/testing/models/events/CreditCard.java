/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
