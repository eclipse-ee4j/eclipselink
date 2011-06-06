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
 
package org.eclipse.persistence.testing.sdo.util;

//import com.sun.tools.javac.Main;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class CompileUtil {

	private static CompileUtil _instance;
	
	private CompileUtil() {
	}
	
	public static synchronized CompileUtil instance() {
		if (_instance == null) {
			_instance = new CompileUtil();
		}
		return _instance;
	}

	public int compile(String classpath, Object[] javaFiles) {
        String[] args = new String[javaFiles.length + 3];
        args[0] = "javac";
        args[1] = "-cp";
        args[2] = classpath;
        System.arraycopy(javaFiles, 0, args, 3, javaFiles.length);

        int exitVal = -1;

		try {
			Process proc = Runtime.getRuntime().exec(args);
            InputStream stderr = proc.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            if (line != null) {
                System.out.println("<ERROR>");   
                while (line != null) {
                    System.out.println(line);
                    if ((line = br.readLine()) == null) {
                        System.out.println("</ERROR>");
                    }
                }
            }
            
            exitVal = proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return exitVal;
	}

    public int compileOld(String classpath, Object[] javaFiles) {
        String[] args = new String[javaFiles.length + 2];
        args[0] = "-cp";
        args[1] = classpath;
        System.arraycopy(javaFiles, 0, args, 2, javaFiles.length);

        //int returnVal = Main.compile(args);
        //return returnVal;
        return -1;
    }
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(); 
	}
}
