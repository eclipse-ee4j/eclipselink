/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
