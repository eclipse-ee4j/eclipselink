/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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