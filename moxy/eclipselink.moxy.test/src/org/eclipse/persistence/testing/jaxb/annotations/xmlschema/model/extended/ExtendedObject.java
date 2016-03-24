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

import org.eclipse.persistence.testing.jaxb.annotations.xmlschema.model.base.BaseObject;

public class ExtendedObject extends BaseObject {
    @Override
    public boolean equals(Object o) {
        if (o instanceof ExtendedObject) {
            return super.equals(o);
        } else {
            return false;
        }
    }
}
