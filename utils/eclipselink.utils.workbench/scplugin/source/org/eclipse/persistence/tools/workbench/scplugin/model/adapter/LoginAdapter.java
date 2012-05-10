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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class LoginConfig
 * 
 * @see LoginConfig
 * 
 * @author Tran Le
 */
public abstract class LoginAdapter extends SCAdapter implements Property {
	// property change
	public final static String PLATFORM_CLASS_PROPERTY = "platformClass";
	public final static String USER_NAME_PROPERTY = "userName";
	public final static String PASSWORD_PROPERTY = "password";
	public final static String TABLE_QUALIFIER_PROPERTY = "tableQualifier";
	public final static String ENCRYPTION_CLASS_PROPERTY = "encryptionClass";
	public final static String EXTERNAL_TRANSACTION_CONTROLLER_PROPERTY = "externalTransactionController";
	public final static String EXTERNAL_CONNECTION_POOLING_PROPERTY = "externalConnectionPooling";

	public final static String DEFAULT_SEQUENCE_PROPERTY = "defaultSequence";
	private volatile SequencingAdapter sequencing;
	public final static String SEQUENCES_COLLECTION = "sequences";	
	public final static String SEQUENCE_PREALLOCATION_SIZE_PROPERTY = "sequencePreallocationSize";
	public final static String SEQUENCE_TABLE_PROPERTY = "sequenceTable";
	public final static String SEQUENCE_NAME_FIELD_PROPERTY = "sequenceNameField";
	public final static String SEQUENCE_COUNTER_FIELD_PROPERTY = "sequenceCounterField";

	private volatile Collection properties;
	
	private Boolean savePassword;
		public static final String SAVE_PASSWORD_PROPERTY = "savePassword";
	
	private Boolean saveUserName;
		public static final String SAVE_USERNAME_PROPERTY = "saveUsername";

