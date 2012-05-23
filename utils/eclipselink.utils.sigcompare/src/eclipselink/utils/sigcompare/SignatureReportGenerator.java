/*******************************************************************************
 * Copyright (c) 2010-2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - initial sig compare utility (Bug 352151)
 ******************************************************************************/
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
