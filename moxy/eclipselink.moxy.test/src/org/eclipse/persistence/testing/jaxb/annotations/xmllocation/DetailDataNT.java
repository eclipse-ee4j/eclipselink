/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 19 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import javax.xml.bind.annotation.XmlElement;

public class DetailDataNT extends SubDataNT {

    @Override
    public String toString() {
        String sloc = " noLoc";
        if (locator != null) {
            sloc = " L" + locator.getLineNumber() + " C" + locator.getColumnNumber() + " " + locator.getSystemId();
        }

        return "DetailData(" + info + ")" + sloc;
    }

}
