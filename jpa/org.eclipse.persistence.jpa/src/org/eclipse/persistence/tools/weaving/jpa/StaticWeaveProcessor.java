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
package org.eclipse.persistence.tools.weaving.jpa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.exceptions.StaticWeaveException;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.internal.jpa.weaving.AbstractStaticWeaveOutputHandler;
import org.eclipse.persistence.tools.weaving.jpa.StaticWeaveClassTransformer;
import org.eclipse.persistence.internal.jpa.weaving.StaticWeaveDirectoryOutputHandler;
import org.eclipse.persistence.internal.jpa.weaving.StaticWeaveJAROutputHandler;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Description</b>: The StaticWeaveProcessor controls the static weaving process.  It is invoked by both the command line 
 * StaticWeave class and the StaticWeaveAntTask. 
 * <p>
 * <b>Responsibilities</b>: Process the source classes, performs weaving as necessary out outputs to the target
 */
@SuppressWarnings("deprecation")
public class StaticWeaveProcessor {
    private URL source;
    private URL target;
    private URL persistenceInfo;
    private String persistenceXMLLocation;
    private Writer logWriter;
    private ClassLoader classLoader;
    private int logLevel = SessionLog.OFF; 
    
    private static final int NUMBER_OF_BYTES = 1024;
    
    /**
     * Constructs an instance of StaticWeaveProcessor
     * @param source the name of the location to be weaved
     * @param target the name of the location to be weaved to
     * @throws MalformedURLException
     */
    public StaticWeaveProcessor(String source, String target)throws MalformedURLException{
        if (source != null) {
            this.source=new File(source).toURL();
        }
        if (target != null) {
            this.target=new File(target).toURL();
        }
    }

    /**
     * Constructs an instance of StaticWeaveProcessor
     * @param source the File object of the source to be weaved
     * @param target the File object of the target to be weaved to
     * @throws MalformedURLException
     */
    public StaticWeaveProcessor(File source, File target)throws MalformedURLException {
        this.source=source.toURL();
        this.target=target.toURL();
    }
    
    /**
     * Constructs an instance of StaticWeaveProcessor
     * @param source the URL of the source to be weaved
     * @param target the URL of the target to be weaved to
     */
    public StaticWeaveProcessor(URL source, URL target){
        this.source = source;
        this.target = target;
    }
    
    /**
     * The method allows user to specify the output for the log message.
     * @param log writer - the location where the log message writes to. the default value is standard out
     */
    public void setLog(Writer logWriter){
        this.logWriter = logWriter;
    }
    
    /**
     * The method allows user to define nine levels EclipseLink logging.
     * @param level - the integer value of log level. default is OFF.
     */
    public void setLogLevel(int level){
        this.logLevel = level;
    }
    
    /**
     * Set the user classloader.
     */
    public void setClassLoader(ClassLoader classLoader){
        this.classLoader=classLoader;
    }
    
    /**
     * Set an explicitly identified URL of the location containing persistence.xml.
     * @param persistenceInfo the URL of the location containing persistence.xml, the URL 
     * must point to the root of META-INF/persistence.xml
     */
    public void setPersistenceInfo(URL persistenceInfo){
        this.persistenceInfo = persistenceInfo;
    }

    /**
     * Set an explicitly identified the location containing persistence.xml.
     * @param persistenceinfo the path of the location containing persistence.xml, the path 
     * must point to the root of META-INF/persistence.xml
     */
    public void setPersistenceInfo(String persistenceInfoPath) throws MalformedURLException{
        if (persistenceInfoPath != null){
            this.persistenceInfo = new File(persistenceInfoPath).toURL();
        }
    }

    public String getPersistenceXMLLocation() {
        return persistenceXMLLocation;
    }

    /**
     * Set a specific location to look for persistence.xml
     * by default we will look in META-INF/persistence.xml
     * @param persistenceXMLLocation
     */
    public void setPersistenceXMLLocation(String persistenceXMLLocation) {
        this.persistenceXMLLocation = persistenceXMLLocation;
    }

