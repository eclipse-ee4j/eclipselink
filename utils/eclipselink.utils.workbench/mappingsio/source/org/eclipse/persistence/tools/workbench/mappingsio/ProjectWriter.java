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
package org.eclipse.persistence.tools.workbench.mappingsio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNominative;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProjectSubFileComponentContainer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.oxm.XMLMarshaller;

/**
 * A new instance of this class is created for each project write:
 * 	new ProjectWriter(ioManager, project, listener).write()
 */
class ProjectWriter {

	/** The I/O manager that created this writer. */
	private ProjectIOManager ioManager;

	/** The project to be written. */
	private MWProject project;

	/** A listener that will be notified whenever an "expected" file is missing. */
	private FileNotFoundListener listener;

	/** The collection of sub-component writers. */
	private SubComponentWriter[] subComponentWriters;

	/** A collection of the changes (deletes and writes) that need to be written. */
	private Collection changes;


	// ********** constructors **********

	ProjectWriter(ProjectIOManager ioManager, MWProject project, FileNotFoundListener listener) {
		super();
		this.ioManager = ioManager;
		this.project = project;
		if (project.getSaveDirectory() == null) {
			throw new NullPointerException("The project's save directory must be set before it is written.");
		}
		this.listener = listener;
	}


	// ********** public stuff **********

