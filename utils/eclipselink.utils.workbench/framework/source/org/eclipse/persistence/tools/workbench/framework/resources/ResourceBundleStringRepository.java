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
package org.eclipse.persistence.tools.workbench.framework.resources;

import java.util.Collection;
import java.util.ListResourceBundle;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A ResourceBundleStringRepository implements the StringRepository interface
 * by wrapping a JDK ResourceBundle and returning the strings stored in the
 * resource bundle, unaltered and with NO formatting (i.e. the arguments
 * are ignored).
 * 
 * Subclasses really only need to override the protected methods
 * #get(String key) and #format(String string, Object[] arguments).
 */
public class ResourceBundleStringRepository implements StringRepository {

	/**
	 * The wrapped JDK resource bundle.
	 */
	private ResourceBundle resourceBundle;

	private static final Object[] EMPTY_ARGUMENTS = new Object[0];


	// ********** constructors/initialization **********

	/**
	 * Construct a string repository that wraps the specified
	 * resource bundle.
	 */
	public ResourceBundleStringRepository(String resourceClassName) {
		super();
		this.initialize(resourceClassName);
	}

	/**
	 * Construct a string repository that wraps the specified
	 * resource bundle.
	 */
	public ResourceBundleStringRepository(Class resourceClass) {
		this(resourceClass.getName());
	}

	protected void initialize(String resourceClassName) {
		this.resourceBundle = ResourceBundle.getBundle(resourceClassName);
		this.validate(this.resourceBundle);
	}

	protected void validate(ResourceBundle bundle) {
		if (bundle instanceof ListResourceBundle) {
			this.validate((ListResourceBundle) bundle);
		}
	}

	/**
	 * a bit of hackery to help us out in practice
	 */
	protected void validate(ListResourceBundle bundle) {
		Object[][] contents = (Object[][]) ClassTools.invokeMethod(bundle, "getContents");
		// check for duplicate keys in resource bundle
		Collection keys = new HashBag(contents.length);
		for (int i = contents.length; i-- > 0; ) {
			String key = (String) contents[i][0];
			if (keys.contains(key)) {
				throw new IllegalStateException("duplicate resource key: " + key);
			}
			keys.add(key);
		}
	}


	// ********** StringRepository implementation **********

	/**
	 * @see StringRepository#hasString(String)
	 */
	public boolean hasString(String key) {
		return (key == null) || CollectionTools.contains(this.resourceBundle.getKeys(), key);
	}

	/**
	 * @see StringRepository#getString(String)
	 */
	public String getString(String key) {
		return this.getString(key, EMPTY_ARGUMENTS);
	}

	/**
	 * @see StringRepository#getString(String, Object)
	 */
	public String getString(String key, Object argument) {
		return this.getString(key, new Object[] {argument});
	}

	/**
	 * @see StringRepository#getString(String, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2) {
		return this.getString(key, new Object[] {argument1, argument2});
	}

	/**
	 * @see StringRepository#getString(String, Object, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2, Object argument3) {
		return this.getString(key, new Object[] {argument1, argument2, argument3});
	}

	/**
	 * @see StringRepository#getString(String, Object[])
	 */
	public String getString(String key, Object[] arguments) {
		return this.format(this.get(key), arguments);
	}


	// ********** internal methods **********

	/**
	 * Return the string associated with the specified key
	 * by looking it up in the resource bundle.
	 * Throw a MissingStringException if the string is not in the
	 * resource bundle.
	 * Subclasses can extend this method to manipulate the string
	 * returned from the resource bundle before it gets formatted.
	 */
	protected String get(String key) {
		if (key == null) {
			return EMPTY_STRING;
		}
		try {
			return this.resourceBundle.getString(key);
		} catch (MissingResourceException ex) {
			if (ex.getKey().equals(key)) {
				throw new MissingStringException("Missing string: " + key, key);
			}
			throw ex;
		}
	}

	/**
	 * Format the specified string with the specified arguments.
	 * The default behavior is to return the string unchanged;
	 * subclasses can format the string as necessary.
	 */
	protected String format(String string, Object[] arguments) {
		return string;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.resourceBundle.getClass().getName());
	}

}
