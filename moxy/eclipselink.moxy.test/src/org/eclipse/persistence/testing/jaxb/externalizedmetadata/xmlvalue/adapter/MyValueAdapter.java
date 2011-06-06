/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - October 27/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapts from True/False to T/F.
 *
 */
public class MyValueAdapter extends XmlAdapter<String, Boolean> {

    public Boolean unmarshal(String v) throws Exception {
        return new Boolean(v.equals("T") ? "true" : "false");
    }

    public String marshal(Boolean v) throws Exception {
        return (v ? "T" : "F"); 
    }
}