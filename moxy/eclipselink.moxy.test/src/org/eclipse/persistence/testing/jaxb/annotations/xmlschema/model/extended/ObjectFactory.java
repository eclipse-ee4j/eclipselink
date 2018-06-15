/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Ondrej Cerny
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
