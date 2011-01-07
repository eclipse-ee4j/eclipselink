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
package org.eclipse.persistence.tools.workbench.utility.iterators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A <code>ResultSetIterator</code> wraps a <code>ResultSet</code>
 * and transforms its rows for client consumption. Subclasses can override
 * <code>#buildNext(ResultSet)</code> to build the expected object from
 * the current row of the result set.
 * <p>
 * To use, supply:<ul>
 * <li> a <code>ResultSet</code>
 * <li> an <code>Adapter</code> that converts a row in the <code>ResultSet</code>
 * into the desired object
 * (alternatively, subclass <code>ResultSetIterator</code>
 * and override the <code>buildNext(ResultSet)</code> method)
 * </ul>
 * <p>
 */
public class ResultSetIterator
	implements Iterator
{
	private final ResultSet resultSet;
	private final Adapter adapter;
	private Object next;

	private static final Object END = new Object();


	/**
	 * Construct an iterator on the specified result set that returns
	 * the objects produced by the specified adapter.
	 */
	public ResultSetIterator(ResultSet resultSet, Adapter adapter) {
		super();
		this.resultSet = resultSet;
		this.adapter = adapter;
		this.next = this.buildNext();
	}

	/**
	 * Construct an iterator on the specified result set that returns
	 * the first object in each row of the result set.
	 */
	public ResultSetIterator(ResultSet resultSet) {
		this(resultSet, Adapter.DEFAULT_INSTANCE);
	}

	/**
	 * Build the next object for the iterator to return.
	 * Close the result set when we reach the end.
	 */
	private Object buildNext() {
		try {
			if (this.resultSet.next()) {
				return this.buildNext(this.resultSet);
			}
			this.resultSet.close();
			return END;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * By default, return the first object in the current row
	 * of the result set. Any <code>SQLException</code>s will
	 * be caught and wrapped in a <code>RuntimeException</code>.
	 */
	protected Object buildNext(ResultSet rs) throws SQLException {
		return this.adapter.buildNext(rs);
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return this.next != END;
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.next == END) {
			throw new NoSuchElementException();
		}
		Object temp = this.next;
		this.next = this.buildNext();
		return temp;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}


	// ********** interface **********

	/**
	 * Used by <code>ResultSetIterator</code> to convert a
	 * <code>ResultSet</code>'s current row into the next object
	 * to be returned by the <code>Iterator</code>.
	 */
	public interface Adapter {

		/**
		 * Return an object corresponding to the result set's
		 * "current" row. Any <code>SQLException</code>s will
		 * be caught and wrapped in a <code>RuntimeException</code>.
		 * @see java.sql.ResultSet
		 */
		Object buildNext(ResultSet rs) throws SQLException;


		Adapter DEFAULT_INSTANCE =
			new Adapter() {
				// return the first object in the current row of the result set
				public Object buildNext(ResultSet rs) throws SQLException {
					// result set columns are indexed starting with 1
					return rs.getObject(1);
				}
			};
	}

}
