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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This class can be used alongside DefaultStringRepository to look up
 * strings and their mnemonics from the same JDK resource bundle.
 * The mnemonics can be indicated with an ampersand ('&') embedded
 * in the resource string. The same resource string can fetched by both
 * DefaultStringRepository and this repository. DefaultStringRepository
 * will strip off the ampersand before returning the string; while this
 * repository will use the ampersand to determine the mnemonic character
 * and index within the string.
 * 
 * @see DefaultStringRepository
 */
public class DefaultMnemonicRepository implements MnemonicRepository {

	/**
	 * The JDK resource bundle holding the strings that optionally have
	 * mnemonics.
	 */
	private ResourceBundle resourceBundle;


	// ********** constructors/initialization **********

	/**
	 * Construct a mnemonic repository that for the specified
	 * resource bundle.
	 */
	public DefaultMnemonicRepository(String resourceClassName) {
		super();
		this.initialize(resourceClassName);
	}

	/**
	 * Construct a mnemonic repository that for the specified
	 * resource bundle.
	 */
	public DefaultMnemonicRepository(Class resourceClass) {
		this(resourceClass.getName());
	}

	protected void initialize(String resourceClassName) {
		this.resourceBundle = ResourceBundle.getBundle(resourceClassName);
	}


	// ********** MnemonicRepository implementation **********

    /**
     * @see MnemonicRepository#hasMnemonic(String)
     */
    public boolean hasMnemonic(String key) {
    	return (key == null) || CollectionTools.contains(this.resourceBundle.getKeys(), key);
    }
    
	/**
	 * @see MnemonicRepository#getMnemonic(String)
	 */
	public int getMnemonic(String key) {
		if  (key == null) {
			return -1;
		}
		String string = this.getString(key);
		int index = string.indexOf(DefaultStringRepository.MNEMONIC_CHAR);
		// return -1 if the ampersand is missing or at the very end of the string
		if ((index == -1) || (index == string.length() - 1)) {
			return -1;
		}
		// return the character immediately following the ampersand
		// it must be converted to uppercase, for some mysterious reason...
		return Character.toUpperCase(string.charAt(index + 1));
	}

	/**
	 * @see MnemonicRepository#getMnemonicIndex(String)
	 */
	public int getMnemonicIndex(String key) {
		return this.getString(key).indexOf(DefaultStringRepository.MNEMONIC_CHAR);
	}


	// ********** internal methods **********

	/**
	 * Return the string associated with the specified key
	 * by looking it up in the resource bundle.
	 * Throw a MissingMnemonicException if the string is not in the
	 * resource bundle.
	 * Subclasses can extend this method to manipulate the string
	 * returned from the resource bundle before it we look for the
	 * mnemonic.
	 */
	protected String getString(String key) {
		try {
			return this.resourceBundle.getString(key);
		} catch (MissingResourceException ex) {
			if (ex.getKey().equals(key)) {
				throw new MissingMnemonicException("Missing mnemonic: " + key, key);
			}
			throw ex;
		}
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.resourceBundle.getClass().getName());
	}

}
