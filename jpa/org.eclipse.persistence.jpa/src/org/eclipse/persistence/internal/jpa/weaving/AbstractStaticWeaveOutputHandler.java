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
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * The abstract class provides a set of methods to out outputs into the sepcified archive file.
 */
public abstract class AbstractStaticWeaveOutputHandler{
    protected JarOutputStream outputStreamHolder=null;
    
    /**
     * create directory into target directory, or insert directory entry into outputstream.
     * @param dirPath
     * @throws IOException
     */
    abstract public void addDirEntry(String dirPath)throws IOException;
    
    /**
     * Write entry bytes into target, this is usually called if class has been tranformed
     * @param targetEntry
     * @param entryBytes
     * @throws IOException
     */
    abstract public void addEntry(JarEntry targetEntry,byte[] entryBytes)throws IOException;
    
    /**
     * Write entry into target, this method usually copy original class into target.
     * @param jis
     * @param entry
     * @throws IOException
     */
    abstract public void addEntry(InputStream jis,JarEntry entry) throws IOException,URISyntaxException;

    
    /**
     * Close the output stream.
     * @throws IOException
     */
    public void closeOutputStream() throws IOException {
        if(outputStreamHolder!=null){
            outputStreamHolder.close();
        }
    }
    
    /**
     * Get the ouput stream instance.
     * @return
     */
    public JarOutputStream getOutputStream(){
        return this.outputStreamHolder;
    }


    // This is part of the ugly workaround for a design flaw
    // in the JDK zip API, the entry will not write into the target zip file 
    // properly if this method not being gone through.
    protected void readwriteStreams(InputStream in, OutputStream out) throws IOException
    {
        int numRead;
        byte[] buffer = new byte[8*1024];

        while ((numRead = in.read(buffer,0,buffer.length)) != -1) {
            out.write(buffer,0,numRead);
        }   
    }
}
