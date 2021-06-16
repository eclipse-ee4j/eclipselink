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
// Matt MacIvor - 2.4
package org.eclipse.persistence.testing.jaxb.sun.prefixmapper;

import org.eclipse.persistence.oxm.XMLConstants;

public class NonELPrefixMapperImpl extends NonELPrefixMapperSuperClass {
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

