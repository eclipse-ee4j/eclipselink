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
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.mappingsio.FileNotFoundListener;
import org.eclipse.persistence.tools.workbench.mappingsio.LegacyProjectReadCallback;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.DefaultSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A new instance of this class is created for each project read:
 * 	MWProject project = new ProjectReader(ioManager, file, preferences, listener).read()
 */
class Project60Reader {

	/** The I/O manager that created this reader. */
	private Project60IOManager ioManager;

	/** The project file to be read. */
	private File file;

	/** The user preferences used to configure the SPI manager. */
	private Preferences preferences;

	/** A listener that will be notified whenever an "expected" file is missing. */
	private FileNotFoundListener listener;

	// ********** constructors **********

	Project60Reader(Project60IOManager ioManager, File file, Preferences preferences, FileNotFoundListener listener) {
		super();
		this.ioManager = ioManager;
		this.file = file;
		this.preferences = preferences;
		this.listener = listener;
	}


	// ********** public stuff **********

	/**
	 * Read a project from the file, using the appropriate "schema".
	 */
	MWProject read() {
		return this.readProject();
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.file);
	}


	// ********** internal stuff **********

	/**
	 * read a normal (non-legacy) project from the file and return it
	 */
	private MWProject readProject() {
		// first read in the project, but none of its components (classes, metadata, descriptors)
		MWProject project;
		try {
			project = (MWProject) this.readObject(this.file);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// "inject" the SPIManager once we have the base project
		this.injectSPIManager(project, new DefaultSPIManager(this.preferences, project.getName()));

		// then use the names stored throughout the project to read up its components
		SubComponentReader[] subComponentReaders = this.buildSubComponentReaders(project);
		for (int i = 0; i < subComponentReaders.length; i++) {
			subComponentReaders[i].read();
		}

		// now trigger all the handles to resolve etc.
		project.postProjectBuild();

		return project;
	}

	/**
	 * build readers for all the project's sub-components that are
	 * read in separately: classes, tables, descriptors
	 */
	private SubComponentReader[] buildSubComponentReaders(MWProject project) {
		return new SubComponentReader[] {
			new SubComponentReader(project.getClassRepository()),
			new SubComponentReader(project.getMetaDataSubComponentContainer()),
			new SubComponentReader(project.getDescriptorRepository()),
		};
	}

	/**
	 * Use TopLink to unmarshal an object from the specified XML file.
	 * Let the exceptions through so we can swallow the FileNotFoundException
	 * when reading sub-components.
	 */
	Object readObject(File xmlFile) throws IOException {
		InputStream stream = null;
		Object object = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(xmlFile));
			object = this.ioManager.getUnmarshaller().unmarshal(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return object;
	}

	/**
	 * return the base directory for all the project files;
	 * the project file is in this directory, while all the other
	 * files are in subdirectories of this directory
	 */
	File baseDirectory() {
		return this.file.getParentFile();
	}

	String defaultFileNameExtension() {
		return this.ioManager.defaultFileNameExtension();
	}

	String subDirectoryNameFor(Object container) {
		return this.ioManager.subDirectoryNameFor(container);
	}

	void fireFileNotFound(File missingFile) {
		this.ioManager.fireFileNotFound(this.listener, missingFile);
	}

	private void injectSPIManager(MWProject project, SPIManager spiManager) {
		ClassTools.invokeMethod(project, "setSPIManagerForIOManager", SPIManager.class, spiManager);
	}

	// ********** inner classes **********
	
	/**
	 * Delegate sub-component-related behavior to this class.
	 */
	private class SubComponentReader {
		/** the container that will hold the sub-components once they are read */
		private ProjectSubFileComponentContainer container;
	
		SubComponentReader(ProjectSubFileComponentContainer container) {
			this.container = container;
		}
	
		void read() {
			String ext = Project60Reader.this.defaultFileNameExtension();
			// build the sub-directory that holds the sub-components
			String subDirectoryName = Project60Reader.this.subDirectoryNameFor(this.container);
			File subDirectory = new File(this.baseDirectory(), subDirectoryName);
	
			// the sub-component names are set by TopLink when the project is read;
			// and reset by ProjectWriter when the project is saved
			Collection names = CollectionTools.set(this.container.originalProjectSubFileComponentNames());
	
			// now use the sub-component names to read in the actual sub-components
			Collection subComponents = new Vector(names.size());
			for (Iterator stream = names.iterator(); stream.hasNext(); ) {
				String name = (String) stream.next();
				String fileName = FileTools.FILE_NAME_ENCODER.encode(name);
				File subFile = new File(subDirectory, fileName + ext);
				MWModel subComponent = (MWModel) this.readObject(subFile);
				if (subComponent == null) {
					Project60Reader.this.fireFileNotFound(subFile);
					stream.remove();		// keep the list of names in synch with the files
				} else {
					subComponent.setParent((org.eclipse.persistence.tools.workbench.utility.node.Node) this.container);
					subComponents.add(subComponent);
				}
			}
	
			// and finally, put the sub-components into the container
			this.container.setProjectSubFileComponents(subComponents);
		}
	
		private File baseDirectory() {
			return Project60Reader.this.baseDirectory();
		}
	
		private Object readObject(File xmlFile) {
			try {
				return Project60Reader.this.readObject(xmlFile);
			} catch (FileNotFoundException ex) {
				return null;
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	
	}

}
