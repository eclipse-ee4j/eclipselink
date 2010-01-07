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
package org.eclipse.persistence.tools.workbench.framework.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.KeyStroke;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class can be used alongside DefaultStringRepository to look up
 * strings and their accelerators from the same JDK resource bundle.
 * By convention, the strings used to build the accelerator keystrokes
 * are stored in the resource bundle with the same key as the text
 * label for the accelerator's command with ".ACCELERATOR" appended to it.
 * 
 * @see DefaultStringRepository
 */
public class DefaultAcceleratorRepository implements AcceleratorRepository {

	/**
	 * The JDK resource bundle holding the accelerators.
	 */
	private ResourceBundle resourceBundle;


	// ********** constructors/initialization **********

	/**
	 * Construct an accelerator repository that for the specified
	 * resource bundle.
	 */
	public DefaultAcceleratorRepository(String resourceClassName) {
		super();
		this.initialize(resourceClassName);
	}

	/**
	 * Construct an accelerator repository that for the specified
	 * resource bundle.
	 */
	public DefaultAcceleratorRepository(Class resourceClass) {
		this(resourceClass.getName());
	}

	protected void initialize(String resourceClassName) {
		this.resourceBundle = ResourceBundle.getBundle(resourceClassName);
	}


	// ********** AcceleratorRepository implementation **********
    
    /**
     * @see AcceleratorRepository#hasAccelerator(String)
     */
    public boolean hasAccelerator(String key) {
    	return (key == null) || CollectionTools.contains(this.resourceBundle.getKeys(), key);
    }
    
	/**
	 * @see AcceleratorRepository#getAccelerator(String)
	 */
	public KeyStroke getAccelerator(String key) {
		return (key == null) ? null : KeyStroke.getKeyStroke(this.getString(key));
	}


	// ********** internal methods **********

	/**
	 * Return the string associated with the specified key
	 * by looking it up in the resource bundle.
	 * Throw a MissingAcceleratorException if the string is not in the
	 * resource bundle.
	 * Subclasses can extend this method to manipulate the string
	 * returned from the resource bundle before it we convert it to
	 * a key stroke.
	 */
	protected String getString(String key) {
		try {
			return this.resourceBundle.getString(key);
		} catch (MissingResourceException ex) {
			if (ex.getKey().equals(key)) {
				throw new MissingAcceleratorException("Missing accelerator: " + key, key);
			}
			throw ex;
		}
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.resourceBundle.getClass().getName());
	}

}
