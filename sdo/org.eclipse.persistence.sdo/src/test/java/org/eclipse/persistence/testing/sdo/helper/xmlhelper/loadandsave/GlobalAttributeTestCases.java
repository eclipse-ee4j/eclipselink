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
//     bdoughan - September 10/2010 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import java.util.ArrayList;
import java.util.List;

public class GlobalAttributeTestCases extends LoadAndSaveTestCases {

    public GlobalAttributeTestCases(String name) {
        super(name);
    }

    @Override
    protected String getRootInterfaceName() {
        return "Root";
    }

    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add(NON_DEFAULT_JAVA_PACKAGE_DIR);
        return packages;
    }

    @Override
    protected String getSchemaName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/GlobalAttributes.xsd");
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/GlobalAttributes.xml");
    }

    @Override
    protected void registerTypes() {
        defineTypes();
    }

    @Override
    protected String getControlRootName() {
        return "root";
    }

}
