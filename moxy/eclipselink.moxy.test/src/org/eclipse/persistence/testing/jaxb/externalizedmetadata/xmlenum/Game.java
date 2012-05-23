/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 11/2010 - 2.0 - Initial implementation
 ******************************************************************************/
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