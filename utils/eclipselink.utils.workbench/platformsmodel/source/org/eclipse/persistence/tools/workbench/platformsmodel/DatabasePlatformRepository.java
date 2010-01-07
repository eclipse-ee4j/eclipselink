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
package org.eclipse.persistence.tools.workbench.platformsmodel;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.XMLTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.DefaultChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * This is a repository of all the database platforms in the platforms
 * resource directory. We also hold on to the JDBC type repository.
 */
public final class DatabasePlatformRepository
	extends AbstractNodeModel
{
	/** something worth displaying */
	private String name;
		public static final String NAME_PROPERTY = "name";

	/** the file that holds the platform repository settings and the JDBC type repository */
	private File file;
		public static final String FILE_PROPERTY = "file";

	/** the database platforms */
	private Collection platforms;
		public static final String PLATFORMS_COLLECTION = "platforms";

	/** the default database platform */
	private DatabasePlatform defaultPlatform;
		public static final String DEFAULT_PLATFORM_PROPERTY = "defaultPlatform";

	/** used to map Java and JDBC types to each other */
	private JDBCTypeRepository jdbcTypeRepository;
		// the JDBC type repository is never replaced once the platform repository is built

	/** used to notifier listeners of changes */
	private ChangeNotifier changeNotifier;

	/** used to generate problems */
	private Validator validator;

	/**
	 * store the file names, so we can determine what to delete on save;
	 * this is transient and for internal use only
	 */
	private Collection originalPlatformShortFileNames;

	/**
	 * store the original file when it is renamed (but not moved),
	 * so we can delete it on save;
	 * this is transient and for internal use only
	 */
	private File originalFile;


	/**
	 * the name of the directory that holds the platform XML files,
	 * relative to the repository file's location
	 */
	private static final String PLATFORMS_DIRECTORY_NAME = "platforms";

	/**
	 * the name of the default database platform repository file,
	 * it should be on the classpath
	 */
	private static final String DEFAULT_PLATFORM_REPOSITORY_FILE_NAME = "platforms.dpr";

	/**
	 * the default database platform repository, built from the file
	 * named above
	 */
	private static DatabasePlatformRepository defaultRepository;


	// ********** static methods **********

	/**
	 * return the default database platform repository, which is built
	 * from the file platforms.dpr found on the classpath
	 */
	public static DatabasePlatformRepository getDefault() {
		if (defaultRepository == null) {
			defaultRepository = buildDefault();
		}
		return defaultRepository;
	}

	private static DatabasePlatformRepository buildDefault() {
		try {
			return new DatabasePlatformRepository(buildDefaultFile());
		} catch (CorruptXMLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static File buildDefaultFile() {
		try {
			return FileTools.resourceFile("/" + DEFAULT_PLATFORM_REPOSITORY_FILE_NAME);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}


	// ********** constructors **********

	/**
	 * (when reading in an existing repository)
	 * clients must specify where we find the file...
	 */
	public DatabasePlatformRepository(File file) throws CorruptXMLException {
		super();
		if (file == null) {
			throw new NullPointerException();
		}
		this.file = file;
		this.read();
	}

	/**
	 * ...or clients must specify the name of the repository
	 * (when building one from scratch)
	 */
	public DatabasePlatformRepository(String name) {
		super();
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.jdbcTypeRepository = new JDBCTypeRepository(this);
		this.originalPlatformShortFileNames = Collections.EMPTY_SET;
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.platforms = new Vector();
		this.changeNotifier = DefaultChangeNotifier.instance();
		this.validator = NULL_VALIDATOR;
	}


	// ********** accessors **********

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}


	/**
	 * this will only be null on a newly-created, unsaved repository
	 */
	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		File old = this.file;
		this.file = file;
		this.firePropertyChanged(FILE_PROPERTY, old, file);

		if ((old != null) && old.exists()) {
			if (old.getParentFile().equals(file.getParentFile())) {
				// if the file was renamed but not moved, save the original for later deletion
				if ( ! old.getName().equals(file.getName())) {
					this.originalFile = old;
				}
			} else {
				// if the location of the file has changed, mark everything dirty...
				this.markEntireBranchDirty();
				// ...and clear out the original file names, since we won't be deleting them on save
				this.originalPlatformShortFileNames = Collections.EMPTY_SET;
			}
		}
	}


	public Iterator platforms() {
		return new CloneIterator(this.platforms) {
			protected void remove(Object current) {
				DatabasePlatformRepository.this.removePlatform((DatabasePlatform) current);
			}
		};
	}

	public int platformsSize() {
		return this.platforms.size();
	}

	/**
	 * the file name is the "short" file name - it should not include the directory;
	 * since the file name is no longer derived from the platform name,
	 * we need to specify both the name of the platform and where
	 * it will be stored, both of which will be checked for uniqueness
	 */
	public DatabasePlatform addPlatform(String platformName, String platformShortFileName) {
		this.checkPlatform(platformName, platformShortFileName);
		return this.addPlatform(new DatabasePlatform(this, platformName, platformShortFileName));
	}

	private DatabasePlatform addPlatform(DatabasePlatform platform) {
		this.addItemToCollection(platform, this.platforms, PLATFORMS_COLLECTION);
		// the repository itself is not "dirty", but its branch is
		this.markBranchDirty();
		if (this.defaultPlatform == null) {
			this.setDefaultPlatform(platform);
		}
		return platform;
	}

	public void removePlatform(DatabasePlatform platform) {
		this.removeItemFromCollection(platform, this.platforms, PLATFORMS_COLLECTION);
		// the repository itself is not "dirty", but its branch is
		this.markBranchDirty();
		this.resetDefaultPlatform();
	}

	public void removePlatforms(Collection pforms) {
		this.removeItemsFromCollection(pforms, this.platforms, PLATFORMS_COLLECTION);
		// the repository itself is not "dirty", but its branch is
		this.markBranchDirty();
		this.resetDefaultPlatform();
	}

	public void removePlatforms(Iterator pforms) {
		this.removeItemsFromCollection(pforms, this.platforms, PLATFORMS_COLLECTION);
		// the repository itself is not "dirty", but its branch is
		this.markBranchDirty();
		this.resetDefaultPlatform();
	}


	/**
	 * this will only be null when we have no platforms
	 */
	public DatabasePlatform getDefaultPlatform() {
		return this.defaultPlatform;
	}

	/**
	 * the default cannot be set to null unless we have no
	 * platforms
	 */
	public void setDefaultPlatform(DatabasePlatform defaultPlatform) {
		if ((defaultPlatform == null) && (this.platforms.size() > 0)) {
			throw new NullPointerException();
		}
		Object old = this.defaultPlatform;
		this.defaultPlatform = defaultPlatform;
		this.firePropertyChanged(DEFAULT_PLATFORM_PROPERTY, old, defaultPlatform);
	}


	public JDBCTypeRepository getJDBCTypeRepository() {
		return this.jdbcTypeRepository;
	}


	/**
	 * as the root node, we must implement this method
	 */
	public ChangeNotifier getChangeNotifier() {
		return this.changeNotifier;
	}

	/**
	 * allow clients to install another change notifier
	 */
	public void setChangeNotifier(ChangeNotifier changeNotifier) {
		this.changeNotifier = changeNotifier;
	}


	/**
	 * as the root node, we must implement this method
	 */
	public Validator getValidator() {
		return this.validator;
	}

	/**
	 * allow clients to install an active validator
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}


	// ********** queries **********

	private File platformsDirectory() {
		return new File(this.file.getParentFile(), PLATFORMS_DIRECTORY_NAME);
	}

	public DatabasePlatform platformNamed(String databasePlatformName) {
		synchronized (this.platforms) {
			for (Iterator stream = this.platforms.iterator(); stream.hasNext(); ) {
				DatabasePlatform platform = (DatabasePlatform) stream.next();
				if (platform.getName().equals(databasePlatformName)) {
					return platform;
				}
			}
			throw new IllegalArgumentException("missing database platform named: " + databasePlatformName);
		}
	}

	public DatabasePlatform platformForRuntimePlatformClassNamed(String runtimePlatformClassName) {
		synchronized (this.platforms) {
			for (Iterator stream = this.platforms.iterator(); stream.hasNext(); ) {
				DatabasePlatform platform = (DatabasePlatform) stream.next();
				if (platform.getRuntimePlatformClassName().equals(runtimePlatformClassName)) {
					return platform;
				}
			}
			throw new IllegalArgumentException("missing database platform for run-time platform class: " + runtimePlatformClassName);
		}
	}

	private Iterator platformNames() {
		return new TransformationIterator(this.platforms()) {
			protected Object transform(Object next) {
				return ((DatabasePlatform) next).getName();
			}
		};
	}

	private Iterator platformShortFileNames() {
		return new TransformationIterator(this.platforms()) {
			protected Object transform(Object next) {
				return ((DatabasePlatform) next).getShortFileName();
			}
		};
	}

	private Iterator lowerCasePlatformShortFileNames() {
		return new TransformationIterator(this.platformShortFileNames()) {
			protected Object transform(Object next) {
				return ((String) next).toLowerCase();
			}
		};
	}


	// ********** behavior **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.platforms) { children.addAll(this.platforms); }
		children.add(this.jdbcTypeRepository);
	}

	protected void addTransientAspectNamesTo(Set transientAspectNames) {
		super.addTransientAspectNamesTo(transientAspectNames);
		transientAspectNames.add(PLATFORMS_COLLECTION);
	}

	protected void addProblemsTo(List currentProblems) {
		if (this.platforms.isEmpty()) {
			currentProblems.add(this.buildProblem("001"));
		}
		super.addProblemsTo(currentProblems);
	}

	/**
	 * check whether the default platform is still in the repository;
	 * if it's not, fix it
	 */
	private void resetDefaultPlatform() {
		synchronized (this.platforms) {
			if ( ! this.platforms.contains(this.defaultPlatform)) {
				if (this.platforms.isEmpty()) {
					this.setDefaultPlatform(null);
				} else {
					this.setDefaultPlatform((DatabasePlatform) this.platforms.iterator().next());
				}
			}
		}
	}

	/**
	 * add and return a clone of the specified platform;
	 * the clone will be identical to the original, except its
	 * name and file name will be slightly different
	 */
	public DatabasePlatform clone(DatabasePlatform original) {
		String originalName = original.getName();
		String originalFileName = original.getShortFileName();
		String originalFileNameBase = FileTools.stripExtension(originalFileName);
		String originalFileNameExtension = FileTools.extension(originalFileName);

		DatabasePlatform clone = null;
		int cloneCount = 1;
		boolean success = false;
		while ( ! success) {
			cloneCount++;
			String cloneName = originalName + cloneCount;
			String cloneFileName = originalFileNameBase + cloneCount + originalFileNameExtension;
			try {
				clone = this.addPlatform(cloneName, cloneFileName);
				success = true;
			} catch (IllegalArgumentException ex) {
				String msg = ex.getMessage();
				if ((msg.indexOf(cloneName) != -1) || (msg.indexOf(cloneFileName) != -1)) {
					continue;	// try again
				}
				throw ex;	// must be some other problem...
			}
		}
		clone.cloneFrom(original);
		return clone;
	}

	/**
	 * tell all the platforms a JDBC type has been added to the
	 * JDBC type repository, so they need to synchronize
	 */
	void jdbcTypeAdded(JDBCType addedJDBCType) {
		synchronized (this.platforms) {
			for (Iterator stream = this.platforms.iterator(); stream.hasNext(); ) {
				((DatabasePlatform) stream.next()).jdbcTypeAdded(addedJDBCType);
			}
		}
	}

	/**
	 * disallow duplicate platform names and files within a single repository
	 */
	private void checkPlatform(DatabasePlatform platform) {
		this.checkPlatform(platform.getName(), platform.getShortFileName());
	}

	private void checkPlatform(String platformName, String platformShortFileName) {
		this.checkPlatformName(platformName);
		this.checkPlatformShortFileName(platformShortFileName);
	}

	/**
	 * check whether a platform with the same name already exists;
	 * if it does, throw an IllegalArgumentException
	 */
	void checkPlatformName(String platformName) {
		if ((platformName == null) || (platformName.length() == 0)) {
			throw new IllegalArgumentException("platform name is required");
		}
		if (CollectionTools.contains(this.platformNames(), platformName)) {
			throw new IllegalArgumentException("duplicate platform name: " + platformName);
		}
	}

	/**
	 * check whether a platform with the same file name already exists;
	 * if it does, throw an IllegalArgumentException;
	 * ignore case since Windows file names are case-insensitive - meaning
	 * we cannot have two files whose names differ only by their case
	 */
	void checkPlatformShortFileName(String platformShortFileName) {
		if ((platformShortFileName == null) || (platformShortFileName.length() == 0)) {
			throw new IllegalArgumentException("platform short file name is required");
		}
		if (FileTools.fileNameIsInvalid(platformShortFileName)) {
			throw new IllegalArgumentException("invalid file name: " + platformShortFileName);
		}
		if (CollectionTools.contains(this.lowerCasePlatformShortFileNames(), platformShortFileName.toLowerCase())) {
			throw new IllegalArgumentException("duplicate file name: " + platformShortFileName);
		}
	}


	// ********** i/o **********

	// ***** read
	private void read() throws CorruptXMLException {
		Document document = XMLTools.parse(this.file);
		Node root = XMLTools.child(document, "platforms");
		if (root == null) {
			throw this.buildCorruptXMLException("missing root node: platforms");
		}

		this.name = XMLTools.childTextContent(root, "name", null);
		if ((this.name == null) || (this.name.length() == 0)) {
			throw this.buildCorruptXMLException("name is required");
		}

		ClassTools.setFieldValue(this, "comment", XMLTools.childTextContent(root, "comment", ""));

		// read up the JDBC repository first, since the JDBC types are referenced elsewhere
		this.jdbcTypeRepository = new JDBCTypeRepository(this, XMLTools.child(root, "jdbc-type-repository"));

		this.readPlatforms();

		String defaultPlatformName = XMLTools.childTextContent(root, "default-platform", null);
		if ((defaultPlatformName == null) || (defaultPlatformName.length() == 0)) {
			if (this.platforms.size() == 0) {
				// no problem
			} else {
				throw this.buildCorruptXMLException("default platform name is required");
			}
		} else {
			if (this.platforms.size() == 0) {
				throw this.buildCorruptXMLException("default platform should not be specified when there are no platforms");
			}
			try {
				this.defaultPlatform = this.platformNamed(defaultPlatformName);
			} catch (IllegalArgumentException ex) {
				throw this.buildCorruptXMLException(ex);
			}
		}

		// now save all the platform file names for later
		this.originalPlatformShortFileNames = CollectionTools.collection(this.platformShortFileNames());

		this.markEntireBranchClean();
	}

	/**
	 * read in all the platform files
	 */
	private void readPlatforms() throws CorruptXMLException {
		File platformsDirectory = this.platformsDirectory();
		if (platformsDirectory.exists() && platformsDirectory.isDirectory()) {
			File[] platformFiles = platformsDirectory.listFiles();
			for (int i = platformFiles.length; i-- > 0; ) {
				this.readPlatform(platformFiles[i]);
			}
		}
	}

	/**
	 * read only files with an extension of .xml
	 */
	private void readPlatform(File platformFile) throws CorruptXMLException {
		if (platformFile.isFile() && FileTools.extension(platformFile).toLowerCase().equals(".xml")) {
			DatabasePlatform platform = new DatabasePlatform(this, platformFile);
			try {
				this.checkPlatform(platform);	// check for duplicates
			} catch (IllegalArgumentException ex) {
				throw this.buildCorruptXMLException(ex);
			}
			this.platforms.add(platform);
		}
	}

	/**
	 * tack the repository file on to the message
	 */
	private CorruptXMLException buildCorruptXMLException(String message) {
		return new CorruptXMLException(message + " (" + this.file.getPath() + ")");
	}

	/**
	 * tack the repository file on to the message
	 */
	private CorruptXMLException buildCorruptXMLException(Throwable t) {
		return new CorruptXMLException(this.file.getPath(), t);
	}

	// ***** write
	public void write() {
		if (this.isCleanBranch()) {
			return;
		}
		if (this.file == null) {
			throw new IllegalStateException("the repository's file must be set before it is written");
		}
		// write the platforms first, that might be all we need to write out
		this.writePlatforms();

		// if, after writing out all the platforms, the repository is still dirty,
		// we need to write out the repository itself
		if (this.isDirtyBranch()) {
			this.writeRepositoryFile();
			this.markEntireBranchClean();
		}
	}

	private void writePlatforms() {
		File platformsDirectory = this.platformsDirectory();
		if (platformsDirectory.exists()) {
			if ( ! platformsDirectory.isDirectory()) {
				throw new IllegalStateException("platforms directory is not a directory: " + platformsDirectory.getAbsolutePath());
			}
		} else {
			if ( ! platformsDirectory.mkdirs()) {
				throw new RuntimeException("unable to create platforms directory: " + platformsDirectory.getAbsolutePath());
			}
		}
		this.deleteOldPlatformFiles(platformsDirectory);
		synchronized (this.platforms) {
			for (Iterator stream = this.platforms.iterator(); stream.hasNext(); ) {
				((DatabasePlatform) stream.next()).write(platformsDirectory);
			}
		}
	}

	/**
	 * delete the platform files that were read in
	 * earlier but are no longer needed
	 */
	private void deleteOldPlatformFiles(File platformsDirectory) {
		// build the list of files to be deleted
		Collection deletedPlatformFileNames = new HashBag(this.originalPlatformShortFileNames);
		Collection currentPlatformFileNames = CollectionTools.collection(this.platformShortFileNames());
		deletedPlatformFileNames.removeAll(currentPlatformFileNames);

		// now delete them
		for (Iterator stream = deletedPlatformFileNames.iterator(); stream.hasNext(); ) {
			String fileName = (String) stream.next();
			(new File(platformsDirectory, fileName)).delete();
		}

		// reset the file names for the next write
		this.originalPlatformShortFileNames = currentPlatformFileNames;
	}

	private void writeRepositoryFile() {
		Document document = XMLTools.newDocument();
		Node root = document.createElement("platforms");
		document.appendChild(root);
		XMLTools.addSimpleTextNode(root, "name", this.name);

		XMLTools.addSimpleTextNode(root, "comment", (String) ClassTools.getFieldValue(this, "comment"), "");

		// the default platform can be null when there are no platforms
		if (this.defaultPlatform != null) {
			XMLTools.addSimpleTextNode(root, "default-platform", this.defaultPlatform.getName());
		}

		this.jdbcTypeRepository.write(root.appendChild(document.createElement("jdbc-type-repository")));

		XMLTools.print(document, this.file);

		if (this.originalFile != null) {
			// the "original file" is only set when the repos file is renamed but not moved
			if ( ! this.originalFile.delete()) {
				throw new RuntimeException("unable to delete original file: " + this.originalFile.getPath());
			}
			this.originalFile = null;
		}
	}


	// ********** printing and displaying **********

	public String displayString() {
		return this.name;
	}

	public void toString(StringBuffer sb) {
		for (Iterator stream = this.platforms(); stream.hasNext(); ) {
			DatabasePlatform platform = (DatabasePlatform) stream.next();
			platform.toString(sb);
			if (stream.hasNext()) {
				sb.append(", ");
			}
		}
	}

}
