/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.currency;

public class CurrencyRange
{
	private Currency lowLimit;
	private Currency highLimit;
	private String trend;
/**
 * 
 * @return test.oracle.models.currency.Currency
 */
public Currency getHighLimit() {
	return highLimit;
}
/**
 * 
 * @return test.oracle.models.currency.Currency
 */
public Currency getLowLimit() {
	return lowLimit;
}
/**
 * 
 * @return java.lang.String
 */
public java.lang.String getTrend() {
	return trend;
}
/**
 * 
 * @param newHighLimit test.oracle.models.currency.Currency
 */
public void setHighLimit(Currency newHighLimit) {
	highLimit = newHighLimit;
}
/**
 * 
 * @param newLowLimit test.oracle.models.currency.Currency
 */
public void setLowLimit(Currency newLowLimit) {
	lowLimit = newLowLimit;
}
/**
 * 
 * @param newTrend java.lang.String
 */
public void setTrend(java.lang.String newTrend) {
	trend = newTrend;
}
}
