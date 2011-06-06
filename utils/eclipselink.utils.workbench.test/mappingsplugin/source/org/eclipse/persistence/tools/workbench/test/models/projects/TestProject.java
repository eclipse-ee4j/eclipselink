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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWAggregateDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.utility.Classpath;



public abstract class TestProject {

	
	// ********** instance variables **********
	
	private MWProject project;
	
	
	// ********** constructors **********
	
	protected TestProject() {
		super();
		this.initialize();
	}


	// ********** initialization **********

	protected void initialize() {
		this.project = this.buildEmptyProject();
		this.initializeProject();
	}
	
	protected abstract MWProject buildEmptyProject();

	protected void initializeProject() {
		this.initializeClassRepository();
	}

	protected void initializeClassRepository() {
		this.classRepository().addClasspathEntry(Classpath.locationFor(this.getClass()));
	}
		
	
	// ********** accessors **********

	/**
	 * subclasses will implement #getProject to cast the project appropriately
	 */
	protected MWProject getProjectInternal() {
		return this.project;
	}


	// ********** convenience methods **********


	protected MWDescriptor addDescriptorForTypeNamed(String typeName) {
		try {
			return this.getProjectInternal().addDescriptorForType(this.refreshedTypeNamed(typeName));
		} catch (InterfaceDescriptorCreationException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected MWAggregateDescriptor addAggregateDescriptorForTypeNamed(String typeName) {
		return ((MWRelationalProject) this.getProjectInternal()).addAggregateDescriptorForType(this.refreshedTypeNamed(typeName));
	}

	protected MWClassRepository classRepository() {
		return this.getProjectInternal().getRepository();
	}

	protected MWClass typeFor(Class javaClass) {
		return this.typeNamed(javaClass.getName());
	}

	protected MWClass typeNamed(String name) {
		return this.getProjectInternal().typeNamed(name);
	}

	protected MWClass refreshedTypeNamed(String className) {
		try {
			MWClass result = this.getProjectInternal().typeNamed(className);
			result.refresh();
			return result;
		} catch (ExternalClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void refreshTypeNamed(String className) {
		this.refreshedTypeNamed(className);
	}

	// moved this here, from MWClass, because it is just wrong  ~bjv
	protected MWMethod methodNamed(MWClass type, String methodName) {
		for (Iterator stream = type.allMethods(); stream.hasNext(); ) {
			MWMethod method = (MWMethod) stream.next();
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new IllegalArgumentException(type.getName() + "#" + methodName);
	}

	protected MWDescriptor descriptorWithShortName(String name) {
		for (Iterator stream = this.getProjectInternal().descriptors(); stream.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) stream.next();
			if (descriptor.getMWClass().shortName().equals(name)) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException(name);
	}


	// ********** static methods **********

	private static final String CR = System.getProperty("line.separator");

	public static void main(String[] arg){
		printClasspath();   
	}

	private static void printClasspath() {
		System.out.println("**********");

		StringBuffer sb = new StringBuffer(4000);
		appendClasspathTo(sb);
		System.out.print(sb.toString());

		System.out.println("**********");
	}

	protected static String buildFileMayNotBeInClasspathMessage(String fileName) {
		StringBuffer sb = new StringBuffer(4000);
		sb.append("Please make sure "+ fileName +" is in your classpath."); sb.append(CR);
		sb.append("It could be that it is not in your classpath."); sb.append(CR);
		sb.append("1. Make sure it is lying under one of the directories listed as follows."); sb.append(CR);
		sb.append("2. Make sure your code is using a system classloader to find resources."); sb.append(CR);
		sb.append("Currently your classpath is as follows:"); sb.append(CR);
		appendClasspathTo(sb);
		return sb.toString();
    }

	private static void appendClasspathTo(StringBuffer sb) {
		String classpath = System.getProperties().getProperty("java.class.path");
 		for (StringTokenizer stream = new StringTokenizer(classpath, File.pathSeparator); stream.hasMoreTokens(); ) {
			sb.append("    ");
			sb.append(stream.nextToken());
			sb.append(CR);
	    }
	}

	protected static SPIManager spiManager() {
		return MappingsModelTestTools.buildSPIManager();
	}

}
