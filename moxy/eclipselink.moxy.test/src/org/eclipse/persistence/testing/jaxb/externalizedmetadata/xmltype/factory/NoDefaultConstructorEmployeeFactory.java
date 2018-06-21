/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//  - rbarkhouse - 08 November 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.factory;

public class NoDefaultConstructorEmployeeFactory {

    private String i;

    public NoDefaultConstructorEmployeeFactory(String info) {
        i = info;
    }

    public static Employee getNewEmployee() {
        Employee emp = new Employee();
        emp.fromFactoryMethod = true;
        return emp;
    }

}
