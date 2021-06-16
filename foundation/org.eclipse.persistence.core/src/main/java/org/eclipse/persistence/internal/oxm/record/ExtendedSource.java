/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
