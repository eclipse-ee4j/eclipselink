/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Represents a wrapped Employee.  Allows dummy wrapper policy could be
 * implemented for testing.
 * Added for bugs 2612366 and 2766379.
 * @author Stephen McRitchie
 */
public class WrappedEmployee extends Employee {
    public WrappedEmployee() {
    }

    public String getFirstName() {
        throw new RuntimeException("Trying to access a wrapped object.");
    }

    public String getLastName() {
        throw new RuntimeException("Trying to access a wrapped object.");
    }
}