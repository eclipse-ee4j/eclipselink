/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.io.Serializable;

/**
 * This filter provides a simple framework for combining the behavior of a pair
 * of filters.
 *
 * @version 11.2.0
 * @since 10.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class CompoundFilter<T> implements Filter<T>,
                                                   Cloneable,
                                                   Serializable
{
	/**
	 * The first <code>Filter</code> used to accept the value.
	 */
	protected final Filter<T> filter1;

	/**
	 * The second <code>Filter</code> used to accept the value.
	 */
	protected final Filter<T> filter2;

	/**
	 * The version number of this class which is used during deserialization to
	 * verify that the sender and receiver of a serialized object have loaded
	 * classes for that object that are compatible with respect to serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>CompoundFilter</code> that will "accept" any object
	 * that is accept by both of the specified wrapped filters.
	 *
	 * @param filter1 The first <code>Filter</code> used to accept the value
	 * @param filter2 The second <code>Filter</code> used to accept the value
	 */
	protected CompoundFilter(Filter<T> filter1, Filter<T> filter2)
	{
		super();

		checkFilter1(filter1);
		checkFilter2(filter2);

		this.filter1 = filter1;
		this.filter2 = filter2;
	}

	private void checkFilter1(Filter<T> filter)
	{
		if (filter == null)
		{
			throw new IllegalArgumentException("The first Filter cannot be null");
		}
	}

	private void checkFilter2(Filter<T> filter)
	{
		if (filter == null)
		{
			throw new IllegalArgumentException("The second Filter cannot be null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CompoundFilter<T> clone()
	{
		try
		{
			return (CompoundFilter<T>) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException("The cloning of this " + getClass().getSimpleName() + " was not successful.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof CompoundFilter<?>))
		{
			return false;
		}

		CompoundFilter<?> other = (CompoundFilter<?>) object;

		return (filter1.equals(other.filter1) && filter2.equals(other.filter2)) ||
		       (filter1.equals(other.filter2) && filter2.equals(other.filter1));
	}

	/**
	 * Returns the left <code>Filter</code> of this compound filter.
	 *
	 * @return The first <code>Filter</code>
	 */
	public Filter<T> getFilter1()
	{
		return filter1;
	}

	/**
	 * Returns the second <code>Filter</code> of this compound filter.
	 *
	 * @return The second <code>Filter</code>
	 */
	public Filter<T> getFilter2()
	{
		return filter2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return filter1.hashCode() ^ filter2.hashCode();
	}

	/**
	 * Returns a string representation of the filter's operator.
	 *
	 * @return The string value of the operator
	 */
	protected abstract String operatorString();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" (filter1=");
		sb.append(filter1);
		sb.append(", filter2=");
		sb.append(filter2);
		sb.append(")");
		return sb.toString();
	}
}