    /**
     * Set an explicitly identified the location containing persistence.xml.
     * @param persistenceinfo the file containing persistence.xml, the file 
     * should contain META-INF/persistence.xml
     */
    public void setPersistenceInfo(File persistenceInfoFile) throws MalformedURLException{
        if (persistenceInfoFile!=null){
            this.persistenceInfo=persistenceInfoFile.toURL();
        }
    }
    
    
    /**
     * This method performs weaving function on the class individually from the specified source.
     * @throws Exception.
     */
    public void performWeaving() throws URISyntaxException,MalformedURLException,IOException{
        preProcess();
        process();
    }
    
    /**
     * INTERNAL:
     * This method perform all necessary steps(verification, pre-build the target directory)
     * prior to the invocation of the weaving function.
     */
    private void preProcess() throws URISyntaxException,MalformedURLException{
        //Instantiate default session log
        AbstractSessionLog.getLog().setLevel(this.logLevel);
        if(logWriter!=null){
            ((DefaultSessionLog)AbstractSessionLog.getLog()).setWriter(logWriter);
        }

        //Make sure the source is existing
        if(!(new File(Helper.toURI(source)).exists())){
            throw StaticWeaveException.missingSource();
        }
        
        URI targetURI = Helper.toURI(target);
        //Verification target and source, two use cases create warning or exception.
        //1. If source is directory and target is jar - 
        //   This will lead unknown outcome, user attempt to use this tool to pack outcome into a Jar. 
        //   Warning message will be logged, this is can be worked around by other utilities.
        //2. Both source and target are specified as a same jar -  
        //   User was trying to perform weaving in same Jar which is not supported, an Exception will be thrown.
        if(isDirectory(source) && targetURI.toString().endsWith(".jar")){
            AbstractSessionLog.getLog().log(SessionLog.WARNING, ToStringLocalization.buildMessage("staticweave_processor_unknown_outcome", new Object[]{null}));
        }
        
        if(!isDirectory(source) && target.toString().equals(source.toString())){
            throw StaticWeaveException.weaveInplaceForJar(source.toString());
        }
        
        //pre-create target if it is directory and dose not exist.
        //Using the method File.isDirectory() is not enough to determine what the type(dir or jar) 
        //of the target(specified by URL)that user want to create. File.isDirectory() will return false in 
        //two possibilities, the location either is not directory or the location dose not exist. 
        //Therefore pre-build of the directory target is required. Pre-build for the file(JAR) target 
        //is not required since it gets built automatically by opening outputstream.  
        if(!(new File(targetURI)).exists()){
            if(!targetURI.toString().endsWith(".jar")){
                (new File(targetURI)).mkdirs();
                //if directory fails to build, which may leads to unknown outcome since it will 
                //be treated as single file in the class StaticWeaveHandler and automatically gets built
                //by outputstream.

                //re-assign URL.
                target = (new File(targetURI)).toURL();
            }
        }
    }
    
