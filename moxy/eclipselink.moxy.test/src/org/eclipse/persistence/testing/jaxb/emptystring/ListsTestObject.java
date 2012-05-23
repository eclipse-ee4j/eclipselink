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
*     Denise Smith - 2.3
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.emptystring;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListsTestObject {
	private List<String> strings;
	private List<Integer> integers;
	private List<BigDecimal> bigDecimals;
	
	public List<String> getStrings() {
		return strings;
	}
	public void setStrings(List<String> strings) {
		this.strings = strings;
	}
	public List<Integer> getIntegers() {
		return integers;
	}
	public void setIntegers(List<Integer> integers) {
		this.integers = integers;
	}
	public List<BigDecimal> getBigDecimals() {
		return bigDecimals;
	}
	public void setBigDecimals(List<BigDecimal> bigDecimals) {
		this.bigDecimals = bigDecimals;
	}

	public boolean equals(Object obj){
		if(!(obj instanceof ListsTestObject)){
			return false;
		}
		ListsTestObject compareObject = (ListsTestObject)obj;
		if(integers.size() != compareObject.integers.size()){
			return false;
		}
		if(!integers.containsAll(compareObject.integers)){
			return false;
		}
		
		if(bigDecimals.size() != compareObject.bigDecimals.size()){
			return false;
		}
		if(!bigDecimals.containsAll(compareObject.bigDecimals)){
			return false;
		}
		
		if(strings.size() != compareObject.strings.size()){
			return false;
		}
		if(!strings.containsAll(compareObject.strings)){
			return false;
		}
		
		return true;
	}

}
