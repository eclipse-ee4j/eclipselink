/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Denise Smith - September 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MyAdapter extends XmlAdapter<String, Something> {     
    public MyAdapter() {
    }
    
    @Override
    public Something unmarshal(String v) throws Exception {        
        return new Something(v);
    }

    @Override
    public String marshal(Something v) throws Exception {        
        return v.value;
    }
}