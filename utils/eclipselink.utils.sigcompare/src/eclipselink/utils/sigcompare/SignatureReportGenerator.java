/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  dclarke - initial sig compare utility (Bug 352151)
package eclipselink.utils.sigcompare;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Generate a public signature comparison for the two provided versions
 * excluding all patterns in {@link ExcludePatterns}
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class SignatureReportGenerator {

    public void generateReport(Properties properties, String sourceVersion, String targetVersion, boolean printAlternatives) throws Exception {
        String sourceFileName = properties.getProperty(sourceVersion);
        File sourceJarFile = new File(sourceFileName);
        Map<String, ClassSignature> sourceSigs = new SignatureImporter().importSignatures(sourceJarFile);

        String targetFileName = properties.getProperty(targetVersion);
        File targetJarFile = new File(targetFileName);
        Map<String, ClassSignature> targetSigs = new SignatureImporter().importSignatures(targetJarFile);

        FileWriter out = new FileWriter("EclipseLink_"+sourceVersion + "-" + targetVersion + "_public_diff.txt");

        ExcludePatterns excludes = new ExcludePatterns();

        for (ClassSignature sourceSig : sourceSigs.values()) {
            if (!excludes.exclude(sourceSig.getName())) {
                ClassSignature targetSig = targetSigs.get(sourceSig.getName());

                if (targetSig == null) {
                    out.write(sourceSig.getName() + ClassSignature.newline);
                } else {
                    sourceSig.compare(targetSig, out, excludes, printAlternatives);
                }
            }
        }

        out.close();
    }

}
