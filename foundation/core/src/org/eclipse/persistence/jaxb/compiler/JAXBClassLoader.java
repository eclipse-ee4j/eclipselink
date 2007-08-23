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

import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.io.*;


/**
 * INTERNAL:
 * <p><b>Purpose:</b>To allow all classes in a specified directory to be loaded at once
 * without needing to know their fully qualified names in advance
 * 
 * This class will iterate over a collection of URLs to load classes. If any of the URLs 
 * represent a directory, the loader will recursivly walk the directory hierarchy 
 * loading any .class files it finds along the way
 * 
 * @see org.eclipse.persistence.jaxb.compiler.SchemaGenMain
 * @since Oracle TopLink 11.1.1.0.0
 * @author mmacivor
 *
 */
public class JAXBClassLoader extends URLClassLoader {
    
    public JAXBClassLoader(URL[] urls) {
        super(urls);
    }
    
    /*
     * Iterates over the list of URLs. For any that represent a directory, 
     * traverse the file system, and find all class files, and load the
     * relevant classes
     */
    public Class[] loadAllClasses() {
        URL[] urls = this.getURLs();
        ArrayList classNames = new ArrayList();
        for(int i = 0; i < urls.length; i++) {
            URL next = urls[i];
            File nextFile = new File(next.getFile());
            if(nextFile.exists() && nextFile.isDirectory()) {
                appendClassNames(nextFile, classNames, "");
            }
        }
        Class[] classes = new Class[classNames.size()];
        for(int i = 0; i < classNames.size(); i++) {
            try {
                System.out.println(classNames.get(i));
                classes[i] = loadClass((String)classNames.get(i));
            } catch(ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return classes;
    }
        
        
    public void appendClassNames(File directory, ArrayList classNames, String currentPackage) {
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length; i++) {
            File next = files[i];
            if(next.isDirectory()) {
                String nextPackage = currentPackage;
                if(nextPackage.equals("")) {
                    nextPackage = next.getName();
                } else {
                    nextPackage = nextPackage + "." + next.getName();
                }
                appendClassNames(next, classNames, nextPackage);
            } else if(next.getName().endsWith(".class")) {
                String className = next.getName().substring(0, next.getName().indexOf("."));
                if(next.getName().equals("package-info.class")) {
                    try {
                        this.loadClass(className);
                    }catch(Exception ex) {}
                } else if(currentPackage.equals("")) {
                    classNames.add(className);
                } else {
                    classNames.add(currentPackage + "." + className);
                }
            }
        }
    }
}


