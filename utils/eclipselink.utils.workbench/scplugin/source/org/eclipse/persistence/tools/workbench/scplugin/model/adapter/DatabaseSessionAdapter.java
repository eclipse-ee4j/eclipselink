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
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatformRepository;
import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.scplugin.SCProblemsConstants;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.event.SessionEventManagerConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DatabaseLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.EISLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class DatabaseSessionConfig
 * 
 * @see DatabaseSessionConfig
 * 
 * @author Tran Le
 */
public class DatabaseSessionAdapter extends SessionAdapter implements LoginHandler {
	// property change
	private volatile ProjectAdapter primaryProject;
	public final static String PRIMARY_PROJECT_PROPERTY = "primaryProject";
	private Collection additionalProjects;	
	public final static String ADDITIONAL_PROJECTS_COLLECTION = "additionalProjects";	
	private volatile boolean useAdditionalProjects;
	public final static String USE_ADDITIONAL_PROJECTS_COLLECTION = "useAdditionalProjects";	
	public final static String EXTERNAL_CONNECTION_POOLING_PROPERTY = "externalConnectionPooling";

	private volatile LoginAdapter login;
	public final static String LOGIN_CONFIG_PROPERTY = "login";

	public static final String MAP_XML_TYPE = "project-xml";
	public static final String MAP_CLASS_TYPE = "project-class";
	public static final String DEFAULT_MAPPING_TYPE = MAP_CLASS_TYPE;
	public static final String[] VALID_MAPPING_PROJECT_TYPE = {
		MAP_XML_TYPE,
		MAP_CLASS_TYPE
	};
	private volatile SessionBrokerAdapter broker; // parent broker

	/**
	 * Creates a new DatabaseSessionAdapter for the specified model object.
	 */
	DatabaseSessionAdapter( SCAdapter parent, DatabaseSessionConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new DatabaseSession.
	 */
	protected DatabaseSessionAdapter( SCAdapter parent, String name, ServerPlatform sp, DataSource ds) {
		
		super( parent, name);
		
		this.initializePlatforms( sp, ds);
	}	
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		
		DatabaseSessionConfig session = new DatabaseSessionConfig();
		//TOREVIEW - collection not initialized in model class & SessionEventManagerConfig is not initialized
		//FL_TOREVIEW
		session.setAdditionalProjects( new Vector());
		session.setSessionEventManagerConfig( new SessionEventManagerConfig());

		return session;		
	}
	/**
	 * Builds login adapter and intializes it config model.
	 */
	private LoginAdapter buildLogin( DataSource ds) {

	    LoginAdapter login = ds.buildLoginAdapter( this);
		this.databaseSession().setLoginConfig(( LoginConfig)login.getModel());
	
		login.setPlatformClass( ds.getPlatformClassName());
		return login;
	}
	/**
	 * Builds a Default Login based on user's preferences.
	 * Used when no login and no datasource info available. 
	 */
	LoginAdapter buildDefaultLogin() {

        DataSource ds = null;
        String dsType = this.preferences().get( SCPlugin.DATA_SOURCE_TYPE_PREFERENCE, "database");
        if( dsType.equals( SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_RELATIONAL_CHOICE))
            
            ds = buildDefaultRdbmsDataSource();
        else if( dsType.equals( SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_EIS_CHOICE))
            
            ds = buildDefaultEisDataSource();
        else if( dsType.equals( SCPlugin.DATA_SOURCE_TYPE_PREFERENCE_XML_CHOICE))
            
            ds = buildDefaultXmlDataSource();
        else
			throw new IllegalArgumentException( dsType);

        return buildLogin( ds);
	}
	
	private DataSource buildDefaultRdbmsDataSource() {	

        String platformName = this.preferences().get( SCPlugin.DATABASE_PLATFORM_PREFERENCE, SCPlugin.DATABASE_PLATFORM_PREFERENCE_DEFAULT);
        DatabasePlatform platform = DatabasePlatformRepository.getDefault().platformNamed( platformName);
        return new DataSource( platform);
	}
	
	private DataSource buildDefaultEisDataSource() {	

        String platformName = this.preferences().get( SCPlugin.EIS_PLATFORM_PREFERENCE, SCPlugin.EIS_PLATFORM_PREFERENCE_DEFAULT);
        return new DataSource( platformName);
	}
	
