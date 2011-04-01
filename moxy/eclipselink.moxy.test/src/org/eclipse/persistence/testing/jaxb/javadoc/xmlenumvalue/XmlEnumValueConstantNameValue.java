/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;
// Example 2
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

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
