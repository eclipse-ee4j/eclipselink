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
 *     rbarkhouse - 2009-08-13 13:49:00 - initial implementation
 ******************************************************************************/
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