	private DataSource buildDefaultXmlDataSource() {	

        return DataSource.buildXmlDataSource();
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final DatabaseSessionConfig databaseSession() {
		
		return ( DatabaseSessionConfig)this.getModel();
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize() {
		
		super.initialize();
	
		this.additionalProjects = new Vector();
	}
	/**
	* Initializes this new model inst. var. and aggregates.
	*/
   protected void initialize( Object newConfig) {
	   super.initialize( newConfig);

	   this.databaseSession().setAdditionalProjects( new Vector()); 
	   
	   // This will make the file valid but it will still have a problem
	   this.addPrimaryProjectXmlNamed( "");
   }
	/**
	 * Initializes this adapter from the specified config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.primaryProject = ( ProjectAdapter)this.adapt( databaseSession().getPrimaryProject());

		this.additionalProjects.addAll( this.adaptAll( this.getAdditionalProjectsConfigs()));
		this.useAdditionalProjects = !additionalProjects.isEmpty();

		this.login = ( LoginAdapter)this.adapt( databaseSession().getLoginConfig());
	}
	
	protected void postInitializationFromModel() {
	    super.postInitializationFromModel();
	    
		if( this.configVersionIsPre10g()) {
			if( login == null)
			    this.login = this.buildLogin( DataSource.buildDefault());
			
			else if( StringTools.stringIsEmpty( this.login.getPlatformClass()))
			    this.setDefaultLoginPlatform();
		}
		else {
			if( login == null)
			    this.login = this.buildLogin( DataSource.buildDefault());
		}
		return;
	}
	
	private void setDefaultLoginPlatform() {
	    DataSource ds = null;
	    
	    if( this.login.platformIsRdbms())
	        ds = buildDefaultRdbmsDataSource();
	    else if( this.login.platformIsEis())
	        ds = buildDefaultEisDataSource();
	    else if (this.login.platformIsXml())
	    	ds = buildDefaultXmlDataSource();
	    else
	        throw new IllegalStateException();
	    
        login.setPlatformClass( ds.getPlatformClassName());
	}
	/**
	 * Initializes Server platform and Login platform of this new model..
	 */
	private void initializePlatforms( ServerPlatform sp, DataSource ds) {
	    
		this.login = this.buildLogin( ds);

	    this.initializeServerPlatform( sp);
	}
	/**
	 * Pre Saving: Prepares this instance config model and 
	 * its children config model for saving. 
	 * If Login is clean, set this config model loginConfig to null to prevent being saved.
	 */
	protected void preSaving() {
		super.preSaving();
		
		if( this.getLogin().hasNoConfigToSave()) 
		    setEmptyLoginConfig();
		else
		    this.getLogin().preSaving();
		return;
	}
	/**
	 * Post Saving: Re-intilizes this instance config model and 
	 * its children config model after saving. 
	 */
	protected void postSaving() {
		super.postSaving();
		
		syncSessionConfigLogin();

		this.getLogin().postSaving();
		return;
	}
	/**
	 * Handles saving case when Login hasn't been configured,
	 * and prevent default data to be written to file.
	 */
	private void setEmptyLoginConfig() {
	    
	    if( this.login.platformIsRdbms()) {
	        DatabaseLoginConfig emptyConfig = new DatabaseLoginConfig();
	        emptyConfig.setByteArrayBinding( XMLSessionConfigProject.BYTE_ARRAY_BINDING_DEFAULT);
	        emptyConfig.setOptimizeDataConversion( XMLSessionConfigProject.OPTIMIZE_DATA_CONVERSION_DEFAULT);
	        emptyConfig.setTrimStrings( XMLSessionConfigProject.TRIM_STRINGS_DEFAULT);
	        emptyConfig.setMaxBatchWritingSize(  new Integer( XMLSessionConfigProject.MAX_BATCH_WRITING_SIZE_DEFAULT));
	        emptyConfig.setJdbcBatchWriting( XMLSessionConfigProject.JDBC20_BATCH_WRITING_DEFAULT);
	        databaseSession().setLoginConfig( emptyConfig);
	    }
	    else if( this.login.platformIsEis()) {
	        databaseSession().setLoginConfig( new EISLoginConfig());
	    }
	}
	/**
	 * Synchronizes this config model loginConfig to its adapter.
	 */
	private void syncSessionConfigLogin() {
	    
		if( databaseSession().getLoginConfig() != ( LoginConfig)this.getLogin().getModel())
		    databaseSession().setLoginConfig(( LoginConfig)this.getLogin().getModel());
	}
	/**
	 * Returns this login adapter.
	 */
	public LoginAdapter getLogin() {
		
		return this.login;
	}
	/**
	 * Convenience method to get the login class platform.
	 */
	public String getPlatform() {
		
		return this.getLogin().getPlatformClass();
	}
	/**
	 * Convenience method to get the datasource platform.
	 */
	public String getDataSourceName() {
		
		return this.getLogin().getPlatformName();
	}
	
	public boolean platformIsRdbms() {
		
		return this.login.platformIsRdbms();
	}
	
	public boolean platformIsEis() {
		
		return this.login.platformIsEis();
	}
	
	public boolean platformIsXml() {
		
		return this.login.platformIsXml();
	}
		
	public void toString( StringBuffer sb) {
		super.toString( sb);
		
		sb.append( ", ");
		if( login == null) 
			sb.append( "NO LOGIN");
		else
		    this.login.toString( sb);
	}

	@Override
	public void setName( String name) {
		String oldName = getName();
		super.setName(name);

		if( this.isManaged()) {
			getBroker().sessionRenamed( oldName, name);
		}
	}

	public SessionBrokerAdapter getBroker() {
		return broker;
	}
	
	void setBroker( SessionBrokerAdapter broker) {
		this.broker = broker;
		this.setManaged( broker != null);
	}
	
	protected void setManaged( boolean managed) {
		super.setManaged( managed);

		if( managed) {
			this.initializeDefaults();
		}
		this.updateExternalConnectionPooling();
	}

	void externalTransactionControllerClassChanged() {
		
		if( this.hasJTA() && getServerPlatform().isCustom()) {
		    String jtaClass = (( CustomServerPlatformAdapter)getServerPlatform()).getExternalTransactionControllerClass();
		    boolean hasJtaClass = !StringTools.stringIsEmpty( jtaClass);
			getLogin().setUsesExternalTransactionController( hasJtaClass);
		}
		getLogin().setUsesExternalTransactionController( this.hasJTA());

		updateExternalConnectionPooling();
	}
	/**
	 * Returns an iterator on a collection of additionalProjects.
	 */
	public Iterator additionalProjects() {
		
		return new CloneIterator(this.additionalProjects);
	}
	/**
	 * Returns an iterator on a collection of additionalProjects.
	 */
	public Iterator additionalProjectNames() {
		
		return new TransformationIterator( additionalProjects()) {
			protected Object transform( Object next) {
				return (( ProjectAdapter) next).getName();
			}
		};
	}
	/**
	 * Returns the collection of additionalProjects from the config model.
	 */
	private Collection getAdditionalProjectsConfigs() {
		
		if( this.databaseSession().getAdditionalProjects() == null)
			this.databaseSession().setAdditionalProjects( new Vector());
			
		return this.databaseSession().getAdditionalProjects();
	}
	/**
	 * Returns ProjectAdapter.
	 */
	private ProjectAdapter getPrimaryProject() {
		
		return this.primaryProject;
	}
	public boolean isPrimaryProjectXml() {

		return ( this.primaryProject instanceof ProjectXMLAdapter);
	}
	public boolean isPrimaryProjectClass() {

		return ( this.primaryProject instanceof ProjectClassAdapter);
	}
	public boolean isServer() {
		return false;
	}
	/**
	* Returns this logLevel.
	*/
   public String getPrimaryProjectName() {

   	if( this.getPrimaryProject() == null)
			return null;

	   return this.getPrimaryProject().getName();
   }

   public ProjectClassAdapter addPrimaryProjectClassNamed( String name) {
		ProjectAdapter project = new ProjectClassAdapter( this, name);
	
		setPrimaryProject( project);
		return ( ProjectClassAdapter)this.primaryProject;
   }

   public ProjectXMLAdapter addPrimaryProjectXmlNamed( String name) {
		ProjectAdapter project = new ProjectXMLAdapter( this, name);
	
		setPrimaryProject( project);
		return ( ProjectXMLAdapter)this.primaryProject;
   }
   
   public void removePrimaryProject() {

		Object old = this.primaryProject;
	
		this.primaryProject = null;
		this.databaseSession().setPrimaryProject( null);
		this.firePropertyChanged( PRIMARY_PROJECT_PROPERTY, old, null);
   }
   /**
	* Sets this name and the config model.
	*/
   private void setPrimaryProject( ProjectAdapter project) {
		
		Object old = this.primaryProject;

		this.primaryProject = project;
		this.databaseSession().setPrimaryProject(( ProjectConfig)project.getModel());
		this.firePropertyChanged( PRIMARY_PROJECT_PROPERTY, old, project);
   }
   /**
	* Factory method for adding a Project.
	*/
   public ProjectClassAdapter addProjectClassNamed( String name) {
		
		ProjectClassAdapter project = new ProjectClassAdapter( this, name);
		
		this.addAdditionalProjects( project);
		this.fireItemAdded( ADDITIONAL_PROJECTS_COLLECTION, project);
		return project;
   }
   /**
	* Factory method for adding a Project.
	*/
   public ProjectXMLAdapter addProjectXmlNamed( String name) {
		
		ProjectXMLAdapter project = new ProjectXMLAdapter( this, name);
		
		this.addAdditionalProjects( project);
		this.fireItemAdded( ADDITIONAL_PROJECTS_COLLECTION, project);
		return project;
   }
   /**
	* Remove a Project.
	*/
   public ProjectAdapter removeProject( ProjectAdapter project) {

		this.removeAdditionalProject( project);
		return project;
   }
   /**
    * Returns the Project named with the given value.
    */
   public ProjectAdapter projectNamed( String name) {

   	for( Iterator iter = additionalProjects(); iter.hasNext();) {

   		ProjectAdapter project = ( ProjectAdapter)iter.next();

   		if( name.equals( project.getName()))
   			return project;
		}

   	return null;
   }
   /**
	* Updates the Login ExternalConnectionPooling.
	* Enable when External Transaction Controller is defined.
	* When it is managed, depends on the Broker JTA and/or the Database Driver type.
	*/
	public void updateExternalConnectionPooling() {

       this.setExternalConnectionPooling( this.hasJTA() || getLogin().databaseDriverIsDataSource());
   }
   /**
	* Adds the given AdditionalProjects.
	*/
   private ProjectAdapter addAdditionalProjects( ProjectAdapter projectAdapter) {
	   // add config
	   this.getAdditionalProjectsConfigs().add( projectAdapter.getModel());
	   // add adapter
	   this.additionalProjects.add( projectAdapter);
		
	   return projectAdapter;
   }
   /**
	* Removes the given AdditionalProjects.
	*/
   private void removeAdditionalProject( ProjectAdapter projectAdapter) {	
	   // remove config
	   this.getAdditionalProjectsConfigs().remove( projectAdapter.getModel());
	   // notify listeners
	   this.removeItemFromCollection(projectAdapter, additionalProjects, ADDITIONAL_PROJECTS_COLLECTION);
   }
   
   private void removeAllAdditionalProjects() {
	   this.removeItemsFromCollection(CollectionTools.collection(additionalProjects()), additionalProjects, ADDITIONAL_PROJECTS_COLLECTION);
	   this.getAdditionalProjectsConfigs().clear();
   }
   
   public boolean usesAdditionalProjects() {
   	return useAdditionalProjects;
   }
   
   public void setUseAdditionalProjects( boolean useAdditionalProjects) {
   	boolean old = this.usesAdditionalProjects();
   	this.useAdditionalProjects = useAdditionalProjects;
   	this.firePropertyChanged( USE_ADDITIONAL_PROJECTS_COLLECTION, old, useAdditionalProjects);

   	if(! usesAdditionalProjects())
   		removeAllAdditionalProjects();
   }
	/**
	 * Returns usesExternalConnectionPooling.
	 */
	public boolean usesExternalConnectionPooling() {
		
		return this.getLogin().usesExternalConnectionPooling();
	}
	/**
	 * Sets usesExternalConnectionPooling and the config model.
	 */
	public void setExternalConnectionPooling( boolean value) {
		
		boolean old = this.getLogin().usesExternalConnectionPooling();

		this.getLogin().setExternalConnectionPooling( value);
		this.firePropertyChanged( EXTERNAL_CONNECTION_POOLING_PROPERTY, old, value);
	}
	/**
	 * Adds the children of this adapter to the given list.
	 * @param children The list of children
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);

		if( getLogin() != null)
			children.add( getLogin());
	}
	/**
	 * Add any problems from this adapter to the given set.
	 */
	protected void addProblemsTo( List branchProblems) {
		
		super.addProblemsTo(branchProblems);
	
		verifyProblemMappingProject(branchProblems);
		
		verifyProblemPlatformClass( branchProblems);
	}

	private void verifyProblemPlatformClass( List branchProblems) {
		
	    if( !this.login.platformIsRdbms()) return;
		try {
			DatabasePlatformRepository.getDefault().platformForRuntimePlatformClassNamed( this.login.getPlatformClass());
		}
		catch ( IllegalArgumentException e) {
			branchProblems.add( buildProblem( SCProblemsConstants.DATABASE_LOGIN_PLATFORM_CLASS_NAME, displayString(), this.login.getPlatformClass()));
		}
	}
	
	private void verifyProblemMappingProject( List branchProblems) {
	
		if( StringTools.stringIsEmpty( getPrimaryProjectName())) {
			branchProblems.add( buildProblem( SCProblemsConstants.SESSION_DATABASE_MAPPING_PROJECT, displayString()));
		}
	}

}
