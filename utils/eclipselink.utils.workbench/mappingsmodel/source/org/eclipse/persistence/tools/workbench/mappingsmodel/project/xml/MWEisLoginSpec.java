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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.internal.security.JCEEncryptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.DatasourceLogin;

public final class MWEisLoginSpec extends MWModel {
	/** the name should never be null or empty */
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private volatile String userName;
		public static final String USER_NAME_PROPERTY = "userName";

	private volatile String password;
		public static final String PASSWORD_PROPERTY = "password";
		
	private volatile boolean savePassword;
		public static final String SAVE_PASSWORD_PROPERTY = "savePassword";

	private MWClassHandle connectionSpecClassHandle;
		public static final String CONNECTION_SPEC_CLASS_PROPERTY = "connectionSpecClass";

	private volatile String connectionFactoryUrl;
		public static final String CONNECTION_FACTORY_URL_PROPERTY = "connectionFactoryUrl";

	private volatile Collection properties;
		public final static String PROPERTY_COLLECTION = "property";

	private volatile String j2cAdapterName;
		public static String J2C_ADAPTER_NAME_PROPERTY = "j2cAdapter";
			public static String AQ_ADAPTER_NAME = "Oracle AQ";
			public static String JMS_ADAPTER_NAME = "JMS";
			public static String XML_FILE_ADAPTER_NAME = "XML File";
			public static String DEFAULT_ADAPTER_NAME = AQ_ADAPTER_NAME;
		public static String[] j2cAdapterNames = { AQ_ADAPTER_NAME, JMS_ADAPTER_NAME, XML_FILE_ADAPTER_NAME };
		public static final String J2C_ADAPTER_NAMES_COLLECTION = "j2cAdapterNames";

		public static String JMS_ADAPTER_CLASS_NAME = "org.eclipse.persistence.eis.adapters.jms.JMSPlatform";
		public static String ORACLE_AQ_ADAPTER_CLASS_NAME = "org.eclipse.persistence.eis.adapters.aq.AQPlatform";
		public static String XML_FILE_ADAPTER_CLASS_NAME = "org.eclipse.persistence.eis.adapters.xmlfile.XMLFilePlatform";
	
	public static final String PLATFORM_PREFERENCE = "eis platform";
	public static final String PLATFORM_PREFERENCE_DEFAULT = AQ_ADAPTER_NAME;


	/** cache this - it's expensive to instantiate */
	private static transient JCEEncryptor ENCRYPTOR;

