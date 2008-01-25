/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsio.legacy;

import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Session;
import deprecated.sdk.SDKFieldValue;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Record;
import deprecated.xml.JARClassLoader;
import deprecated.xml.XMLAccessor;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLFileLogin;
import deprecated.xml.XMLFileStreamPolicy;
import deprecated.xml.XMLStreamPolicy;
import deprecated.xml.XMLTranslator;

/**
 * This is just a cut-and-paste of the original XMLFileAccessor class.
 * This is only used for backward compatibility reading of 3.5-4.5 projects.
 */
class MWXMLFileAccessor45 extends deprecated.sdk.SDKAccessor implements XMLAccessor {

	/** The base directory for the tree of XML subdirectories/files. */
	private File baseDirectory;

	/** The extension to be appended to the generated XML file names. */
	private String fileExtension;

	/** Indicate whether to create directories if they are missing. */
	private boolean createsDirectoriesAsNeeded;

	/** The default XML translator for the XML calls. */
	private XMLTranslator translator;

	/** A list of the invalid file name characters that will be morphed into escape sequences. */
	private String invalidFileNameCharacters;

	/** A list of the default invalid file name characters that will be morphed into escape sequences.
	<p>		\ is the filename separator in DOS/Windows and the escape character in Unix
	<p>		/ is the filename separator in Unix and the command option tag in DOS
	<p>		: is the filename separator in MacOS and the drive indicator in DOS
	<p>		* is a DOS wildcard character
	<p>		? is a DOS wildcard character
	<p>		" is used by DOS to delimit file names with spaces
	<p>		< is a DOS redirection character
	<p>		> is a DOS redirection character
	<p>		| is a DOS redirection character
	<p>		& is our own escape character
	 */
	public static String DEFAULT_INVALID_FILENAME_CHARACTERS = "\\/:*?\"<>|&";

	/** Cache the value of the highest invalid file name character */
	private int maxInvalidFileNameCharacter;

	/** Cache the document directories, keyed by root element name */
	private Map documentDirectoryCache;
	private boolean cachesDocumentDirectories = true;

	/** The custom JARClassLoader. */
	private static ClassLoader customClassLoader;

	/** Performance tweak: .class generates a Class.forName(String), which can bite. */
	protected static final Class STRING_CLASS = String.class;

	/** This file filter will return accept all the files with the appropriate file
	extension, as determined by XMLFileAccessor.getFileExtension(String). */
	protected abstract class ExtensionFileFilter implements FileFilter {
		String elementName;

		public ExtensionFileFilter(String elementName) {
			this.elementName = elementName;
		}

		public boolean accept(File file) {
			// the file must be a normal file (i.e. not a directory)
			if ( ! this.isValidFile(file)) {
				return false;
			}
			// the file name must end with the appropriate extension
			String fileName = file.getName();
			String ext = MWXMLFileAccessor45.this.getFileExtension(elementName);
			int pos = fileName.lastIndexOf(ext);
			if (pos == -1) {
				return false;
			} else {
				return pos == (fileName.length() - ext.length());
			}
		}

		protected boolean isValidFile(File file) {
			return file.isFile();
		}
	}

