/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
