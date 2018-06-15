/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - December 17/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.elementref;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MyAdapter extends XmlAdapter<AbstractAdd, String> {
    private static String SALES = "salesOrderAdd";
    private static String INVOICE = "invoiceAdd";
    private static String SALES_TEXT = "Sales Order Text";
    private static String INVOICE_TEXT = "Invoice Text";

    public MyAdapter() {
    }

    @Override
    public String unmarshal(AbstractAdd v) throws Exception {
        if (v instanceof SalesOrderAdd) {
            return SALES;
        }
        return INVOICE;
    }

    @Override
    public AbstractAdd marshal(String v) throws Exception {
        if (SALES.equals(v)) {
            return new SalesOrderAdd(SALES_TEXT);
        }
        return new InvoiceAdd(INVOICE_TEXT);
    }
}
