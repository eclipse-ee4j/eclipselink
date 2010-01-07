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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.internal.sessions.factories.model.login.AppendNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DescriptorLevelDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.IgnoreNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.NoDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.NodeOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.RelativePositionOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLBinderPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLLoginConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class XMLLoginConfig
 * 
 * @see XMLLoginConfig
 * 
 * @author Tran Le
 */
public final class XMLLoginAdapter extends LoginAdapter {

		public final static String EQUAL_NAMESPACE_RESOLVERS_PROPERTY = "equalNamespaceResolvers";
		
		public final static String DOCUMENT_PRESERVATION_POLICY_PROPERTY = "documentPreservationPolicy";
			public final static String DESCRIPTOR_LEVEL_DOCUMENT_PRESERVATION_POLICY = "Descriptor Level";
				public final static String NO_DOCUMENT_PRESERVATION_POLICY = "No Document";
				public final static String XML_BINDER_PRESERVATION_POLICY = "XML Binder";
				
		public final static String NODE_ORDERING_POLICY_PROPERTY = "nodeOrderingPolicy";
			public final static String APPEND_NEW_ELEMENTS = "Append New Elements";
			public final static String IGNORE_NEW_ELEMENTS = "Ignore New Elements";
			public final static String RELATIVE_POSITION = "Relative Position";

			
		public final static String SAX_RUNTIME_PLATFORM_CLASS_NAME = "oracle.toplink.ox.platform.SAXPlatform";
		public final static String DOM_RUNTIME_PLATFORM_CLASS_NAME = "oracle.toplink.ox.platform.DOMPlatform";
		public final static String DEFAULT_PLATFORM_CLASS_NAME = SAX_RUNTIME_PLATFORM_CLASS_NAME;
		
			
    	/**
    	 * Creates a new XMLLoginConfig for the specified model object.
    	 */
    XMLLoginAdapter( SCAdapter parent, XMLLoginConfig scConfig) {
    		
    		super( parent, scConfig);
    	}
    	/**
    	 * Creates a new XMLLoginAdapter.
    	 */
    	protected XMLLoginAdapter( SCAdapter parent) {
    		
    		super( parent);
    	}
    	
    	@Override
    	protected void initializeDefaults() {
    		super.initializeDefaults();
    		
    		this.setPlatformClass(getDefaultPlatformClassName());
    		this.setDocumentPreservationPolicy(NO_DOCUMENT_PRESERVATION_POLICY);
    		this.setNodeOrderingPolicy(APPEND_NEW_ELEMENTS);
    	}
    	
    	@Override
    	protected void initializeFromModel(Object scConfig) {
    		super.initializeFromModel(scConfig);
    		
    		if (getDocumentPreservationPolicyConfig() == null) {
    			setDocumentPreservationPolicy(NO_DOCUMENT_PRESERVATION_POLICY);
    		}
    		
    		if (getNodeOrderingPolicyConfig() == null) {
    			setNodeOrderingPolicy(APPEND_NEW_ELEMENTS);
    		}
    	}
    	/**
    	 * Factory method for building this model.
    	 */
    	protected Object buildModel() {
    		
    		return new XMLLoginConfig();
    	}
    	
    	private DocumentPreservationPolicyConfig getDocumentPreservationPolicyConfig() {
    		return login().getDocumentPreservationPolicy();
    	}
    	
    	private NodeOrderingPolicyConfig getNodeOrderingPolicyConfig() {
    		return getDocumentPreservationPolicyConfig().getNodeOrderingPolicy();
    	}

    	/**
    	 * Returns the datasource platform class from user's preference.
    	 */
    	protected String getDefaultPlatformClassName() {

    	    return DEFAULT_PLATFORM_CLASS_NAME;
    	}
         
    	public Collection<String> getPlatformClassOptions() {
    		Collection<String> values = new ArrayList<String>(2);
    		values.add(XMLLoginAdapter.SAX_RUNTIME_PLATFORM_CLASS_NAME);
    		values.add(XMLLoginAdapter.DOM_RUNTIME_PLATFORM_CLASS_NAME);
    		return values;
    	}
    	
    	public Collection<String> getDocumentPreservationPolicyOptions() {
    		Collection<String> values = new ArrayList<String>(3);
    		values.add(NO_DOCUMENT_PRESERVATION_POLICY);
    		values.add(DESCRIPTOR_LEVEL_DOCUMENT_PRESERVATION_POLICY);
    		values.add(XML_BINDER_PRESERVATION_POLICY);
    		return values;
    	}
    	
    	public Collection<String> getNodeOrderingPolicyOptions() {
    		Collection<String> values = new ArrayList<String>(3);
    		values.add(APPEND_NEW_ELEMENTS);
    		values.add(IGNORE_NEW_ELEMENTS);
    		values.add(RELATIVE_POSITION);
    		return values;
        }
        
