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
package org.eclipse.persistence.tools.workbench.scplugin.model.meta;

// JDK
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionRepository;
import org.eclipse.persistence.tools.workbench.utility.node.NodeModel;

// Mapping Workbench

/**
 * A <code>ClassRepository</code> specifies the class meta information for a
 * sessions.xml. Since it is not stored withing the XML file, another file
 * contains that information, which is located at:
 * <code>&lt;ORACHE_HOME&gt;/toplink/config/sc.xml</code>.
 *
 * @see SCSessionsPropertiesIO - Manages reading from and writing to sc.xml
 * @see SCModel - Global access to the repository
 * @see SCPlugin - Manages retrieving the classpath information from sc.xml and
 * loading/creating a {@link TopLinkSessionsAdapter}
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public interface ClassRepository extends ClassDescriptionRepository,
													  NodeModel
{
	/**
	 * Identifies a change in the list of classpath entries.
	 */
	public static final String CLASSPATH_ENTRIES_LIST = "classpathEntries";

	/**
	 * Adds the given list of entries at the specified index.
	 * 
	 * @param index The index of insertion
	 * @param entry The entries to be added
	 */
	public void addClasspathEntries(int index, List entries);

	/**
	 * Adds the given entry at the specified index.
	 * 
	 * @param index The index of insertion
	 * @param entry The entry to be added
	 */
	public void addClasspathEntry(int index, String entry);

	/**
	 * Returns the <code>Iterator</code> over the copy of the list of entries.
	 * 
	 * @return An iteration over the entries
	 */
	public ListIterator classpathEntries();

	/**
	 * Returns the count of classpath entries.
	 * 
	 * @return The count of entries
	 */
	public int classpathEntriesSize();

	/**
	 * Returns the entry location at the specified index.
	 * 
	 * @param index The index of the entry to retrieve
	 * @return The desired entry
	 */
	public String getClasspathEntry(int index);

	/**
	 * Removes the entry positioned at the given index.
	 * 
	 * @param index The position of the entry to be removed
	 * @return The entry that was removed
	 */
	public String removeClasspathEntry(int index);

	/**
	 * Replaces the entry at the given index with a new entry.
	 * 
	 * @param index The index of the entry to be replaced
	 * @param newEntry The new entry to replace the old one
	 * @return The old entry
	 */
	public String replaceClasspathEntry(int index, String newEntry);
}
