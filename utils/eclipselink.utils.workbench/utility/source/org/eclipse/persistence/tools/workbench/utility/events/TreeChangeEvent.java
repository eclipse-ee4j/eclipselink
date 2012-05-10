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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventObject;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * A "TreeChange" event gets delivered whenever a model changes a "bound"
 * or "constrained" tree. A TreeChangeEvent object is sent as an
 * argument to the TreeChangeListener.
 * 
 * Normally a TreeChangeEvent is accompanied by the tree name and a path
 * to the part of the tree that was changed.
 */
public class TreeChangeEvent extends EventObject {

	/** Name of the tree that changed. May be null, if not known. */
	private String treeName;

    /**
     * Path to the parent of the part of the tree that was changed.
     * May be null, if not known or if the entire tree changed.
     */
	protected Object[] path;

	private static final long serialVersionUID = 1L;


	/**
	 * Construct a new TreeChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 */
	public TreeChangeEvent(Object source) {
		super(source);
	}
	
	/**
	 * Construct a new TreeChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param treeName The programmatic name of the tree that was changed.
	 */
	public TreeChangeEvent(Object source, String treeName) {
		this(source);
		this.treeName = treeName;
	}
	
	/**
	 * Construct a new TreeChangeEvent.
	 *
	 * @param source The object on which the Event initially occurred.
	 * @param treeName The programmatic name of the tree that was changed.
	 * @param path The path to the part of the tree that was changed.
	 */
	public TreeChangeEvent(Object source, String treeName, Object[] path) {
		this(source, treeName);
		this.path = path;
	}
	
	/**
	 * Return the programmatic name of the tree that was changed.
	 */
	public String getTreeName() {
		return this.treeName;
	}

	/**
	 * Return the path to the part of the tree that was changed.
	 * May be null, if not known.
	 */
	public Object[] getPath() {
		return this.path;
	}

	public String toString() {
		return StringTools.buildToStringFor(this, this.treeName);
	}

}
