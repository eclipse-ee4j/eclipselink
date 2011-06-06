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
package org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

import org.eclipse.persistence.internal.codegen.ClassDefinition;
import org.eclipse.persistence.internal.codegen.CodeGenerator;

public final class SourceCodeGenerator 
{
	private ApplicationContext context;

	private MWProject project;
	private Collection descriptors;
	private File sourceRootDirectory;

	private boolean overwriteFiles;
	private boolean continueGenerating;
	private boolean anyFileWritten;

	private Collection fileAlreadyExistsListeners;
	private Collection continuableExceptionListeners;

	// **************** member types ******************************************
	
	public class ContinuableExceptionEvent extends EventObject {
		private Exception exception;
		protected ContinuableExceptionEvent(Object source, Exception exception) {
			super(source);
			this.exception = exception;
		}
		public Exception getException() {
			return this.exception;
		}
	}
	
	public interface ContinuableExceptionListener extends EventListener {
		boolean continueOnException(ContinuableExceptionEvent cee);
	}
	
	public class FileAlreadyExistsEvent extends EventObject {
		private File file;
		protected FileAlreadyExistsEvent(Object source, File file) {
			super(source);
			this.file = file;
		}
		public File getFile() {
			return this.file;
		}
	}
	
	public interface FileAlreadyExistsListener extends EventListener {
		boolean fileAlreadyExists(MWProject project, FileAlreadyExistsEvent event);
	}
	
	
	// **************** constructor *******************************************
	
	public SourceCodeGenerator(ApplicationContext context) {
		this.context = context;
		this.fileAlreadyExistsListeners = new Vector();
		this.continuableExceptionListeners = new Vector();
		this.continueGenerating = true;
	}
	
	
	// **************** accessors *********************************************
	
	private ApplicationContext getApplicationContext() {
		return this.context;
	}
	
	public boolean shouldOverwriteFiles() {
		return this.overwriteFiles;
	}
	
	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}
	

	// **************** behavior *********************************************
	
	public void generateSourceCode(MWProject project, Collection descriptors)
	{
		this.generateSourceCode(project, descriptors, null);
	}
	
	public void generateSourceCode(MWProject project, Collection descriptors, File sourceRootDirectory)
	{
		this.project = project;
		this.descriptors = descriptors;
		this.sourceRootDirectory = sourceRootDirectory;

		if (!this.overwriteFiles) {
			checkForExistingFiles();
		}

		for (Iterator it = descriptors.iterator(); it.hasNext();)
			 generateSourceCode((MWDescriptor) it.next());
	}
	
	private void generateSourceCode(MWDescriptor descriptor) {
		writeClassDefinition(new BasicDescriptorClassCodeGenPolicy(descriptor, getApplicationContext()).classDefinition());
	}
	
	public void checkForExistingFiles() {
		File existingFile = null;

		for (Iterator iter = this.descriptors.iterator(); iter.hasNext(); ) {
			MWDescriptor descriptor = (MWDescriptor) iter.next();
			existingFile = doesFileExist(descriptor);

			if (existingFile != null) {
				break;
			}
		}

		if (existingFile != null) {
			fireFileAlreadyExistsEvent(existingFile);
		}
	}

	private File doesFileExist(MWDescriptor descriptor) {
		return doesFileExist(new BasicDescriptorClassCodeGenPolicy(descriptor, getApplicationContext()).classDefinition());
	}

	private File doesFileExist(ClassDefinition classDef) {
		if (! this.continueGenerating)
			return null;

		// could be null in the case of a core type
		if (classDef == null)
			return null;

		File sourceRootDirectory = this.sourceRootDirectory;

		if (sourceRootDirectory == null)
			sourceRootDirectory = this.project.absoluteModelSourceDirectory();

		String writeDirectory = buildDirectory(classDef.getPackageName(), sourceRootDirectory);
		File javaFile = new File(writeDirectory, classDef.getName() + ".java");

		if (javaFile.exists())
			return javaFile;

		return null;
	}

	public void writeClassDefinition(ClassDefinition classDef) 
	{
		if (! this.continueGenerating)
			return;
		
		// could be null in the case of a core type
		if (classDef == null)
			return;
		
		File sourceRootDirectory = this.sourceRootDirectory;
		
		if (sourceRootDirectory == null)
			sourceRootDirectory = this.project.absoluteModelSourceDirectory();
			
		String writeDirectory = buildDirectory(classDef.getPackageName(), sourceRootDirectory);
		File javaFile = new File(writeDirectory, classDef.getName() + ".java");
		
		CodeGenerator codeGenerator = new CodeGenerator();
		
		try 
		{
			FileOutputStream stream = new FileOutputStream(javaFile);
			codeGenerator.setOutput(new OutputStreamWriter(stream, "utf-8"));
		} 
		catch (IOException ioe) 
		{
			fireContinuableExceptionEvent(ioe);
			return;
		}
		
		classDef.write(codeGenerator);
		this.anyFileWritten = true;
		
		try 
		{
			codeGenerator.getOutput().flush();
			codeGenerator.getOutput().close();
		} 
		catch (IOException ioe) 
		{
			fireContinuableExceptionEvent(ioe);
			return;
		}
	}
	
	public void removeFileAlreadyExistsListener(FileAlreadyExistsListener listener) {
		this.fileAlreadyExistsListeners.remove(listener);
	}
	
	private String buildDirectory(String packageName, File rootDirectory) {
		StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
		File currentDirectory = rootDirectory;
		currentDirectory.mkdir();
		while (tokenizer.hasMoreTokens()) {
			currentDirectory = new File(currentDirectory.getPath() + File.separator + tokenizer.nextToken());
			currentDirectory.mkdir();
		}
		return currentDirectory.getPath();
	}

	
	// **************** continuable exception stuff ***************************
	
	public void addContinuableExceptionListener(ContinuableExceptionListener listener) {
		this.continuableExceptionListeners.add(listener);
	}
	
	private void fireContinuableExceptionEvent(Exception exception) {
		for (Iterator listeners = this.continuableExceptionListeners.iterator(); listeners.hasNext();)
			this.continueGenerating &= ((ContinuableExceptionListener) listeners.next()).continueOnException(new ContinuableExceptionEvent(this, exception));
	}
	
	
	// **************** file already exists stuff *****************************
	
	public void addFileAlreadyExistsListener(FileAlreadyExistsListener listener) {
		this.fileAlreadyExistsListeners.add(listener);
	}
	
	private void fireFileAlreadyExistsEvent(File file) {
		for (Iterator listeners = this.fileAlreadyExistsListeners.iterator(); listeners.hasNext();)
			this.continueGenerating &= ((FileAlreadyExistsListener) listeners.next()).fileAlreadyExists(this.project, new FileAlreadyExistsEvent(this, file));
	}	
	public boolean isAnyFileWritten() {
		return this.anyFileWritten;
	}
}
