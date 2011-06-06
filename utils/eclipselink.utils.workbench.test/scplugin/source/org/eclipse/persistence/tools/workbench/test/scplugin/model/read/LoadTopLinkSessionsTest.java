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
package org.eclipse.persistence.tools.workbench.test.scplugin.model.read;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.scplugin.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;


public class LoadTopLinkSessionsTest extends TestCase
{
	public LoadTopLinkSessionsTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(LoadTopLinkSessionsTest.class, "TopLink sessions.xml Tests");
	}

	private File prepareSessionsXmlFile(ZipFile zipFile, ZipEntry entry) throws Exception
	{
		// Create the Channel from the ZipEntry
		InputStream inputStream = zipFile.getInputStream(entry);
		ReadableByteChannel sourceChannel = Channels.newChannel(inputStream);

		// Create the Channel for the destination
		String fileName = entry.getName().replace('/', '_');
		File file = File.createTempFile(fileName, null);
		FileOutputStream fos = new FileOutputStream(file);
		FileChannel destinationChannel = fos.getChannel();

		// Copy the information
		try
		{
			destinationChannel.transferFrom(sourceChannel, 0, entry.getSize());
		}
		finally
		{
			sourceChannel.close();
			destinationChannel.close();
		}

		return file;
	}

	private void readSessionsXml(File file)
	{
		new TopLinkSessionsAdapter(file.getPath(), false);
	}

	private void showResults(Collection exceptions)
	{
		StringBuffer sb = new StringBuffer();

		for (Iterator iter = exceptions.iterator(); iter.hasNext();)
		{
			Problem problem = (Problem) iter.next();

			sb.append("Could not be loaded: ");
			sb.append(problem.fileName);
			sb.append("\n");
			sb.append(problem.exception.toString());

			if (iter.hasNext())
				sb.append("\n\n\n");
		}

//		fail(sb.toString());
	}

	public void testLoadingTopLinkSessions() throws Exception
	{
		URL url = getClass().getResource("/toplink.sessions.xml.zip");
		assertTrue("File missing: toplink.sessions.xml.zip", url != null);

		File location = FileTools.buildFile(url);
		ZipFile zipFile = new ZipFile(location);

		Vector exceptions = new Vector();
		int count = 0;

		for (Enumeration enumeration = zipFile.entries(); enumeration.hasMoreElements();)
		{
			ZipEntry entry = (ZipEntry) enumeration.nextElement();

			if (!entry.isDirectory())
			{
				File file = null;

				// Copy the content from the zip entry into a "unzipped" file
				try
				{
					file = prepareSessionsXmlFile(zipFile, entry);
					readSessionsXml(file);
					count++;
				}
				catch (Throwable e)
				{
					exceptions.add(new Problem(file, e));
				}

				// Clean after
				if (file != null)
				{
					file.delete();
				}
			}
		}

		if (exceptions.isEmpty())
		{
			assertEquals("The number of file read is not accurate.", count, 70);
		}
		else
		{
			showResults(exceptions);
		}
	}

	private class Problem
	{
		Throwable exception;
		File fileName;

		Problem(File fileName, Throwable exception)
		{
			super();
			this.exception = exception;
			this.fileName = fileName;
		}
	}
}
