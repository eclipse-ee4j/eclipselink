/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.io.*;
import java.lang.reflect.Field;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Record;
import deprecated.sdk.SDKFieldValue;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <code>XMLFileAccessor</code> is an implementation
 * of the <code>XMLAccessor</code> interface that uses the
 * native O/S file system to store XML documents:<ul>
 * <li>The base directory is analogous to a relational database that
 * contains a collection of related tables.
 * <li>Each subdirectory in the base directory is analogous to a table
 * and is named accordingly.
 * <li>Each file in a given subdirectory is analogous to a row within
 * the table. The name of the file is derived from the primary key of the
 * row, giving us a primitive index (the file system directory).
 * <li>Each file consists of a single XML document. The root element of
 * the XML document has the same name as the containing subdirectory.
 * <li>The child elements contained within the root element are analogous
 * to the fields within a row. But these can be nested a bit more than
 * a normalized relational database rows, with repeating elements and
 * the like.
 * </ul>
 * The above-described default behavior for organizing the accessor's files
 * can be overridden at the root element name level by using
 * <code>XMLFileAccessorFilePolicy</code>s. A file policy can be used to
 * define what files are used for a specific root element name.
 * <p>
 * If necessary, this <code>Accessor</code> will dynamically load a
 * <code>DefaultXMLTranslator</code>, either by using a
 * <code>JARClassLoader</code>, to load the translator from specific set of JAR files,
 * or by using the current class loader, if no JAR files have been specified.
 * The static method <code>DatabaseLogin.setXMLParserJARFileNames()</code>
 * is used to set the paths to the JAR files to be used for custom class loading.
 *
 * @see XMLCall
 * @see XMLFileLogin
 * @see JARClassLoader
 * @see org.eclipse.persistence.sessions.DatabaseLogin
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLFileAccessor extends deprecated.sdk.SDKAccessor implements XMLAccessor {

    /** The file policies, keyed by root element name. */
    private Map filePolicies;
    private XMLFileAccessorFilePolicy defaultFilePolicy;

    /** The default XML translator for the XML calls. */
    private XMLTranslator translator;

    /** The custom JARClassLoader. */
    private static ClassLoader customClassLoader;

    /** Performance tweak: .class generates a Class.forName(String), which can bite. */
    protected static final Class STRING_CLASS = String.class;

    // constructors

    /**
     * Default constructor.
     */
    public XMLFileAccessor() {
        super();
        this.setFilePolicies(this.buildDefaultFilePolicies());
        this.setDefaultFilePolicy(this.buildDefaultDefaultFilePolicy());
    }

    // static methods

    /**
     * Build and return the default custom class loader.
     */
    private static ClassLoader buildCustomClassLoader() {
        String[] jarFileNames = DatabaseLogin.getXMLParserJARFileNames();
        if (jarFileNames == null) {
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

    // instance methods

    /**
     * Set the file policy for the specified root element name.
     */
    public void addFilePolicy(String rootElementName, XMLFileAccessorFilePolicy filePolicy) {
        this.getFilePolicies().put(rootElementName, filePolicy);
    }

    /**
     * Build and return the default file policy map.
     * By default, it is empty.
     */
    protected Map buildDefaultFilePolicies() {
        return new HashMap();
    }

    /**
     * Build and return the default file policy.
     * The default policy generates files as described in
     * the class comment above.
     */
    protected XMLFileAccessorFilePolicy buildDefaultDefaultFilePolicy() {
        return new DefaultXMLFileAccessorFilePolicy();
    }

    /**
     * Build and return the default XML translator.
     */
    protected XMLTranslator buildDefaultXMLTranslator() {
        return (XMLTranslator)this.instantiate("deprecated.xml.xerces.DefaultXMLTranslator");
    }

    /**
     * Return a stream policy for the specified files.
     */
    protected XMLStreamPolicy buildStreamPolicy(Enumeration files) {
        return new XMLFileStreamPolicy(files);
    }

    /**
     * Return a stream policy for the specified file.
     */
    protected XMLStreamPolicy buildStreamPolicy(File file) {
        return new XMLFileStreamPolicy(file);
    }

    /**
     * Establish a connection to the "data store".
     */
    public void connect(Login login, AbstractSession session) throws XMLDataStoreException {
        super.connect(login, session);
        if (!(login instanceof XMLFileLogin)) {
            throw this.buildIncorrectLoginInstanceProvidedException(XMLFileLogin.class);
        }
        try {
            this.validateFilePolicies();
        } catch (XMLDataStoreException e) {
            this.setIsConnected(false);
            throw e;
        }
        this.setIsConnected(true);
    }

    /**
     * Convert an object to the specified class.
     */
    protected Object convert(Object value, Class javaClass, AbstractSession session) {
        return session.getDatasourcePlatform().convertObject(value, javaClass);
    }

    /**
     * Everything in XML must be strings.
     */
    public Record convert(Record row, AbstractSession session) {
        if ((row == null) || row.isEmpty()) {
            return row;
        }
        AbstractRecord result = new DatabaseRecord(row.size());
        for (Enumeration keys = ((AbstractRecord)row).keys(); keys.hasMoreElements();) {
            DatabaseField key = (DatabaseField)keys.nextElement();
            Object value = row.get(key);

            if (value instanceof SDKFieldValue) {
                // recurse through nested rows
                value = this.convert((SDKFieldValue)value, session);
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
        for (Enumeration stream = fieldValue.getElements().elements(); stream.hasMoreElements();) {
            Object newElement = null;
            if (fieldValue.isDirectCollection()) {
                newElement = this.convert(stream.nextElement(), STRING_CLASS, session);
            } else {
                newElement = this.convert((Record)stream.nextElement(), session);
            }
            newElements.addElement(newElement);
        }
        return fieldValue.clone(newElements);
    }

    /**
     * Create a source for data streams for
     * the XML documents with the specified root element name.
     */
    public void createStreamSource(String rootElementName) throws XMLDataStoreException {
        this.getFilePolicy(rootElementName).createFileSource(rootElementName);
    }

    /**
     * Delete the data for the specified root element and primary key.
     * Return the stream count (1 or 0).
     */
    public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        File file = this.getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
        XMLStreamPolicy policy = this.buildStreamPolicy(file);
        return policy.deleteStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Drop the connection to the "data store".
     */
    public void disconnect(AbstractSession session) throws XMLDataStoreException {
        super.disconnect(session);
    }

    /**
     * Drop the source for data streams for
     * the XML documents with the specified root element name.
     */
    public void dropStreamSource(String rootElementName) throws XMLDataStoreException {
        this.getFilePolicy(rootElementName).dropFileSource(rootElementName);
    }

    /**
     * If necessary, use the custom class loader to pull classes out
     * of our private stash.
     * This prevents classpath conflicts....
     */
    private ClassLoader getClassLoader() {
        ClassLoader result = this.getCustomClassLoader();
        if (result == null) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    result = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(this.getClass()));
                }catch (PrivilegedActionException ex){
                    throw (RuntimeException) ex.getCause();
                }
            }else{
                result = PrivilegedAccessHelper.getClassLoaderForClass(this.getClass());
            }
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
            customClassLoader = XMLFileAccessor.buildCustomClassLoader();
        }
        return customClassLoader;
    }

    /**
     * Return the default file policy.
     */
    public XMLFileAccessorFilePolicy getDefaultFilePolicy() {
        return defaultFilePolicy;
    }

    /**
     * If it exists, return a read stream on the data for the specified
     * root element and primary key.
     * If it does not exist, return null.
     */
    public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        File file = this.getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
        XMLStreamPolicy policy = this.buildStreamPolicy(file);
        return policy.getExistenceCheckStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return a write stream that will overwrite the data for the specified
     * root element and primary key.
     */
    public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        File file = this.getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
        XMLStreamPolicy policy = this.buildStreamPolicy(file);
        return policy.getExistingWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return the map of file policies keyed by root element name.
     */
    protected Map getFilePolicies() {
        return filePolicies;
    }

    /**
     * Return the file policy for the specified root element name.
     */
    public XMLFileAccessorFilePolicy getFilePolicy(String rootElementName) {
        XMLFileAccessorFilePolicy filePolicy = (XMLFileAccessorFilePolicy)this.getFilePolicies().get(rootElementName);
        if (filePolicy == null) {
            filePolicy = this.getDefaultFilePolicy();
        }
        return filePolicy;
    }

    /**
     * Return a new write stream for the specified
     * root element and primary key.
     */
    public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        File file = this.getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
        XMLStreamPolicy policy = this.buildStreamPolicy(file);
        return policy.getNewWriteStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return a read stream on the data for the specified
     * root element and primary key.
     * If the stream is not found return null.
     */
    public Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws XMLDataStoreException {
        File file = this.getFilePolicy(rootElementName).getFile(rootElementName, row, orderedPrimaryKeyElements);
        XMLStreamPolicy policy = this.buildStreamPolicy(file);
        return policy.getReadStream(rootElementName, row, orderedPrimaryKeyElements, this);
    }

    /**
     * Return an enumeration on a collection of read streams,
     * one for *every* document with the specified root element.
     */
    public Enumeration getReadStreams(String rootElementName) throws XMLDataStoreException {
        Enumeration files = this.getFilePolicy(rootElementName).getAllFiles(rootElementName);
        XMLStreamPolicy policy = this.buildStreamPolicy(files);
        return policy.getReadStreams(rootElementName, this);
    }

    /**
     * Return an enumeration on a collection of streams,
     * one for every specified foreign key.
     * If a particular stream is not found the
     * enumeration will return null in its place.
     */
    public Enumeration getReadStreams(String rootElementName, Vector foreignKeyRows, Vector orderedForeignKeyElements) throws XMLDataStoreException {
        return new StreamEnumerator(rootElementName, foreignKeyRows, orderedForeignKeyElements);
    }

    /**
     * Convenience method:
     * Return the appropriately-casted login.
     */
    protected XMLFileLogin getXMLFileLogin() {
        return (XMLFileLogin)this.getLogin();
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
     * Build and return an instance of the specified class.
     */
    protected Object instantiate(Class javaClass) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    return AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(javaClass));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof IllegalAccessException){
                        throw (IllegalAccessException)ex.getCause();
                    }
                    if (ex.getCause() instanceof InstantiationException){
                        throw (InstantiationException)ex.getCause();
                    }
                    throw (RuntimeException) ex.getCause();
                }
            }else{
                return PrivilegedAccessHelper.newInstanceFromClass(javaClass);
            }
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
     * Load the specified class with the appropriate class loader.
     */
    protected Class loadClass(String className) {
        try {
            ClassLoader classLoader = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    classLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(this.getClass()));
                }catch (PrivilegedActionException ex){
                    throw (RuntimeException) ex.getCause();
                }
            }else{
                classLoader = PrivilegedAccessHelper.getClassLoaderForClass(this.getClass());
            }
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw XMLDataStoreException.classNotFound(className);
        }
    }

    /**
     * Used by XMLFileLogin.
     */
    void setBaseDirectoryName(String baseDirectoryName) {
        ((DefaultXMLFileAccessorFilePolicy)this.getDefaultFilePolicy()).setBaseDirectoryName(baseDirectoryName);
    }

    /**
     * Used by XMLFileLogin.
     */
    void setCreatesDirectoriesAsNeeded(boolean createsDirectoriesAsNeeded) {
        ((DefaultXMLFileAccessorFilePolicy)this.getDefaultFilePolicy()).setCreatesDirectoriesAsNeeded(createsDirectoriesAsNeeded);
    }

    /**
     * Set the default file policy.
     */
    public void setDefaultFilePolicy(XMLFileAccessorFilePolicy defaultFilePolicy) {
        this.defaultFilePolicy = defaultFilePolicy;
    }

    /**
     * Used by XMLFileLogin.
     */
    void setFileNameExtension(String fileNameExtension) {
        ((DefaultXMLFileAccessorFilePolicy)this.getDefaultFilePolicy()).setFileNameExtension(fileNameExtension);
    }

    /**
     * Set the map of file policies keyed by root element name.
     */
    protected void setFilePolicies(Map filePolicies) {
        this.filePolicies = filePolicies;
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
        writer.print(this.getDefaultFilePolicy());
    }

    /**
     * Validate the policies' settings.
     */
    protected void validateFilePolicies() {
        this.getDefaultFilePolicy().validateConfiguration();
        for (Iterator stream = this.getFilePolicies().values().iterator(); stream.hasNext();) {
            ((XMLFileAccessorFilePolicy)stream.next()).validateConfiguration();
        }
    }

    // inner classes

    /**
     * This file filter will "accept" all the normal files that have the file
     * extension specified in the constructor.
     */
    protected class StreamEnumerator implements Enumeration {

        /** The parms that will to be passed back to the accessor. */
        private String rootElementName;
        private Enumeration foreignKeyRows;
        private Vector orderedForeignKeyElements;

        public StreamEnumerator(String rootElementName, Vector foreignKeyRows, Vector orderedForeignKeyElements) {
            this.rootElementName = rootElementName;
            this.foreignKeyRows = foreignKeyRows.elements();
            this.orderedForeignKeyElements = orderedForeignKeyElements;
        }

        public boolean hasMoreElements() {
            return foreignKeyRows.hasMoreElements();
        }

        public Object nextElement() {
            return XMLFileAccessor.this.getReadStream(rootElementName, (Record)foreignKeyRows.nextElement(), orderedForeignKeyElements);
        }
    }
}