    /**
     * INTERNAL:
     * The method performs weaving function
     */
    private void process() throws IOException,URISyntaxException{
        // Instantiate output handler.
        AbstractStaticWeaveOutputHandler swoh;
        if (isDirectory(this.target)) {
            swoh= new StaticWeaveDirectoryOutputHandler(this.source,this.target);
        }else{
            swoh= new StaticWeaveJAROutputHandler(new JarOutputStream(new FileOutputStream(new File(Helper.toURI(this.target)))));
        }
        
        // Instantiate classloader.
        this.classLoader = (this.classLoader == null)? Thread.currentThread().getContextClassLoader():this.classLoader;
        this.classLoader = new URLClassLoader(getURLs(), this.classLoader);
        
        // Instantiate the classtransformer, we check if the persistenceinfo URL has been specified.
        StaticWeaveClassTransformer classTransformer=null;
        if (persistenceInfo!=null) {
            classTransformer = new StaticWeaveClassTransformer(persistenceInfo, persistenceXMLLocation, this.classLoader,this.logWriter,this.logLevel);
        } else{
            classTransformer = new StaticWeaveClassTransformer(source, persistenceXMLLocation, this.classLoader,this.logWriter,this.logLevel);
        }

        // Starting process.
        Archive sourceArchive =(new ArchiveFactoryImpl()).createArchive(source, null, null);
        if (sourceArchive != null) {
            try {
                Iterator entries = sourceArchive.getEntries();
                while (entries.hasNext()){
                    String entryName = (String)entries.next();
                    InputStream entryInputStream = sourceArchive.getEntry(entryName);
                
                    // Add a directory entry
                    swoh.addDirEntry(getDirectoryFromEntryName(entryName));
                
                    // Add a regular entry
                    JarEntry newEntry = new JarEntry(entryName);
                
                    // Ignore non-class files.
                    if (!(entryName.endsWith(".class"))) {
                        swoh.addEntry(entryInputStream, newEntry);
                        continue;            
                    }
                
                    String className = PersistenceUnitProcessor.buildClassNameFromEntryString(entryName) ;
                
                    byte[] originalClassBytes=null;
                    byte[] transferredClassBytes=null;
                    try {
                        Class thisClass = this.classLoader.loadClass(className);
                        // If the class is not in the classpath, we simply copy the entry
                        // to the target(no weaving).
                        if (thisClass == null){
                            swoh.addEntry(entryInputStream, newEntry);
                            continue;
                        }
                    
                        // Try to read the loaded class bytes, the class bytes is required for
                        // classtransformer to perform transfer. Simply copy entry to the target(no weaving)
                        // if the class bytes can't be read.
                        InputStream is = this.classLoader.getResourceAsStream(entryName);
                        if (is!=null){
                            ByteArrayOutputStream baos = null;
                            try{
                                baos = new ByteArrayOutputStream();
                                byte[] bytes = new byte[NUMBER_OF_BYTES];
                                int bytesRead = is.read(bytes, 0, NUMBER_OF_BYTES);
                                while (bytesRead >= 0){
                                    baos.write(bytes, 0, bytesRead);
                                    bytesRead = is.read(bytes, 0, NUMBER_OF_BYTES);
                                }
                                originalClassBytes = baos.toByteArray();
                            } finally {
                                baos.close();
                            }
                        } else {
                            swoh.addEntry(entryInputStream, newEntry);
                            continue;
                        }
                    
                        // If everything is OK so far, we perform the weaving. we need three parameters in order to
                        // class to perform weaving for that class, the class name,the class object and class bytes.
                        transferredClassBytes = classTransformer.transform(className.replace('.', '/'), thisClass, originalClassBytes);
                    
                        // If transferredClassBytes is null means the class dose not get woven.
                        if (transferredClassBytes!=null){
                            swoh.addEntry(newEntry, transferredClassBytes);
                        } else {
                            swoh.addEntry(entryInputStream, newEntry);
                        }
                    } catch (IllegalClassFormatException e) {
                        AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.WARNING, e);
                        // Anything went wrong, we need log a warning message, copy the entry to the target and
                        // process next entry.
                        swoh.addEntry(entryInputStream, newEntry);
                        continue;
                    } catch (ClassNotFoundException e) {
                        AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.WARNING, e);
                        swoh.addEntry(entryInputStream, newEntry);
                        continue;
                    } finally {
                        // Need close the inputstream for current entry before processing next one. 
                        entryInputStream.close();
                    }
                }
            } finally {
                sourceArchive.close();
                swoh.closeOutputStream();
            }
        }
    }
    
    //Extract directory from entry name.    
    public static String getDirectoryFromEntryName(String entryName){
        String result="";
        if (entryName==null ) {
            return result;
        }
        if(entryName.lastIndexOf("/")>=0){
            result=entryName.substring(0, entryName.lastIndexOf("/"))+File.separator;
        } 
        return result;
    }
    
    /**
     * Determine whether or not the URL is pointing to directory.
     */
    private boolean isDirectory(URL url) throws URISyntaxException{
        File file = new File(Helper.toURI(url));
        if (file.isDirectory()) {
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Generate URL array for specified source and persistenceinfo.
     */
    private URL[] getURLs(){
        if((this.source!=null) && (this.persistenceInfo!=null)){
            return new URL[]{this.persistenceInfo,this.source};
        } else if(this.source!=null){
            return new URL[]{this.source};
        } else if (this.persistenceInfo!=null){
            return new URL[]{this.persistenceInfo};
        }
        return new URL[]{};
    }
}
