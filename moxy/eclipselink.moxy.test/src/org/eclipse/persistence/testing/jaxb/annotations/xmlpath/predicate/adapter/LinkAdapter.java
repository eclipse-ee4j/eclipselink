/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 04 May 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import static org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter.CustomerTestCases.STREET;
import static org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter.CustomerTestCases.ALT_STREET;

import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.Address;

public class LinkAdapter extends XmlAdapter<String, Object> {

    @Override
    public String marshal(Object arg0) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (arg0 instanceof Address) {
            Address a = (Address) arg0;
            sb.append(a.getStreet());
        } else {
            PhoneNumber p = (PhoneNumber) arg0;
            sb.append(p.getAreaCode());
            sb.append(p.getNumber());
        }
        return sb.toString();
    }

    @Override
    public Object unmarshal(String arg0) throws Exception {
        if (arg0.equals(STREET) || arg0.equals(ALT_STREET)) {
            Address a = new Address();
            a.setStreet(arg0);
            return a;
        } else {
            PhoneNumber p = new PhoneNumber();
            p.setAreaCode(Integer.valueOf(arg0.substring(0, 3)));
            p.setNumber(Integer.valueOf(arg0.substring(3)));
            return p;
        }
    }
}
