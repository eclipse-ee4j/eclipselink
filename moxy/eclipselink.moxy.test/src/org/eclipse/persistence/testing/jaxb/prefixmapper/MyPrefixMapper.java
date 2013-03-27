/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.prefixmapper;

import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.oxm.XMLConstants;

public class MyPrefixMapper extends NamespacePrefixMapper {

    @Override
    public String getPreferredPrefix(String arg0, String arg1, boolean arg2) {
        if(arg0.equals("someuri")) {
            return "newPrefix";
        }
        if(arg0.equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
            return "xsi";
        }
        return arg1;
    }
    
    @Override
    public java.lang.String[] getPreDeclaredNamespaceUris() {
        return new String[] {"extraUri"};
    }
    
    @Override
    public java.lang.String[] getPreDeclaredNamespaceUris2() {
        return new String[] {"newPrefix", "someuri", "xsi", javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "somePrefix", "my.uri"};
    }
}
