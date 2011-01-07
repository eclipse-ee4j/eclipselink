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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWRefreshCachePolicy 
	extends MWModel {
	
	private volatile boolean alwaysRefreshCache;
		public final static String ALWAYS_REFRESH_CACHE_PROPERTY = "alwaysRefreshCache";
	private volatile boolean onlyRefreshCacheIfNewerVersion;
	public final static String ONLY_REFRESH_IF_NEWER_VERSION_PROPERTY = "onlyRefreshCacheIfNewerVersion";

	private volatile boolean disableCacheHits;
		public final static String DISABLE_CACHE_HITS_PROPERTY = "disableCacheHits";


	// *************** static methods *************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRefreshCachePolicy.class);
		
		XMLDirectMapping alwaysRefreshCacheMapping = (XMLDirectMapping) descriptor.addDirectMapping("alwaysRefreshCache", "always-refresh-cache/text()");
		alwaysRefreshCacheMapping.setNullValue(Boolean.FALSE);

		XMLDirectMapping onlyRefreshCacheIfNewerVersionMapping = (XMLDirectMapping) descriptor.addDirectMapping("onlyRefreshCacheIfNewerVersion", "only-refresh-cache-if-newer-version/text()");
		onlyRefreshCacheIfNewerVersionMapping.setNullValue(Boolean.FALSE);

		XMLDirectMapping disableCacheHitsMapping = (XMLDirectMapping) descriptor.addDirectMapping("disableCacheHits", "disable-cache-hits/text()");
		disableCacheHitsMapping.setNullValue(Boolean.FALSE);
		
		
		return descriptor;
	}


	// ********** Constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWRefreshCachePolicy() {
		super();
	}

	MWRefreshCachePolicy(MWModel parent) {
		super(parent);
	}
	
	
	// ********** Initialization **********
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.alwaysRefreshCache = false;
		this.onlyRefreshCacheIfNewerVersion = false;
		this.disableCacheHits = false;		
	}
		
		
	// ********** accessors **********
	
	public boolean isAlwaysRefreshCache() {
		return this.alwaysRefreshCache;
	}
	
	public void setAlwaysRefreshCache(boolean alwaysRefreshCache) {
		boolean oldAlwaysRefreshCache = this.alwaysRefreshCache;
		this.alwaysRefreshCache = alwaysRefreshCache;
		firePropertyChanged(ALWAYS_REFRESH_CACHE_PROPERTY, oldAlwaysRefreshCache, alwaysRefreshCache);	
	}

	public boolean isOnlyRefreshCacheIfNewerVersion() {
		return this.onlyRefreshCacheIfNewerVersion;
	}
	
	public void setOnlyRefreshCacheIfNewerVersion(boolean onlyRefreshCacheIfNewerVersion) {
		boolean oldOnlyRefreshCacheIfNewerVersion = this.onlyRefreshCacheIfNewerVersion;
		this.onlyRefreshCacheIfNewerVersion = onlyRefreshCacheIfNewerVersion;
		firePropertyChanged(ONLY_REFRESH_IF_NEWER_VERSION_PROPERTY, oldOnlyRefreshCacheIfNewerVersion, onlyRefreshCacheIfNewerVersion);	
	}

	public boolean isDisableCacheHits() {
		return this.disableCacheHits;
	}
	
	public void setDisableCacheHits(boolean disableCacheHits) {
		boolean oldDisableCacheHits = this.disableCacheHits;
		this.disableCacheHits = disableCacheHits;
		firePropertyChanged(DISABLE_CACHE_HITS_PROPERTY, oldDisableCacheHits, disableCacheHits);	
	}


	
	// **************** runtime conversion ***************
	
	void adjustRuntimeDescriptor(ClassDescriptor descriptor) {
		descriptor.setShouldAlwaysRefreshCache(this.isAlwaysRefreshCache());
		descriptor.setShouldDisableCacheHits(this.isDisableCacheHits());
		descriptor.setShouldOnlyRefreshCacheIfNewerVersion(this.isOnlyRefreshCacheIfNewerVersion());
	}
	
	public void legacySetDisableCacheHits(boolean disableCacheHits) {
		this.disableCacheHits = disableCacheHits;
	}
	
	public void legacySetAlwaysRefreshCache(boolean alwaysRefreshCache) {
		this.alwaysRefreshCache = alwaysRefreshCache;
	}
	
	public void legacySetOnlyRefreshCacheIfNewerVersion(boolean onlyRefreshCacheIfNewerVersion) {
		this.onlyRefreshCacheIfNewerVersion = onlyRefreshCacheIfNewerVersion;
	}
	
}
