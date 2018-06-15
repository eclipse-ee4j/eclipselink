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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation;

import java.util.Calendar;

public class Period {
    Calendar _StartDate;
    Calendar _EndDate;

    public Period() {
    }

    public void setStartDate(java.util.Calendar value) {
        _StartDate = value;
    }

    public java.util.Calendar getStartDate() {
        return _StartDate;
    }

    public void setEndDate(java.util.Calendar value) {
        _EndDate = value;
    }

    public java.util.Calendar getEndDate() {
        return _EndDate;
    }
}
