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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.weaving.jpa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import org.eclipse.persistence.exceptions.StaticWeaveException;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
* <p>
* <b>Description</b>: This is the static weave ant task definition class that verifies the value of specified attributes and invokes 
*  StaticWeaveProcessor to weave classes.
* <p>
* <b>Usage</b>:
* <ul>
* <li>Ensure the classpath contains all the classes necessary to load the classes in the source.
* <li>The lib containing this weaving Ant task must be added into the classpath by using the -lib option on Ant command line instead of using the classpath attribute of the taskdef Ant task.
* <li>Define weaving Ant task and Ant target by using following attributes:
* <ul><li>source  - specify source location. In the default configuration StaticWeaveAntTask assumes the source contains the persistence.xml,if this is not the case, the location containing the persistence.xml must be explicitly identified by the attribute 'persistenceinfo'.
* <li>target - specify the output location (either a directory or a jar).  
* <li>persistenceinfo - specify the location containing the persistence.xml. This is optional and should only be specified if the source does not contain the persistence.xml.
* <li>log - specify a logging file. This is optional.
* <li>loglevel - specify a literal value of EclipseLink logging level(OFF,SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST) The default value is OFF(8). This is optional.
* </ul>
* <li>The weaving will be performed in place if source and target point to the same location. Weaving in place is ONLY applicable for directory-based sources.
* </ul>
*<b>Example</b>:
*<br>
*<code>
*&lt;target name="define.task" description="New task definition for EclipseLink static weaving"/&gt;<br>
*&nbsp;&nbsp;&lt;taskdef name="weave" classname="org.eclipse.persistence.tools.weaving.jpa.StaticWeaveAntTask"/&gt;<br>
*&lt;/target&gt;<br>
*&lt;target name="weaving" description="perform weaving." depends="define.task"&gt;<br>
*&nbsp;&nbsp;&lt;weave source= "c:\foo.jar" target = "c:\wovenfoo.jar" persistenceinfo="c:\foo-containing-persistenceinfo.jar"&gt;<br>
*&nbsp;&nbsp;&nbsp;&nbsp;&lt;classpath&gt;<br>
*&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;pathelement path="c:\foo-dependent.jar"/&gt;<br>
*&nbsp;&nbsp;&nbsp;&nbsp;&lt;/classpath&gt;<br>
*&nbsp;&nbsp;&lt;/weave&gt;<br>
*&lt;/target&gt;
*</code>
*/

public class StaticWeaveAntTask extends Task{
    
    private String source;
    private String persistenceinfo;
    private String target;
    private Vector classPaths = new Vector();
    private int logLevel = SessionLog.OFF;
    private Writer logWriter;    
    
    /**
     * Set the input archive to be used to weave.
     */
    public void setSource(String source) {
           this.source = source;
    }

    /**
     * Set output archive to be used to weave to.
     */
    public void setTarget(String target) {
           this.target = target;
    }
   
    /**
     * Set the log file.
     */
    public void setLog(String logFile) throws IOException {
        try{
           this.logWriter = new FileWriter(logFile);
        }catch(Exception e){
            throw StaticWeaveException.openLoggingFileException(logFile,e);
        }
    }
    
    public void setLogLevel(String logLevel) {
        if (logLevel.equalsIgnoreCase("OFF") ||
            logLevel.equalsIgnoreCase("SEVERE") || 
            logLevel.equalsIgnoreCase("WARNING") || 
            logLevel.equalsIgnoreCase("INFO") || 
            logLevel.equalsIgnoreCase("CONFIG") || 
            logLevel.equalsIgnoreCase("FINE") || 
            logLevel.equalsIgnoreCase("FINER") || 
            logLevel.equalsIgnoreCase("FINEST") || 
            logLevel.equalsIgnoreCase("ALL")) {
            this.logLevel = AbstractSessionLog.translateStringToLoggingLevel(logLevel.toUpperCase());
        } else{
            throw StaticWeaveException.illegalLoggingLevel(logLevel);
        }
    }
    
    public void setPersistenceinfo(String persistenceinfo) {
        this.persistenceinfo = persistenceinfo;
    }
    
    /**
     * Add the dependent classpath in order to load classes from the specified input jar.
     */
    public void addClasspath(Path path) {
        classPaths.add(path);
    }
    
    /**
     * Parse the class path element and store them into vector.
     */
    private Vector getPathElement(){
        Vector pathElements = new Vector();
        for(int i=0;i<classPaths.size();i++){
            String thisPath = ((Path)classPaths.get(i)).toString();
            if(thisPath!=null){
               String[] thisSplitedPath=thisPath.split(File.pathSeparator);
               if(thisSplitedPath!=null){
                   for(int j=0;j<thisSplitedPath.length;j++){
                     pathElements.add(thisSplitedPath[j]);
                   }
               }
            }
        }
        return pathElements;
    }
    
    /**
     * Convert the path element into the URL which further pass into
     * the classloader. 
     */
    private URL[] getURLs(){
        Vector pathElements = getPathElement();
        URL[] urls = new URL[pathElements.size()];
        for(int i=0;i<pathElements.size();i++){
           try {
               urls[i] = (new File((String)pathElements.get(i))).toURI().toURL();
           } catch (MalformedURLException e) {
               throw StaticWeaveException.exceptionPerformWeaving(e, pathElements.get(i));
           }
        }
        return urls;
    }

    /**
     * Execute ant task.
     */
    public void execute() {
       verifyOptions();
       start();
    }
   
    /**
     * Verify the value of attributes.
     * @throws BuildException
     */
    private void verifyOptions() throws BuildException {
       if (source==null) {
           throw StaticWeaveException.missingSource();
       }
       
       if (target==null) {
           throw StaticWeaveException.missingTarget();
       }
    }
   
    /**
     * Invoke weaving process.
     */
    private void start() {
       try {
           StaticWeaveProcessor weave = new StaticWeaveProcessor(source, target);
           URL[] urls = getURLs();
           if (urls!=null) {
               URLClassLoader classLoader = new URLClassLoader(getURLs(), Thread.currentThread().getContextClassLoader());
               weave.setClassLoader(classLoader);
           }
           if (persistenceinfo!=null) {
               weave.setPersistenceInfo(persistenceinfo);
           }
           if (logWriter!=null) {
               weave.setLog(logWriter);
           }
           weave.setLogLevel(this.logLevel);
           weave.performWeaving();
       } catch (Exception e) {
           AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.SEVERE, e);
           throw StaticWeaveException.exceptionPerformWeaving(e, source);
       }
    }
}
