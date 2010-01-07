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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.scplugin.model.SCModel;
import org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * Base class for all Session Configuration adapters.
 * This family of class allows the Session Configuration model classes
 * to be integrated with the Mapping Workbench UI framework.
 * 
 * Typically, subclasses should considering implementing the following methods:
 * 	the constructor for creating a new config object.
 * 	the constructor for adapting an existing config object.
 *		#addChildrenTo(List children)
 *		#buildModel()
 *		#initialize()
 *		#initialize( Object config)
 *		#initializeDefaults()
 *		#initializeFromModel( Object config)
 *		#postInitializationFromModel()
 *		#preSaving
 *		#postSaving
 *		#toString( StringBuffer sb)
 *
 * @author Tran Le
 */
public abstract class SCAdapter extends SCModel {

	private final static String adapterPackageName = ClassTools.packageNameFor( SCAdapter.class) + ".";
	private final static String adapterClassSuffix = "Adapter";
	public final static String modelClassSuffix = "Config";

	private volatile Object config;
	private volatile boolean configClean;			// false when config element containts users data.
	private volatile boolean configRequired;		// true when config element is mandated in the schema.

	/**
	 * Constructor for SCAdapter.
	 */
	SCAdapter() {
		super();
	}
	/**
	 * Creates a new SCAdapter and builds a new config object.
	 */
	protected SCAdapter( SCAdapter parent) {
		super( parent);

		this.initialize( buildModel());
		this.initializeDefaults();
		
		this.setConfigClean( true);
	}
	/**
	 * Creates a SCAdapter for adapting the specified config object.
	 */
	SCAdapter( SCAdapter parent, Object configObject) {
		super( parent);
		
		this.initializeFromModel( configObject);
		
		this.setConfigClean( false); 	// contains user's config
	}
	/**
	 * Internal: Factory method for building this model.
	 */
	protected abstract Object buildModel();

