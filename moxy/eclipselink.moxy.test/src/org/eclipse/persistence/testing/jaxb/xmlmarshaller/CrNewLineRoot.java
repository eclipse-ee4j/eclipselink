/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class CrNewLineRoot {

    @XmlAttribute public String slashNAttribute;
    @XmlAttribute public String slashRAttribute;
    @XmlAttribute public String slashRslashNAttribute;
    @XmlAttribute public String slashNslashRAttribute;

    public String slashNElement;
    public String slashRElement;
    public String slashRslashNElement;
    public String slashNslashRElement;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        CrNewLineRoot test = (CrNewLineRoot) obj;
        if(!equals(slashNAttribute, test.slashNAttribute)) {
            return false;
        }
        if(!equals(slashRAttribute, test.slashRAttribute)) {
            return false;
        }
        if(!equals(slashRslashNAttribute, test.slashRslashNAttribute)) {
            return false;
        }
        if(!equals(slashNslashRAttribute, test.slashNslashRAttribute)) {
            return false;
        }
        if(!equals(slashNElement, test.slashNElement)) {
            return false;
        }
        if(!equals(slashRElement, test.slashRElement)) {
            return false;
        }
        if(!equals(slashRslashNElement, test.slashRslashNElement)) {
            return false;
        }
        if(!equals(slashNslashRElement, test.slashNslashRElement)) {
            return false;
        }
        return true;
    }

    public boolean equals(String control, String test) {
        if(control == test) {
            return true;
        } else if(null == control || null == test) {
            return false;
        } else {
            return control.equals(test);
        }
    }

}
