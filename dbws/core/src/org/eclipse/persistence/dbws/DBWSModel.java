/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.dbws;

// Javase imports

// Java extension imports

// TopLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceModel;

/**
 * <p><b>PUBLIC</b>: model object for toplink-dbws.xml descriptor file. A DBWS (also known as
 * an {@link XRServiceAdapter}) requires the following resources:
 * <ul>
 * <li>metadata in the form of a descriptor file called <tt><b>toplink-dbws.xml</b></tt> in the
 * <code>META-INF/</code> directory<br>
 * (inside a <tt>.jar</tt> file, as an external 'exploded' directory on the classpath<br>
 * or in the WEB-INF/classes/META-INF/ directory inside a <tt>.war</tt> file).<br>
 * </li>
 * <li>an XML Schema Definition (<tt>.xsd</tt>) file called <tt><b>toplink-dbws-schema.xsd</b></tt><br>
 * (located at the root directory of a <tt>.jar</tt>/<tt>war</tt> file, or at the root of the<br>
 * first directory on the classpath)
 * </li>
 * <li>a TopLink <tt>sessions.xml</tt> file called <tt><b>toplink-dbws-sessions.xml</b></tt> (in the
 * <code>META-INF/</code> directory)<br>
 * &nbsp; the naming convention for the <tt>sessions.xml</tt> files can be overriden by the<br>
 * <b>optional</b> <tt>&lt;sessions-file&gt;</tt> entry in the <code>toplink-dbws.xml</code>
 * descriptor file.
 * </li>
 * <li>TopLink metadata in the form of a TopLink {@link Project} (either deployment XML located<br>
 * in the <code>META-INF/</code> directory or Java classes on the classpath or in the<br>
 * <code>WEB-INF/classes</code> directory inside a <tt>.war</tt> file).<br>
 * &nbsp;<br>
 * <p>A typical <code>DBWS</code> requires two projects: one to represent the O-R side, the other to
 * represent the O-X side.<br>
 * The O-R and O-X <code>Projects</code> metadata must have:<br>
 * i) identical case-sensitive <code>Project</code> names:<pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;toplink:object-persistence version="Oracle TopLink - 11g Release 1 (11.1.1.0.0) (Build YYMMDD)"
 *   xmlns:opm="http://xmlns.oracle.com/ias/xsds/opm"
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:toplink="http://xmlns.oracle.com/ias/xsds/toplink"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   &gt;
 *   &lt;opm:name&gt;example&lt;/opm:name&gt;
 * or
 * ...
 * import org.eclipse.persistence.sessions.Project;
 * public class SomeORProject extends Project {
 *   public SomeORProject () {
 *     setName("Example");
 *     ...
 * }
 * public class SomeOXProject extends Project {
 *   public SomeOXProject () {
 *     setName("Example");
 *     ...
 * }
 * </pre>
 * ii) identical case-sensitive aliases for {@link ClassDescriptor Descriptors} that are common
 * between the projects:
 * <pre>
 * &lt;opm:class-mapping-descriptor xsi:type="toplink:relational-class-mapping-descriptor"&gt;
 *   &lt;opm:class&gt;some.package.SomeClass&lt;/opm:class&gt;
 *   &lt;opm:alias&gt;SomeAlias&lt;/opm:alias&gt;
 * ...
 * &lt;opm:class-mapping-descriptor xsi:type="toplink:xml-class-mapping-descriptor"&gt;
 *   &lt;opm:class&gt;some.package.SomeClass&lt;/opm:class&gt;
 *   &lt;opm:alias&gt;SomeAlias&lt;/opm:alias&gt;
 * </pre>
 * </li>
 * </ul>
 * An example <tt><b>toplink-dbws.xml</b></tt> descriptor file:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;dbws
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   &gt;
 *   &lt;name&gt;example&lt;/name&gt;
 *   &lt;sessions-file&gt;example-dbws-sessions.xml&lt;/sessions-file&gt;
 *   &lt;query&gt;
 *     &lt;name&gt;countEmployees&lt;/name&gt;
 *     &lt;result&gt;
 *       &lt;type&gt;xsd:int&lt;/type&gt;
 *       &lt;simple-xml-format&gt;
 *         &lt;simple-xml-format-tag&gt;employee-info&lt;/simple-xml-format-tag&gt;
 *         &lt;simple-xml-tag&gt;aggregate-info&lt;/simple-xml-tag&gt;
 *       &lt;/simple-xml-format&gt;
 *     &lt;/result&gt;
 *     &lt;sql&gt;&lt;![CDATA[select count(*) from EMP]]&gt;&lt;/sql&gt;
 *   &lt;/query&gt;
 *   &lt;query&gt;
 *     &lt;name&gt;findAllEmployees&lt;/name&gt;
 *     &lt;result isCollection="true"&gt;
 *       &lt;type&gt;empType&lt;/type&gt;
 *     &lt;/result&gt;
 *     &lt;sql&gt;&lt;![CDATA[select * from EMP]]&gt;&lt;/sql&gt;
 *   &lt;/query&gt;
 * &lt;/dbws&gt;
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class DBWSModel extends XRServiceModel {

}
