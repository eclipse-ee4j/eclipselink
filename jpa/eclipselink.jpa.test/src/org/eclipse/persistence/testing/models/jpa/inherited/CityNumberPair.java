/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - Test for bug 282523
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public interface CityNumberPair {

    public String getCity() ;

    public int getNumber();

    public void setCity(String city);

    public void setNumber(int number);
}
