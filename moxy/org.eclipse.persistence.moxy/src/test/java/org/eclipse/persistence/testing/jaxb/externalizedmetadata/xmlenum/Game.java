/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - January 11/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

@jakarta.xml.bind.annotation.XmlRootElement(name="hand")
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
