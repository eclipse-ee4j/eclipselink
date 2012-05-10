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
 *     Praba Vijayaratnam - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlenumvalue;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MyEnumModel {

	private MyEnum e;

	public void setMyEnum(MyEnum myEnum) {
		e = myEnum;

	}

	public MyEnum getMyEnum() {
		return e;
	}

	public boolean equals(Object object) {
		MyEnumModel example = ((MyEnumModel) object);
		return example.e.equals(this.e);
	}
}
