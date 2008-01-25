/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SequencingConfig
 * 
 * @see SequencingConfig
 * 
 * @author Tran Le
 */
class SequencingAdapter extends SCAdapter {

	public static final String DEFAULT_SEQUENCE_NAME = "Default";
	public static final String NATIVE_SEQUENCE_NAME = "Native";
	public static final String CUSTOM_SEQUENCE_NAME = "Custom";

	private Collection sequences;
	private volatile SequenceAdapter defaultSequence;
	
	/**
	 * Creates a new Sequencing for the specified model object.
	 */
	SequencingAdapter( SCAdapter parent, SequencingConfig scConfig) {
		super( parent, scConfig);
	}
	/**
	 * Creates a new Sequencing.
	 */
	protected SequencingAdapter( SCAdapter parent) {
		super( parent);
	}
	/**
	 * Subclasses should override this method to add their children
	 * to the specified collection.
	 * @see #children()
	 */
	protected void addChildrenTo( List children) {
		super.addChildrenTo( children);

		synchronized (this.sequences) { children.addAll( this.sequences); }

		if( this.defaultSequence != null) 
			children.add( this.defaultSequence);
	
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SequencingConfig();
	}
	/**
	 * Returns the adapter's Config Model Object.
	 */
	private final SequencingConfig config() {
			
		return ( SequencingConfig)this.getModel();
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	int getDefaultSequencePreallocationSize() {
		
		return this.defaultSequence.getPreallocationSize();
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	void setDefaultSequencePreallocationSize( int value) {

		this.defaultSequence.setPreallocationSize( value);
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	String getTableSequenceTable() {
	    if( this.defaultSequence.isNative()) return null;
	    
		return (( TableSequenceAdapter)this.defaultSequence).getSequenceTable();
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	void setTableSequenceTable( String value) {

		// CR#3962710, this can't be done since while replacing the table name,
		// isCustom() returns false and it should be true
//		if( !this.defaultSequence.isCustom()) throw new IllegalAccessError( this.defaultSequence.toString());

		(( TableSequenceAdapter)this.defaultSequence).setSequenceTable( value);
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	String getTableSequenceNameField() {
	    if( this.defaultSequence.isNative()) return null;

		return (( TableSequenceAdapter)this.defaultSequence).getSequenceNameField();
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	void setTableSequenceNameField( String value) {
		(( TableSequenceAdapter)this.defaultSequence).setSequenceNameField( value);
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	String getTableSequenceCounterField() {
	    if( this.defaultSequence.isNative()) return null;

		return (( TableSequenceAdapter)this.defaultSequence).getSequenceCounterField();
	}
	/**
	 * Facade to provide the interface to DefaultSequence property.
	 */
	void setTableSequenceCounterField( String value) {
	    
		// CR#5548368, this can't be done since while replacing the table name,
		// isCustom() returns false and it should be true
//	    if( !this.defaultSequence.isCustom()) throw new IllegalAccessError( this.defaultSequence.toString());

		(( TableSequenceAdapter)this.defaultSequence).setSequenceCounterField( value);
	}
	
	private boolean defaultSequenceNameEquals( String name) {
		
		return this.defaultSequence != null && this.defaultSequence.getName().equals( name);
	}
	/**
	 * Facade to provide the interface to the Multiple Sequence structure.
	 */
	SequenceAdapter setDefaultTableSequenceTable( String name, int preallocationSize) {
		if( this.defaultSequenceNameEquals( name)) return this.defaultSequence;
		
		this.defaultSequence = new TableSequenceAdapter( this, name, preallocationSize);
		this.config().setDefaultSequenceConfig(( SequenceConfig)this.defaultSequence.getModel());
		
		this.initializeDefaultSequenceDefaults();
		return this.defaultSequence;
	}
	/**
	 * Facade to provide the interface to the Multiple Sequence structure.
	 */
	SequenceAdapter setNativeSequencing( String name, int preallocationSize) {
		if( this.defaultSequenceNameEquals( name)) return this.defaultSequence;
		
		this.defaultSequence = new NativeSequenceAdapter( this, name, preallocationSize);
		this.config().setDefaultSequenceConfig(( SequenceConfig)this.defaultSequence.getModel());
	
		return this.defaultSequence;
	}
	/**
	 * Facade to provide the interface to the Multiple Sequence structure.
	 */
	SequenceAdapter setCustomTableSequence( String name, int preallocationSize) {
		if( this.defaultSequenceNameEquals( name)) return this.defaultSequence;

		this.defaultSequence = new TableSequenceAdapter( this, name, preallocationSize);
		this.config().setDefaultSequenceConfig(( SequenceConfig)this.defaultSequence.getModel());
		
		this.initializeCustomTableSequenceDefaults();	    
		return this.defaultSequence;
	}	
	
	private void initializeDefaultSequenceDefaults() {
		TableSequenceAdapter sequence = ( TableSequenceAdapter)this.defaultSequence;

		sequence.setSequenceTable( XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT);
		sequence.setSequenceCounterField( XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT);
		sequence.setSequenceNameField( XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT);
	}
	
	private void initializeCustomTableSequenceDefaults() {
		TableSequenceAdapter sequence = ( TableSequenceAdapter)this.defaultSequence;

		sequence.setSequenceTable( "MY_" + XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT);
		sequence.setSequenceCounterField( "MY_" + XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT);
		sequence.setSequenceNameField( "MY_" + XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT);
	}
	/**
	 * Remove the default sequence.
	 */
	SequenceAdapter removeDefaultSequence() {
		if( this.defaultSequence == null) return null;
		SequenceAdapter removedSequence = this.defaultSequence;
		
		// remove config
		this.config().setDefaultSequenceConfig( null);
		// remove adapter
		this.defaultSequence = null;
		
		return removedSequence;
	}
	/**
	 * Factory method for adding a native sequence.
	 */
	SequenceAdapter addNativeSequenceNamed( String name, int preallocationSize) {
		
		SequenceAdapter sequence = new NativeSequenceAdapter( this, name, preallocationSize);
		
		return this.addSequence( sequence);
	}
	/**
	 * Remove the sequence with the given name.
	 */
	SequenceAdapter removeSequenceNamed( String name) {
		
		return this.removeSequence( this.sequenceNamed( name));
	}
	/**
	 * Adds the given sequence.
	 */
	private SequenceAdapter addSequence( SequenceAdapter sequenceAdapter) {
		// add config
		this.getSequenceConfigs().add( sequenceAdapter.getModel());
		// add adapter
		this.sequences.add( sequenceAdapter);
		
		return sequenceAdapter;
	}
	/**
	 * Removes the given sequence.
	 */
	private SequenceAdapter removeSequence( SequenceAdapter sequenceAdapter) {	

		// remove config
		this.getSequenceConfigs().remove( sequenceAdapter.getModel());
		// remove adapter
		this.sequences.remove( sequenceAdapter);
		
		return sequenceAdapter;
	}
	/**
	 * Returns the collection of sequences from the config model.
	 */
	private Collection getSequenceConfigs() {
		
		return this.config().getSequenceConfigs();
	}
	
	boolean usesNativeSequencing() {
		
		return  this.defaultSequenceNameEquals( NATIVE_SEQUENCE_NAME);
	}
	/**
	 * Factory method for building a child default SequencingAdapter.
	 */
	protected SequenceAdapter buildDefaultSequence() {

	    SequenceAdapter sequence = new TableSequenceAdapter( this, 
				SequencingAdapter.DEFAULT_SEQUENCE_NAME,  
				XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
		
		this.config().setDefaultSequenceConfig(( SequenceConfig)sequence.getModel());

		return sequence;
}
	
	protected SequenceAdapter getDefaultSequence() {
		
		return this.defaultSequence;
	}
	/**
	 * Initializes this adapter.
	 */
	protected void initialize() {
		super.initialize();
		
		this.sequences = new Vector();

	}
	/**
	 * Initializes this new model inst. var. and aggregates.
	 */
	protected void initialize( Object newConfig) {
		super.initialize( newConfig);
		
		this.defaultSequence = buildDefaultSequence();

		this.config().setSequenceConfigs( new Vector()); 
	}
	
	protected void initializeDefaults() {
		super.initializeDefaults();
		
		setDefaultSequencePreallocationSize( XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
	}
	/**
	 * Initializes this adapter from the specified config model.
	 */
	protected void initializeFromModel( Object scConfig) {
		super.initializeFromModel( scConfig);

		this.defaultSequence = ( SequenceAdapter)this.adapt( config().getDefaultSequenceConfig());

		if( this.defaultSequence == null) {
			this.defaultSequence = buildDefaultSequence();
		}

		//TOREVIEW
		if( getSequenceConfigs() != null) { // && this.getConfigFileVersion() == "9.0.4"
			this.sequences.addAll( this.adaptAll( this.getSequenceConfigs()));
		}
	}
	/**
	 * Returns the appropriate sequence.
	 */
	SequenceAdapter sequenceNamed( String name) {
	
		for( Iterator i = sequences(); i.hasNext();) {
			SequenceAdapter sequence = ( SequenceAdapter) i.next();
			if( name.equals( sequence.getName()))
				return sequence;
		}	
		return null;	
	}
	/**
	 * Returns an iterator on a collection of sequences adapters.
	 */
	Iterator sequences() {
		
		return new CloneIterator(this.sequences);
	}
	
	int sequencesSize() {
		
		return this.sequences.size();
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	boolean isDefault() {
		
		return this.defaultSequence.isDefault();
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	boolean isNative() {
			
		return this.defaultSequence.isNative();
	}	
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	boolean isCustom() {
		
		return this.defaultSequence.isCustom();
	}
}
