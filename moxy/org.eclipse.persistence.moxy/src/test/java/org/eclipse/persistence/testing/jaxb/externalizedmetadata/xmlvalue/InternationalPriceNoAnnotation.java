/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - October 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

public class InternationalPriceNoAnnotation {
    public java.math.BigDecimal price;
    public String currency;

    public boolean equals(Object obj){
        if(obj instanceof InternationalPriceNoAnnotation priceObj){
            return currency.equals(priceObj.currency) && price.equals(priceObj.price);
        }
        return false;
    }
}
