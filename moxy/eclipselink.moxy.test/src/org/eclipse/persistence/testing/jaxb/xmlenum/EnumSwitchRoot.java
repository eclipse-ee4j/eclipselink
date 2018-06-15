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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlenum;

public class EnumSwitchRoot {

    private Department department;

    private Department foo() {
        switch(department) {
        case SALES: {
            return Department.SALES;
        }
        case SUPPORT: {
            return Department.SUPPORT;
        }
        }
        return null;
    }

}
