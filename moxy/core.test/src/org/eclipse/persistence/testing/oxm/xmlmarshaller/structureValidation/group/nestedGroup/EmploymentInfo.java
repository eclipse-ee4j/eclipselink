/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedGroup;

import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.Period;

public class EmploymentInfo {
    int _Id;
    Period _G2;

    public EmploymentInfo() {
        _G2 = new Period();
    }

    public int getId() {
        return _Id;
    }

    public void setId(int value) {
        _Id = value;
    }

    public Period getG2() {
        return _G2;
    }

    public void setG2(Period value) {
        _G2 = value;
    }
}