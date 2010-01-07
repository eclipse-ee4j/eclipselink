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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Delegate to the default implementations of the various interfaces
 * extended by ResourceRepository.
 * 
 * This resource repository can use the SAME key to extract three different
 * types of resources from a single resource bundle:
 * 1. Strings are fetched from the resource bundle with the same
 *     key that was passed to this repository. The string will be stripped
 *     of its first ampersand.
 * 2. Mnemonics are calculated from the string fetched from the resource
 *     bundle with the same key that was passed to this repository.
 *     The mnemonic is calculated from the position of the first
 *     ampersand in the string.
 */
public class DefaultResourceRepository implements ResourceRepository {
	private StringRepository stringRepository;
	private MnemonicRepository mnemonicRepository;
	private AcceleratorRepository acceleratorRepository;
	private IconRepository iconRepository;


	// ********** constructors/initialization **********

	public DefaultResourceRepository(Class resourceBundleClass) {
		this(resourceBundleClass, null);
	}

	public DefaultResourceRepository(IconResourceFileNameMap iconResourceFileNameMap) {
		this(null, iconResourceFileNameMap);
	}

	public DefaultResourceRepository(Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		super();
		this.stringRepository = this.buildStringRepository(resourceBundleClass);
		this.mnemonicRepository = this.buildMnemonicRepository(resourceBundleClass);
		this.acceleratorRepository = this.buildAcceleratorRepository(resourceBundleClass);
		this.iconRepository = this.buildIconRepository(iconResourceFileNameMap);
	}

	protected StringRepository buildStringRepository(Class resourceBundleClass) {
		return (resourceBundleClass == null) ? StringRepository.NULL_INSTANCE : new DefaultStringRepository(resourceBundleClass);
	}

	protected MnemonicRepository buildMnemonicRepository(Class resourceBundleClass) {
		return (resourceBundleClass == null) ? MnemonicRepository.NULL_INSTANCE : new DefaultMnemonicRepository(resourceBundleClass);
	}

	protected AcceleratorRepository buildAcceleratorRepository(Class resourceBundleClass) {
		return (resourceBundleClass == null) ? AcceleratorRepository.NULL_INSTANCE : new DefaultAcceleratorRepository(resourceBundleClass);
	}

	protected IconRepository buildIconRepository(IconResourceFileNameMap iconResourceFileNameMap) {
		return (iconResourceFileNameMap == null) ? IconRepository.NULL_INSTANCE : new DefaultIconRepository(iconResourceFileNameMap);
	}


	// ********** ResourceRepository implementation **********
    
    /**
     * @see StringRepository#hasString(String)
     */
    public boolean hasString(String key) {
        return this.stringRepository.hasString(key);
    }
    
	/**
	 * @see StringRepository#getString(String)
	 */
	public String getString(String key) {
		return this.stringRepository.getString(key);
	}

	/**
	 * @see StringRepository#getString(String, Object)
	 */
	public String getString(String key, Object argument) {
		return this.stringRepository.getString(key, argument);
	}

	/**
	 * @see StringRepository#getString(String, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2) {
		return this.stringRepository.getString(key, argument1, argument2);
	}

	/**
	 * @see StringRepository#getString(String, Object, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2, Object argument3) {
		return this.stringRepository.getString(key, argument1, argument2, argument3);
	}

	/**
	 * @see StringRepository#getString(String, Object[])
	 */
	public String getString(String key, Object[] arguments) {
		return this.stringRepository.getString(key, arguments);
	}

    /**
     * @see MnemonicRepository#hasMnemonic(String)
     */
    public boolean hasMnemonic(String key) {
        return this.mnemonicRepository.hasMnemonic(key);
    }
    
	/**
	 * @see MnemonicRepository#getMnemonic(String)
	 */
	public int getMnemonic(String key) {
		return this.mnemonicRepository.getMnemonic(key);
	}

	/**
	 * @see MnemonicRepository#getMnemonicIndex(String)
	 */
	public int getMnemonicIndex(String key) {
		return this.mnemonicRepository.getMnemonicIndex(key);
	}

	/**
	 * @see AcceleratorRepository#hasAccelerator(String)
	 */
    public boolean hasAccelerator(String key) {
        return this.acceleratorRepository.hasAccelerator(key);
    }
    
	/**
	 * @see AcceleratorRepository#getAccelerator(String)
	 */
	public KeyStroke getAccelerator(String key) {
		return this.acceleratorRepository.getAccelerator(key);
	}

	/**
	 * @see IconRepository#hasIcon(String)
	 */
    public boolean hasIcon(String key) {
        return this.iconRepository.hasIcon(key);
    }
    
	/**
	 * @see IconRepository#getIcon(String)
	 */
	public Icon getIcon(String key) {
		return this.iconRepository.getIcon(key);
	}


	// ********** overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this);
	}

}
