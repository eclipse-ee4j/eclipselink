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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.io.NullWriter;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import deprecated.xml.XMLAccessor;
import deprecated.xml.XMLDataStoreException;
import deprecated.xml.XMLInsertCall;
/**
 * This override policy simulates the reading and writing of MWClass
 * XML files. Writes are simulated by simply do nothing; while reads are
 * simulated by dynamically generating the appropriate XML stream in
 * memory.
 */
abstract class DynamicClassOverridePolicy50 implements MWAccessorOverridePolicy50 {
	/** backpointer to parent, so we can delete files */
	private XMLAccessor parentAccessor;

	/** the writer for simulating writes - it does nothing */
	private Writer nullWriter;

	/** the root element name used for MWClasses */
	private String rootElementName;

	/**
	 * a "mini-session" that is used to dynamically
	 * generate the XML for the override classes
	 */
	private Session session;

	/**
	 * this project will generate the MWClasses that are written out
	 * to the session above, to generate the XML for the overridden classes
	 */
	private MWRelationalProject project;

	
	/**
	 * private-protected constructor
	 */
	DynamicClassOverridePolicy50(XMLAccessor parentAccessor, Session session, String rootElementName) {
		super();
		this.initialize();
		this.parentAccessor = parentAccessor;
		this.session = session;
		this.rootElementName = rootElementName;
	}
	
	/**
	 * return true if the stream is for a MWClass
	 */
	boolean overridesStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) {	// private-protected
		return rootElementName.equals(this.rootElementName);
	}
	
	/**
	 * deletes are NOT supported - this will cause the accessor
	 * to delete the stream, if it exists, using the "normal"
	 * (non-override) method...
	 */
	public Integer deleteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		throw new UnsupportedAccessorOperationException50();
	}
	
	/**
	 * delegate to the normal read stream
	 */
	public Reader getExistenceCheckStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		return this.getReadStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * simulate an update if appropriate
	 */
	public Writer getExistingWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		return this.getWriteStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * simulate an insert if appropriate
	 */
	public Writer getNewWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		return this.getWriteStream(rootElementName, row, orderedPrimaryKeyElements);
	}
	
	/**
	 * simulate a write if appropriate
	 */
	Writer getWriteStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {
		if (this.overridesStream(rootElementName, row, orderedPrimaryKeyElements)) {
			// not sure whether this is appropriate:
			// delete the stream if it is present, since it is not used and could be misleading
			this.getParentAccessor().deleteStream(rootElementName, row, orderedPrimaryKeyElements);
			return nullWriter;
		} else {
			throw new UnsupportedAccessorOperationException50();
		}
	}
	
	/**
	 * dynamically build the appropriate XML read stream in memory if appropriate
	 */
	public abstract Reader getReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException;
	
	/**
	 * dynamically build the appropriate XML read stream in memory
	 */
	Reader buildReadStream(String rootElementName, Record row, Vector orderedPrimaryKeyElements) throws UnsupportedAccessorOperationException50, XMLDataStoreException {	// private-protected
		ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		Writer writer = new OutputStreamWriter(baos);
		String className = this.buildClassName(row, orderedPrimaryKeyElements);
        if ((className.length() == 0) || ClassTools.classNamedIsArray(className)) {
            throw new UnsupportedAccessorOperationException50();
        }
		session.executeQuery(this.buildInsertQuery(writer, className));
		return new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
	}
	
	/**
	 * build an insert query for the specified class; use the specified
	 * writer - it will write on a byte array that will subsequently be used
	 * by a reader to simulate a file read
	 */
	private DatabaseQuery buildInsertQuery(Writer writer, String className) throws XMLDataStoreException {
		XMLInsertCall insertCall = new XMLInsertCall();
		insertCall.setWriter(writer);
		InsertObjectQuery insertQuery = new InsertObjectQuery(this.typeNamed(className));
		insertQuery.setCall(insertCall);
		return insertQuery;
	}
	
	/**
	 * return the specified MWClass - this will to be used to generate the XML
	 * used in a simulated read
	 */
	abstract MWClass typeNamed(String className) throws XMLDataStoreException;	// private-protected
	
	/**
	 * build a class name from the specified row's primary key elements
	 */
	String buildClassName(Record row, Vector orderedPrimaryKeyElements) {	// private-protected
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Enumeration stream = orderedPrimaryKeyElements.elements(); stream.hasMoreElements(); ) {
			pw.print(row.get((DatabaseField) stream.nextElement()));
		}
		return sw.toString();
	}
	
	/**
	 * initialize the newly-created instance
	 */
	void initialize() {	// private-protected
		nullWriter = NullWriter.instance();
//		project = new MWRelationalProject(ClassTools.shortClassNameForObject(this));
		project = (MWRelationalProject) ClassTools.newInstance(MWRelationalProject.class, String.class, ClassTools.shortClassNameForObject(this));
	}
	
	/**
	 * return a project can be used to generate MWClasses
	 */
	MWRelationalProject getProject() {	// private-protected
		return project;
	}
	
	/**
	 * return the accessor holding the policy
	 */
	XMLAccessor getParentAccessor() {	// private-protected
		return parentAccessor;
	}

	/**
	 * return the root element name
	 */
	String getRootElementName() {	// private-protected
		return rootElementName;
	}

}

