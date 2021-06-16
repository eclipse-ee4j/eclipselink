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
//     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.required;

import java.util.Collection;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class RequiredTestObject {

    @XmlElement(required=true)
    public String direct;                                           // XMLDirectMapping

    @XmlAttribute(required=true)
    public String directAttribute;                                  // XMLDirectMapping (attribute)

    @XmlElement(required=true)
    public Collection<String> directCollection;                     // XMLCompositeDirectCollectionMapping

    @XmlElement(required=true)
    public RequiredTestSubObject compositeObject;                   // XMLCompositeObjectMapping

    @XmlElement(required=true)
    public Collection<RequiredTestSubObject> compositeCollection;   // XMLCompositeCollectionMapping

}