	/**
	 * Creates a new LoginAdapter for the specified model object.
	 */
	LoginAdapter( SCAdapter parent, LoginConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new LoginAdapter.
	 */
	protected LoginAdapter( SCAdapter parent) {
		
		super( parent);
	}
	
	LoginAdapter() {
		
		super();
	}
	/**
	 * Subclasses should override this method to add their children
	 * to the specified collection.
	 * @see #children()
	 */
	protected void addChildrenTo( List children) {
		super.addChildrenTo( children);
		
		if( this.sequencing != null)
			children.add( this.sequencing);
	}
	/**
	 * Facade for setting defaultSequence and firing defaultSequence property changed.
	 */
	public SequenceAdapter setDefaultTableSequenceTable() {
		SequenceAdapter old = this.sequencing.getDefaultSequence();
		SequenceAdapter sequence = this.sequencing.setDefaultTableSequenceTable( SequencingAdapter.DEFAULT_SEQUENCE_NAME, getSequencePreallocationSize());
		this.firePropertyChanged( DEFAULT_SEQUENCE_PROPERTY, old, sequence);
		return sequence;
	}
	/**
	 * Facade for setting nativeSequencing and firing defaultSequence property changed.
	 */
	public SequenceAdapter setNativeSequencing() {
		SequenceAdapter old = this.sequencing.getDefaultSequence();
		SequenceAdapter sequence = this.sequencing.setNativeSequencing( SequencingAdapter.NATIVE_SEQUENCE_NAME, getSequencePreallocationSize());
		this.firePropertyChanged( DEFAULT_SEQUENCE_PROPERTY, old, sequence);
		return sequence;
	}
	
	@Override
	protected void initializeDefaults() {
		super.initializeDefaults();
		
		if( XMLSessionConfigProject.NATIVE_SEQUENCING_DEFAULT) setNativeSequencing();
		
	}
	
	/**
	 * Remove the sequence with the given name.
	 */
	public void removeSequenceNamed( String name) {
		
		SequenceAdapter sequence = this.sequencing.removeSequenceNamed( name);
		
		if( sequence != null)
			this.fireItemRemoved( SEQUENCES_COLLECTION, sequence);
	}
	/**
	 * Returns this config model property.
	 */
	public int getSequencePreallocationSize() {

		if( getDefaultSequence() == null)
			return XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT;

		return this.sequencing.getDefaultSequencePreallocationSize();
	}
	/**
	 * Sets this config model property.
	 */
	public void setSequencePreallocationSize( int value) {
		
		int old = getSequencePreallocationSize();

		this.sequencing.setDefaultSequencePreallocationSize( value);
		this.firePropertyChanged( SEQUENCE_PREALLOCATION_SIZE_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public String getSequenceTable() {

		return this.sequencing.getTableSequenceTable();
	}
	/**
	 * Sets this config model property.
	 */
	public void setSequenceTable( String value) {
		
		Object old = this.getSequenceTable();

		this.sequencing.setTableSequenceTable( value);
		this.firePropertyChanged( SEQUENCE_TABLE_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public String getSequenceNameField() {

		return this.sequencing.getTableSequenceNameField();
	}
	/**
	 * Sets this config model property.
	 */
	public void setSequenceNameField( String value) {
		
		Object old = this.getSequenceNameField();

		this.sequencing.setTableSequenceNameField( value);
		this.firePropertyChanged( SEQUENCE_NAME_FIELD_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public String getSequenceCounterField() {

		return this.sequencing.getTableSequenceCounterField();
	}
	/**
	 * Sets this config model property.
	 */
	public void setSequenceCounterField( String value) {
		
		Object old = this.getSequenceCounterField();

		this.sequencing.setTableSequenceCounterField( value);
		this.firePropertyChanged( SEQUENCE_COUNTER_FIELD_PROPERTY, old, value);
	}
	/**
	 * Returns the datasource platform.
	 */
	public String getPlatformName() {
		
	    if( this.getPlatformClass() == null)
	        return "Unknown Data Source";
		String className = ClassTools.shortNameForClassNamed( this.getPlatformClass());
		className = className.replaceAll( "Platform", "");

		return className;
	}
	/**
	 * Returns the datasource platform class from user's preference.
	 */
	protected abstract String getDefaultPlatformClassName();

	/**
	 * Returns the datasource platform class.
	 */
	public String getPlatformClass() {
		
		return this.loginConfig().getPlatformClass();
	}
	/**
	 * Sets this config model datasource platform class.
	 */
	public void setPlatformClass( String value) {
		
		Object old = this.loginConfig().getPlatformClass();

		this.loginConfig().setPlatformClass( value);
		this.firePropertyChanged( PLATFORM_CLASS_PROPERTY, old, value);
	}
	/**
	 * Returns this userName.
	 */
	public String getUserName() {
		
		return this.loginConfig().getUsername();
	}
	/**
	 * Sets this userName and the config model.
	 */
	public void setUserName( String name) {
		
		Object old = this.loginConfig().getUsername();
		
		this.loginConfig().setUsername( name);
		this.firePropertyChanged( USER_NAME_PROPERTY, old, name);
	}
	/**
	 * Returns this config model property.
	 */
	public String getPassword() {
		
		return this.loginConfig().getPassword();
	}
	/**
	 * Sets this config model property.
	 */
	public void setPassword( String value) {
		
		Object old = this.loginConfig().getPassword();

		this.loginConfig().setPassword( value);
		this.firePropertyChanged( PASSWORD_PROPERTY, old, value);
	}
	
	public Boolean isSavePassword() {
		return savePassword;
	}

	public void setSavePassword(Boolean savePassword) {
		Boolean old = this.isSavePassword();
		this.savePassword = savePassword;
		this.firePropertyChanged(SAVE_PASSWORD_PROPERTY, old.booleanValue(), savePassword.booleanValue());
		if (!this.isSavePassword()) {
			this.setPassword(null);
		}
	}

	public Boolean isSaveUsername() {
		return saveUserName;
	}

	public void setSaveUsername(Boolean saveUsername) {
		Boolean old = this.isSaveUsername();
		this.saveUserName = saveUsername;
		this.firePropertyChanged(SAVE_USERNAME_PROPERTY, old.booleanValue(), saveUsername.booleanValue());
		if (!this.isSaveUsername()) {
			this.setUserName(null);
		}
	}

	/**
	 * Returns this config model property.
	 */
	public String getTableQualifier() {
		
		return this.loginConfig().getTableQualifier();
	}
	/**
	 * Sets this config model property.
	 */
	public void setTableQualifier( String value) {
		
		Object old = this.loginConfig().getTableQualifier();

		this.loginConfig().setTableQualifier( value);
		this.firePropertyChanged( TABLE_QUALIFIER_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property.
	 */
	public String getEncryptionClass() {
		
		return this.loginConfig().getEncryptionClass();
	}
	/**
	 * Sets this config model property.
	 */
	public void setEncryptionClass( String value) {
		
		Object old = this.loginConfig().getEncryptionClass();

		this.loginConfig().setEncryptionClass( value);
		this.firePropertyChanged( ENCRYPTION_CLASS_PROPERTY, old, value);
	}
	/**
	 * Returns usesExternalConnectionPooling.
	 */
	public boolean usesExternalConnectionPooling() {
		
		return this.loginConfig().getExternalConnectionPooling();
	}
	/**
	 * Sets usesExternalConnectionPooling and the config model.
	 */
	public void setExternalConnectionPooling( boolean value) {
		
		boolean old = this.loginConfig().getExternalConnectionPooling();
		this.loginConfig().setExternalConnectionPooling( value);
		this.firePropertyChanged( EXTERNAL_CONNECTION_POOLING_PROPERTY, old, value);
	}
	/**
	 * Returns this config model property..
	 */
	public boolean usesExternalTransactionController() {
		
		return this.loginConfig().getExternalTransactionController();
	}
	/**
	 * Sets this config model property.
	 */
	public void setUsesExternalTransactionController( boolean value) {
		
		boolean old = this.loginConfig().getExternalTransactionController();

		this.loginConfig().setExternalTransactionController( value);
		this.firePropertyChanged( EXTERNAL_TRANSACTION_CONTROLLER_PROPERTY, old, value);
	}
	
	public void toString( StringBuffer sb) {
		
	    String platform = this.getPlatformClass();
	    if( platform == null)
	        sb.append( "no platform");
	    else
	        sb.append( ClassTools.shortNameForClassNamed( platform));
	}
    /**
     * Returns true when this config element has not been setup.
     */
    protected boolean isACleanConfig() {
        boolean cleanConfig = super.isACleanConfig();
            
    	return cleanConfig && this.platformClassIsDefault();
    }
    /**
     * Returns true when this uses the default Platform Class.
     */
    protected boolean platformClassIsDefault() {
        return this.getDefaultPlatformClassName().equals( this.getPlatformClass());
    }
	/**
	 * Initializes this adapter from the config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);
		
		if ( this.loginConfig().getEncryptedPassword() == null) {
		    this.loginConfig().setPassword( "");
		} else if (this.loginConfig().getPassword() != null && !this.loginConfig().getPassword().equals("")){
			this.setSavePassword(Boolean.TRUE);
		}
		
		if (this.loginConfig().getUsername() != null && !this.loginConfig().getUsername().equals("")) {
			this.setSaveUsername(Boolean.TRUE);
		}

		if ( this.loginConfig().getPlatformClass() == null)
		    this.loginConfig().setPlatformClass( this.getDefaultPlatformClassName());
		
		if ( this.loginConfig().getSequencingConfig() == null)
			this.sequencing = buildSequencing();
		else
			this.sequencing = ( SequencingAdapter)this.adapt((( LoginConfig)scConfig).getSequencingConfig());

		if ( this.loginConfig().getPropertyConfigs() != null) {
			this.properties.addAll( this.adaptAll( this.loginConfig().getPropertyConfigs()));
		}
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize() {
		super.initialize();
	
		this.properties = new Vector();
		this.savePassword = Boolean.FALSE;
		this.saveUserName = Boolean.FALSE;
	}
	/**
	 * Initializes this new model.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);

		this.sequencing = buildSequencing();
	}
	
	public boolean usesNativeSequencing() {
		
		return this.sequencing.usesNativeSequencing();
	}
	
	boolean platformIsRdbms() {
		
		return false;
	}
	
	boolean platformIsEis() {
		
		return false;
	}

	boolean platformIsXml() {
		
		return false;
	}
	/**
	 * Returns false.
	 */
	public boolean databaseDriverIsDriverManager() {

		return false;
	}
	/**
	 * Returns false.
	 */
	public boolean databaseDriverIsDataSource() {

		return false;
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final LoginConfig loginConfig() {
		
		return ( LoginConfig)this.getModel();
	}
	
	protected SequencingAdapter getSequencing() {
		
		return this.sequencing;
	}
	
	protected SequencingConfig getSequenceConfig() {
		
		return this.loginConfig().getSequencingConfig();
	}
	/**
	 * Returns an iterator on this collection of properties.
	 */
	public Iterator properties() {
		
		return this.getProperties().iterator();
	}
	/**
	 * Returns an iterator on this collection of properties.
	 */
	public int propertySize() {
		
		return this.getProperties().size();
	}
	/**
	 * Returns the collection of properties from the config model.
	 */
	private Collection getProperties() {
		
		return this.properties;
	}
	/**
	 * Returns the collection of properties from the config model.
	 */
	private Collection getPropertyConfigs() {

		if ( this.loginConfig().getPropertyConfigs() == null)
			this.loginConfig().setPropertyConfigs(new Vector());

		return this.loginConfig().getPropertyConfigs();
	}
	/**
	 * Adds the given properties and fire notification.
	 */
	public PropertyAdapter addProperty( String name, String value) {

		PropertyAdapter property = buildPropertyAdapter(name, value);

		this.getPropertyConfigs().add(property.propertyConfig());
		addItemToCollection(property, getProperties(), PROPERTY_COLLECTION);

		return property;
	}
	/**
	 * Factory method for building a child default SequencingAdapter.
	 */
	protected SequencingAdapter buildSequencing() {

		SequencingAdapter sequencing = new SequencingAdapter( this);
		
		return sequencing;
	}
	
	private PropertyAdapter buildPropertyAdapter(String name, String value) {

		return new PropertyAdapter(this, buildPropertyConfig( name, value));
	}

	private PropertyConfig buildPropertyConfig( String name, String value) {

		PropertyConfig config = new PropertyConfig();
		config.setName(name);
		config.setValue(value);
		return config;
	}
	/**
	 * Removes the given properties and fire notification.
	 */
	public void removeProperty( PropertyAdapter property) {

		this.getPropertyConfigs().remove(property.propertyConfig());
		removeItemFromCollection(property, getProperties(), PROPERTY_COLLECTION);
	}
	/**
	 * Removes all the properties and fire notification.
	 */
	protected void removeAllProperties() {

		Vector copy = new Vector(properties);

		for (Iterator iter = copy.iterator(); iter.hasNext();) {
			removeProperty(( PropertyAdapter) iter.next());
		}
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean sequencingIsDefault() {
			
		return this.sequencing.isDefault();
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean sequencingIsNative() {
			
		return this.sequencing.isNative();
	}	
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean sequencingIsCustom() {
			
		return this.sequencing.isCustom();
	}

	public SequenceAdapter getDefaultSequence() {
		
		return this.sequencing.getDefaultSequence();
	}

	/**
	 * Returns an iterator on a collection of sequences adapters.
	 */
	public ListIterator<SequenceAdapter> sequences() {
		
		return this.sequencing.sequences();
	}

	/**
	 * Removes the given sequence.
	 */
	public SequenceAdapter removeSequence( SequenceAdapter sequenceAdapter) {	
		// remove adapter
		this.sequencing.removeSequence(sequenceAdapter);
		fireListChanged(SEQUENCES_COLLECTION);
		return sequenceAdapter;
	}

	public SequenceAdapter addSequence(String name, SequenceType sequenceType) {
		SequenceAdapter newAdapter = this.sequencing.addSequence(name, sequenceType);
		fireListChanged(SEQUENCES_COLLECTION);
		return newAdapter;
	}

	public SequenceAdapter createAndSetDefaultSequence(String name, SequenceType type) {
		SequenceAdapter old = this.getDefaultSequence();
		SequenceAdapter adapter = this.sequencing.createAndSetDefaultSequence(name, type);
		firePropertyChanged(DEFAULT_SEQUENCE_PROPERTY, old, adapter);
		return adapter;
	}

	public int sequencesSize() {
		return this.sequencing.sequencesSize();
	}
	
	public Iterator<String> sequenceNames() {
		return this.sequencing.sequenceNames();
	}
}
