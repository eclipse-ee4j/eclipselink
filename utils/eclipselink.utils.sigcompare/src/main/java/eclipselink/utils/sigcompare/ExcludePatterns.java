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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import static eclipselink.utils.sigcompare.ClassSignature.SEPARATOR;

/**
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class ExcludePatterns {

    private static final String EXCLUDE_FILE = "exclude-patterns.txt";

    private List<String> patterns;

    ExcludePatterns() {
        try {
            initialize();
        } catch (Exception e) {
            throw new RuntimeException("Could not load exclude file: " + EXCLUDE_FILE, e);
        }
    }

    List<String> getPatterns() {
        return patterns;
    }

    boolean exclude(String... names) {
        if (names == null || names.length < 1){
            throw new IllegalArgumentException("No names provided");
        }

        StringWriter writer = new StringWriter();
        for (int i = 0; i < names.length; i++) {
            writer.write(names[i]);
            writer.write(SEPARATOR);
        }

        String name = writer.toString();

        for (String pattern: getPatterns()) {
            if (name.startsWith(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Load the exclude patterns
     * @throws IOException
     */
    void initialize() throws IOException {
        this.patterns = new ArrayList<String>();

        File excludeFile = new File(EXCLUDE_FILE);

        FileReader in = new FileReader(excludeFile);
        BufferedReader br = new BufferedReader(in);
        try {
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                strLine = strLine.trim();
                if (!strLine.isEmpty() && !strLine.startsWith("#")) {
                    this.patterns.add(strLine);
                }
            }
        } finally {
            in.close();
        }
    }
}
