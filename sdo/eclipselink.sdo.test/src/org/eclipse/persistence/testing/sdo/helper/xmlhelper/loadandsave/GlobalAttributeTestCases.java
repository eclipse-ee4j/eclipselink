/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - September 10/2010 - Initial implementation
******************************************************************************/
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
