/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
