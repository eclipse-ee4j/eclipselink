/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Example MOXy XJC implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.xjc;

import java.io.File;
import java.io.FileWriter;

import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.*;

/**
 * Custom XJC implementation that ensures the EclipseLink MOXy-specific
 * jaxb.properties file is generated in the package where the classes are
 * created.
 * 
 * @author dclarke
 * @author rbarkhouse
 * @since EclipseLink 1.1.2
 */
public class MOXyXJC {

    public static void main(String[] args) throws Throwable {
        Listener listener = new Listener();

        int compileSuccess = -1;
        String[] arguments = args;
        
        System.out.println("args:" + args);
        if(args[0].equals("-empty_output")) {
            arguments = new String[args.length - 1];
            for(int i = 1; i < args.length; i++) {
                arguments[i - 1] = args[i];
            }
        }
        System.out.println("arguments:" + arguments);
        try {
            compileSuccess = Driver.run(arguments, listener);
        } catch (BadCommandLineException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
                System.out.println();
            }
            Driver.usage(e.getOptions(), false);
            System.exit(1);
        }

        if (compileSuccess == 0) {
            String destDir = ".";
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-d")) {
                    destDir = args[i + 1];
                    break;
                }
            }

            if(listener.getGeneratedPackagePath() != null) {
                File jaxbPropsFile = new File(destDir + File.separator + listener.getGeneratedPackagePath(), "jaxb.properties");
                FileWriter writer = new FileWriter(jaxbPropsFile);
                writer.write("javax.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory");
                listener.generatedFile(listener.getGeneratedPackagePath() + File.separator + "jaxb.properties", 0, 0);
                writer.close();
            }
        }

        System.exit(compileSuccess);
    }

    static class Listener extends XJCListener {

        private ConsoleErrorReporter cer = new ConsoleErrorReporter(System.err);
        private String generatedPackagePath = null;

        public void generatedFile(String fileName, int count, int total) {
            message(fileName);
            if (this.generatedPackagePath == null) {
                this.generatedPackagePath = fileName.substring(0, fileName.lastIndexOf(File.separator));
            }
        }

        public String getGeneratedPackagePath() {
            return generatedPackagePath;
        }

        public void message(String msg) {
            System.out.println(msg);
        }

        public void error(SAXParseException exception) {
            cer.error(exception);
        }

        public void fatalError(SAXParseException exception) {
            cer.fatalError(exception);
        }

        public void warning(SAXParseException exception) {
            cer.warning(exception);
        }

        public void info(SAXParseException exception) {
            cer.info(exception);
        }

    }

}