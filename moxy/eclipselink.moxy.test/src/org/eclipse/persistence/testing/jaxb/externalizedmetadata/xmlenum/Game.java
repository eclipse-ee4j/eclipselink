/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - January 11/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

@javax.xml.bind.annotation.XmlRootElement(name="hand")
public class Game {
    public Card card;
    public Coin coin;

    public boolean equals(Object obj) {
        Game gObj;
        try {
            gObj = (Game) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        if (card.compareTo(gObj.card) != 0) {
            return false;
        }
        if (coin.compareTo(gObj.coin) != 0) {
            return false;
        }
        return true;
    }
}
