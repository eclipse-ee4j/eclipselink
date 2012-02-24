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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NumberFormatTestCases extends JAXBWithJSONTestCases {
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/numbers/numberHolder.json";
	private final static String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/json/numbers/numberHolderWrite.json";
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/numbers/numberHolder.xml";
	private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/json/numbers/numberHolderWrite.xml";
	
	public NumberFormatTestCases(String name) throws Exception {
		super(name);	
		setClasses(new Class[]{NumberHolder.class});
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_RESOURCE_WRITE);
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_RESOURCE_WRITE);
	}
	
	protected Object getControlObject() {
		NumberHolder nh = new NumberHolder();
		nh.intTest = 2;
		nh.integerTest = 3;
		nh.doubleTest = 2.2;
		nh.floatTest =4;
		nh.longTest = 6;
		nh.bigDecimalTest = new BigDecimal(3.456);
		nh.bigIntegerTest = new BigInteger("6");		
		nh.shortTest =5;
		List<Integer> listOfIntegers = new ArrayList<Integer>();
		listOfIntegers.add(7);
		listOfIntegers.add(9);
		nh.listIntegersTest = listOfIntegers;
		
		List<Number> listOfNumbers = new ArrayList<Number>();
		listOfNumbers.add(new BigDecimal(3));
		listOfNumbers.add(new BigDecimal("3.456"));
		listOfNumbers.add(new BigDecimal("4e1"));
		listOfNumbers.add(new BigDecimal("4e+3"));
		listOfNumbers.add(new BigDecimal("4e-3"));
		listOfNumbers.add(new BigDecimal("5E1"));
		listOfNumbers.add(new BigDecimal("6E+2"));
		listOfNumbers.add(new BigDecimal("7E-3"));
		listOfNumbers.add(new BigDecimal("8.56e56"));
		listOfNumbers.add(new BigDecimal(-3));
		listOfNumbers.add(new BigDecimal(-3.5));
		listOfNumbers.add(new BigDecimal(0));
		nh.listNumbersTest = listOfNumbers;
		return nh;
	}

}
