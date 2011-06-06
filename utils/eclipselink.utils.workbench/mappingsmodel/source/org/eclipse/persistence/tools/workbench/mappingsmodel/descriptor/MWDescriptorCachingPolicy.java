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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.NoIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftIdentityMap;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DoesExistQuery;


public final class MWDescriptorCachingPolicy extends MWModel 
	implements MWCachingPolicy
{
	private volatile CacheCoordinationOption cacheCoordination;
    private static TopLinkOptionSet cacheCoordinationOptions;
	
    private volatile CacheIsolationOption cacheIsolation;
    private static TopLinkOptionSet cacheIsolationOptions;
	
	private volatile CacheTypeHolder cacheTypeHolder;
		public static final String CACHE_TYPE_HOLDER_PROPERTY = "cacheTypeHolder";
	private static TopLinkOptionSet cacheTypeOptions;
	public static final String DESCRIPTOR_INHERITANCE_PROPERTY = "descriptorInheritance";
	
	private volatile ExistenceCheckingOption existenceChecking;
    private static TopLinkOptionSet existenceCheckingOptions;

    private volatile CacheSizeHolder cacheSizeHolder;
    	public static final String CACHE_SIZE_HOLDER_PROPERTY = "cacheSizeHolder";
    	public static final String USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY = "useProjectDefaultCacheSize";
       
    private volatile MWCacheExpiry cacheExpiry;
        public static final String CACHE_EXPIRY_PROPERTY = "cacheExpiry";
   

    public static final CacheTypeOption PROJECT_DEFAULT_CACHE_TYPE = new CacheTypeOption(CACHE_TYPE_PROJECT_DEFAULT, "CACHING_POLICY_DEFAULT_VALUE", null);
    
	public synchronized static TopLinkOptionSet cacheTypeOptions() {
		if (cacheTypeOptions == null) {
            List list = new ArrayList();
            list.add(PROJECT_DEFAULT_CACHE_TYPE);
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE, "CACHING_POLICY_CACHE_TYPE_WEAK_WITH_SOFT_SUBCACHE", SoftCacheWeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE, "CACHING_POLICY_CACHE_TYPE_WEAK_WITH_HARD_SUBCACHE", HardCacheWeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_WEAK, "CACHING_POLICY_CACHE_TYPE_WEAK", WeakIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_SOFT, "CACHING_POLICY_CACHE_TYPE_SOFT", SoftIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_FULL, "CACHING_POLICY_CACHE_TYPE_FULL", FullIdentityMap.class.getName()));
            list.add(new CacheTypeOption(CACHE_TYPE_NONE, "CACHING_POLICY_CACHE_TYPE_NONE", NoIdentityMap.class.getName()));
		    cacheTypeOptions = new TopLinkOptionSet(list);
		}
		
		return cacheTypeOptions;
	}	

    public synchronized static TopLinkOptionSet existenceCheckingOptions() {
        if (existenceCheckingOptions == null) {
            List list = new ArrayList();
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_PROJECT_DEFAULT, "CACHING_POLICY_DEFAULT_VALUE", 0));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_CHECK_CACHE, "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_CACHE", DoesExistQuery.CheckCache));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_CHECK_DATABASE, "CACHING_POLICY_EXISTENCE_CHECKING_CHECK_DATABASE", DoesExistQuery.CheckDatabase));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_ASSUME_EXISTENCE, "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_EXISTENCE", DoesExistQuery.AssumeExistence));
            list.add(new ExistenceCheckingOption(EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE, "CACHING_POLICY_EXISTENCE_CHECKING_ASSUME_NON_EXISTENCE", DoesExistQuery.AssumeNonExistence));
            existenceCheckingOptions = new TopLinkOptionSet(list);
        }
       
        return existenceCheckingOptions;
    }   

    public synchronized static TopLinkOptionSet cacheCoordinationOptions() {
        if (cacheCoordinationOptions == null) {
            List list = new ArrayList();
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_PROJECT_DEFAULT, "CACHING_POLICY_DEFAULT_VALUE", ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_NONE, "CACHING_POLICY_CACHE_COORDINATION_NONE", ClassDescriptor.DO_NOT_SEND_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_SYNCHRONIZE_CHANGES, "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES", ClassDescriptor.SEND_OBJECT_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS, "CACHING_POLICY_CACHE_COORDINATION_SYNCHRONIZE_CHANGES_AND_NEW_OBJECTS", ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
            list.add(new CacheCoordinationOption(CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS, "CACHING_POLICY_CACHE_COORDINATION_INVALIDATE_CHANGED_OBJECTS", ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
            cacheCoordinationOptions = new TopLinkOptionSet(list);
        }
        
        return cacheCoordinationOptions;
    }

    public synchronized static TopLinkOptionSet cacheIsolationOptions() {
        if (cacheIsolationOptions == null) {
            List list = new ArrayList();
            list.add(new CacheIsolationOption(CACHE_ISOLATION_PROJECT_DEFAULT, "CACHING_POLICY_DEFAULT_VALUE"));
            list.add(new CacheIsolationOption(CACHE_ISOLATION_ISOLATED, "CACHING_POLICY_CACHE_ISOLATION_ISOLATED"));
            list.add(new CacheIsolationOption(CACHE_ISOLATION_SHARED, "CACHING_POLICY_CACHE_ISOLATION_SHARED"));
            cacheIsolationOptions = new TopLinkOptionSet(list);
        }
        
        return cacheIsolationOptions;
    }

	// ********** static methods **********
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWDescriptorCachingPolicy.class);

		// Cache Size
		XMLDirectMapping cacheSizeMapping = new XMLDirectMapping();
        cacheSizeMapping.setAttributeName("cacheSizeHolder");
        cacheSizeMapping.setGetMethodName("getCacheSizeForTopLink");
        cacheSizeMapping.setSetMethodName("setCacheSizeForTopLink");
        cacheSizeMapping.setXPath("cache-size/text()");
        cacheSizeMapping.setNullValue(new Integer(-1));
        descriptor.addMapping(cacheSizeMapping);        

		// Cache Type
		XMLDirectMapping cacheTypeMapping = (XMLDirectMapping) descriptor.addDirectMapping("cacheTypeHolder", "getCacheTypeForTopLink", "setCacheTypeForTopLink", "cache-type/text()");
		ObjectTypeConverter cacheTypeConverter = new ObjectTypeConverter();
		cacheTypeOptions().addConversionValuesForTopLinkTo(cacheTypeConverter);
		cacheTypeMapping.setConverter(cacheTypeConverter);
		cacheTypeMapping.setNullValue(cacheTypeOptions().topLinkOptionForMWModelOption(CACHE_TYPE_PROJECT_DEFAULT));

		// Existence Checking
		ObjectTypeConverter existenceCheckingConverter = new ObjectTypeConverter();
        existenceCheckingOptions().addConversionValuesForTopLinkTo(existenceCheckingConverter);
		XMLDirectMapping existenceCheckingMapping = new XMLDirectMapping();
		existenceCheckingMapping.setAttributeName("existenceChecking");
		existenceCheckingMapping.setXPath("existence-checking/text()");
		existenceCheckingMapping.setNullValue(existenceCheckingOptions().topLinkOptionForMWModelOption(EXISTENCE_CHECKING_PROJECT_DEFAULT));
		existenceCheckingMapping.setConverter(existenceCheckingConverter);
		descriptor.addMapping(existenceCheckingMapping);		

		// Cache Coordination
		ObjectTypeConverter cacheCoordinationConverter = new ObjectTypeConverter();
        cacheCoordinationOptions().addConversionValuesForTopLinkTo(cacheCoordinationConverter);
		XMLDirectMapping cacheCoordinationMapping = new XMLDirectMapping();
		cacheCoordinationMapping.setAttributeName("cacheCoordination");
		cacheCoordinationMapping.setXPath("cache-coordination/text()");
		cacheCoordinationMapping.setConverter(cacheCoordinationConverter);
		cacheCoordinationMapping.setNullValue(cacheCoordinationOptions().topLinkOptionForMWModelOption(CACHE_COORDINATION_PROJECT_DEFAULT));
		descriptor.addMapping(cacheCoordinationMapping);		

		// Cache Isolation
		ObjectTypeConverter cacheIsolationConverter = new ObjectTypeConverter();
        cacheIsolationOptions().addConversionValuesForTopLinkTo(cacheIsolationConverter);
		XMLDirectMapping cacheIsolationMapping = new XMLDirectMapping();
		cacheIsolationMapping.setAttributeName("cacheIsolation");
		cacheIsolationMapping.setXPath("cache-isolation/text()");
		cacheIsolationMapping.setConverter(cacheIsolationConverter);
		cacheIsolationMapping.setNullValue(cacheIsolationOptions().topLinkOptionForMWModelOption(CACHE_ISOLATION_PROJECT_DEFAULT));
		descriptor.addMapping(cacheIsolationMapping);		

        XMLCompositeObjectMapping cacheExpiryMapping = new XMLCompositeObjectMapping();
        cacheExpiryMapping.setAttributeName("cacheExpiry");
        cacheExpiryMapping.setReferenceClass(MWDescriptorCacheExpiry.class);
        cacheExpiryMapping.setGetMethodName("getCacheExpiryForTopLink");
        cacheExpiryMapping.setSetMethodName("setCacheExpiryForTopLink");
        cacheExpiryMapping.setXPath("cache-expiry");
        descriptor.addMapping(cacheExpiryMapping);

		return descriptor;
	}

	/**
	 * constructors
	 */
	private MWDescriptorCachingPolicy()
	{
		// for TopLink use only
		super();
	}

	public MWDescriptorCachingPolicy(MWTransactionalPolicy parent)
	{
		super(parent);
	}

	protected void initialize() {
		super.initialize();
        this.cacheSizeHolder = new CacheSizeHolderImpl();
        this.cacheTypeHolder = new CacheTypeHolderImpl();
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.cacheCoordination      = (CacheCoordinationOption) cacheCoordinationOptions().topLinkOptionForMWModelOption(CACHE_COORDINATION_PROJECT_DEFAULT);
		this.cacheIsolation         = (CacheIsolationOption) cacheIsolationOptions().topLinkOptionForMWModelOption(CACHE_ISOLATION_PROJECT_DEFAULT);
        this.existenceChecking      = (ExistenceCheckingOption) existenceCheckingOptions().topLinkOptionForMWModelOption(EXISTENCE_CHECKING_PROJECT_DEFAULT);
        this.cacheExpiry            = new MWProjectDefaultCacheExpiry(this);
	}

    protected void addChildrenTo(List list) {
        super.addChildrenTo(list);
        list.add(this.cacheExpiry);
    }
    
    //TODO this for cacheExpiry???
	public void initializeFrom(MWCachingPolicy otherPolicy) {
		setCacheCoordination(otherPolicy.getCacheCoordination());
		setCacheIsolation(otherPolicy.getCacheIsolation());
		setCacheSize(otherPolicy.getCacheSize());
		setCacheType(otherPolicy.getCacheType());		
		setExistenceChecking(otherPolicy.getExistenceChecking());		
	}
	
    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) ((MWTransactionalPolicy) this.getParent()).getParent();
    }
    
	public ExistenceCheckingOption getExistenceChecking() {
		return this.existenceChecking;
	}

	public void setExistenceChecking(ExistenceCheckingOption newExistenceChecking) {
        ExistenceCheckingOption old = this.existenceChecking;
		this.existenceChecking = newExistenceChecking;
		firePropertyChanged(EXISTENCE_CHECKING_PROPERTY, old, this.existenceChecking);
	}

    public void setExistenceChecking(String existenceChecking) {
        setExistenceChecking((ExistenceCheckingOption) existenceCheckingOptions().topLinkOptionForMWModelOption(existenceChecking));
    }
    
	public CacheCoordinationOption getCacheCoordination() {
		return this.cacheCoordination;
	}

	public void setCacheCoordination(CacheCoordinationOption cacheCoordination) {
        CacheCoordinationOption oldCacheCoordination = this.cacheCoordination;
		this.cacheCoordination = cacheCoordination;
		firePropertyChanged(CACHE_COORDINATION_PROPERTY, oldCacheCoordination, cacheCoordination);
	}

	public CacheIsolationOption getCacheIsolation() {
		return this.cacheIsolation;
	}

	public void setCacheIsolation(CacheIsolationOption cacheIsolation) {
        CacheIsolationOption oldCacheIsolation = this.cacheIsolation;
		this.cacheIsolation = cacheIsolation;
		firePropertyChanged(CACHE_ISOLATION_PROPERTY, oldCacheIsolation, cacheIsolation);

		// If Isolated then Coordination is set to NONE
		if (cacheIsolation.getMWModelOption() == CACHE_ISOLATION_ISOLATED) {
			setCacheCoordination((CacheCoordinationOption) cacheCoordinationOptions().topLinkOptionForMWModelOption(CACHE_COORDINATION_NONE));
		}
		// If Project Default, then Coordination is also set to Project Default
		else if (cacheIsolation.getMWModelOption() == CACHE_ISOLATION_PROJECT_DEFAULT) {
			setCacheCoordination((CacheCoordinationOption) cacheCoordinationOptions().topLinkOptionForMWModelOption(CACHE_COORDINATION_PROJECT_DEFAULT)); //TODO this looks wrong!!!!
		}
	}

	public CacheTypeHolder getCacheTypeHolder() {
		return this.cacheTypeHolder;
	}
	
	public CacheTypeOption getCacheType() {
		return this.cacheTypeHolder.getCacheType();
	}

	public void setCacheType(CacheTypeOption cacheType) {
	    CacheTypeOption oldCacheType = this.cacheTypeHolder.getCacheType();
		this.cacheTypeHolder.setCacheType(cacheType);
		firePropertyChanged(CACHE_TYPE_PROPERTY, oldCacheType, cacheType);
	}
	
	public void setCacheType(String cacheTypeString) {
	    setCacheType((CacheTypeOption) cacheTypeOptions().topLinkOptionForMWModelOption(cacheTypeString));
	}

	private void setCacheTypeHolder(CacheTypeHolder cacheTypeHolder) {
		Object old = this.cacheTypeHolder;
		this.cacheTypeHolder = cacheTypeHolder;
		firePropertyChanged(CACHE_TYPE_HOLDER_PROPERTY, old, this.cacheTypeHolder);
	}
	
    public boolean usesProjectDefaultCacheSize() {
    	return this.cacheSizeHolder.usesProjectDefaultCacheSize();
    }
    
    public void setUseProjectDefaultCacheSize(boolean useProjectDefaultCacheSize) {
        boolean old = this.cacheSizeHolder.usesProjectDefaultCacheSize();
        this.cacheSizeHolder.setUseProjectDefaultCacheSize(useProjectDefaultCacheSize);
        firePropertyChanged(USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY, old, this.cacheSizeHolder.usesProjectDefaultCacheSize());
    }
    
    public void setDontUseProjectDefaultCacheSize(int size) {
        setCacheSize(size);
        firePropertyChanged(USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY, true, false);
    }

    public CacheSizeHolder getCacheSizeHolder() {
    	return this.cacheSizeHolder;
    }
    
    public int getCacheSize() {
		return this.cacheSizeHolder.getCacheSize();
	}

	public void setCacheSize(int cacheSize) {
		int oldCacheSize = this.cacheSizeHolder.getCacheSize();
		this.cacheSizeHolder.setCacheSize(cacheSize);
		firePropertyChanged(CACHE_SIZE_PROPERTY, oldCacheSize, cacheSize);
	}
    
	private void setCacheSizeHolder(CacheSizeHolder cacheSize) {
		Object old = this.cacheSizeHolder;
		this.cacheSizeHolder = cacheSize;
		firePropertyChanged(CACHE_SIZE_HOLDER_PROPERTY, old, this.cacheSizeHolder);
	}
	
    public MWCacheExpiry getCacheExpiry() {
        return this.cacheExpiry;
    }
	
    public void setCacheExpiry(MWCacheExpiry cacheExpiry) {
        Object old = this.cacheExpiry;
        this.cacheExpiry = cacheExpiry;
        firePropertyChanged(CACHE_EXPIRY_PROPERTY, old, this.cacheExpiry);
    }
    
    public void setUseProjectDefaultCacheExpiry(boolean projectCacheExpiry) {
        if (projectCacheExpiry) {
            setCacheExpiry(new MWProjectDefaultCacheExpiry(this));
        }
        else {
            if (getCacheExpiry() instanceof MWProjectDefaultCacheExpiry) {
                //TODO initialize based on project defaults
                setCacheExpiry(new MWDescriptorCacheExpiry(this));
            }
        }
    }
    
    public void descriptorInheritanceChanged() {
    	if (isRootDescriptor()) {
    		if (this.cacheSizeHolder instanceof NullCacheSizeHolder) {
    			setCacheSizeHolder(new CacheSizeHolderImpl());
    		}
    		if (this.cacheTypeHolder instanceof NullCacheTypeHolder) {
    			setCacheTypeHolder(new CacheTypeHolderImpl());
    		}
    	}
    	else {
    		if (!(this.cacheSizeHolder instanceof NullCacheSizeHolder)) {
    			setCacheSizeHolder(new NullCacheSizeHolder());
    		}
    		if (!(this.cacheTypeHolder instanceof NullCacheTypeHolder)) {
    			setCacheTypeHolder(new NullCacheTypeHolder());
    		}
    	}
    	firePropertyChanged(DESCRIPTOR_INHERITANCE_PROPERTY, null);
    }
    
	//root meaning the top of the inheritance hierarchy, not necessarily marked as isRoot = true
	//the descriptor that has no parent descriptor or when the hierarchy beings to loop
	public boolean isRootDescriptor() {
		MWDescriptor descriptor = null;
		for (Iterator i = getOwningDescriptor().inheritanceHierarchy(); i.hasNext();) {
			descriptor = (MWDescriptor) i.next();
		}
		return getOwningDescriptor() == descriptor;
	}
    
    
	// ***************** runtime conversion **********************
	
	public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor)
	{
		MWCachingPolicy projectCachingPolicy = getProject().getDefaultsPolicy().getCachingPolicy();

        if (this.existenceChecking.getMWModelOption() == EXISTENCE_CHECKING_PROJECT_DEFAULT) {
            runtimeDescriptor.getQueryManager().getDoesExistQuery().setExistencePolicy(((Integer) projectCachingPolicy.getExistenceChecking().getTopLinkModelOption()).intValue());
        }
        else {
            this.existenceChecking.setMWOptionOnTopLinkObject(runtimeDescriptor);
        }

		// Cache Type=
        this.cacheTypeHolder.adjustRuntimeDescriptor(runtimeDescriptor);

		this.cacheSizeHolder.adjustRuntimeDescriptor(runtimeDescriptor);

		// Cache Coordination

		if (this.cacheCoordination.getMWModelOption() == CACHE_ISOLATION_PROJECT_DEFAULT) {
			runtimeDescriptor.setCacheSynchronizationType(((Integer) projectCachingPolicy.getCacheCoordination().getTopLinkModelOption()).intValue());
		}
        else {
            this.cacheCoordination.setMWOptionOnTopLinkObject(runtimeDescriptor);
        }

		// Cache Isolation

		if (this.cacheIsolation.getMWModelOption() == CACHE_COORDINATION_PROJECT_DEFAULT) {
            runtimeDescriptor.setIsIsolated(projectCachingPolicy.getCacheIsolation().getMWModelOption() == CACHE_ISOLATION_ISOLATED);
		}
        else {
            this.cacheIsolation.setMWOptionOnTopLinkObject(runtimeDescriptor);
        }
        
        this.cacheExpiry.adjustRuntimeDescriptor(runtimeDescriptor);
	}
	

	// **************** TopLink only methods **********************
	
    private MWDescriptorCacheExpiry getCacheExpiryForTopLink() {
        return (MWDescriptorCacheExpiry) this.cacheExpiry.getPersistedPolicy();
    }
    
    private void setCacheExpiryForTopLink(MWDescriptorCacheExpiry policy) {
        if (policy != null) {
            this.cacheExpiry = policy;
        }
        else {
            this.cacheExpiry = new MWProjectDefaultCacheExpiry(this);
        }
    }
    
    public MWCachingPolicy getPersistedPolicy() {
		return this;
	}
	
    private int getCacheSizeForTopLink() {
        return this.cacheSizeHolder.getCacheSizeForTopLink();
    }
    
    private void setCacheSizeForTopLink(int cacheSize) {
		this.cacheSizeHolder = new CacheSizeHolderImpl();
		this.cacheSizeHolder.setCacheSizeForTopLink(cacheSize);
    }
    
    private CacheTypeOption getCacheTypeForTopLink() {
        return this.cacheTypeHolder.getCacheTypeForTopLink();
    }
    
    private void setCacheTypeForTopLink(CacheTypeOption cacheType) {
		this.cacheTypeHolder = new CacheTypeHolderImpl();
		this.cacheTypeHolder.setCacheTypeForTopLink(cacheType);
    }
    public void postProjectBuild() {
    	super.postProjectBuild();
    	if (!isRootDescriptor()) {
    		this.cacheSizeHolder = new NullCacheSizeHolder();
    		this.cacheTypeHolder = new NullCacheTypeHolder();
    	}
    }
    
	public interface CacheSizeHolder {
		/**
		 * Return whether the cacheSize can be set, don't allow user
		 * to set this if false;
		 */
		boolean sizeCanBeSet();
		
	    boolean usesProjectDefaultCacheSize();
	    void setUseProjectDefaultCacheSize(boolean useProjectDefaultCacheSize);

	    int getCacheSize();
		void setCacheSize(int cacheSize);
	    
		int getCacheSizeForTopLink();
		void setCacheSizeForTopLink(int cacheSize);
	    
		void adjustRuntimeDescriptor(ClassDescriptor descriptor);
		
	}
	
	private class NullCacheSizeHolder implements CacheSizeHolder {
		public boolean sizeCanBeSet() {
			return false;
		}
		
		public boolean usesProjectDefaultCacheSize() {
       		MWDescriptor rootDescriptor = getOwningDescriptor().getInheritancePolicy().getRootDescriptor();
    		return rootDescriptor.getTransactionalPolicy().getCachingPolicy().usesProjectDefaultCacheSize();
		}
		public void setUseProjectDefaultCacheSize(boolean arg0) {
			//throw new UnsupportedOperationException();
		}
		public int getCacheSize() {
       		MWDescriptor rootDescriptor = getOwningDescriptor().getInheritancePolicy().getRootDescriptor();
    		return rootDescriptor.getTransactionalPolicy().getCachingPolicy().getCacheSize();
		}
		public void setCacheSize(int arg0) {
			//throw new UnsupportedOperationException();
		}
		public int getCacheSizeForTopLink() {
			return -1;
		}
		public void setCacheSizeForTopLink(int arg0) {
		}
		
		public void adjustRuntimeDescriptor(ClassDescriptor arg0) {
			
		}
	}		
	
	private class CacheSizeHolderImpl implements CacheSizeHolder {
	    private volatile int cacheSize;
	    private volatile transient boolean useProjectDefaultCacheSize;
	        public static final String USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY = "useProjectDefaultCacheSize";

	    public CacheSizeHolderImpl() {
	        this.cacheSize = -1;
	        this.useProjectDefaultCacheSize = true;
	    }
	    
	    public boolean sizeCanBeSet() {
	    	return true;
	    }
	    public boolean usesProjectDefaultCacheSize() {
	        return this.useProjectDefaultCacheSize;
	    }
	    
	    public void setUseProjectDefaultCacheSize(boolean useProjectDefaultCacheSize) {
	        boolean old = this.useProjectDefaultCacheSize;
	        this.useProjectDefaultCacheSize = useProjectDefaultCacheSize;
	        if (old != this.useProjectDefaultCacheSize) {
	            //not sure whether we should use setCacheSize() for these
	            if (this.useProjectDefaultCacheSize) {
	                this.cacheSize = -1;
	            }
	            else if (this.cacheSize == -1){
	                setCacheSize(getProject().getDefaultsPolicy().getCachingPolicy().getCacheSize());
	            }
	        }
	        firePropertyChanged(USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY, old, this.useProjectDefaultCacheSize);
	    }
	    
	    public void setDontUseProjectDefaultCacheSize(int size) {
	        setCacheSize(size);
	        firePropertyChanged(USE_PROJECT_DEFAULT_CACHE_SIZE_PROPERTY, true, false);
	    }

	    public int getCacheSize() {
			return this.cacheSize;
		}

		public void setCacheSize(int cacheSize) {
			int oldCacheSize = this.cacheSize;
			this.cacheSize = cacheSize;
	        if (oldCacheSize != this.cacheSize) {
	            if (oldCacheSize == -1) {
	                setUseProjectDefaultCacheSize(false);
	            }
	        }
			firePropertyChanged(CACHE_SIZE_PROPERTY, oldCacheSize, cacheSize);
		}

		public int getCacheSizeForTopLink() {
	        if (useProjectDefaultCacheSize) {
	            return -1;
	        }
	        return this.cacheSize;
		}
	    
	    public void setCacheSizeForTopLink(int cacheSize) {
	        if (cacheSize == -1) {
	            useProjectDefaultCacheSize = true;
	        }
	        else {
	        	useProjectDefaultCacheSize = false;
	        }
	        this.cacheSize = cacheSize;
	    }
	    
	    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
			// Cache Size
	        if (usesProjectDefaultCacheSize()) {
	            runtimeDescriptor.setIdentityMapSize(getProject().getDefaultsPolicy().getCachingPolicy().getCacheSize());
	        }
	        else {
	            runtimeDescriptor.setIdentityMapSize(getCacheSize());
	        }
	    }

	}
	
	public interface CacheTypeHolder {
		/**
		 * Return whether the cacheType can be set, don't allow user
		 * to set this if false;
		 */
		boolean typeCanBeSet();

		CacheTypeOption getCacheType();
		void setCacheType(CacheTypeOption cacheType);
		
		void adjustRuntimeDescriptor(ClassDescriptor descriptor);
		CacheTypeOption getCacheTypeForTopLink();
		void setCacheTypeForTopLink(CacheTypeOption cacheType);
		
	}
	
	private class NullCacheTypeHolder implements CacheTypeHolder {
		public boolean typeCanBeSet() {
			return false;
		}
		public CacheTypeOption getCacheType() {
       		MWDescriptor rootDescriptor = MWDescriptorCachingPolicy.this.getOwningDescriptor().getInheritancePolicy().getRootDescriptor();
    		return rootDescriptor.getTransactionalPolicy().getCachingPolicy().getCacheType();
		}
		
		public void setCacheType(CacheTypeOption arg0) {
			//throw new UnsupportedOperationException();				
		}
		public void adjustRuntimeDescriptor(ClassDescriptor arg0) {				
		}
		
		public CacheTypeOption getCacheTypeForTopLink() {
			return null;
		}
		public void setCacheTypeForTopLink(CacheTypeOption cacheType) {
			
		}
	}	
	
	private class CacheTypeHolderImpl implements CacheTypeHolder {
		
		private CacheTypeOption cacheType;
		
		private CacheTypeHolderImpl() {
			this.cacheType = (CacheTypeOption) cacheTypeOptions().topLinkOptionForMWModelOption(CACHE_TYPE_PROJECT_DEFAULT);
		}
		
		public boolean typeCanBeSet() {
			return true;
		}
		
		public CacheTypeOption getCacheType() {
			return this.cacheType;
		}

		public void setCacheType(CacheTypeOption cacheType) {
			this.cacheType = cacheType;
		}

		public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
			if (this.cacheType.getMWModelOption() == CACHE_TYPE_PROJECT_DEFAULT) {
			    runtimeDescriptor.setIdentityMapClass(ClassTools.classForName((String) getProject().getDefaultsPolicy().getCachingPolicy().getCacheType().getTopLinkModelOption()));
			}
			else {
			    this.cacheType.setMWOptionOnTopLinkObject(runtimeDescriptor);
			}
		}
		
		public CacheTypeOption getCacheTypeForTopLink() {
			return this.cacheType;
		}
		
		public void setCacheTypeForTopLink(CacheTypeOption cacheType) {
			this.cacheType = cacheType;
		}
	}
}
