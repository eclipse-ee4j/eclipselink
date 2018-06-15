/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - October 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue;

public class CDNPricesNoAnnotation {
    public java.util.List prices;

    public boolean equals(Object obj){
        if(obj instanceof CDNPricesNoAnnotation){
            CDNPricesNoAnnotation priceObj = (CDNPricesNoAnnotation)obj;
            if(prices.size() != priceObj.prices.size()){
                return false;
            }
            return prices.containsAll(priceObj.prices) && priceObj.prices.containsAll(prices);
        }
        return false;
    }
}
