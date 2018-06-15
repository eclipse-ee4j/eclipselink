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

public class DetailData extends SubData {

    @Override
    public String toString() {
        String sloc = " noLoc";
        if (getLoc() != null) {
            sloc = " L" + getLoc().getLineNumber() + " C" + getLoc().getColumnNumber() + " " + getLoc().getSystemId();
        }

        return "DetailData(" + info + ")" + sloc;
    }

}
