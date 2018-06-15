/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.6 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.transform.Source;

import org.eclipse.persistence.internal.oxm.Unmarshaller;

/**
 * This class is used to introduce new methods to the standard Source
 * interface that can be leveraged by MOXy.
 */
public abstract class ExtendedSource implements Source {

    private String systemId;

    public abstract XMLReader createReader(Unmarshaller unmarshaller);

    public abstract XMLReader createReader(Unmarshaller unmarshaller, Class unmarshalClass);

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

}
