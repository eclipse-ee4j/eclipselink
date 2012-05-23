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
package org.eclipse.persistence.tools.workbench.mappingsio;

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

import org.eclipse.persistence.tools.workbench.mappingsio.legacy.LegacyIOFacade;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.DefaultSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SimpleSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * A new instance of this class is created for each project read:
 * 	MWProject project = new ProjectReader(ioManager, file, preferences, listener).read()
 */
class ProjectReader {

	/** The I/O manager that created this reader. */
	private ProjectIOManager ioManager;

	/** The project file to be read. */
	private File file;

	/** The user preferences used to configure the SPI manager. */
	private Preferences preferences;

	/** A listener that will be notified whenever an "expected" file is missing. */
	private FileNotFoundListener listener;

	/**
	 * A callback object that checks whether the reader
	 * should continue with the reading a legacy project.
	 */
	private LegacyProjectReadCallback legacyProjectReadCallback;

	/** An XML document corresponding to the project file. */
	private Document document;

	/** The version of the schema describing the project file. */
	private String schemaVersion;

	/** @see MWProject#CURRENT_PROJECT_ROOT_ELEMENT_NAME */
	private static final String PROJECT_ROOT_ELEMENT_NAME_4_X = "BldrProject";

	/** @see MWProject#CURRENT_SCHEMA_VERSION_ELEMENT_NAME */
	private static final String SCHEMA_VERSION_ELEMENT_NAME_5_0 = "schemaVersion";	// 9.0.4/10.0.0
	private static final String SCHEMA_VERSION_ELEMENT_NAME_4_X = "version";

	/** @see MWProject#CURRENT_SCHEMA_VERSION */
	private static final String SCHEMA_VERSION_7_0 = "7.0"; //11.1.X.X
	private static final String SCHEMA_VERSION_6_0 = "6.0"; // 10.1.3.X.X
	private static final String SCHEMA_VERSION_5_X = "5";	// 9.0.4/10.0.0
	private static final String SCHEMA_VERSION_4_5 = "4.5";
		private static final String PRODUCT_VERSION_4_6 = "4.6"; // uses SCHEMA_VERSION_4_5
		private static final String PRODUCT_VERSION_9_0_3 = "9.0.3"; // uses SCHEMA_VERSION_4_5


	// ********** constructors **********

	ProjectReader(ProjectIOManager ioManager, File file, Preferences preferences, FileNotFoundListener listener, LegacyProjectReadCallback legacyProjectReadCallback) {
		super();
		this.ioManager = ioManager;
		this.file = file;
		this.preferences = preferences;
		this.listener = listener;
		this.legacyProjectReadCallback = legacyProjectReadCallback;
	}


	// ********** public stuff **********

	/**
	 * Read a project from the file, using the appropriate "schema".
	 * We will return null if we are reading a legacy project and the
	 * legacy project callback indicates we should not read the project.
	 */
	MWProject read() {
		// pre-parse the file to find the schema version
		this.document = XMLTools.parse(this.file);
		this.schemaVersion = this.schemaVersion();

		if (this.schemaVersion.equals(MWProject.CURRENT_SCHEMA_VERSION)) {
			return this.readProject();
		}
		return this.readLegacyProject();
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.file);
	}


	// ********** internal stuff **********

	/**
	 * return the version of the schema used for the project file
	 * and associated xml files
	 * @see ProjectIOManager#CURRENT_SCHEMA_VERSION
	 */
	private String schemaVersion() {
		Node rootNode = this.rootNode();
		// first try the current element name
		// then move back in time, trying previous element names
		Node schemaVersionNode = XMLTools.child(rootNode, MWProject.CURRENT_SCHEMA_VERSION_ELEMENT_NAME);
		if (schemaVersionNode == null) {
			schemaVersionNode = XMLTools.child(rootNode, SCHEMA_VERSION_ELEMENT_NAME_5_0);
		}
		if (schemaVersionNode == null) {
			schemaVersionNode = XMLTools.child(rootNode, SCHEMA_VERSION_ELEMENT_NAME_4_X);
		}
		if (schemaVersionNode == null) {
			throw new IllegalArgumentException();	// must not be a valid project file...
		}
		return XMLTools.textContent(schemaVersionNode);
	}

	/**
	 * return the root node of the project file
	 * @see ProjectIOManager#CURRENT_PROJECT_XML_DOCUMENT_NAME
	 */
	private Node rootNode() {
		// first try the current document name
		// then move back in time, trying previous document names
		Node rootNode = XMLTools.child(this.document, MWProject.CURRENT_PROJECT_ROOT_ELEMENT_NAME);
		if (rootNode == null) {
			rootNode = XMLTools.child(this.document, PROJECT_ROOT_ELEMENT_NAME_4_X);
		}
		if (rootNode == null) {
			throw new IllegalArgumentException();	// must not be a valid project file...
		}
		return rootNode;
	}

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

		// we don't set the save directory on legacy projects,
		// because it will be set by the user and saved if necessary
		ClassTools.invokeMethod(project, "setSaveDirectoryForIOManager", File.class, this.baseDirectory());

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

	private MWProject readLegacyProject() {
		MWProject project;
		
		if (this.schemaVersion.startsWith(SCHEMA_VERSION_6_0)) {
			this.legacyProjectReadCallback.checkLegacyRead(this.schemaVersion);
			project = LegacyIOFacade.read60Project(file, preferences);
		} else {
			// must be an unsupported version...
			throw new IllegalStateException(this.schemaVersion);
		}
		
		// legacy projects are marked dirty, forcing the user to save it in a new location
		project.markEntireBranchDirty();
		project.setIsLegacyProject(true);
		return project;	
	}

	/**
	 * this SPI manager is used for pre-6.0 projects;
	 * it ignores any user preferences
	 */
	private SPIManager buildSimpleSPIManager() {
		SimpleSPIManager mgr = new SimpleSPIManager();
		mgr.setExternalClassRepositoryFactory(CFExternalClassRepositoryFactory.instance());
		return mgr;
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
			String ext = ProjectReader.this.defaultFileNameExtension();
			// build the sub-directory that holds the sub-components
			String subDirectoryName = ProjectReader.this.subDirectoryNameFor(this.container);
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
					ProjectReader.this.fireFileNotFound(subFile);
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
			return ProjectReader.this.baseDirectory();
		}
	
		private Object readObject(File xmlFile) {
			try {
				return ProjectReader.this.readObject(xmlFile);
			} catch (FileNotFoundException ex) {
				return null;
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	
	}

}
