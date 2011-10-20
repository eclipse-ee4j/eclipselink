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
 *     Praba Vijayaratnam - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlvalue;

import javax.xml.bind.annotation.*;

/*
 * Example 1: Map a class to XML Schema simpleType
 */
@XmlRootElement(name = "us-price")
public class USPrice {

	@XmlValue
	public double price;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean equals(Object o) {
		if (!(o instanceof USPrice) || o == null) {
			return false;
		} else {
			return ((USPrice) o).price == this.price;
		}
	}

}
