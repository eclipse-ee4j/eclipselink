/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p><b>Purpose</b>:Used in conjunction with SDOClassGenerator.  FileCodeWriter will be
 * used by default with SDOClassGenerator unless another CodeWriter is
 * specified by the user.  Writes files to the file system.
 * @see org.eclipse.persistence.sdo.helper.SDOClassGenerator
 * @see org.eclipse.persistence.sdo.helper.CodeWriter
 */
public class FileCodeWriter implements CodeWriter {
    private static final String fsep = System.getProperty("file.separator");

    /** The source directory to write the generated files to.  This will be prepended to the package. */
    private String sourceDir;

    public FileCodeWriter() {
    }

    /**
     * <p>Called from org.eclipse.persistence.sdo.helper.SDOClassGenerator for each generated interface if a
     * CodeWriter was passed into the generate method.
     *
     * @param dir The directory corresponding to the package of the generated source file
     * @param filename The name of the generated source file including the .java extension
     * @param content StringBuffer containing the contents of the generated interface.
     */
    public void writeInterface(String dir, String filename, StringBuffer content) {
        writeFile(dir, filename, content);
    }

    /**
       * <p>Called from org.eclipse.persistence.sdo.helper.SDOClassGenerator for each generated interface if a
       * CodeWriter was passed into the generate method.
       *
       * @param dir The directory corresponding to the package of the generated source file
       * @param filename The name of the generated source file including the .java extension
       * @param content StringBuffer containing the contents of the generated implementation class.
       */
    public void writeImpl(String dir, String filename, StringBuffer content) {
        writeFile(dir, filename, content);
    }

    /**
      * <p>Called by both writeInterface and writeImpl
      * @param dir The directory corresponding to the package of the generated source file
      * param filename The name of the generated source file including the .java extension
      * @param content StringBuffer containing the contents of the generate Java file
       */
    public void writeFile(String dir, String filename, StringBuffer content) {
        if (sourceDir != null) {
            dir = sourceDir + fsep + dir;
        }
        FileOutputStream outStream = null;
        try {
            File directory = new File(dir);
            directory.mkdirs();
            File file = new File(dir, filename);
            outStream = new FileOutputStream(file);
            byte[] bytes = new String(content).getBytes();
            outStream.write(bytes);
            outStream.flush();          
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
              outStream.close();
            }catch(IOException e){
              e.printStackTrace();
            }
        }
    }

    /**
    * Sets the sourceDir variable.
    * @param sourceDir
    */
    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    /**
    * Sets the sourceDir variable.
    * @param The specified source directory. This will be prepended to the package.
    */
    public String getSourceDir() {
        return sourceDir;
    }
}
