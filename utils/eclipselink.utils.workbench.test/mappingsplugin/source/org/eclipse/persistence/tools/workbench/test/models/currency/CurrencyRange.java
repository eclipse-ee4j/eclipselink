/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.currency;

public class CurrencyRange
{
	private Currency lowLimit;
	private Currency highLimit;
	private String trend;
/**
 * 
 * @return org.eclipse.persistence.tools.workbench.test.models.currency.Currency
 */
public Currency getHighLimit() {
	return highLimit;
}
/**
 * 
 * @return org.eclipse.persistence.tools.workbench.test.models.currency.Currency
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
 * @param newHighLimit org.eclipse.persistence.tools.workbench.test.models.currency.Currency
 */
public void setHighLimit(Currency newHighLimit) {
	highLimit = newHighLimit;
}
/**
 * 
 * @param newLowLimit org.eclipse.persistence.tools.workbench.test.models.currency.Currency
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
