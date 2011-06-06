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
package org.eclipse.persistence.tools.workbench.ant.taskdefs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.eclipse.persistence.tools.workbench.ant.AntExtensionBundle;
import org.eclipse.persistence.tools.workbench.framework.resources.DefaultStringRepository;
import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;

/**
 * Base Ant task for projects.
 * Runs this task in the current VM. Classpath can be specified using 
 * a nested element <code>classpath</code>
 */
public abstract class ProjectTask extends Task {
    
    private CommandlineJava commandline;

    private AntClassLoader classLoader;
    
    private String userClasspath;
    
    protected StringRepository stringRepository;
    
	protected ProjectTask() {
	    this.initialize();
	}
		
	protected void initialize() {
	    this.classLoader = null;
	    this.userClasspath = "";
		this.stringRepository = new DefaultStringRepository( AntExtensionBundle.class);
	}
	/**
	 * This method is called from execute(), prior excecution of custom execute method.
	 * Permits to validate this task attributes before execution.
	 */
	protected void preExecute() throws BuildException {
	    // do nothing by default;
	}
	
    public void execute() throws BuildException {
        
        this.preExecute();
    }
    
	protected int execute( Object[] args) throws BuildException {
        try {
            log(  this.stringRepository.getString( "usingSysProperties", System.getProperties().toString()), Project.MSG_VERBOSE);
            createClassLoader();
            if( classLoader != null) {
                classLoader.setThreadContextLoader();
            }

            TaskRunner runner  = new TaskRunner( classLoader);
            
            return runner.execute( args);
        } 
        catch( InvocationTargetException ie) {
            log( ie.getTargetException().toString(), Project.MSG_ERR);
                throw new BuildException( ie.getTargetException());
        }
        catch( Throwable e) {
		    Throwable t = ( e.getCause() == null) ? e : e.getCause();
            log( t.toString(), Project.MSG_ERR);
            throw new BuildException( t);
        }
        finally {
            if( classLoader != null) {
                classLoader.resetThreadContextLoader();
            }
        }
    }

    protected abstract String getProjectRunnerClassName();

    /**
     * Returns the Java command line.
     */
    protected CommandlineJava getCommandline() {
        if ( commandline == null) {
            commandline = new CommandlineJava();
        }
        return commandline;
    }
    /**
     * Adds path to classpath used for running MW.
     *
     * @return reference to the classpath in the embedded java command line
     */
    public Path createClasspath() {
        return getCommandline().createClasspath( getProject()).createPath();
    }
    /**
     * Creates and configures an AntClassLoader instance from the
     * nested classpath element.
     */
    private void createClassLoader() {
        Path commandlineClasspath = getCommandline().getClasspath();
        
        if( commandlineClasspath != null) {
            if( classLoader == null) {
                Path classpath = ( Path)commandlineClasspath.clone();
                
                if( this.userClasspath.length() > 0) {
           			Path path = new Path( getProject(), this.userClasspath);
					classpath.append( path);
                }
                classLoader = getProject().createClassLoader( classpath);

                classLoader.setParentFirst( false);
                classLoader.addJavaLibraries();
                log(  this.stringRepository.getString( "usingClasspath", classLoader.getClasspath()), Project.MSG_VERBOSE);
            }
        }
    }
    /**
     * Set the classpath to be used when running this task
     *
     * @param s an Ant Path object containing the classpath.
     */
    public void setClasspath( Path s) {
        createClasspath().append( s);
    }
    /**
     * Classpath to use, by reference.
     *
     * @param reference a reference to an existing classpath
     */
    public void setClasspathRef( Reference reference) {
        createClasspath().setRefid( reference);
    }
    
	protected String getUserClasspath() {

	    return this.userClasspath;
	}
	
	protected void setUserClasspath( String userClasspath) {

	    this.userClasspath = userClasspath;
	}
    /**
     * Loads and runs the task specified by getProjectRunnerClassName() with the given classloader.
     */
    private class TaskRunner {
        
        private Object mappingsRunner;
        protected StringRepository stringRepository;
    
        private TaskRunner( ClassLoader classLoader) {
    	    this.initialize( classLoader);
    	}
    	
    	private void initialize( ClassLoader classLoader) {
    	    
    		this.stringRepository = new DefaultStringRepository( AntExtensionBundle.class);
            this.mappingsRunner = loadMappingsRunner( classLoader);
    	}

        private Object loadMappingsRunner( ClassLoader classLoader) {
            Class mappingRunnerClass = null;
            Object runner = null;
            try {
                mappingRunnerClass = Class.forName( getProjectRunnerClassName(), true, classLoader);
            } 
            catch( ClassNotFoundException e) {
				throw new RuntimeException( this.stringRepository.getString( "errorWhileExporting", getProjectRunnerClassName()), e);
            }
            try {
                runner = mappingRunnerClass.newInstance();
            } 
    		catch( InstantiationException ie) {
				throw new RuntimeException( this.stringRepository.getString( "instantiationExceptionAtInstantiation", mappingRunnerClass.getName()), ie);
    		}
    		catch( IllegalAccessException iae) {
				throw new RuntimeException( this.stringRepository.getString( "illegalAccessExceptionAtInstantiation", mappingRunnerClass.getName()), iae);
    		}
            return runner;
        }
        /**
         * Look up and run the method execute of MappingsRunner.
         * @throws InvocationTargetException
         */
        private int execute( Object[] args) throws InvocationTargetException {
            Method task = null;
            Integer result;
                try {
                    Class[] parameters = new Class[ args.length];
                    for( int i = 0; i < args.length; i++) {
                        if( args[ i] == null) {
                			throw new IllegalArgumentException( this.stringRepository.getString( "executeMethodCannotBeNull"));
                        }
                        parameters[ i] = args[ i].getClass();                        
                    }
        			task = mappingsRunner.getClass().getMethod( "execute", parameters);
        			if( task.getReturnType().isAssignableFrom( Integer.class)) {			    
        				throw new RuntimeException( this.stringRepository.getString( "expectIntegerReturningType", task.getName()));
        			}
                } 
                catch ( NoSuchMethodException e) {
    				throw new RuntimeException( this.stringRepository.getString( "executeMethodNotFound", mappingsRunner.getClass().getName()), e);
                }

    			try {
    			    result = ( Integer)task.invoke( mappingsRunner, args);
                } 
    			catch( IllegalArgumentException e) {
    				throw new RuntimeException( this.stringRepository.getString( "illegalArgumentExceptionAtInvocation", task.getName()), e);
                } 
    			catch( IllegalAccessException iae) {
    				throw new RuntimeException( this.stringRepository.getString( "illegalAccessExceptionAtInvocation", task.getName()), iae);
    			}
            return result.intValue();
        }
    }
}