	/**
	 * Write out the project and its sub-components to its save directory.
	 */
	void write() throws ReadOnlyFilesException {
		// build up the sub-component writers
		this.subComponentWriters = this.buildSubComponentWriters();
		// gather up all the changes once
		this.changes = this.buildProposedChanges();

		this.checkForReadOnlyFiles();
		this.commit();
		this.resetProject();
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.project.getName());
	}


	// ********** internal stuff **********

	/**
	 * Build writers for all the project's sub-components that are
	 * written out separately: classes, tables, descriptors.
	 */
	private SubComponentWriter[] buildSubComponentWriters() {
		return new SubComponentWriter[] {
			new SubComponentWriter(this.project.getClassRepository()),
			new SubComponentWriter(this.project.getMetaDataSubComponentContainer()),
			new SubComponentWriter(this.project.getDescriptorRepository()),
		};
	}

	/**
	 * Build all the changes during initialization.
	 * They will be checked for read-only and
	 * committed during the write.
	 */
	private Collection buildProposedChanges() {
		Collection proposedChanges = new ArrayList();
		if (this.project.hasChangedMainProjectSaveFile()) {
			proposedChanges.add(new Write(this.project, this.project.saveFile()));
		}
		for (int i = 0; i < this.subComponentWriters.length; i++) {
			this.subComponentWriters[i].addChangesTo(proposedChanges);
		}
		return proposedChanges;
	}

	/**
	 * If any of the writes or deletes correspond to a read-only
	 * file, throw an exception. Clients can get a list of the read-only
	 * files from the exception.
	 */
	private void checkForReadOnlyFiles() throws ReadOnlyFilesException {
		Collection readOnlyFiles = new ArrayList(this.changes.size());
		for (Iterator stream = this.changes.iterator(); stream.hasNext(); ) {
			Change change = (Change) stream.next();
			change.addReadOnlyFilesTo(readOnlyFiles);
		}
		if ( ! readOnlyFiles.isEmpty()) {
			throw new ReadOnlyFilesException(readOnlyFiles);
		}
	}

	/**
	 * Commit all the changes.
	 */
	private void commit() {
		for (Iterator stream = this.changes.iterator(); stream.hasNext(); ) {
			((Change) stream.next()).commit();
		}
	}

	/**
	 * Update the project, now that it has been written.
	 */
	private void resetProject() {
		// clear all the dirty flags in the project
		this.project.markEntireBranchClean();
		// save the sub-component names for the next write...
		for (int i = 0; i < this.subComponentWriters.length; i++) {
			this.subComponentWriters[i].resetContainer();
		}
	}

	/**
	 * Return the base directory for all the project files.
	 * The project file is in this directory, while all the other
	 * files are in subdirectories of this directory.
	 */
	File baseDirectory() {
		return this.project.getSaveDirectory();
	}

	XMLMarshaller marshaller() {
		return this.ioManager.getMarshaller();
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


	// ********** inner classes **********

	/**
	 * Delegate sub-component-related behavior to this class.
	 */
	private class SubComponentWriter {
		/** the container that holds the sub-components */
		private ProjectSubFileComponentContainer container;
	
		SubComponentWriter(ProjectSubFileComponentContainer container) {
			this.container = container;
		}

		/**
		 * Determine which sub-components need to be written and
		 * which need to be deleted; add them all to the list of changes.
		 */
		void addChangesTo(Collection proposedChanges) {
			String ext = ProjectWriter.this.defaultFileNameExtension();
			// build the sub-directory that holds the sub-components
			String subDirectoryName = ProjectWriter.this.subDirectoryNameFor(this.container);
			File subDirectory = new File(ProjectWriter.this.baseDirectory(), subDirectoryName);
	
			// build the writes...
			Set currentNames = new HashSet();	// ...while simultaneously gathering up the current names
			Set currentFiles = new HashSet();		// and files
			for (Iterator stream = this.container.projectSubFileComponents(); stream.hasNext(); ) {
				MWModel subComponent = (MWModel) stream.next();
				String subComponentName = ((MWNominative) subComponent).getName();
				currentNames.add(subComponentName);
				String subComponentFileName = FileTools.FILE_NAME_ENCODER.encode(subComponentName);
				File subComponentFile = new File(subDirectory, subComponentFileName + ext);
				currentFiles.add(subComponentFile);
				if (subComponent.isDirtyBranch()) {
					proposedChanges.add(new Write(subComponent, subComponentFile));
				}
			}
	
			// build the deletes
			Collection originalNames = CollectionTools.set(this.container.originalProjectSubFileComponentNames());
			Collection deletedNames = this.calculateDeleted(originalNames, currentNames);
			for (Iterator stream = deletedNames.iterator(); stream.hasNext(); ) {
				String deleteFileName = FileTools.FILE_NAME_ENCODER.encode((String) stream.next());
				File deleteFile = new File(subDirectory, deleteFileName + ext);
				if (currentFiles.contains(deleteFile)) {
					// do nothing
					// 'currentFiles' will only "contain" the 'deleteFile' on Windows, where two files can be
					// "equal" when their names differ only by case; this will happen if the user renames
					// an object by only changing the case of the object's name: on Linux, the appropriate
					// file will be added (e.g. "foo.xml") and the appropriate file removed (e.g. "FOO.xml");
					// on Windows though, the appropriate file will be added (e.g. "foo.xml"), but then it
					// will be deleted immediately afterwards (when we try to delete "FOO.xml") by the
					// Delete Change if we don't perform this check
				} else {
					proposedChanges.add(new Delete(deleteFile));
				}
			}
		}

		/**
		 * Return the collection of objects that were removed from
		 * the specified starting collection as a result of its transformation
		 * into the specified ending collection.
		 */
		private Collection calculateDeleted(Collection start, Collection end) {
			Collection deleted = new HashSet(start);
			deleted.removeAll(end);
			return deleted;
		}
	
		/**
		 * Store the current names in the container,
		 * for use on the next write.
		 */
		void resetContainer() {
			Set currentNames = new HashSet();
			for (Iterator stream = this.container.projectSubFileComponents(); stream.hasNext(); ) {
				currentNames.add(((MWNominative) stream.next()).getName());
			}
			this.container.setOriginalProjectSubFileComponentNames(currentNames);
		}
	
		/**
		 * Return the collection of objects that were added to
		 * the specified starting collection as a result of its transformation
		 * into the specified ending collection.
		 */
	// unused...
	//	private Collection calculateAdded(Collection start, Collection end) {
	//		Collection added = new HashSet(end);
	//		added.removeAll(start);
	//		return added;
	//	}
	
	}
	
	
	/**
	 * Record the file to be changed (deleted or written).
	 */
	private abstract class Change {
		protected File file;
	
		Change(File file) {
			super();
			this.file = file;
		}
	
		/**
		 * If the change is associated with a read-only file,
		 * add the file to the specified collection.
		 */
		void addReadOnlyFilesTo(Collection readOnlyFiles) {
			if (this.file.exists() && ! this.file.canWrite()) {
				readOnlyFiles.add(this.file);
			}
		}
	
		/**
		 * Commit the change to disk.
		 */
		abstract void commit();
	
		public String toString() {
			return StringTools.buildToStringFor(this, this.file);
		}
	
		XMLMarshaller marshaller() {
			return ProjectWriter.this.marshaller();
		}
	}
	
	
	/**
	 * Record the file to be deleted.
	 */
	private class Delete extends Change {
		Delete(File file) {
			super(file);
		}
	
		/**
		 * Simply delete the file.
		 */
		void commit() {
			if (this.file.exists()) {
				this.file.delete();
			} else {
				ProjectWriter.this.fireFileNotFound(this.file);
			}
		}
	
	}
	
	
	/**
	 * Pair an object with the file to which it will be written.
	 */
	private class Write extends Change {
		private Object object;
	
		Write(Object object, File file) {
			super(file);
			this.object = object;
		}
	
		/**
		 * Use TopLink to write the object to the file.
		 */
		void commit() {
			try {
				this.commit2();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	
		private void commit2() throws IOException {
			this.checkDirectory();
			if (this.file.exists()) {
				// we do this because Windows file names are case-insensitive;
				// delete the original file so we don't re-use it for an object with the
				// same name but different case (e.g. we don't want to store the
				// "foo" object in the "FOO.xml" file, which is what would happen
				// on Windows if we don't delete the original file); this also allows
				// the project to be moved to a Linux machine without incident
				this.file.delete();
			}
			OutputStream stream = null;
			try {
				stream = new BufferedOutputStream(new FileOutputStream(this.file), 2048);
				this.marshaller().marshal(this.object, stream);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		}
	
		private void checkDirectory() {
			File dir = this.file.getParentFile();
			if ( ! dir.exists()) {
				if ( ! dir.mkdirs()) {
					throw new RuntimeException("unable to create directory: " + dir.getAbsolutePath());
				}
			}
		}
	
	}

}
