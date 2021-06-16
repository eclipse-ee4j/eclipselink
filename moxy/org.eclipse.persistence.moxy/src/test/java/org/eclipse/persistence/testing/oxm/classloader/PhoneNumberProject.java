/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.classloader;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class PhoneNumberProject extends Project {

    public PhoneNumberProject() {
        super();
        this.addDescriptor(getPhoneNumberDescriptor());
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        try {
            ClassLoader phoneNumberClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/PhoneNumber.jar");
            Class phoneNumberClass = phoneNumberClassLoader.loadClass("org.eclipse.persistence.testing.oxm.classloader.PhoneNumber");

            XMLDescriptor xmlDescriptor = new XMLDescriptor();
            xmlDescriptor.setJavaClass(phoneNumberClass);
            xmlDescriptor.setDefaultRootElement("phone-number");

            XMLDirectMapping nameMapping = new XMLDirectMapping();
            nameMapping.setAttributeName("number");
            nameMapping.setXPath("text()");
            xmlDescriptor.addMapping(nameMapping);

            return xmlDescriptor;
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
