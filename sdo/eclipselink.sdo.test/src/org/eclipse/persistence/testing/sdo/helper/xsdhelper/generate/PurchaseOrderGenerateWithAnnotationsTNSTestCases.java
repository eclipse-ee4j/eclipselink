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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.generate;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

public class PurchaseOrderGenerateWithAnnotationsTNSTestCases extends PurchaseOrderGenerateWithAnnotationsTestCases {
    public PurchaseOrderGenerateWithAnnotationsTNSTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(PurchaseOrderGenerateWithAnnotationsTNSTestCases.class);
    }

    public String getControlFileName() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotationsGeneratedTNS.xsd";
    }
    
    public String getControlFileNameDifferentOrder() {
        return "org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotationsGeneratedTNSRoundTrip.xsd";
    }

    public java.util.List defineTypesFromSchema() {
        InputStream is = getSchemaInputStream("org/eclipse/persistence/testing/sdo/schemas/PurchaseOrderWithAnnotationsTNS.xsd");
        return ((SDOXSDHelper)xsdHelper).define(is, null);
    }

    public String getControlUri() {
        return "my.uri";
    }
}
