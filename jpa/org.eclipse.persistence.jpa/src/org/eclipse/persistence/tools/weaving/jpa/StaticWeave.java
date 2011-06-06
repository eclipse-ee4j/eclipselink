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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.persistence.exceptions.StaticWeaveException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
* <p>
* <b>Description</b>: This is the static weave command line processing class that verifies command options and invokes 
* StaticWeaveProcessor to statically weave the classes. 
* <p>
*&nbsp;<b>Usage</b>:<br> 
*&nbsp;&nbsp;StaticWeave [options] source target<br>
*&nbsp;<b>Options</b>:<br>
*&nbsp;&nbsp;-classpath<br>
*&nbsp;&nbsp;&nbsp;&nbsp;Set the user class path, use ";" as the delimiter in Window system and ":" in Unix system.<br>
*&nbsp;&nbsp;-log <br>
*&nbsp;&nbsp;&nbsp;&nbsp;The path of log file, the standard output will be the default.<br>
*&nbsp;&nbsp;-loglevel<br>
*&nbsp;&nbsp;&nbsp;&nbsp;Specify a literal value for eclipselink log level(OFF,SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST). The default value is OFF.<br>
*&nbsp;&nbsp;-persistenceinfo<br>
*&nbsp;&nbsp;&nbsp;&nbsp;The path contains META-INF/persistence.xml. This is ONLY required when the source does not include it.
*&nbsp;The classpath must contain all the classes necessary in oder to perform weaving.<br><br>
*&nbsp;The weaving will be performed in place if source and target point to the same location. Weaving in place is ONLY applicable for directory-based sources.<br>
*<b>Example</b>:<br>
*&nbsp;To weave all entities contained in c:\foo-source.jar with its persistence.xml contained within c:\foo-containing-persistence-xml.jar, and output to c:\\foo-target.jar,<br>
*&nbsp;StaticWeave -persistenceinfo c:\foo-containing-persistencexml.jar -classpath c:\classpath1;c:\classpath2 c:\foo-source.jar c:\foo-target.jar
* 
**/

public class StaticWeave {

        // command line arguments
        private String[] argv;

        // The location path of the source, null if none was given 
        private String source;

        // The location path containing persistence.xml, null if none was given 
        private String persistenceinfopath;
        
        private String persistenceXmlLocation;

        // The location path of the target, null if none was given 
        private String target;

        private int loglevel=SessionLog.OFF;
        
        private Writer logWriter;

        private PrintStream vout = System.out;
        
        private String[] classpaths;

        public static void main(String[] argv) {

            StaticWeave staticweaver = new StaticWeave(argv);

            try {
                // Verify the command line arguments 
                staticweaver.processCommandLine();
                staticweaver.start();
            } catch (Exception e) {
                throw StaticWeaveException.exceptionPerformWeaving(e, argv);
            }
        }
        

        public StaticWeave(String[] argv) {
            this.argv = argv;
        }

        /**
         * Invoke StaticWeaveProcessor to perform weaving.
         */
        public void start() throws Exception {

            //perform weaving
            StaticWeaveProcessor staticWeaverProcessor= new StaticWeaveProcessor(this.source,this.target);
            if(persistenceinfopath!=null){
                staticWeaverProcessor.setPersistenceInfo(this.persistenceinfopath);
            }
            if (persistenceXmlLocation != null){
                staticWeaverProcessor.setPersistenceXMLLocation(this.persistenceXmlLocation);
            }
            if(classpaths!=null){
                staticWeaverProcessor.setClassLoader(getClassLoader());
            }
            if(logWriter!=null){
               staticWeaverProcessor.setLog(logWriter);
            }
            staticWeaverProcessor.setLogLevel(loglevel);
            staticWeaverProcessor.performWeaving();
        }


