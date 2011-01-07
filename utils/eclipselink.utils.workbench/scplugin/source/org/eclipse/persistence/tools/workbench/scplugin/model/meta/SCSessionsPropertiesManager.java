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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.scplugin.SCPlugin;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * This manager takes care to handle {@link SCSessionsProperties}, which are
 * objects containing design information that are not persisted into the
 * sessions.xml. A copy is always returned in order to not persist unsaved
 * properties. When it is time to save the extra information for a newly saved
 * sessions.xml, {@link #update(SCSessionsProperties)} needs to be called.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public final class SCSessionsPropertiesManager extends AbstractModel
{
	/**
	 * Holds onto the preferences to retrieve the default Sessions Configuration
	 * name (eg: sessions).
	 */
	private final Preferences preferences;

	/**
	 * The <code>Collection</code> of {@link SCSessionsPropertiesManager}.
	 */
	private Collection sessionsProperties;

	/**
	 * The list of unsaved file names.
	 */
	private Collection unsavedSessionsFileNames;

	/**
	 * The file name to be used for untitled sessions.xml, which is "sessions".
	 */
	public static final String UNTITLED_FILE_NAME = "sessions";

	/**
	 * Creates a new <code>SCSessionsPropertiesManager</code>.
	 *
	 * @param preferences The parent node where the classpath node is located
	 */
	public SCSessionsPropertiesManager(Preferences preferences)
	{
		super();
		this.preferences = preferences.node("classpath");
		read();
	}

	/**
	 * Creates a new <code>SCSessionsProperties</code> that will contains the
	 * class repository information for the sessions.xml located at the given
	 * path.
	 *
	 * @param path The location of the sessions.xml to be created
	 * @param classpath The classpath entries
	 * @return A new {@link SCSessionsProperties}
	 */
	SCSessionsProperties addSessionsProperties(File path,
	                                           String[] classpath,
	                                           int index)
	{
		SCSessionsProperties properties = buildSessionsProperties(path, classpath, index);
		properties.manager = this;
		this.sessionsProperties.add(properties);
		return properties;
	}

	/**
	 * Adds the given <code>SCSessionsProperties</code> to this manager.
	 *
	 * @param properties The <code>SCSessionsProperties</code> to be added
	 */
	void addSessionsProperties(SCSessionsProperties properties)
	{
		if (!this.sessionsProperties.contains(properties))
		{
			this.sessionsProperties.add(properties);
		}
	}

	/**
	 * Creates a new <code>SCSessionsProperties</code> that will contains the
	 * class repository information for the sessions.xml located at the given
	 * path.
	 *
	 * @param path The location of the sessions.xml to be created
	 * @param classpath The classpath entries
	 * @return A new {@link SCSessionsProperties}
	 */
	private SCSessionsProperties buildSessionsProperties(File path,
																		  String[] classpath,
																		  int index)
	{
		return new SCSessionsProperties(this, path, classpath, index);
	}

	/**
	 * Retrieves the <code>SCSessionsProperties</code> that contains the class
	 * repository information for the sessions.xml located at the given path.
	 *
	 * @param path The path of the sessions.xml to retrieve its class repository
	 * information
	 * @return A copy of the original <code>SCSessionsProperties</code> that will
	 * be required to be saved through {@link #update(SCSessionsProperties)} when
	 * the file will be saved
	 */
	public SCSessionsProperties getSessionsProperties(File path)
	{
		for (Iterator iter = sessionsProperties(); iter.hasNext(); )
		{
			SCSessionsProperties properties = (SCSessionsProperties) iter.next();

			if (properties.getPath().equals(path))
			{
				return properties;
			}
		}

		// Keep a history of the file name, only for untitled file
		if (path.getPath().startsWith(untitledFileName()))
		{
			this.unsavedSessionsFileNames.add(path);
		}

		// This one will not be encapsulated until update() is called for it
		return buildSessionsProperties(path, new String[0], -1);
	}

	/**
	 * Initializes this <code>SCSessionsPropertiesManager</code>.
	 */
	protected void initialize()
	{
		super.initialize();

		this.sessionsProperties = new Vector();
		this.unsavedSessionsFileNames = new Vector();
	}

	/**
	 * Returns the next untitled file name for a sessions.xml, the name will be
	 * "Sessions1.xml", then "Sessions2.xml" and so on.
	 *
	 * @return A non-fully qualified file name with this format SesssionX.xml
	 * where X is the next number of untitled sessions.xml that was created
	 */
	public File nextUntitledSessionsFile()
	{
		String defaultUntitledFileName = untitledFileName();
		boolean firstUntitledUsed = false;
		int index = 0;

		for (Iterator iter = unsavedSessionsFileNames(); iter.hasNext(); )
		{
			File file = (File) iter.next();
			String fileName = FileTools.stripExtension(file.getPath());

			if (fileName.startsWith(defaultUntitledFileName))
			{
				String remaining = fileName.substring(defaultUntitledFileName.length(), fileName.length());

				try
				{
					if (remaining.length() == 0)
					{
						firstUntitledUsed = true;
						index++;
					}
					else
					{
						index = Math.max(new Integer(remaining).intValue(), index);
					}
				}
				catch (Exception e)
				{
					// Valid exception, what is after sessions is not a number
				}
			}
		}

		StringBuffer sb = new StringBuffer(13);
		sb.append(defaultUntitledFileName);

		if (firstUntitledUsed) // sessions0.xml is used as sessions.xml
			sb.append(++index); // ++index to get the next available index

		sb.append(".xml");

		return new File(sb.toString());
	}

	/**
	 * Changes the location of the sessions.xml of the original <code>SCSessionsProperties</code>.
	 *
	 * @param properties The copy where the path has been changed
	 */
	public void pathChanged(final SCSessionsProperties properties)
	{
		SCSessionsProperties original = null;
		File path = properties.getPath();

		for (Iterator iter = sessionsProperties(); iter.hasNext(); )
		{
			SCSessionsProperties nextProperties = (SCSessionsProperties) iter.next();

			if ((properties != nextProperties) &&
				 nextProperties.getPath().equals(path))
			{
				original = nextProperties;
				break;
			}
		}

		// The new original will be recreated on save
		if (original != null)
		{
			this.sessionsProperties.add(properties);
			this.sessionsProperties.remove(original);
		}
	}

	/**
	 * Reads the properties for all sessions.xml files that were edited in the
	 * Mapping Workbench.
	 *
	 * @return The root object containing all the information about the classpath
	 * for any sessions.xml that was edited
	 */
	private void read()
	{
		// Attempt to load the manager
		try
		{
			String[] keys = this.preferences.keys();

			for (int index = 0; index < keys.length; index++)
			{
				String key = keys[index];

				// 10.1.3.x format
				// location_X  = path
				// classpath_x = classpath
				if (key.startsWith(SCSessionsProperties.LOCATION_TAG))
				{
					int underscoreIndex = key.indexOf("_");
					int entryIndex = Integer.valueOf(key.substring(underscoreIndex + 1)).intValue();

					String fileName  = this.preferences.get(key, null);
					String classpath = this.preferences.get(SCSessionsProperties.CLASSPATH_TAG + entryIndex, null);

					if ((fileName.length() > 0) && !StringTools.stringIsEmpty(classpath))
					{
						String[] classpathEntries = classpath.split(File.pathSeparator);
						addSessionsProperties(new File(fileName), classpathEntries, entryIndex);
					}
				}
				// 10.1.3.0.0 format
				// path = classpath
				else if (!key.startsWith(SCSessionsProperties.CLASSPATH_TAG))
				{
					String classpath = this.preferences.get(key, null);

					if ((key.length() > 0) &&
						 !StringTools.stringIsEmpty(classpath))
					{
						String[] classpathEntries = classpath.split(File.pathSeparator);
						SCSessionsProperties properties = addSessionsProperties(new File(key), classpathEntries, -1);

						// Convert to the new format
						properties.save(this.preferences.parent());

						// Remove the old format
						this.preferences.remove(key);
					}
				}
			}
		}
		catch (Exception e)
		{
			// Ignore and simply have no classpath information
		}
	}

	/**
	 * Returns an <code>Iterator</code> over the <code>SCSessionsProperties</code>
	 * that were loaded.
	 *
	 * @return The iterator of <code>SCSessionsProperties</code>
	 */
	private Iterator sessionsProperties()
	{
		return this.sessionsProperties.iterator();
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
		buffer.append(", sessions=");
		buffer.append(this.sessionsProperties);
	}

	/**
	 * Returns an <code>Iterator</code> over the file name of untitled
	 * sessions.xml that were created.
	 *
	 * @return The iterator over a collection of <code>File</code>s
	 */
	private Iterator unsavedSessionsFileNames()
	{
		return this.unsavedSessionsFileNames.iterator();
	}

	/**
	 * Retrieves the default name to be used for the Sessions Configuration file
	 *
	 * @return The default name to be used for the Sessions Configuration file or
	 * {@link #UNTITLED_FILE_NAME} if the preferences does not have any value set
	 */
	private String untitledFileName()
	{
		return this.preferences.parent().get(SCPlugin.NEW_NAME_SESSIONS_CONFIGURATION_PREFERENCE,
												  UNTITLED_FILE_NAME);
	}
}
