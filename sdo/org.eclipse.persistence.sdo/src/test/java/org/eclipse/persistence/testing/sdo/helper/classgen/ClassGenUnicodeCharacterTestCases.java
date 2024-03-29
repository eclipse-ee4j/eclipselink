/*
 * Copyright (c) 2018, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.sdo.helper.classgen;

import static org.eclipse.persistence.sdo.SDOConstants.EMPTY_STRING;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import junit.textui.TestRunner;

public class ClassGenUnicodeCharacterTestCases extends SDOClassGenTestCases {

    public ClassGenUnicodeCharacterTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.classgen.BaseTypeTestCases" };
        TestRunner.main(arguments);
    }

    @Override
    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/DeptView.xsd";
    }

    @Override
    protected String getSourceFolder() {
        return "./unicode";
    }

    @Override
    protected String getControlSourceFolder() {
        return "./org/eclipse/persistence/testing/sdo/helper/classgen/unicode";
    }

    @Override
    protected List getControlFileNames() {
        ArrayList list = new ArrayList();
        list.add("DeptView\u00c9SDO.java");
        list.add("DeptView\u00c9SDOImpl.java");
        return list;
    }

    @Override
    protected List<String> getPackages() {
        List<String> packages = new ArrayList<String>();
        packages.add("model/mbcs/common");
        packages.add("model/mbcs/common");
        return packages;
    }

    @Override
    public String getSchema(InputStream is, String fileName) {
        StringBuilder xsdSchema = new StringBuilder(EMPTY_STRING);
        try {
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            String nextLine = reader.readLine();
            while(nextLine != null) {
                xsdSchema.append(nextLine);
                nextLine = reader.readLine();
            }
            log(xsdSchema.toString());
            return xsdSchema.toString();
        } catch (Exception e) {
            log(getClass() + ": Reading error for : " + fileName + " message: " + e.getClass() + " " + e.getMessage());
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return xsdSchema.toString();
    }

}
