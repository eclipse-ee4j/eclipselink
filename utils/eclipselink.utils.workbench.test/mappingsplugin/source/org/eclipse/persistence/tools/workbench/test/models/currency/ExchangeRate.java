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

public class ExchangeRate
{
	private Integer id;
	private CurrencyRange currencyRange;
	private Integer periodLength;
/**
 * 
 * @return org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange
 */
public CurrencyRange getCurrencyRange() {
	return currencyRange;
}
/**
 * 
 * @return java.lang.Integer
 */
public java.lang.Integer getId() {
	return id;
}
/**
 * 
 * @return java.lang.Integer
 */
public java.lang.Integer getPeriodLength() {
	return periodLength;
}
/**
 * 
 * @param newCurrencyRange org.eclipse.persistence.tools.workbench.test.models.currency.CurrencyRange
 */
public void setCurrencyRange(CurrencyRange newCurrencyRange) {
	currencyRange = newCurrencyRange;
}
/**
 * 
 * @param newId java.lang.Integer
 */
public void setId(java.lang.Integer newId) {
	id = newId;
}
/**
 * 
 * @param newPeriodLength java.lang.Integer
 */
public void setPeriodLength(java.lang.Integer newPeriodLength) {
	periodLength = newPeriodLength;
}
}
