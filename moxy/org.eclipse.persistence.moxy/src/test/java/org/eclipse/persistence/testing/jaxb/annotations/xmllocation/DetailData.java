/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 19 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmllocation;

import jakarta.xml.bind.annotation.XmlElement;

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
