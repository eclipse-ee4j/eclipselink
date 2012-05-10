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
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DataHelperTestCases extends SDOTestCase {
    protected StringBuffer sb;

    public DataHelperTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        sb = new StringBuffer();
    }

    public void makeString(String s) {
        sb.append(s);
    }

    public void clear() {
        sb.delete(0, sb.length());
    }
}
