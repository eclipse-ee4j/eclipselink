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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

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

        String sourceFileName = properties.getProperty("source-jar");
        File sourceJarFile = new File(sourceFileName);
        Map<String, ClassSignature> sourceSigs = new SignatureImporter().importSignatures(sourceJarFile);

        String targetFileName = properties.getProperty("target-jar");
        File targetJarFile = new File(targetFileName);
        Map<String, ClassSignature> targetSigs = new SignatureImporter().importSignatures(targetJarFile);

        String reportFileName = properties.getProperty("report-file");
        FileWriter out = new FileWriter(reportFileName);

        ExcludePatterns excludes = new ExcludePatterns();
        
        for (ClassSignature sourceSig : sourceSigs.values()) {
            if (!excludes.exclude(sourceSig.getName())) {
                ClassSignature sig230 = targetSigs.get(sourceSig.getName());

                if (sig230 == null) {
                    out.write("Could not find " + sourceSig.getName());
                } else {
                    sourceSig.compare(sig230, out, excludes);
                }
            }
        }

        out.close();

        System.out.println("Done.");
    }

}
