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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequenceConfig;
import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequencingConfig;
import org.eclipse.persistence.tools.workbench.scplugin.model.SequenceType;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SequencingConfig
 * 
 * @see SequencingConfig
 * 
 * @author Tran Le
 */
public class SequencingAdapter extends SCAdapter {

	public static final String DEFAULT_SEQUENCE_NAME = "Default";
	public static final String NATIVE_SEQUENCE_NAME = "Native";
	public static final String CUSTOM_SEQUENCE_NAME = "Custom";

	private List<SequenceAdapter> sequences;
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
	
	private void initializeDefaultSequenceDefaults() {
		TableSequenceAdapter sequence = ( TableSequenceAdapter)this.defaultSequence;

		sequence.setSequenceTable( XMLSessionConfigProject.SEQUENCE_TABLE_DEFAULT);
		sequence.setSequenceCounterField( XMLSessionConfigProject.SEQUENCE_COUNTER_FIELD_DEFAULT);
		sequence.setSequenceNameField( XMLSessionConfigProject.SEQUENCE_NAME_FIELD_DEFAULT);
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
		this.getSequenceConfigs().add((SequenceConfig) sequenceAdapter.getModel());
		// add adapter
		this.sequences.add( sequenceAdapter);
		
		return sequenceAdapter;
	}
	
	public SequenceAdapter addSequence(String name, SequenceType sequenceType)
	{
		SequenceAdapter sequence = buildSequence(name, sequenceType);
		return addSequence(sequence);
	}

	private SequenceAdapter buildSequence(String name, SequenceType sequenceType)
	{
		switch (sequenceType)
		{
			case DEFAULT:     return new DefaultSequenceAdapter(this, name, 50);
			case NATIVE:      return new NativeSequenceAdapter(this, name, 50);
			case TABLE:       return new TableSequenceAdapter(this, name, 50);
			case UNARY_TABLE: return new UnaryTableSequenceAdapter(this, name, 50);
			default:          return null;
		}
	}

	/**
	 * Removes the given sequence.
	 */
	public SequenceAdapter removeSequence( SequenceAdapter sequenceAdapter) {	

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
	
	public SequenceAdapter getDefaultSequence() {		
		return this.defaultSequence;
	}
	
	public void setDefaultSequence(SequenceAdapter adapter) {
		this.defaultSequence.setTheDefaultSequence(false);
		this.defaultSequence = adapter;
		adapter.setTheDefaultSequence(true);
		this.config().setDefaultSequenceConfig(( SequenceConfig)this.defaultSequence.getModel());
	}
	
	public SequenceAdapter createAndSetDefaultSequence(String name, SequenceType type) {
		SequenceAdapter adapter = this.buildSequence(name, type);
		this.setDefaultSequence(adapter);
		return adapter;
	}

	/**
	 * Factory method for building a child default SequencingAdapter.
	 */
	protected SequenceAdapter buildDefaultSequence() {

	    SequenceAdapter sequence = new TableSequenceAdapter( this, 
				SequencingAdapter.DEFAULT_SEQUENCE_NAME,  
				XMLSessionConfigProject.SEQUENCE_PREALLOCATION_SIZE_DEFAULT);
		sequence.setTheDefaultSequence(true);		
		this.config().setDefaultSequenceConfig(( SequenceConfig)sequence.getModel());

		return sequence;
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
		} else {
			this.defaultSequence.setTheDefaultSequence(true);
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
	public ListIterator<SequenceAdapter> sequences() {
		
		return new CloneListIterator(this.sequences);
	}
	
	public Iterator<String> sequenceNames() {
		Iterator<SequenceAdapter> sequencesIter = (Iterator<SequenceAdapter>)sequences();
		Collection<String> names = new ArrayList<String>(sequencesSize());
		while(sequencesIter.hasNext()) {
			names.add(sequencesIter.next().getName());
		}
		return names.iterator();
	}
	
	public int sequencesSize() {
		
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
