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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;

import org.eclipse.persistence.internal.helper.Helper;

/**
 * The class provides a set of methods to pack passed-in entries into the sepcified archive file.
 * the class handle directory output.
 */
public class StaticWeaveDirectoryOutputHandler extends AbstractStaticWeaveOutputHandler{
    private URL source=null;
    private URL target=null;
    
    
    /**
     * Construct an instance of StaticWeaveDirectoryOutputHandler.
     * @param source
     * @param target
     */
    public StaticWeaveDirectoryOutputHandler(URL source,URL target){
        this.source=source;
        this.target=target;
    }
    /**
     * create directory into target directory.
     * @param dirPath
     * @throws IOException
     */
    public void addDirEntry(String dirPath)throws IOException {
       File file = new File(this.target.getPath()+File.separator+dirPath).getAbsoluteFile();
       if (!file.exists()){
           file.mkdirs();
       }
    }
    
    /**
     * Write entry bytes into target, this method is usually invoked  if class has been tranformed
     * @param targetEntry
     * @param entryBytes
     * @throws IOException
     */
    public void addEntry(JarEntry targetEntry,byte[] entryBytes)throws IOException{
        FileOutputStream fos = null;
        try {
	    	File target  = new File(this.target.getPath()+targetEntry.getName()).getAbsoluteFile();
	        if(!target.exists()) {
	            target.createNewFile();
	        }
	        fos = new FileOutputStream(target);
	        fos.write(entryBytes);
        } finally {
        	Helper.close(fos);
        }
    }
    
    /**
     * Write entry into target, this method usually copy original class into target.
     * @param jis
     * @param entry
     * @throws IOException
     */
    public void addEntry(InputStream jis,JarEntry entry) throws IOException,URISyntaxException {    
        File target  = new File(this.target.getPath()+entry.getName()).getAbsoluteFile();
        if(!target.exists()) {
            target.createNewFile();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if((new File(Helper.toURI(this.source))).isDirectory()){
                File sourceEntry = new File(this.source.getPath()+entry.getName());
                fis = new FileInputStream(sourceEntry);
                byte[] classBytes = new byte[fis.available()];
                fis.read(classBytes);
                fos = new FileOutputStream(target);
                fos.write(classBytes);
            }else{
                readwriteStreams(jis,(new FileOutputStream(target)));
            }
        } finally {
        	Helper.close(fis);
        	Helper.close(fos);
        }
    }
}
