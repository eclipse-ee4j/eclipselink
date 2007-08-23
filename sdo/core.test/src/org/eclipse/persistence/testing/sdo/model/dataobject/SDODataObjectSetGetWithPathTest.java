/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.Property;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectSetGetWithPathTest extends SDODataObjectTestCases {
    private static final String PATH = "propertyName.0/name";
    private static final String CONTROL_STRING = "test";

    public SDODataObjectSetGetWithPathTest(String name) {
        super(name);
    }
}