	private static JCEEncryptor getEncryptor() {
		if (ENCRYPTOR == null) {
			try {
				ENCRYPTOR = new JCEEncryptor();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return ENCRYPTOR;
	}


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWEisLoginSpec() {
		super();
	}

	MWEisLoginSpec(MWEisProject newProject, String j2cAdapterName) {
		super(newProject);
		initialize(j2cAdapterName);
	}


	// ********** initialization **********

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.connectionSpecClassHandle = new MWClassHandle(this, this.buildConnectionSpecClassScrubber());
		this.savePassword = false;	
		this.properties = new Vector();
	}

	protected void initialize(String j2cAdapterName) {
		this.j2cAdapterName = j2cAdapterName;
	}


	// ********** accessors **********

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.getProject().nodeRenamed(this);
		}
	}

	public String getConnectionFactoryURL() {
		return this.connectionFactoryUrl;
	}

	public void setConnectionFactoryURL(String url) {
		Object old = this.connectionFactoryUrl;
		this.connectionFactoryUrl = url;
		this.firePropertyChanged(CONNECTION_FACTORY_URL_PROPERTY, old, url);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		Object old = this.userName;
		this.userName = userName;
		this.firePropertyChanged(USER_NAME_PROPERTY, old, userName);
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		Object old = this.password;
		this.password = password;
		this.firePropertyChanged(PASSWORD_PROPERTY, old, password);
	}

	public boolean isSavePassword() {
		return this.savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		boolean old = this.savePassword;
		this.savePassword = savePassword;
		this.firePropertyChanged(SAVE_PASSWORD_PROPERTY, old, savePassword);
	}

	public MWClass getConnectionSpecClass() {
		return this.connectionSpecClassHandle.getType();
	}

	public void setConnectionSpecClass(MWClass newConnectionSpecClass) {
		MWClass old = getConnectionSpecClass();
		this.connectionSpecClassHandle.setType(newConnectionSpecClass);
		firePropertyChanged(CONNECTION_SPEC_CLASS_PROPERTY, old, connectionSpecClassHandle.getType());
	}

	public Collection getProperties() {
		return this.properties;
	}

	public Iterator properties() {
		return getProperties().iterator();
	}

	public int propertySize() {
		return this.getProperties().size();
	}

	protected void removeAllProperties() {
		Vector copy = new Vector(properties);

		for (Iterator iter = copy.iterator(); iter.hasNext();) {
			removeProperty((MWProperty) iter.next());
		}
	}

	public MWProperty addProperty(String key, String value) {
		MWProperty property = new MWProperty(this);
		property.setKey(key);
		property.setValue(value);
		// buildPropertyModel(name, value);
		addItemToCollection(property, getProperties(), PROPERTY_COLLECTION);

		return property;
	}

	public void removeProperty(MWProperty property) {
		removeItemFromCollection(property, getProperties(), PROPERTY_COLLECTION);
	}

	public String getJ2CAdapterName() {
		return this.j2cAdapterName;
	}

	public void setJ2CAdapterName(String newValue) {
		Object oldValue = this.j2cAdapterName;
		this.j2cAdapterName = newValue;
		firePropertyChanged(J2C_ADAPTER_NAME_PROPERTY, oldValue, newValue);
	}

	public static Iterator j2CAdapterNames() {
		return CollectionTools.iterator(j2cAdapterNames);
	}

	public static Collection getJ2CAdapterNames() {
		return CollectionTools.collection(j2cAdapterNames);
	}

	public static int j2CAdapterNamesSize() {
		return j2cAdapterNames.length;
	}

	private String encryptedPassword() {
		return (this.password == null) ? null : getEncryptor().encryptPassword(password);
	}


	// ********** displaying and printing **********

	public void toString(StringBuffer sb) {
		sb.append(this.name);
	}

	public String displayString() {
		return this.name;
	}


	// ********** Containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.connectionSpecClassHandle);
	}

	private NodeReferenceScrubber buildConnectionSpecClassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWEisLoginSpec.this.setConnectionSpecClass(null);
			}
			public String toString() {
				return "MWEisLoginSpec.buildConnectionSpecClassScrubber()";
			}
		};
	}


	// ********** Runtime Conversion **********

	public DatasourceLogin buildRuntimeLogin() {
		EISLogin eisLogin = new EISLogin();

		if (getJ2CAdapterName().equals(AQ_ADAPTER_NAME)) {
			eisLogin.setPlatformClassName(ORACLE_AQ_ADAPTER_CLASS_NAME);
		} else if (getJ2CAdapterName().equals(JMS_ADAPTER_NAME)) {
			eisLogin.setPlatformClassName(JMS_ADAPTER_CLASS_NAME);
		} else if (getJ2CAdapterName().equals(XML_FILE_ADAPTER_NAME)) {
			eisLogin.setPlatformClassName(XML_FILE_ADAPTER_CLASS_NAME);
		} else {
			throw new UnsupportedOperationException("The j2cAdapterName is not valid");
		}

		MWClass connectionSpecType = getConnectionSpecClass();
		if (connectionSpecType != null) {
			// TODO use EISLogin#setConnectionSpecClassName(String) once the
			// runtime implements it
			eisLogin.setConnectionSpec(this.newInstance(connectionSpecType));
		}

		eisLogin.setConnectionFactoryURL(this.getConnectionFactoryURL());

		String userName = this.getUserName();
		if (userName != null) {
			eisLogin.setUserName(userName);
		}

		String password = this.getPassword();
		if (this.savePassword && password != null) {
			eisLogin.setPassword(password);
		}

		for (Iterator propsIter = getProperties().iterator(); propsIter.hasNext();) {
			MWProperty property = (MWProperty) propsIter.next();
			eisLogin.setProperty(property.getKey(), property.getValue());
		}

		return eisLogin;
	}

	// checked exceptions suck
	private EISConnectionSpec newInstance(MWClass connectionSpecType) {
		try {
			return (EISConnectionSpec) ClassTools.newInstance(connectionSpecType.getName(), this.getClass().getClassLoader());
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns the collection of properties from the config model.
	 */
	public Collection getPropertiesForTopLink() {

		return getProperties();
	}

	public void setPropertiesForTopLink(Collection properties) {
		this.properties = (properties == null) ? new Vector() : properties;
	}

	public String getJ2CAdapterNameForTopLink() {
		return this.j2cAdapterName;
	}

	public void setJ2CAdapterNameForTopLink(String newValue) {
		if (newValue == null) {
			newValue = DEFAULT_ADAPTER_NAME;
		}

		this.j2cAdapterName = newValue;
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWEisLoginSpec.class);

		descriptor.addDirectMapping("name", "name/text()");

		descriptor.addDirectMapping("j2cAdapterName", "getJ2CAdapterNameForTopLink", "setJ2CAdapterNameForTopLink", "j2c-adapter-name/text()");

		descriptor.addDirectMapping("userName", "user-name/text()");
		descriptor.addDirectMapping("password", "getPasswordForTopLink", "setPasswordForTopLink", "password/text()");
		descriptor.addDirectMapping("connectionFactoryUrl", "connection-factory-url/text()");
		((XMLDirectMapping) descriptor.addDirectMapping("savePassword", "save-password/text()")).setNullValue(Boolean.FALSE);

		XMLCompositeObjectMapping connectionSpecClassHandleMapping = new XMLCompositeObjectMapping();
		connectionSpecClassHandleMapping.setAttributeName("connectionSpecClassHandle");
		connectionSpecClassHandleMapping.setGetMethodName("getConnectionSpecClassHandleForTopLink");
		connectionSpecClassHandleMapping.setSetMethodName("setConnectionSpecClassHandleForTopLink");
		connectionSpecClassHandleMapping.setReferenceClass(MWClassHandle.class);
		connectionSpecClassHandleMapping.setXPath("connection-spec-class-handle");
		descriptor.addMapping(connectionSpecClassHandleMapping);

		XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
		propertiesMapping.setReferenceClass(MWProperty.class);
		propertiesMapping.setAttributeName("properties");
		propertiesMapping.setGetMethodName("getPropertiesForTopLink");
		propertiesMapping.setSetMethodName("setPropertiesForTopLink");
		propertiesMapping.setXPath("property");
		descriptor.addMapping(propertiesMapping);

		return descriptor;
	}

	private String getPasswordForTopLink() {
		return (this.savePassword) ? this.encryptedPassword() : null;
	}
	
	private void setPasswordForTopLink(String password) {
		this.password = (password == null) ? null : getEncryptor().decryptPassword(password);
	}

	/**
	 * check for null
	 */
	private MWClassHandle getConnectionSpecClassHandleForTopLink() {
		return (this.connectionSpecClassHandle.getType() == null) ? null : this.connectionSpecClassHandle;
	}
	private void setConnectionSpecClassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildConnectionSpecClassScrubber();
		this.connectionSpecClassHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

}
