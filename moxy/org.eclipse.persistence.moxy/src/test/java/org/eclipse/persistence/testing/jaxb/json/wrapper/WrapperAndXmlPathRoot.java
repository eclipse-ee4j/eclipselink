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
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.wrapper;

import java.util.*;
import jakarta.xml.bind.annotation.*;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class WrapperAndXmlPathRoot {

    @XmlPath("personal-info/name/text()")
    public String name;

    @XmlElementWrapper
    @XmlElement(name="foo")
    public List<WrapperAndXmlPathRoot> foos = new ArrayList<WrapperAndXmlPathRoot>();

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        WrapperAndXmlPathRoot test = (WrapperAndXmlPathRoot) obj;
        if(!equals(name, test.name)) {
            return false;
        }
        return equals(foos, test.foos);
    }

    private boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        } else if(null == test) {
            return null == control;
        }
        return control.equals(test);
    }

    private boolean equals(List<?> control, List<?> test) {
        if(null == control) {
            return null == test;
        } else if(null == test) {
            return null == control;
        } else if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
