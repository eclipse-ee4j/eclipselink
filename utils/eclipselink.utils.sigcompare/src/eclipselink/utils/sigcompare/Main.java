/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  dclarke - initial sig compare utility (Bug 352151)
package eclipselink.utils.sigcompare;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Generate report for specific versions
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class Main {

    private static final String PROPS_FILE = "sig-compare.properties";

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();

        File propsFile = new File(PROPS_FILE);
        if (!propsFile.exists()) {
            throw new RuntimeException("Could not find file: " + PROPS_FILE);
        }
        InputStream in = new FileInputStream(propsFile);
        properties.load(in);
        in.close();

        String printAlternativesValue = properties.getProperty("print-alternatives");
        String compareBaseLine = properties.getProperty("compare-latest");
        if (null != compareBaseLine && "true".equals(compareBaseLine)) {
            SignatureReportGenerator gen = new SignatureReportGenerator();
            gen.generateReport(properties, "baseline", "latest", false);
        } else {
            boolean printAlternatives = printAlternativesValue == null ? false : Boolean.valueOf(printAlternativesValue);

            SignatureReportGenerator gen = new SignatureReportGenerator();
            gen.generateReport(properties, "2.1.2", "2.2.0", printAlternatives);
            gen.generateReport(properties, "2.1.2", "2.3.0", printAlternatives);
            gen.generateReport(properties, "2.2.0", "2.3.0", printAlternatives);
        }
        System.out.println("Done.");
    }

}
