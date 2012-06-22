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
package org.eclipse.persistence.testing.oxm.schemamodelgenerator.required;

import java.util.Collection;

public class RequiredTestObject {

    public String direct;                                           // XMLDirectMapping
    public String directAttribute;                                  // XMLDirectMapping (attribute)
    public Collection<String> directCollection;                     // XMLCompositeDirectCollectionMapping
    public RequiredTestSubObject compositeObject;                   // XMLCompositeObjectMapping
    public Collection<RequiredTestSubObject> compositeCollection;   // XMLCompositeCollectionMapping

}