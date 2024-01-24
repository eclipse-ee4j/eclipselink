/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;


public class LoadAndSaveImportsWithInheritanceTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveImportsWithInheritanceTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveImportsWithInheritanceTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return "SchemaA.xsd";
    }

    @Override
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/importswithinheritance/c-type.xml");
    }


    @Override
    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/importswithinheritance/c-type_noschema.xml");
    }

    @Override
    protected String getControlRootURI() {
        return "SchemaCNamespace";
    }

    @Override
    protected String getControlRootName() {
        return "childType";
    }

    @Override
    protected String getRootInterfaceName() {
        return "ChildType";
    }

    @Override
    protected List defineTypes() {
        try {
            URL url = new URL(getSchemaLocation() + getSchemaName());
            InputStream is = url.openStream();
            return xsdHelper.define(is, getSchemaLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getSchemaLocation() {
        return FILE_PROTOCOL + USER_DIR + "/org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/importswithinheritance/";
    }

    @Override
    protected List getPackages() {
        List packages = new ArrayList();
        packages.add("schemacnamespace");
        packages.add("schemaanamespace");
        packages.add("schemabnamespace");
        return packages;
    }

    @Override
    protected void generateClasses(String tmpDirName) throws Exception {
        URL url = new URL(getSchemaLocation() + getSchemaName());
        InputStream is = url.openStream();
        SDOClassGenerator classGenerator = new SDOClassGenerator(aHelperContext);
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        schemaResolver.setBaseSchemaLocation(getSchemaLocation());
        StreamSource ss = new StreamSource(is);
        classGenerator.generate(ss, tmpDirName, schemaResolver);
    }

    @Override
    public void registerTypes() {

        Type stringType = typeHelper.getType("commonj.sdo", "String");

        DataObject rootType = defineType("SchemaANamespace", "RootType");
        DataObject someElementProp = addProperty(rootType, "someElement", stringType);
        someElementProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        Type rootSDOType = typeHelper.define(rootType);

        DataObject middleType = defineType("SchemaBNamespace", "MiddleType");
        List baseTypes = new ArrayList();
        baseTypes.add(rootSDOType);
        middleType.set("baseType", baseTypes);
        DataObject someMiddleElementProp = addProperty(middleType, "someMiddleElement", stringType);
        someMiddleElementProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        Type middleSDOType = typeHelper.define(middleType);

        DataObject childType = defineType("SchemaCNamespace", "ChildType");
        baseTypes = new ArrayList();
        baseTypes.add(middleSDOType);
        childType.set("baseType", baseTypes);
        DataObject someChildElementProp = addProperty(childType, "someChildElement", stringType);
        someChildElementProp.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        Type childSDOType = typeHelper.define(childType);
    }
}