    	boolean platformIsXml() {
    		
    		return true;
    	}
    	/**
    	 * Returns the adapter's Config Model Object.
    	 */
    	private final XMLLoginConfig login() {
    		return ( XMLLoginConfig)this.getModel();
    	}
    	   	
    	public boolean isEqualNamespaceResolvers() {
    		return this.login().getEqualNamespaceResolvers();
    	}
    	
    	public void setEqualNamespaceResolvers(boolean value) {
    		boolean old = this.login().getEqualNamespaceResolvers();
    		this.login().setEqualNamespaceResolvers(value);
    		this.firePropertyChanged(EQUAL_NAMESPACE_RESOLVERS_PROPERTY, old, value);
    	}
    	
    	public String getDocumentPreservationPolicyType() {
    		if (getDocumentPreservationPolicyConfig() instanceof DescriptorLevelDocumentPreservationPolicyConfig) {
    			return DESCRIPTOR_LEVEL_DOCUMENT_PRESERVATION_POLICY;
    		} else if (getDocumentPreservationPolicyConfig() instanceof NoDocumentPreservationPolicyConfig) {
    			return NO_DOCUMENT_PRESERVATION_POLICY;
    		} else if (getDocumentPreservationPolicyConfig() instanceof XMLBinderPolicyConfig) {
    			return XML_BINDER_PRESERVATION_POLICY;
    		} else {
    			throw new IllegalStateException("Invalid document preservation policy type.");
    		}	
    	}
 
       	public void setDocumentPreservationPolicy(String policyType) {
    		Object old = login().getDocumentPreservationPolicy();
       		DocumentPreservationPolicyConfig config = this.setConfigDocumentPreservationPolicy(policyType);
       		this.setNodeOrderingPolicy(APPEND_NEW_ELEMENTS);
    		this.firePropertyChanged(DOCUMENT_PRESERVATION_POLICY_PROPERTY, old, config);
    	}
       	
       	private DocumentPreservationPolicyConfig setConfigDocumentPreservationPolicy(String policyType) {
       		DocumentPreservationPolicyConfig config = null; 
       		if (NO_DOCUMENT_PRESERVATION_POLICY.equals(policyType)) {
       			config = new NoDocumentPreservationPolicyConfig();
       			login().setDocumentPreservationPolicy(config);
       		} else if (DESCRIPTOR_LEVEL_DOCUMENT_PRESERVATION_POLICY.equals(policyType)) {
       			config = new DescriptorLevelDocumentPreservationPolicyConfig();
       			login().setDocumentPreservationPolicy(config);
       		} else if (XML_BINDER_PRESERVATION_POLICY.equals(policyType)) {
       			config = new XMLBinderPolicyConfig();
       			login().setDocumentPreservationPolicy(config);
       		}
       		return config;
       	}

    	public String getNodeOrderingPolicyType() {
    		if (getNodeOrderingPolicyConfig() instanceof AppendNewElementsOrderingPolicyConfig) {
    			return APPEND_NEW_ELEMENTS;
    		} else if (getNodeOrderingPolicyConfig() instanceof IgnoreNewElementsOrderingPolicyConfig) {
    			return IGNORE_NEW_ELEMENTS;
    		} else if (getNodeOrderingPolicyConfig() instanceof RelativePositionOrderingPolicyConfig) {
    			return RELATIVE_POSITION;
    		} else {
    			throw new IllegalStateException("Invalid node ordering policy type.");
    		}	
    	}
 
       	public void setNodeOrderingPolicy(String policyType) {
    		Object old = this.getNodeOrderingPolicyConfig();
    		NodeOrderingPolicyConfig config = this.setConfigNodeOrderingPolicy(policyType);
    		this.firePropertyChanged(NODE_ORDERING_POLICY_PROPERTY, old, config);
    	}
       	
       	private NodeOrderingPolicyConfig setConfigNodeOrderingPolicy(String policyType) {
       		NodeOrderingPolicyConfig config = null; 
       		if (APPEND_NEW_ELEMENTS.equals(policyType)) {
       			config = new AppendNewElementsOrderingPolicyConfig();
       			getDocumentPreservationPolicyConfig().setNodeOrderingPolicy(config);
       		} else if (IGNORE_NEW_ELEMENTS.equals(policyType)) {
       			config = new IgnoreNewElementsOrderingPolicyConfig();
       			getDocumentPreservationPolicyConfig().setNodeOrderingPolicy(config);
       		} else if (RELATIVE_POSITION.equals(policyType)) {
       			config = new RelativePositionOrderingPolicyConfig();
       			getDocumentPreservationPolicyConfig().setNodeOrderingPolicy(config);
       		}
       		return config;
       	}
    }
