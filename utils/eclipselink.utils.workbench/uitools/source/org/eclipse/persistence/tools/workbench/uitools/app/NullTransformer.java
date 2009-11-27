/*
 * Copyright (c) 2006, 2007, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.Transformer;


/**
 * A <code>null</code> implementation of a <code>Transformer</code>. The
 * singleton instance can be typed cast properly when using generics.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public final class NullTransformer implements Transformer
{
	/**
	 * The singleton instance of this <code>NullTransformer</code>.
	 */
	private static Transformer INSTANCE;

	/**
	 * Creates a new <code>NullTransformer</code>.
	 */
	private NullTransformer()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 */
	public Object transform(Object object)
	{
		return object;
	}

	/**
	 * Returns the singleton instance of this <code>NullTransformer</code>.
	 *
	 * @return The singleton instance of this <code>NullTransformer</code>
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Transformer instance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new NullTransformer();
		}

		return (Transformer) INSTANCE;
	}
}