/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - April 2018 - 2.7.2
package org.eclipse.persistence.testing.jaxb.json.nil;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name = "format")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaskFormat {

    @XmlElementWrapper(name = "formatEntries")
    @XmlElement(name = "formatEntry")
    private ArrayList<MaskFormatEntry> formatEntries;

    public ArrayList<MaskFormatEntry> getFormatEntries() {
        return this.formatEntries;
    }

    public void setFormatEntries(ArrayList<MaskFormatEntry> formatEntries) {
        this.formatEntries = formatEntries;
    }

    public boolean equals(Object obj) {
        MaskFormat maskFormat = (MaskFormat) obj;

        if((formatEntries == null && maskFormat.getFormatEntries() != null) || (formatEntries != null && !formatEntries.equals(maskFormat.getFormatEntries()))){
            return false;
        }
        return true;
    }

}
