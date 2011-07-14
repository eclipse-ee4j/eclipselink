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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


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
            writer.write("::");
        }
        
        String name = writer.toString();
        
        for (String pattern: getPatterns()) {
            if (name.startsWith(pattern)) {
                return true;
            }
        }
        
        return false;
    }

    void initialize() throws IOException {
        this.patterns = new ArrayList<String>();
        
        File excludeFile = new File(EXCLUDE_FILE);
        
        FileReader in = new FileReader(excludeFile);
        BufferedReader br = new BufferedReader(in);
        try {
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                strLine = strLine.trim();
                if (!strLine.startsWith("#")) {
                    this.patterns.add(strLine);
                }
            }
        } finally {
            in.close();
        }
    }
}
