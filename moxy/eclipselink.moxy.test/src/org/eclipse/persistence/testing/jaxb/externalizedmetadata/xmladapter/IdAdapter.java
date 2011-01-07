/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - October 20/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class IdAdapter extends XmlAdapter<String, Integer> {
    public IdAdapter() {}
    
    public String marshal(Integer arg0) throws Exception {
        return String.valueOf(arg0.intValue());
    }

    public Integer unmarshal(String arg0) throws Exception {
        return Integer.valueOf(arg0);
    }
}
