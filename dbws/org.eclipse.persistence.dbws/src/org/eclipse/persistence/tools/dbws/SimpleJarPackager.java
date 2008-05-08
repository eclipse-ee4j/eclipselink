/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import static java.util.jar.Attributes.Name.MANIFEST_VERSION;

// EclipseLink imports
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;

/**
 * <p>
 * <b>INTERNAL:</b> SimpleJarPackager implements the {@link DBWSPackager} interface.
 * The output files from the {@link DBWSBuilder} are written to the <tt>stageDir</tt>
 * in a jar file.
 * <p>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle EclipseLink 1.x
 * <pre>
 * <b>stageDir</b>
 * Jar-file layout
 *    \
 *    |   <b>eclipselink-dbws-schema.xsd</b>
 *    |   swaref.xsd                  -- optional if attachements are enabled
 *    \---META-INF
 *        |   <b>eclipselink-dbws-or.xml</b>
 *        |   <b>eclipselink-dbws-ox.xml</b>
 *        |   <b>eclipselink-dbws-sessions.xml</b>
 *        |   <b>eclipselink-dbws.xml</b>
 *
 * </pre>
 */
public class SimpleJarPackager extends SimplePackager {

	static final int BUF_SIZE = 1024;

	static final String DEFAULT_JAR_FILENAME = "aDBWSservice.jar";
	static final String DEFAULT_MANIFEST =
		MANIFEST_VERSION.toString() + ": 1.0\n" +
		"Created-by: DBWSBuilder SimpleJarPackager 1.0\n\n";

	protected String jarFilename = null;
	protected JarOutputStream jarOutputStream = null;

	public SimpleJarPackager() {
		this(DEFAULT_JAR_FILENAME);
	}

	public SimpleJarPackager(String jarFilename) {
		super();
		this.jarFilename = jarFilename;
	}

	@Override
	public void end() {
		JarOutputStream jarOutputStream = null;
		File f = null;
		FileInputStream fis = null;
		byte[] buffer = new byte[BUF_SIZE];
		try {
			byte[] byteArray = DEFAULT_MANIFEST.getBytes("ISO-8859-1");
			Manifest manifest = new Manifest(new ByteArrayInputStream(byteArray));
			jarOutputStream = new JarOutputStream(new FileOutputStream(
				new File(stageDir, jarFilename)), manifest);

			// copy files from disk into jar file, then clean up

			// eclipselink-dbws-schema.xsd
			jarOutputStream.putNextEntry(new JarEntry(DBWS_SCHEMA_XML));
			f = new File(stageDir, DBWS_SCHEMA_XML);
			fis = new FileInputStream(f);
			for (int read = 0; read != -1; read = fis.read(buffer)) {
				jarOutputStream.write(buffer, 0, read);
			}
			fis.close();
			f.deleteOnExit();

			if (hasAttachments) {
				// swaref.xsd
				jarOutputStream.putNextEntry(new JarEntry(SWAREF_FILENAME));
				f = new File(stageDir, SWAREF_FILENAME);
				fis = new FileInputStream(f);
				for (int read = 0; read != -1; read = fis.read(buffer)) {
					jarOutputStream.write(buffer, 0, read);
				}
				fis.close();
			}

			// META-INF/eclipselink-dbws-or.xml
			f = new File(stageDir, DBWS_OR_XML);
			if (f.length() > 0) {
				jarOutputStream.putNextEntry(new JarEntry(META_INF_PATHS[0] + DBWS_OR_XML));
				fis = new FileInputStream(f);
				for (int read = 0; read != -1; read = fis.read(buffer)) {
					jarOutputStream.write(buffer, 0, read);
				}
				fis.close();
				}
			f.deleteOnExit();

			// META-INF/eclipselink-dbws-ox.xml
			f = new File(stageDir, DBWS_OX_XML);
			if (f.length() > 0) {
				jarOutputStream.putNextEntry(new JarEntry(META_INF_PATHS[0] + DBWS_OX_XML));
				fis = new FileInputStream(f);
				for (int read = 0; read != -1; read = fis.read(buffer)) {
					jarOutputStream.write(buffer, 0, read);
				}
				fis.close();
			}
			f.deleteOnExit();

			// META-INF/eclipselink-dbws-sessions.xml
			jarOutputStream.putNextEntry(new JarEntry(META_INF_PATHS[0] + DBWS_SESSIONS_XML));
			f = new File(stageDir, DBWS_SESSIONS_XML);
			fis = new FileInputStream(f);
			for (int read = 0; read != -1; read = fis.read(buffer)) {
				jarOutputStream.write(buffer, 0, read);
			}
			fis.close();
			f.deleteOnExit();

			// META-INF/eclipselink-dbws.xml
			jarOutputStream.putNextEntry(new JarEntry(META_INF_PATHS[0] + DBWS_SERVICE_XML));
			f = new File(stageDir, DBWS_SERVICE_XML);
			fis = new FileInputStream(f);
			for (int read = 0; read != -1; read = fis.read(buffer)) {
				jarOutputStream.write(buffer, 0, read);
			}
			fis.close();
			f.deleteOnExit();

			jarOutputStream.close();
		}
		catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	public String getOrProjectPathPrefix() {
		return META_INF_PATHS[0];
	}
	@Override
	public String getOxProjectPathPrefix() {
		return META_INF_PATHS[0];
	}
}