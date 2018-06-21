/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;
// Example 2
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
     public class XmlEnumValueConstantNameValue {

         private Coin coin;
        private Card card = Card.CLUBS;

        public Coin getCoin() {
            return coin;
        }

        public void setCoin(Coin coin) {
            this.coin = coin;
        }

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

         public boolean equals(Object object) {
             XmlEnumValueConstantNameValue example = ((XmlEnumValueConstantNameValue)object);
            return example.coin.equals(this.coin) && example.card.equals(this.card);
        }
     }
