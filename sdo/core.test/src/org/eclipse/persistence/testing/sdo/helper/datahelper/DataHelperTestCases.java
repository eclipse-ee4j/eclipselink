/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DataHelperTestCases extends SDOTestCase {
    protected StringBuffer sb;

    public DataHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        sb = new StringBuffer();
    }

    public void makeString(String s) {
        sb.append(s);
    }

    public void clear() {
        sb.delete(0, sb.length());
    }
}