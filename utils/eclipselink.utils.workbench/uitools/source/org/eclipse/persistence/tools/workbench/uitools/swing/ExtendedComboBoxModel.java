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
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.util.List;
import javax.swing.ComboBoxModel;

/**
 * This wrapper extends a ComboBoxModel with fixed lists of items on either end.
 * 
 * NB: Be careful using or wrapping this model, since the "extended" 
 * items may be unexpected by the client code or wrapper.
 */
public class ExtendedComboBoxModel 
	extends ExtendedListModel 
	implements CachingComboBoxModel
{

	// ********** constructors **********

	/**
	 * Extend the specified combo box model with a prefix and suffix.
	 */
	public ExtendedComboBoxModel(List prefix, ComboBoxModel comboBoxModel, List suffix) {
		this(prefix, new NonCachingComboBoxModel(comboBoxModel), suffix);
	}

	/**
	 * Extend the specified combo box model with a prefix and suffix.
	 */
	public ExtendedComboBoxModel(List prefix, CachingComboBoxModel comboBoxModel, List suffix) {
		super(prefix, comboBoxModel, suffix);
	}

	/**
	 * Extend the specified combo box model with a singleton prefix and suffix.
	 */
	public ExtendedComboBoxModel(Object prefix, ComboBoxModel comboBoxModel, Object suffix) {
		this(prefix, new NonCachingComboBoxModel(comboBoxModel), suffix);
	}

	/**
	 * Extend the specified combo box model with a singleton prefix and suffix.
	 */
	public ExtendedComboBoxModel(Object prefix, CachingComboBoxModel comboBoxModel, Object suffix) {
		super(prefix, comboBoxModel, suffix);
	}

	/**
	 * Extend the specified combo box model with a prefix.
	 */
	public ExtendedComboBoxModel(List prefix, ComboBoxModel comboBoxModel) {
		this(prefix, new NonCachingComboBoxModel(comboBoxModel));
	}

	/**
	 * Extend the specified combo box model with a prefix.
	 */
	public ExtendedComboBoxModel(List prefix, CachingComboBoxModel comboBoxModel) {
		super(prefix, comboBoxModel);
	}


	/**
	 * Extend the specified combo box model with a singleton prefix.
	 */
	public ExtendedComboBoxModel(Object prefix, ComboBoxModel comboBoxModel) {
		this(prefix,  new NonCachingComboBoxModel(comboBoxModel));
	}

	/**
	 * Extend the specified combo box model with a singleton prefix.
	 */
	public ExtendedComboBoxModel(Object prefix, CachingComboBoxModel comboBoxModel) {
		super(prefix, comboBoxModel);
	}

	/**
	 * Extend the specified combo box model with a suffix.
	 */
	public ExtendedComboBoxModel(ComboBoxModel comboBoxModel, List suffix) {
		this(new NonCachingComboBoxModel(comboBoxModel), suffix);
	}

	/**
	 * Extend the specified combo box model with a suffix.
	 */
	public ExtendedComboBoxModel(CachingComboBoxModel comboBoxModel, List suffix) {
		super(comboBoxModel, suffix);
	}

	/**
	 * Extend the specified combo box with a singleton suffix.
	 */
	public ExtendedComboBoxModel(ComboBoxModel comboBoxModel, Object suffix) {
		this(new NonCachingComboBoxModel(comboBoxModel), suffix);
	}

	/**
	 * Extend the specified combo box with a singleton suffix.
	 */
	public ExtendedComboBoxModel(CachingComboBoxModel comboBoxModel, Object suffix) {
		super(comboBoxModel, suffix);
	}

	/**
	 * Extend the specified combo box model with a prefix containing a single null item.
	 */
	public ExtendedComboBoxModel(ComboBoxModel comboBoxModel) {
		this(new NonCachingComboBoxModel(comboBoxModel));
	}

	/**
	 * Extend the specified combo box model with a prefix containing a single null item.
	 */
	public ExtendedComboBoxModel(CachingComboBoxModel comboBoxModel) {
		super(comboBoxModel);
	}


	// ********** ComboBoxModel implementation **********

	public void setSelectedItem(Object anItem) {
		this.comboBoxModel().setSelectedItem(anItem);
	}

	public Object getSelectedItem() {
		return this.comboBoxModel().getSelectedItem();
	}


	// ********** CachingComboBoxModel implementation **********

	/**
	 * @see CachingComboBoxModel#cacheList()
	 */
	public void cacheList() {
		this.cachingComboBoxModel().cacheList();
	}

	/**
	 * @see CachingComboBoxModel#uncacheList()
	 */
	public void uncacheList() {
		this.cachingComboBoxModel().uncacheList();
	}

	/**
	 * @see CachingComboBoxModel#isCached()
	 */
	public boolean isCached() {
		return this.cachingComboBoxModel().isCached();
	}


	// ********** convenience methods **********

	protected ComboBoxModel comboBoxModel() {
		return (ComboBoxModel) this.listModel;
	}

	protected CachingComboBoxModel cachingComboBoxModel() {
		return (CachingComboBoxModel) this.listModel;
	}

}
