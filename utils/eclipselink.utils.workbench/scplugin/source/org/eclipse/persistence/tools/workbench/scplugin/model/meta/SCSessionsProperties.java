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
package org.eclipse.persistence.tools.workbench.scplugin.model.meta;

import java.io.File;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

// Mapping Workbench

/**
 * This <code>SCSessionsProperties</code> contains the extra information about a
 * sessions.xml. So far the design information that is not persisted directly
 * into the runtime file is its class repository information.
 *
 * @see SCClassRepository
 *
 * @version 10.1.3.0.1
 * @since 10.1.3
 * @author Pascal Filion
 */
public final class SCSessionsProperties extends AbstractModel
{
	/**
	 * This {@link SCClassRepository} contains the classpath entries.
	 */
	private volatile SCClassRepository classRepository;

	/**
	 * This index is used to create the proper keys, those keys are
	 * location_<index> and classpath_<index>.
	 */
	private int index;

	/**
	 * The manager of <code>SCSessionsProperties</code>.
	 */
	volatile SCSessionsPropertiesManager manager;

	/**
	 * The fully qualified path of the sessions.xml, eg: /C:/sessions.xml.
	 */
	private volatile File path;

	/**
	 * The fully qualified path of the sessions.xml retrieved from the persisted
	 * information.
	 */
	private volatile String pathString;

	/**
	 * The name of the classpath node in the preferences.
	 */
	public static final String CLASSPATH_NODE_TAG = "classpath";

	/**
	 * Part of the key used to retrieve the classpath of the sessions.xml with
	 * the same index stored at <code>LOCATION_TAG</code>.
	 */
	public static final String CLASSPATH_TAG = "classpath_";

	/**
	 * Part of the key used to retrieve the fully qualified name of the
	 * sessions.xml.
	 */
	public static final String LOCATION_TAG = "location_";

	/**
	 * Can't create an empty <code>SCSessionsProperties</code>.
	 */
	private SCSessionsProperties()
	{
		super();
	}

	/**
	 * Creates a new <code>SCSessionsProperties</code> for the given sessions.xml.
	 * 
	 * @param manager The manager of <code>SCSessionsProperties</code>
	 * @param path The fully qualified path of the sessions.xml, eg:
	 * C:/MyDirectory/sessions.xml
	 * @exception NullPointerException The fully qualified path of the
	 * sessions.xml cannot be <code>null</code>
	 */
	SCSessionsProperties(SCSessionsPropertiesManager manager,
								File path,
								String[] classpath,
								int index)
	{
		super();
		this.manager = manager;
		initialize(path, classpath, index);
	}

	/**
	 * Creates a new <code>ClassRepository</code>.
	 *
	 * @return {@link SCClassRepository}
	 */
	private SCClassRepository buildClassRepository(String[] classpath)
	{
		return new SCClassRepository(classpath);
	}

	/**
	 * Finds the next available index in the preferences that will be used to
	 * save the location and its classpath.
	 */
	private int findNextAvailableIndex(Preferences preferences)
	{
		int index = 0;
		String newLocation = LOCATION_TAG + index;

		try
		{
			String[] names = preferences.keys();

			while (CollectionTools.contains(names, newLocation))
			{
				newLocation = LOCATION_TAG + ++index;
			}
		}
		catch (Exception e)
		{
			// TODO: How to handle it
		}

		return index;
	}

	/**
	 * Returns the <code>ClassRepository</code> that should be used by the
	 * sessions.xml.
	 *
	 * @return The <code>ClassRepository</code>
	 */
	public ClassRepository getClassRepository()
	{
		return this.classRepository;
	}

	public int getIndex()
	{
		return index;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public File getPath()
	{
		return this.path;
	}

	public String getPathString()
	{
		return pathString;
	}

	/**
	 * Initializes this <code>SCSessionsProperties</code>.
	 *
	 * @param path The fully qualified path of the sessions.xml, eg:
	 * C:/MyDirectory/sessions.xml
	 * @exception NullPointerException The fully qualified path of the
	 * sessions.xml cannot be <code>null</code>
	 */
	private void initialize(File path, String[] classpath, int index)
	{
		if (path == null)
			throw new NullPointerException("The fully qualified path of the sessions.xml cannot be null");

		pathChanged(path);
		this.classRepository = buildClassRepository(classpath);
		this.index = index;
	}

	/**
	 * Changes the location of the sessions.xml.
	 *
	 * @param path The new location
	 */
	public void pathChanged(File path)
	{
		this.path = path;

		// Keep a string version of the path, which is persisted and make sure it
		// is platform independant
		this.pathString = path.getPath();
		this.pathString = this.pathString.replace('\\', '/');

		// Also make sure the original copy has its path changed
		this.manager.pathChanged(this);
	}

	/**
	 * Saves the information contains in this properties.
	 *
	 * @param preferences The preferences where the classpath entries will be
	 * persisted
	 */
	public void save(Preferences preferences)
	{
		preferences = preferences.node(CLASSPATH_NODE_TAG);

		if (this.shouldBeSaved())
		{
			// Find the next available index
			if (this.index == -1)
			{
				this.index = findNextAvailableIndex(preferences);
			}

			preferences.put(LOCATION_TAG  + this.index, this.pathString);
			preferences.put(CLASSPATH_TAG + this.index, this.classRepository.entries());
		}
		else
		{
			preferences.remove(LOCATION_TAG  + this.index);
			preferences.remove(CLASSPATH_TAG + this.index);

			// This will force to retrieve a new index next time the classpath
			// stored in this properties needs to be saved
			this.index = -1;
		}

		this.manager.addSessionsProperties(this);
	}

	/**
	 * Saves the information contains in this properties.
	 *
	 * @param preferences The preferences where the classpath entries will be
	 * persisted
	 */
	public void saveAs(Preferences preferences, File path)
	{
		// Update the new path
		pathChanged(path);

		// Persist the information
		save(preferences);
	}

	/**
	 * Determines whether this <code>SCSessionsProperties</code> should be
	 * persisted or not. If no information should be persisted, that means there
	 * is no lost of data.
	 *
	 * @return <code>true<code> if the information contained here needs to be
	 * persisted; <code>false<code> otherwise
	 */
	boolean shouldBeSaved()
	{
		return this.classRepository.shouldBeSaved();
	}

	/**
	 * Returns a String representation of this <code>SCSessionsProperties</code>.
	 *
	 * @return The short description of this class and its values
	 */
	public final String toString()
	{
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		toString(sb);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Appends more information about this <code>SCSessionsProperties</code> to
	 * the given buffer.
	 *
	 * @param buffer The buffer used to add extra information
	 */
	public void toString(StringBuffer buffer)
	{
		buffer.append("path=");
		buffer.append(this.path);

		buffer.append(", classRepository=");
		buffer.append(this.classRepository);
	}
}
