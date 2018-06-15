/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;

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
    @Override
    public void addDirEntry(String dirPath)throws IOException {
       File file = new File(this.target.getPath()+File.separator+dirPath).getAbsoluteFile();
       if (!file.exists()){
           if (!file.mkdirs()) {
               AbstractSessionLog.getLog().log(AbstractSessionLog.FINE, AbstractSessionLog.WEAVER,
                       ToStringLocalization.buildMessage("staticweave_processor_dir_not_created", new Object[] {file}));
           }
       }
    }

    /**
     * Write entry bytes into target, this method is usually invoked  if class has been tranformed
     * @param targetEntry
     * @param entryBytes
     * @throws IOException
     */
    @Override
    public void addEntry(JarEntry targetEntry,byte[] entryBytes)throws IOException{
        FileOutputStream fos = null;
        try {
            File target  = new File(this.target.getPath()+targetEntry.getName()).getAbsoluteFile();
            if(!target.exists()) {
                if (!target.createNewFile()) {
                    AbstractSessionLog.getLog().log(AbstractSessionLog.FINE, AbstractSessionLog.WEAVER,
                            ToStringLocalization.buildMessage("staticweave_processor_file_not_created", new Object[] {target}));
                }
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
    @Override
    public void addEntry(InputStream jis,JarEntry entry) throws IOException,URISyntaxException {
        File target  = new File(this.target.getPath()+entry.getName()).getAbsoluteFile();
        if(!target.exists()) {
            if (!target.createNewFile()) {
                AbstractSessionLog.getLog().log(AbstractSessionLog.FINE, AbstractSessionLog.WEAVER,
                        ToStringLocalization.buildMessage("staticweave_processor_file_not_created", new Object[] {target}));
            }
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
