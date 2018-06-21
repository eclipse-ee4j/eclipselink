/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.required;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

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