	/**
	 * Returns the Preferences node used by the SC.
	 */
	Preferences preferences() {

		return ((SCAdapter) getParent()).preferences();
	}
	/**
	 * Returns the <code>ClassRepository</code> that should be used by the
	 * sessions.xml.
	 *
	 * @return The repository for classpath entries and classes
	 */
	public ClassRepository getClassRepository()
	{
		return ((SCAdapter) getParent()).getClassRepository();
	}
	/**
	 * Internal: Returns the adapter's Config Object.
	 */
	protected Object getModel() {
	
		return this.config;
	}
	/**
	 * Returns this sessions.xml version.
	 * // true when config is mandated in the schema.
	 */
	protected final String getConfigFileVersion() {
		if( this.getParent() == null) {
		    return (( TopLinkSessionsAdapter)this).getVersion();
		}
		return (( SCAdapter)this.getParent()).getConfigFileVersion();
	}
	/**
	 * Returns true if sessions.xml version is previous to 10g.  
	 * For EL added the version > 3 clause since the version number has reset.  
	 * When EL Workbench surpasses 3.0 this will need to be revisited, but perhaps we 
	 * won't be supporting opening older TL sessions files and this method can be removed.
	 */
	protected final boolean configVersionIsPre10g() {
		String versionString = this.getConfigFileVersion();
		int version = 0;
		for( int i = 0; i < versionString.length(); i++) {
		    char c = versionString.charAt( i);
		    if( Character.isDigit( c)) 
		        version *= 10;
		    else
		        break;
		    version += Character.digit( c, 10);
		}
		return ( version > 3 && version < 10);
	}
	/**
	 * Returns True when this config is clean and is not a mandated element in the schema.
	 */
	public boolean hasNoConfigToSave() {
	
		return this.isACleanConfig() && !isARequiredConfig();
	}
	/**
	 * Returns True when this config element has not been setup.
	 */
	protected boolean isACleanConfig() {
	
		return this.configClean;
	}
	/**
	 * Returns True when this config element is mandated in the schema.
	 */
	public boolean isARequiredConfig() {
	
		return this.configRequired;
	}
	/**
	 * Sets the adapter's Config Object.
	 */
	private final void setModel( Object config) {
		
		this.config = config;
	}
	/**
	 * Sets the configClean flag.
	 * Allows at saving, to ignore this config if it does not contains user data.
	 * True when config element has not been setup.
	 */
	protected final void setConfigClean( boolean clean) {
		
		this.configClean = clean;
	}
	/**
	 * Sets the configRequired flag.
	 * True when config element is mandated in the schema.
	 */
	protected final void setConfigRequired( boolean required) {
		
		this.configRequired = required;
	}
	/**
	 * Mark the object and all its descendants as clean. 
	 * Typically used after a config model and its adapter
	 * has been created and initialized.
	 */
	protected void markEntireConfigurationClean() {
		
		this.setConfigClean( true);
		for( Iterator i = children(); i.hasNext(); ) {
			(( SCAdapter)i.next()).setConfigClean( true);
		}
	}
	/**
	 * Mark the object and its parent as dirty branches
	 */
	public void markBranchDirty() {
	    super.markBranchDirty();
	    
	    this.setConfigClean( false);
	}
	/**
	 * Pre Saving: Prepares this instance config model and 
	 * its children config model for saving. 
	 */
	protected void preSaving() {

		return;
	}
	/**
	 * Post Saving: Re-intilizes this instance config model and 
	 * its children config model after saving. 
	 */
	protected void postSaving() {

		return;
	}
	/**
	 * Builds children adapters for the given modelObjects collection.
	 * and returns a collection of resulting adapters.
	 * 
	 * @param modelObjects
	 */	
	protected Collection adaptAll( Collection configObjects) {
		
		Collection result = new ArrayList( configObjects.size());
		
		for( Iterator i = configObjects.iterator(); i.hasNext(); ) {
			
			result.add( this.adapt( i.next()));
		}
		return result;
	}
	/**
	 * Builds this child adapter for the given modelObject.
	 * 
	 * @param modelObject
	 */
	protected SCAdapter adapt( Object configObject) {	
			
		if( configObject == null) return null;
		
		Constructor adapterConstructor = adapterConstructorFor( configObject);

		return SCAdapter.buildsAdapterWith( adapterConstructor, new Object[] { this, configObject });
	}
	/**
	 * From a collection of adapters extract the modelObjects.
	 */
	protected Collection modelObjectsFrom( Collection adapters) {
		
		Iterator iterator = new TransformationIterator( adapters.iterator()) {
			
				  protected Object transform( Object next) {
					 return (( SCAdapter)next).getModel(); 
				  }
			};
		return CollectionTools.collection( iterator);
	}
	/**
	* From a collection find the adapter for the specified modelObject.
	*/
	protected SCAdapter findAdapterFor( Object modelObject, Iterator iterator) {
		
		for( Iterator i = iterator; i.hasNext(); ){
			SCAdapter adapter = ( SCAdapter)i.next();
			if( adapter.getModel() == modelObject)
				return adapter;
		}
		return null;
   }
	/**
	 * Returns the two-parameter constructor of the given adapterClass
	 * (i.e. ASCAdapter( SCAdapter parent, Object configObject)).
	 * When the adapterClass is an abstract class, finds the appropriate concrete adapter
	 * with the method #adapterClassFor( Class);
	 * 
	 * @param configObject
	 */
	private  final Constructor adapterConstructorFor( Object configObject) {
	    
		Class adapterClass = adapterClassFor( configObject);
		Class configClass = configObject.getClass();
		
		return adapterConstructorFor( configClass, adapterClass, 2);
	}
	/**
	 * Returns the one-parameter constructor of the given adapterClass
	 */
	 final static Constructor adapterConstructor( Class adapterClass) {
	    Object configObject = null;
	    try {
			configObject = (( SCAdapter)adapterClass.newInstance()).buildModel(); // required zero-parameter constructor
		} 
		catch( InstantiationException ie) {
			throw new RuntimeException("Instantiation Exception When Instantiating " + adapterClass.getName(), ie);
		}
		catch( IllegalAccessException iae) {
			throw new RuntimeException("Illegal Access Exception When Instantiating " + adapterClass.getName(), iae);
		}
		return adapterConstructorFor( configObject.getClass(), adapterClass, 1);
	}
	/**
	 * Returns the appropriate adapter constructor based the given numberOfConstructorParameter
	 */
	private final static Constructor adapterConstructorFor( Class configClass, Class adapterClass, int numberOfConstructorParameter) {
		
		for( Iterator i = new ArrayIterator( adapterClass.getDeclaredConstructors()); i.hasNext(); ) {
			Constructor adapterConstructor = ( Constructor)i.next();
			
			Class[] parameters = adapterConstructor.getParameterTypes();

			if( parameters.length == numberOfConstructorParameter) {
				if(( parameters.length == 2) // ( SCAdapter parent, Object configObject)
					&& ( parameters[ 0].isAssignableFrom( SCAdapter.class)) 
					&& ( parameters[ 1].isAssignableFrom( configClass))) 
						return adapterConstructor;
				else if(( parameters.length == 1) // ( SCAdapter parent)
						&& ( parameters[ 0].isAssignableFrom( SCAdapter.class))) 
							return adapterConstructor;
				else if( parameters.length == 0) //zero-parameter
						return adapterConstructor;
			}
		}
		throw new NoSuchElementException( "No Valid Constructor Found for: " + adapterClass.getName());
	}
	/**
	 * Returns the adapter class for the given config object.
	 * 
	 * @see SCAdapter#adapterConstructorFor( Object)
	 */
	private final Class adapterClassFor( Object configObject) {

		String configClassName = ClassTools.shortNameFor( configObject.getClass());
		
		return adapterClassNamed( configClassName);
	}
	/**
	 * Helper method that returns the Adapter class for the given config class name.
	 * 
	 * @throws NoSuchElementException when scModelClass is not found.
	 */
	static final Class adapterClassNamed( String configClassName) {

		String adapterClassName = adapterPackageName 
							+ configClassName.substring( 0, configClassName.lastIndexOf( modelClassSuffix)) 
							+ adapterClassSuffix;
		Class adapterClass = null;
		try {
		    adapterClass = ClassTools.classForName( adapterClassName);
		}
		catch( RuntimeException e) {
			throw new NoSuchElementException( "Adapter Class Not Known For SC Model Class: " + configClassName);
		}
		return adapterClass;
	}
	/**
	 * Builds an SCAdapter with the given constructor.
	 */
	static final SCAdapter buildsAdapterWith( Constructor adapterConstructor, Object[] parameters)	{
	    
		SCAdapter newAdapter = null;
		try {
			newAdapter = ( SCAdapter)adapterConstructor.newInstance( parameters);
		} 
		catch( InvocationTargetException ite) {
			throw new RuntimeException("InvocationTarget Exception When Instantiating " + adapterConstructor.getName(), ite);
		}
		catch( InstantiationException ie) {
			throw new RuntimeException("Instantiation Exception When Instantiating " + adapterConstructor.getName(), ie);
		}
		catch( IllegalAccessException iae) {
			throw new RuntimeException("Illegal Access Exception When Instantiating " + adapterConstructor.getName(), iae);
		}
		return newAdapter;
	}
	/**
	 * Initializes this model from config.
	 */
	protected void initializeFromModel( Object configObject) {
		
		this.setModel( configObject);
	}	
	/**
	 * Post Initialization: allows validation, handle legacy model...
	 */
	protected void postInitializationFromModel() {
		
		for( Iterator iter = children(); iter.hasNext(); ) {
			(( SCAdapter) iter.next()).postInitializationFromModel();
		}
	}
	/**
	 * Initializes this new instance.
	 */
	protected void initialize() {
		super.initialize();

		this.setConfigRequired( false);
	}

	/**
	 * Initializes this new model inst. var. and aggregates.
	 */
	protected void initialize( Object newConfig) {
		
		this.setModel( newConfig);
	}
	/**
	 * Initializes this new model defaults.
	 * Default behavior is to do nothing.
	 */
	protected void initializeDefaults() {
		return;
	}
}
