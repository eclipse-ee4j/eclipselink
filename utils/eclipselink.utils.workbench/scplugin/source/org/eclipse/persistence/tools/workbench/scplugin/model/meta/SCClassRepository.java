/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


/**
 * This object stores the classpath entries for a sessions.xml.
 * 
 * @version 10.1.3
 * @author Pascal Filion
 */
public class SCClassRepository extends AbstractNodeModel
										 implements ClassRepository
{
	/**
	 * The list of classpath entries that is basically the sessions.xml's
	 * classpath.
	 */
	private List classpathEntries;

	/**
	 * Creates a new <code>SCClassRepository</code>.
	 *
	 * @param classpath The entries of the classpath
	 */
	public SCClassRepository(String[] classpathEntries)
	{
		super();
		initialize(classpathEntries);
	}

	/**
	 * Adds the given list of entries at the specified index.
	 * 
	 * @param index The index of insertion
	 * @param entries The entries to be added
	 */
	public void addClasspathEntries(int index, List entries)
	{
		addItemsToList(index, entries, classpathEntries, CLASSPATH_ENTRIES_LIST);
		markEntireBranchDirty();
	}

	/**
	 * Adds the given entry at the specified index.
	 * 
	 * @param index The index of insertion
	 * @param entry The entry to be added
	 */
	public void addClasspathEntry(int index, String entry)
	{
		addItemToList(index, entry, classpathEntries, CLASSPATH_ENTRIES_LIST);
		markEntireBranchDirty();
	}

	/**
	 * Returns the <code>Iterator</code> over the copy of the list of entries.
	 * 
	 * @return An iteration over the entries
	 */
	public ListIterator classpathEntries()
	{
		return new ReadOnlyListIterator(classpathEntries);
	}

	/**
	 * Returns the count of classpath entries.
	 * 
	 * @return The count of entries
	 */
	public int classpathEntriesSize()
	{
		return classpathEntries.size();
	}

	/**
	 * Returns a string representation of the model, suitable for sorting.
	 *
	 * @return Empty string
	 */
	public String displayString()
	{
		return "";
	}

	/**
	 * Returns an string representation of the classpath entries. The entries are
	 * separator with a separator.
	 *
	 * @return A string with all the classpath entries separated by the path
	 * separator retrieved from the System, eg: ';'
	 */
	public String entries()
	{
		StringBuffer entries = new StringBuffer();
		String pathSeparator = System.getProperty("path.separator");

		for (Iterator iter = classpathEntries(); iter.hasNext();)
		{
			String entry = (String)iter.next();
			if (!"".equals(entry)) {
				entries.append(entry);

				if (iter.hasNext())
					entries.append(pathSeparator);
			}
		}

		return entries.toString();
	}

	/**
	 * Returns the entry location at the specified index.
	 * 
	 * @param index The index of the entry to retrieve
	 * @return The desired entry
	 */
	public String getClasspathEntry(int index)
	{
		return (String) classpathEntries.get(index);
	}

	/**
	 * Notifies this object it has been build.
	 */
	protected void initialize()
	{
		super.initialize();

		classpathEntries = new Vector();
		markEntireBranchClean();
	}

	/**
	 * Initializes this <code>SCClassRepository</code>.
	 *
	 * @param entries The classpath entries
	 */
	private void initialize(String[] entries)
	{
		classpathEntries = new Vector(entries.length);
		CollectionTools.addAll(classpathEntries, entries);
	}

	/**
	 * Returns an <code>Iterator</code> on the collection of "class descriptions"
	 * currently in the repository.
	 * 
	 * @return An iteration of core classes and classes on the sessions.xml's
	 * classpath
	 */
	public Iterator classDescriptions()
	{
		return new CompositeIterator(coreClassNames(), types());
	}

	/**
	 * Refreshes the collection of "meta-classes" in the repository.
	 */
	public void refreshClassDescriptions()
	{
	}

	/**
	 * Removes the entry positioned at the given index.
	 * 
	 * @param index The position of the entry to be removed
	 * @return The entry that was removed
	 */
	public String removeClasspathEntry(int index)
	{
		markEntireBranchDirty();
		return (String) removeItemFromList(index, classpathEntries, CLASSPATH_ENTRIES_LIST);
	}

	/**
	 * Replaces the entry at the given index with a new entry.
	 * 
	 * @param index The index of the entry to be replaced
	 * @param newEntry The new entry to replace the old one
	 * @return The old entry
	 */
	public String replaceClasspathEntry(int index, String newEntry)
	{
		markEntireBranchDirty();
		return (String) setItemInList(index, newEntry, classpathEntries, CLASSPATH_ENTRIES_LIST);
	}

	/**
	 * Determines whether this <code>SCSessionsProperties</code> should be
	 * persisted or not. If no information should be persisted, that means there
	 * is no lost of data.
	 *
	 * @return <code>true<code> if there is entries; <code>false<code> otherwise
	 */
	boolean shouldBeSaved()
	{
		return classpathEntriesSize() > 0;
	}

	/**
	 * Appends more information about this <code>SCSessionsProperties</code> to
	 * the given buffer.
	 *
	 * @param buffer The buffer used to add extra information
	 */
	public void toString(StringBuffer buffer)
	{
		buffer.append("classpathEntries=");
		buffer.append(classpathEntries);
	}

	/**
	 * Returns an <code>Iterator</code> over the types that are on the
	 * sessions.xml's classpath.
	 *
	 * @return An iteration over the types contained on the sessions.xml's
	 * classpath entries
	 */
	public Iterator types()
	{
		Classpath cp = new Classpath(classpathEntries);
		return cp.classNamesStream();
	}

	/**
	 * Updates this <code>SCClassRepository</code> with the information contained
	 * in the given repository.
	 *
	 * @param repository The copy used to reflect its data into this one
	 */
	void update(ClassRepository repository)
	{
		removeItemsFromList(0, classpathEntriesSize(), classpathEntries, CLASSPATH_ENTRIES_LIST);
		addItemsToList(0, CollectionTools.list(repository.classpathEntries()), classpathEntries, CLASSPATH_ENTRIES_LIST);
		markEntireBranchClean();
	}

	
	// static stuff

	/** these are the keys use to find the classpath entries for the "core" classes */
	private static final Class[] CORE_KEYS =
		new Class[]
		{
			java.lang.Object.class,		// rt.jar
//			javax.ejb.EnterpriseBean.class,		// javax.ejb
			org.eclipse.persistence.indirection.ValueHolderInterface.class		// toplink.jar
		};

	/** the "core" classes: the MWClasses that are never written out */
	private static Set coreClassNames;

	private static Iterator coreClassNames()
	{
		if (coreClassNames == null)
		{
			coreClassNames = buildCoreClassNames();
		}
		return coreClassNames.iterator();
	}

	// this does NOT include void, boolean, int, float, etc.
	private static Set buildCoreClassNames()
	{
		Set result = new HashSet(10000);
	
		List locations = new ArrayList();
		for (int i = 0; i < CORE_KEYS.length; i++)
		{
			locations.add(Classpath.locationFor(CORE_KEYS[i]));
		}
		Classpath cp = new Classpath(locations);
		cp.addClassNamesTo(result);
	
		return result;
	}

}
