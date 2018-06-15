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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.Column;

import javax.persistence.*;

@Embeddable
public class BarCode {

    protected String codeNumber;

    @Column(insertable=false, updatable=false)
    protected String countryCode;

    public BarCode() {
        super();
    }

    public BarCode(String codeNumber, String countryCode) {
        super();
        setCodeNumber(codeNumber);
        setCountryCode(countryCode);
    }

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
