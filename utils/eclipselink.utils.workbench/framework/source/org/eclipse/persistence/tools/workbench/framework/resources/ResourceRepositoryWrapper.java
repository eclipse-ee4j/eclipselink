/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 * This class wraps another resource repository and extends it with another
 * set of resources. We try to fulfill any request for a resource with
 * the "local" resources, if we do not have the requested resource,
 * we delegate to the "wrapped" resource repository.
 */
public class ResourceRepositoryWrapper
	implements ResourceRepository
{
	/** this repository contains the overrides */
	private ResourceRepository localResourceRepository;

	/** this repository is used if the resource is not found in the "local" repository */
	private ResourceRepository wrappedResourceRepository;


	// ********** constructors/initialization **********
	
	public ResourceRepositoryWrapper(ResourceRepository resourceRepository, Class resourceBundleClass) {
		this(resourceRepository, resourceBundleClass, null);
	}
	
	public ResourceRepositoryWrapper(ResourceRepository resourceRepository, IconResourceFileNameMap iconResourceFileNameMap) {
		this(resourceRepository, null, iconResourceFileNameMap);
	}
	
	public ResourceRepositoryWrapper(ResourceRepository resourceRepository, Class resourceBundleClass, IconResourceFileNameMap iconResourceFileNameMap) {
		super();
		if (resourceRepository == null) {
			throw new NullPointerException();
		}
		this.wrappedResourceRepository = resourceRepository;
		this.localResourceRepository = new DefaultResourceRepository(resourceBundleClass, iconResourceFileNameMap);
	}


	// ********** ResourceRepository implementation **********

	/**
	 * @see StringRepository#hasString(String)
	 */
	public boolean hasString(String key) {
		return this.localResourceRepository.hasString(key) ||
					this.wrappedResourceRepository.hasString(key);
	}

	/**
	 * @see StringRepository#getString(String)
	 */
	public String getString(String key) {
		if (this.localResourceRepository.hasString(key)) {
			return this.localResourceRepository.getString(key);
		}
		return this.wrappedResourceRepository.getString(key);
	}

	/**
	 * @see StringRepository#getString(String, Object)
	 */
	public String getString(String key, Object argument) {
		if (this.localResourceRepository.hasString(key)) {
			return this.localResourceRepository.getString(key, argument);
		}
		return this.wrappedResourceRepository.getString(key, argument);
	}

	/**
	 * @see StringRepository#getString(String, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2) {
		if (this.localResourceRepository.hasString(key)) {
			return this.localResourceRepository.getString(key, argument1, argument2);
		}
		return this.wrappedResourceRepository.getString(key, argument1, argument2);
	}

	/**
	 * @see StringRepository#getString(String, Object, Object, Object)
	 */
	public String getString(String key, Object argument1, Object argument2, Object argument3) {
		if (this.localResourceRepository.hasString(key)) {
			return this.localResourceRepository.getString(key, argument1, argument2, argument3);
		}
		return this.wrappedResourceRepository.getString(key, argument1, argument2, argument3);
	}

	/**
	 * @see StringRepository#getString(String, Object[])
	 */
	public String getString(String key, Object[] arguments) {
		if (this.localResourceRepository.hasString(key)) {
			return this.localResourceRepository.getString(key, arguments);
		}
		return this.wrappedResourceRepository.getString(key, arguments);
	}

    /**
     * @see MnemonicRepository#hasMnemonic(String)
     */
    public boolean hasMnemonic(String key) {
		return this.localResourceRepository.hasMnemonic(key) ||
					this.wrappedResourceRepository.hasMnemonic(key);
    }
    
	/**
	 * @see MnemonicRepository#getMnemonic(String)
	 */
	public int getMnemonic(String key) {
		if (this.localResourceRepository.hasMnemonic(key)) {
			return this.localResourceRepository.getMnemonic(key);
		}
		return this.wrappedResourceRepository.getMnemonic(key);
	}

	/**
	 * @see MnemonicRepository#getMnemonicIndex(String)
	 */
	public int getMnemonicIndex(String key) {
		if (this.localResourceRepository.hasMnemonic(key)) {
			return this.localResourceRepository.getMnemonicIndex(key);
		}
		return this.wrappedResourceRepository.getMnemonicIndex(key);
	}

	/**
	 * @see AcceleratorRepository#hasAccelerator(String)
	 */
    public boolean hasAccelerator(String key) {
		return this.localResourceRepository.hasAccelerator(key) ||
					this.wrappedResourceRepository.hasAccelerator(key);
    }
    
	/**
	 * @see AcceleratorRepository#getAccelerator(String)
	 */
	public KeyStroke getAccelerator(String key) {
		if (this.localResourceRepository.hasAccelerator(key)) {
			return this.localResourceRepository.getAccelerator(key);
		}
		return this.wrappedResourceRepository.getAccelerator(key);
	}

	/**
	 * @see IconRepository#hasIcon(String)
	 */
    public boolean hasIcon(String key) {
		return this.localResourceRepository.hasIcon(key) ||
					this.wrappedResourceRepository.hasIcon(key);
    }
    
	/**
	 * @see IconRepository#getIcon(String)
	 */
	public Icon getIcon(String key) {
		if (this.localResourceRepository.hasIcon(key)) {
			return this.localResourceRepository.getIcon(key);
		}
		return this.wrappedResourceRepository.getIcon(key);
	}


	// ********** overrides **********

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this);
	}

}