        /*
         * Verify command line option. 
         */
        void processCommandLine() throws Exception
        {
            if (argv.length < 2 || argv.length>12) {
                printUsage();
                System.exit(1);
            }
            for (int i=0;i<this.argv.length;i++){
                if (argv[i].equalsIgnoreCase("-classpath")) {
                    // Make sure we did not run out of arguments
                    if ((i + 1) >= argv.length ){
                        printUsage();
                        System.exit(1);
                    }
                    classpaths=argv[i+1].split(File.pathSeparator);
                    i++;
                    continue;
                }
                
                if (argv[i].equalsIgnoreCase("-persistenceinfo")) {
                    if ((i + 1) >= argv.length ){
                           printUsage();
                           System.exit(1);
                    }
                    persistenceinfopath=argv[i+1];
                    i++;
                    continue;
                }
                
                if (argv[i].equalsIgnoreCase("-persistencexml")) {
                    if ((i + 1) >= argv.length ){
                           printUsage();
                           System.exit(1);
                    }
                    persistenceXmlLocation=argv[i+1];
                    i++;
                    continue;
                }
                
                if (argv[i].equalsIgnoreCase("-log")) {
                    if ((i + 1) >= argv.length ){
                           printUsage();
                           System.exit(1);
                    }
                    logWriter=new FileWriter(argv[i+1]);
                    i++;
                    continue;
                }

                if (argv[i].equalsIgnoreCase("-loglevel")) {
                    if ((i + 1) >= argv.length ) {
                           printUsage();
                           System.exit(1);
                    }

                   if ( argv[i+1].equalsIgnoreCase("OFF") ||
                        argv[i+1].equalsIgnoreCase("SEVERE") || 
                        argv[i+1].equalsIgnoreCase("WARNING") || 
                        argv[i+1].equalsIgnoreCase("INFO") || 
                        argv[i+1].equalsIgnoreCase("CONFIG") || 
                        argv[i+1].equalsIgnoreCase("FINE") || 
                        argv[i+1].equalsIgnoreCase("FINER") || 
                        argv[i+1].equalsIgnoreCase("FINEST") || 
                        argv[i+1].equalsIgnoreCase("ALL")) {
                       loglevel=AbstractSessionLog.translateStringToLoggingLevel(argv[i+1].toUpperCase());
                    } else{
                        printUsage();
                        System.exit(1);
                    }
                    i++;
                    continue;
                }
                
                if(source!=null){
                    printUsage();
                    System.exit(1);
                }
                
                if(target!=null){
                    printUsage();
                    System.exit(1);
                }

                source=argv[i];
                if((i+1)>=argv.length){
                    printUsage();
                    System.exit(1);
                }
                i++;
                if(i>=argv.length){
                    printUsage();
                    System.exit(1);
                }
                target=argv[i];
                i++;
            }
            
            
           //Ensure source and target have been specified
           if(source==null){
                printUsage();
                throw StaticWeaveException.missingSource();
           } 
           if(target==null){
                printUsage();
                throw StaticWeaveException.missingTarget();
           } 
        }

        /*
         * print command help message
         */
        private void printUsage() {
            PrintStream o = vout;
            
            // Because we can no longer use Helper.cr() inside of message bundles, we must break
            // up the message into separate lines and use Helper.cr() here instead. (bug6470503)
            String messageString = ToStringLocalization.buildMessage("staticweave_commandline_help_message_1of19");
            messageString += Helper.cr() + Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_2of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_3of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_4of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_5of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_6of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_7of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_8of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_9of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_10of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_11of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_12of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_13of19");
            messageString += Helper.cr() + Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_14of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_15of19");
            messageString += Helper.cr() + Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_16of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_17of19");
            messageString += Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_18of19");
            messageString += Helper.cr() + Helper.cr();
            messageString += ToStringLocalization.buildMessage("staticweave_commandline_help_message_19of19");
            messageString += Helper.cr() + Helper.cr();
            
            o.println(messageString);
        }
        
        /*
         * Convert the specified classpath array to URL array where new classloader will build on.
         */
        @SuppressWarnings("deprecation")
        private ClassLoader getClassLoader() throws MalformedURLException{
            if (classpaths!=null){
                URL[] urls= new URL[classpaths.length];
                for(int i=0;i<classpaths.length;i++){
                   urls[i]=(new File(classpaths[i])).toURL();
                }
                return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
            }else{
                return null;
            }
        }
}
