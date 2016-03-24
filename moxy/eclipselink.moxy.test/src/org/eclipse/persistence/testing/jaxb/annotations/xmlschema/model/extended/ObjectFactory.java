/*******************************************************************************
* Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Ondrej Cerny
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlschema.model.extended;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public ExtensionWrapper createExtensionWrapper() {
        return new ExtensionWrapper();
    }

    public ExtendedObject createExtendedObject() {
        return new ExtendedObject();
    }
}
