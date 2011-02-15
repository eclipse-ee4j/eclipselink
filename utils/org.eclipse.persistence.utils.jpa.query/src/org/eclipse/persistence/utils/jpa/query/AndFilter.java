/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

/**
 * This filter will "accept" any object that is accept by both of the specified
 * wrapped filters. The first filter will always be evaluated, while the second
 * will only be evaluated if necessary.
 *
 * @version 11.0.0
 * @since 10.1.3
 * @author Brian Vosburgh
 */
@SuppressWarnings("nls")
public class AndFilter<T> extends CompoundFilter<T>
{
	/**
	 * The version number of this class which is used during deserialization to
	 * verify that the sender and receiver of a serialized object have loaded
	 * classes for that object that are compatible with respect to serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>AndFilter</code> that will "accept" any object that is
	 * accept by both of the specified wrapped filters.
	 *
	 * @param filter1 The first <code>Filter</code> that might "accept" any object
	 * @param filter2 The second <code>Filter</code> that might "accept" any object
	 */
	public AndFilter(Filter<T> filter1, Filter<T> filter2)
	{
		super(filter1, filter2);
	}

	/**
	 * Creates a new <code>ANDFilter</code> that will "accept" any object that is
	 * accept by all of the specified filters.
	 *
	 * @param filters The list of <code>Filter</code>s encapsulated by a chain of
	 * <code>AndFilter</code>s
	 * @return A new chain of <code>ORFilter</code>s that will "accept" any
	 * object when all <code>Filter</code>s accepts the object
	 */
	public static <T> Filter<T> and(Filter<T>... filters)
	{
		int length = filters.length;

		if (length == 0)
		{
			return NullFilter.instance();
		}

		if (length == 1)
		{
			return filters[0];
		}

		AndFilter<T> filter = new AndFilter<T>(filters[0], filters[1]);

		for (int index = 2; index < length; index++)
		{
			filter = new AndFilter<T>(filter, filters[index]);
		}

		return filter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(T value)
	{
		return filter1.accept(value) && filter2.accept(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AndFilter<T> clone()
	{
		return (AndFilter<T>) super.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String operatorString()
	{
		return "AND";
	}
}