	/**
	 * Default constructor.
	 */
	MWXMLFileAccessor45() {
		super();
		this.initialize();
	}
	/**
	 * Build and return the default custom class loader.
	 */
	private static ClassLoader buildCustomClassLoader() {
		String[] jarFileNames = DatabaseLogin.getXMLParserJARFileNames();
		if (jarFileNames == null ) {
			return null;
		} else {
			return buildCustomClassLoader(jarFileNames);
		}
	}
	/**
	 * Build and return a custom class loader that uses the specified JAR files.
	 */
	private static ClassLoader buildCustomClassLoader(String[] jarFileNames) {
		return new JARClassLoader(jarFileNames);
	}
	/**
	 * Build and return the default XML translator.
	 */
	protected XMLTranslator buildDefaultXMLTranslator() {
		return (XMLTranslator) this.instantiate("deprecated.xml.xerces.DefaultXMLTranslator");
	}
	/**
	 * Return the directory that holds all the documents
	 * with the specified root element name.
	 */
	protected File buildDocumentDirectory(String rootElementName) throws XMLDataStoreException {
		File directory = (File) documentDirectoryCache.get(rootElementName);
		if (directory == null) {
			directory = this.buildFile(this.getBaseDirectory(), rootElementName);
			this.checkDocumentDirectory(directory);
			documentDirectoryCache.put(rootElementName, directory);
		}
		return directory;
	}
	/**
	 * Return a file for the specified parent directory and child file name.
	 * Handle any invalid characters in the child file name.
	 */
	protected File buildFile(File parent, String childFileName) {
		return new File(parent, this.normalizeFileName(childFileName));
	}
	/**
	 * Return a file for the specified file name.
	 * Assume a valid file name. This allows us to create
	 * directories. In the future, we might want to check
	 * for characters that are invalid for directory names.
	 */
	protected File buildFile(String fileName) {
		return new File(fileName);
	}
	/**
	 * Return a file for the specified root element name and primary key.
	 * The name of this file will typically take the form of 
	 * 		[base dir]/[root element]/[key].xml
	 */
	protected File buildFile(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		return this.buildFile(this.buildDocumentDirectory(rootElementName), this.buildFileName(rootElementName, row, orderedPrimaryKeyElements));
	}
	/**
	 * Return a file filter that will return true for any
	 * file that is not a directory and that has the appropriate
	 * file extension (.xml, by default).
	 */
	protected FileFilter buildFileFilter(String rootElementName) {
		return new ExtensionFileFilter(rootElementName) {};
	}
	/**
	 * Return a file name for the specified primary key.
	 */
	protected String buildFileName(String rootElementName, DatabaseRecord row, Vector orderedPrimaryKeyElements) {
		return this.buildFileName(row, orderedPrimaryKeyElements, this.getFileExtension(rootElementName));
	}
	/**
	 * Return a file name for the specified primary key.
	 */
	protected String buildFileName(DatabaseRecord row, Vector orderedPrimaryKeyElements, String fileExtension) {
		Writer sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
	
		Vector values = this.extractPrimaryKeyValues(row, orderedPrimaryKeyElements);
		for (Enumeration stream = values.elements(); stream.hasMoreElements(); ) {
			pw.print(stream.nextElement());
		}
	
		pw.write(fileExtension);
		return sw.toString();
	}
	/**
	 * Return a stream policy for the specified file.
	 */
	protected XMLStreamPolicy buildStreamPolicy(File file) {
		return new XMLFileStreamPolicy(file);
	}
	/**
	 * Return a stream policy for the specified files.
	 */
	protected XMLStreamPolicy buildStreamPolicy(Enumeration files) {
		return new XMLFileStreamPolicy(files);
	}
	/**
	 * Return whether the document directories are cached.
	 */
	boolean cachesDocumentDirectories() {
		return cachesDocumentDirectories;
	}
	/**
	 * Calculate the maximum value of an invalid file name character.
	 * This will be used to short-circuit the search for an
	 * invalid file name character.
	 * @see charIsInvalidForAFileName(int)
	 */
	private void calculateMaxInvalidFileNameCharacter() {
		if (invalidFileNameCharacters == null) {
			throw new NullPointerException();
		}
		maxInvalidFileNameCharacter = 0;
		for (int i = 0; i < invalidFileNameCharacters.length(); i++) {
			char c = invalidFileNameCharacters.charAt(i);
			if (maxInvalidFileNameCharacter < c)
				maxInvalidFileNameCharacter = c;
		}
	}
	/**
	 * Return whether the specified character is an
	 * invalid character for a file name.
	 */
	protected boolean charIsInvalidForAFileName(int c) {
		return (c <= maxInvalidFileNameCharacter) &&
				(invalidFileNameCharacters.indexOf(c) >= 0);
	}
	/**
	 * Check whether the base directory exists.
	 * If appropriate, create it.
	 */
	protected void checkBaseDirectory() throws XMLDataStoreException {
		this.checkDirectory(this.getBaseDirectory());
	}
	/**
	 * Check whether the specified directory exists.
	 * If appropriate, create it.
	 */
	protected void checkDirectory(File directory) throws XMLDataStoreException {
		if (directory.exists()) {
			if ( ! directory.isDirectory()) {
				throw XMLDataStoreException.notADirectory(directory);
			}
		} else {
			if (this.createsDirectoriesAsNeeded()) {
				if ( ! directory.mkdirs()) {
					throw XMLDataStoreException.directoryCouldNotBeCreated(directory);
				}
			} else {
				throw XMLDataStoreException.directoryNotFound(directory);
			}
		}
	}
	/**
	 * Check whether the specified document directory exists.
	 * If appropriate, create it.
	 */
	protected void checkDocumentDirectory(File directory) throws XMLDataStoreException {
		this.checkDirectory(directory);
	}
	/**
	 * Establish a connection to the "data store".
	 */
	void connect(DatasourceLogin login, Session session) throws XMLDataStoreException {
		super.connect(login, (AbstractSession)session);
		if (! (login instanceof MWXMLFileLogin45)) {
			throw this.buildIncorrectLoginInstanceProvidedException(XMLFileLogin.class);
		}
		try {
			this.checkBaseDirectory();
		} catch (XMLDataStoreException e) {
			this.setIsConnected(false);
			throw e;
		}
		this.setIsConnected(true);
	}
	/**
	 * Convert an object to the specified class.
	 */
	protected Object convert(Object value, Class javaClass, Session session) {
		return session.getDatasourcePlatform().convertObject(value, javaClass);
	}
	/**
	 * Everything in XML must be strings.
	 */
	public Record convert(Record row, AbstractSession session) {
		DatabaseRecord result = new DatabaseRecord(row.size());
		for (Enumeration keys = ((DatabaseRecord)row).keys(); keys.hasMoreElements(); ) {
			DatabaseField key = (DatabaseField) keys.nextElement();
			Object value = row.get(key);
	
			if (value instanceof SDKFieldValue) {
				// recurse through nested rows
				value = this.convert((SDKFieldValue) value, session);
			} else {
				// everything else must be converted to a string
				value = this.convert(value, STRING_CLASS, session);
			}
	
			result.put(key, value);
		}
		return result;
	}
	/**
	 * Convert a nested collection of database rows.
	 */
	protected Object convert(SDKFieldValue fieldValue, AbstractSession session) {
		Vector newElements = new Vector(fieldValue.getElements().size());
		for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements(); ) {
			Object newElement = null;
			if (fieldValue.isDirectCollection()) {
				newElement = this.convert(stream.nextElement(), STRING_CLASS, session);
			} else {
				newElement = this.convert((DatabaseRecord) stream.nextElement(), session);
			}
			newElements.addElement(newElement);
		}
		return fieldValue.clone(newElements);
	}
	/**
	 * Return whether the accessor will create directories
	 * as needed, if they do not exist already.
	 */
	boolean createsDirectoriesAsNeeded() {
		return createsDirectoriesAsNeeded;
	}
	/**
	 * Create a source for data streams for
	 * the XML documents with the specified root element name.
	 */
	public void createStreamSource(String rootElementName) throws XMLDataStoreException {
		this.buildDocumentDirectory(rootElementName);
	}
	/**
	 * Delete the data for the specified root element and primary key.
	 * Return the stream count (1 or 0).
	 */
	public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		File file = this.buildFile(rootElementName, (DatabaseRecord)row, orderedPrimaryKeyElements);
		XMLStreamPolicy policy = this.buildStreamPolicy(file);
		return policy.deleteStream(rootElementName, row, orderedPrimaryKeyElements, this);
	}
	/**
	 * Drop the connection to the "data store".
	 */
	public void disconnect(Session session) throws XMLDataStoreException {
		super.disconnect((AbstractSession)session);
		documentDirectoryCache.clear();
	}
	/**
	 * Drop the specified directory and all of its contents.
	 */
	protected void dropDirectory(File directory) {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				this.dropDirectory(file);	// recurse through subdirectories
			} else {
				file.delete();
			}
		}
		directory.delete();
	}
	/**
	 * Drop the source for data streams for
	 * the XML documents with the specified root element name.
	 */
	public void dropStreamSource(String rootElementName) throws XMLDataStoreException {
		this.dropDirectory(this.buildDocumentDirectory(rootElementName));
	}
	/**
	 * Extract the values for the specified primary key elements
	 * from the specified row. Keep them in the same order
	 * as the elements.
	 */
	protected Vector extractPrimaryKeyValues(DatabaseRecord row, Vector orderedPrimaryKeyElements) {
		Vector result = new Vector(orderedPrimaryKeyElements.size());
		for (Enumeration stream = orderedPrimaryKeyElements.elements(); stream.hasMoreElements(); ) {
			result.addElement(row.get((DatabaseField) stream.nextElement()));
		}
		return result;
	}
	/**
	 * All the XML documents are stored in subdirectories
	 * under this base directory.
	 */
	protected File getBaseDirectory() {
		return baseDirectory;
	}
	/**
	 * Return a file for the temporary directory.
	 */
	protected File tempDirectory() {
		return this.buildFile(Helper.tempDirectory());
	}
	/**
	 * If necessary, use the custom class loader to pull classes out
	 * of our private stash.
	 * This prevents classpath conflicts....
	 */
	private ClassLoader getClassLoader() {
		ClassLoader result = this.getCustomClassLoader();
		if (result == null) {
			result = this.getClass().getClassLoader();
		}
		return result;
	}
	/**
	 * Lazy initialize the loader.
	 * Return the custom class loader, if appropriate;
	 * otherwise return null.
	 */
	private ClassLoader getCustomClassLoader() {
		if (customClassLoader == null) {
			customClassLoader = buildCustomClassLoader();
		}
		return customClassLoader;
	}
	/**
	 * Return the default standard file extension that will be
	 * appended to the primary key element value(s) to generate the
	 * complete file name.
	 */
	protected String getDefaultFileExtension() {
		return ".xml";
	}
	/**
	 * If it exists, return a read stream on the data for the specified
	 * root element and primary key.
	 * If it does not exist, return null.
	 */
	public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		File file = this.buildFile(rootElementName, (DatabaseRecord)row, orderedPrimaryKeyElements);
		XMLStreamPolicy policy = this.buildStreamPolicy(file);
		return policy.getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements, this);
	}
	/**
	 * Return a write stream that will overwrite the data for the specified
	 * root element and primary key.
	 */
	public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		File file = this.buildFile(rootElementName, (DatabaseRecord)row, orderedPrimaryKeyElements);
		XMLStreamPolicy policy = this.buildStreamPolicy(file);
		return policy.getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
	}
	/**
	 * Return the standard file extension that will be
	 * appended to the primary key element value(s) to generate the
	 * complete file name.
	 */
	String getFileExtension() {
		return fileExtension;
	}
	/**
	 * Return the standard file extension that will be
	 * appended to the primary key element value(s) to generate the
	 * complete file name.
	 */
	String getFileExtension(String rootElementName) {
		return this.getFileExtension();
	}
	/**
	 * Return the current platform-specific file separator
	 * (e.g. "\" in Windows).
	 */
	protected String getFileSeparator() {
		return Helper.fileSeparator();
	}
	/**
	 * Return an enumeration on a collection of files,
	 * one for every specified foreign key.
	 */
	protected Enumeration getFilesFor(String rootElementName, Vector foreignKeyRows, Vector orderedForeignKeyElements) {
		File[] files = new File[foreignKeyRows.size()];
		int i = 0;
		for (Enumeration stream = foreignKeyRows.elements(); stream.hasMoreElements(); ) {
			files[i++] = this.buildFile(rootElementName, (DatabaseRecord) stream.nextElement(), orderedForeignKeyElements);
		}
		return new FileListEnumerator(files);
	}
	/**
	 * Return an enumeration on a collection of files,
	 * one for *every* document with the specified document directory.
	 */
	protected Enumeration getFilesIn(File documentDirectory, String rootElementName) {
		return new FileListEnumerator(this.listFiles(documentDirectory, this.buildFileFilter(rootElementName)));
	}
	/**
	 * Return a list of the invalid file name characters
	 * that will be morphed into escape sequences.
	 */
	String getInvalidFileNameCharacters() {
		return invalidFileNameCharacters;
	}
	/**
	 * Return a new write stream for the specified
	 * root element and primary key.
	 */
	public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		File file = this.buildFile(rootElementName, (DatabaseRecord)row, orderedPrimaryKeyElements);
		XMLStreamPolicy policy = this.buildStreamPolicy(file);
		return policy.getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
	}
	/**
	 * Return a read stream on the data for the specified
	 * root element and primary key.
	 * If the stream is not found return null.
	 */
	public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
		File file = this.buildFile(rootElementName, (DatabaseRecord)row, orderedPrimaryKeyElements);
		XMLStreamPolicy policy = this.buildStreamPolicy(file);
		return policy.getReadStream(rootElementName, row, orderedPrimaryKeyElements, this);
	}
	/**
	 * Return an enumeration on a collection of read streams,
	 * one for *every* document with the specified root element.
	 */
	public Enumeration getReadStreams(String rootElementName) throws XMLDataStoreException {
		File directory = this.buildDocumentDirectory(rootElementName);
		XMLStreamPolicy policy = this.buildStreamPolicy(this.getFilesIn(directory, rootElementName));
		return policy.getReadStreams(rootElementName, this);
	}
	/**
	 * Return an enumeration on a collection of streams,
	 * one for every specified foreign key.
	 * If a particular stream is not found the
	 * enumeration will return null in its place.
	 */
	public Enumeration getReadStreams(String rootElementName, Vector foreignKeyRows, Vector orderedForeignKeyElements) throws XMLDataStoreException {
		XMLStreamPolicy policy = this.buildStreamPolicy(this.getFilesFor(rootElementName, foreignKeyRows, orderedForeignKeyElements));
		return policy.getReadStreams(rootElementName, foreignKeyRows, orderedForeignKeyElements, this);
	}
	/**
	 * Convenience method:
	 * Return the appropriately-casted login.
	 */
	protected XMLFileLogin getXMLFileLogin() {
		return (XMLFileLogin) this.getLogin();
	}
	/**
	 * Return the default XML translator for all data store calls.
	 */
	public XMLTranslator getXMLTranslator() {
		if (translator == null) {
			translator = this.buildDefaultXMLTranslator();
		}
		return translator;
	}
	/**
	 *	Initialize the accessor:<ul>
	 * <li>initialize the base directory
	 * <li>initialize the file extension
	 * <li>initialize the lazy creation flag
	 * </ul>
	 */
	protected void initialize() {
		baseDirectory = this.tempDirectory();
		fileExtension = this.getDefaultFileExtension();
		createsDirectoriesAsNeeded = false;
		invalidFileNameCharacters = DEFAULT_INVALID_FILENAME_CHARACTERS;
		this.calculateMaxInvalidFileNameCharacter();
		documentDirectoryCache = new HashMap();
	}
	/**
	 * Build and return an instance of the specified class.
	 */
	protected Object instantiate(Class javaClass) {
		try {
			return javaClass.newInstance();
		} catch (InstantiationException ie) {
			throw XMLDataStoreException.instantiationException(javaClass);
		} catch (IllegalAccessException iae) {
			throw XMLDataStoreException.instantiationIllegalAccessException(javaClass);
		}
	}
	/**
	 * Build and return an instance of the specified class.
	 */
	protected Object instantiate(String className) {
		return this.instantiate(this.loadClass(className));
	}
	/**
	 * Return an array of the files in the specified directory that
	 * match the specified filter.
	 */
	protected File[] listFiles(File directory, FileFilter fileFilter) throws XMLDataStoreException {
		return directory.listFiles(fileFilter);
	}
	/**
	 * Load the specified class with the appropriate class loader.
	 */
	protected Class loadClass(String className) {
		try {
			return Class.forName(className, true, this.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw XMLDataStoreException.classNotFound(className);
		}
	}
	/**
	 * Convert the specified (unqualified) file name into something
	 * that should be palatable as a file name
	 * (i.e. replace invalid characters with escape sequences).
	 */
	protected String normalizeFileName(String unqualifiedFileName) {
		// allow for a few invalid characters
		StringBuffer sb = new StringBuffer(unqualifiedFileName.length() + 20);
		for (int i = 0; i < unqualifiedFileName.length(); i++) {
			this.normalizeFileNameCharacterOn(unqualifiedFileName.charAt(i), sb);
		}
		return sb.toString();
	}
	/**
	 * Convert the specified (unqualified) file name character
	 * into something that should be palatable in a file name.
	 */
	protected void normalizeFileNameCharacterOn(char c, StringBuffer sb) {
		if (this.charIsInvalidForAFileName(c)) {
			this.normalizeInvalidFileNameCharacterOn(c, sb);
		} else {
			sb.append(c);
		}
	}
	/**
	 * Convert the specified invalid (unqualified) file name character
	 * into something that should be palatable in a file name
	 * (e.g. '/' => "&#x2f;").
	 */
	protected void normalizeInvalidFileNameCharacterOn(char c, StringBuffer sb) {
		sb.append("&#x");
		sb.append(Integer.toString((int) c, 16));
		sb.append(';');
	}
	/**
	 * All the XML documents will be stored in subdirectories
	 * under the specified base directory.
	 */
	protected void setBaseDirectoryName(String baseDirectoryName) {
		baseDirectory = this.buildFile(baseDirectoryName);
	}
	/**
	 * Set whether the document directories are cached.
	 */
	void setCachesDocumentDirectories(boolean cachesDocumentDirectories) {
		this.cachesDocumentDirectories = cachesDocumentDirectories;
	}
	/**
	 * Set whether the accessor will create directories
	 * as needed, if they do not exist already.
	 */
	void setCreatesDirectoriesAsNeeded(boolean createsDirectoriesAsNeeded) {
		this.createsDirectoriesAsNeeded = createsDirectoriesAsNeeded;
	}
	/**
	 * Set the standard file extension that will be
	 * appended to the primary key element value(s) to generate the
	 * complete file name.
	 */
	void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	/**
	 * Set the list of the invalid file name characters
	 * that will be morphed into escape sequences.
	 */
	void setInvalidFileNameCharacters(String invalidFileNameCharacters) {
		this.invalidFileNameCharacters = invalidFileNameCharacters;
		this.calculateMaxInvalidFileNameCharacter();
	}
	/**
	 * Set the default XML translator for all data store calls.
	 */
	public void setXMLTranslator(XMLTranslator translator) {
		this.translator = translator;
	}
	/**
	 * Append more information to the writer.
	 */
	protected void toString(PrintWriter writer) {
		writer.print(this.getBaseDirectory());
	}


class FileListEnumerator implements Enumeration {
		/** The list of files to iterate over. */
		private File[] fileList;
	
		/** An index to the next file to return. */
		private int next;
	/**
	 * Default constructor.
	 */
	private FileListEnumerator() {
		super();
		next = 0;
	}
	/**
	 * Constructor. Provide the files to be
	 * iterated over.
	 */
	FileListEnumerator(File[] fileList) {
		this();
		this.fileList = fileList;
	}
	/**
	 * Return whether the enumerator has any 
	 * more elements to return.
	 */
	public boolean hasMoreElements() {
		return next < fileList.length;
	}
	/**
	 * Return the next element in the
	 * enumeration. This will be a File.
	 */
	public Object nextElement() {
		if (next < fileList.length) {
			return fileList[next++];
		} else {
			throw new NoSuchElementException("FileListEnumerator");
		}
	}
}
}
