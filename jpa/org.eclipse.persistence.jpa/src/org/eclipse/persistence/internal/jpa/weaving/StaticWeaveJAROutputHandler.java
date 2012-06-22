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
package org.eclipse.persistence.internal.jpa.weaving;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * The class provides a set of methods to pack passing in entries into the sepcified archive file.
 * the class JAR output.
 */
public class StaticWeaveJAROutputHandler extends AbstractStaticWeaveOutputHandler{

    /**
     * Construct an instance of StaticWeaveJAROutputHandler
     * @param outputStreamHolder
     */
    public StaticWeaveJAROutputHandler(JarOutputStream outputStreamHolder){
        super.outputStreamHolder=outputStreamHolder;
    }
    
    /**
     * Add directory entry into outputstream.
     * @param dirPath
     * @throws IOException
     */
    public void addDirEntry(String dirPath)throws IOException {
        // no need to specifically add a directory entry.  directories will be created
        // as files are added
    }
    
    /**
     * Write entry bytes into target, this method is usually called if class has been tranformed
     * @param targetEntry
     * @param entryBytes
     * @throws IOException
     */
    public void addEntry(JarEntry targetEntry,byte[] entryBytes)throws IOException{
        outputStreamHolder.putNextEntry(targetEntry);
        if(entryBytes!=null){
            outputStreamHolder.write(entryBytes);
        }
        outputStreamHolder.closeEntry();
    }
    
    /**
     * Write entry into target, this method usually copy original class into target.
     * @param jis
     * @param entry
     * @throws IOException
     */
    public void addEntry(InputStream jis,JarEntry entry) throws IOException,URISyntaxException {    
        outputStreamHolder.putNextEntry(entry);
        if(!entry.isDirectory()){
           readwriteStreams(jis,outputStreamHolder);
        }
        outputStreamHolder.closeEntry();
    }
}
