/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import com.sun.tools.javac.Main;
import java.io.*;

import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;

/**
 *  INTERNAL
 *  <p><b>Purpose:</b>This class uses provides a commandline utility to generate schema 
 *  files based on a list of .java or .class file names. 
 *  
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Process command-line arguments</li>
 *  <li>Compile .java files if required</li>
 *  <li>Create a class[] to pass into Generator</li>
 *  </ul>
 *  
 *  <p><b>Usage</b>: SchemaGenMain &lt;opts&gt; &lt;files&gt; 
 *  <p> Allowable parameters are:<br>
 *  -ouputDir: Specifies the directory for the schema files to be written. Defaults to 
 *  current directory
 *  <p>
 *  -targetPkg: Specifies package under the output directory where the schema files should
 *  be written. Optional
 *  <p>
 *  -createPropFile: A flag indicating that a jaxb.properties file should be generated during this 
 *  schema generation operation. Optional 
 *  <p>
 *  
 *  <p>This class is used by commandline scripts to generate schema files from a list of
 *  Java source files. In order to do this generation, the files will first be compiled into
 *  a temporary folder. Intended primarily to be used by the JAXB 2.0 TCK.
 *  
 *  @author mmacivor
 *  @since Oracle TopLink 11.1.1.0.0
 *  @see org.eclipse.persistence.jaxb.compiler.JAXBClassLoader
 *  @see org.eclipse.persistence.jaxb.compiler.Generator
 */
public class SchemaGenMain {
    private String outputDir;
    private String targetPkg = "";
    private ArrayList classNames;
    private boolean createPropFile = false;
    private Generator generator;
    
    

    public static void main(String[] args) throws Exception {
        SchemaGenMain schemaGen = new SchemaGenMain();
        schemaGen.processArgs(args);
        if(schemaGen.createPropFile) {
            schemaGen.createJAXBPropertiesFile();
        } else {
            schemaGen.generateSchemas();
        }
    }
    
    public SchemaGenMain() {
        classNames = new ArrayList(10);
    }
    
    private void processArgs(String[] args) {
        int i = args.length;

        for (int j = 0; j < i; j++) {
            if (args[j].equals("-outputDir")) {
                if (j == (i - 1)) {
                    System.exit(0);
                } else {
                    this.outputDir = args[++j];
                }
                continue;
            } else if(args[j].equals("-targetPkg")) {
              if (j == (i - 1)) {
                  System.exit(0);
              } else {
                  this.targetPkg = args[++j];
              }
              continue;
            } else if (args[j].equals("-createPropFile")) {
                this.createPropFile = true;
            } else {
                classNames.add(args[j]);
            }
        }
    }
    
    public void createJAXBPropertiesFile() {
        String targetDir = outputDir + "/" + targetPkg.replace(".", "/");
        File propertiesFile = new File(targetDir + "/jaxb.properties");
        try {
            propertiesFile.createNewFile();
            FileWriter writer = new FileWriter(propertiesFile);
            writer.append("javax.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory");
            writer.flush();
            writer.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.exit(2);
        }
    }
    public void generateSchemas() throws Exception {
        generator = new Generator(new JavaModelInputImpl(getClassesFromFileNames(classNames), new JavaModelImpl()));
        generator.generateSchemaFiles(outputDir + "/" + targetPkg.replace(".", "/"), null);
    }

    public Class[] getClassesFromFileNames(ArrayList names) throws Exception {
        ArrayList<String> javaFileNames = new ArrayList<String>();
        ArrayList<String> classNames = new ArrayList<String>();
        for(int i = 0; i < names.size(); i++) {
            String fileName = (String)names.get(i);
            if(fileName.endsWith(".java")) {
                javaFileNames.add(fileName);
            } else {
                classNames.add(fileName);
            }
        }
        
        URL[] urls = null;
        String[] javacArgs = new String[javaFileNames.size() + 2];
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/tljaxbtmpdir" + System.currentTimeMillis() + "/");
        
        tempFile.mkdirs();
        tempFile.deleteOnExit();
        ArrayList<String> packageInfo = new ArrayList<String>();
        javacArgs[0] = "-d";
        javacArgs[1] = tempFile.getAbsolutePath();
        if(classNames.size() != 0) {
            Class[] classes = new Class[classNames.size()];
            for(int i = 0; i < classNames.size(); i++) {
                classes[i] = Class.forName(classNames.get(i));
            }
            return classes;
        }
        if (javaFileNames.size() != 0) {
            for (int i = 2; i < javaFileNames.size() + 2; i++) {
                String nextFile = javaFileNames.get(i - 2);

                javacArgs[i] = nextFile;
            }
            Main.compile(javacArgs);
            
            if(packageInfo.size() != 0) {
                javacArgs = new String[javaFileNames.size() + 2];
                javacArgs[0] = "-d";
                javacArgs[1] = tempFile.getAbsolutePath();
                
                for(int i = 2; i < packageInfo.size() + 2; i++) {
                    javacArgs[i] = packageInfo.get(i - 2);
                }
                Main.compile(javacArgs);
            }
            
            urls = new URL[1];
            urls[0] = tempFile.toURL();
            JAXBClassLoader loader = new JAXBClassLoader(urls);
            tempFile.delete();
            return loader.loadAllClasses();
        }
        tempFile.delete();
        return null;
    }
}
