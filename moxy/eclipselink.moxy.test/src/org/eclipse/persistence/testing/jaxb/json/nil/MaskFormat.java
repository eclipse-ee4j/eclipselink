/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Radek Felcman - April 2018 - 2.7.2
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.nil;

import javax.xml.bind.annotation.*;
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
