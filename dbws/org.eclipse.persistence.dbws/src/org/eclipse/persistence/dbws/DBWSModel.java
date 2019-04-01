/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.dbws;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.internal.xr.XRServiceModel;

/**
 * <p><b>PUBLIC</b>: model object for eclipselink-dbws.xml descriptor file. A DBWS (also known as
 * an {@link XRServiceAdapter}) requires the following resources:
 * <ul>
 * <li>metadata in the form of a descriptor file called <code><b>eclipselink-dbws.xml</b></code> in the
 * <code>META-INF/</code> directory<br>
 * (inside a <code>.jar</code> file, as an external 'exploded' directory on the classpath<br>
 * or in the WEB-INF/classes/META-INF/ directory inside a <code>.war</code> file).<br>
 * </li>
 * <li>an XML Schema Definition (<code>.xsd</code>) file called <code><b>eclipselink-dbws-schema.xsd</b></code><br>
 * located at the root directory of a <code>.jar</code> file, at the root of the first directory on the<br>
 * classpath or in the <code>WEB-INF/wsdl/</code> directory of a <code>.war</code> file
 * </li>
 * <li>an EclipseLink <code>sessions.xml</code> file called <code><b>eclipselink-dbws-sessions.xml</b></code> (in the
 * <code>META-INF/</code> directory)<br>
 * &nbsp; the naming convention for the <code>sessions.xml</code> files can be overridden by the<br>
 * <b>optional</b> <code>&lt;sessions-file&gt;</code> entry in the <code>eclipselink-dbws.xml</code>
 * descriptor file.
 * </li>
 * <li>EclipseLink metadata in the form of a EclipseLink {@link org.eclipse.persistence.sessions.Project Project}
 * (either deployment XML located<br>
 * in the <code>META-INF/</code> directory or Java classes on the classpath or in the<br>
 * <code>WEB-INF/classes</code> directory inside a <code>.war</code> file).<br>
 * &nbsp;<br>
 * <p>A typical <code>DBWS</code> requires two projects: one to represent the O-R side, the other to
 * represent the O-X side.<br>
 * The O-R and O-X <code>Projects</code> metadata must have:<br>
 * i) identical case-sensitive <code>Project</code> names:<pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;eclipselink:object-persistence version="Eclipse Persistence Services ..."
 *   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *   xmlns:eclipselink="http://xmlns.oracle.com/ias/xsds/eclipselink"
 *   &gt;
 *   &lt;eclipselink:name&gt;example&lt;/eclipselink:name&gt;
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
 * ii) identical case-sensitive aliases for {@link org.eclipse.persistence.descriptors.ClassDescriptor Descriptors}
 * that are common between the projects:
 * <pre>
 * &lt;eclipselink:class-mapping-descriptor xsi:type="eclipselink:relational-class-mapping-descriptor"&gt;
 *   &lt;eclipselink:class&gt;some.package.SomeClass&lt;/eclipselink:class&gt;
 *   &lt;eclipselink:alias&gt;SomeAlias&lt;/eclipselink:alias&gt;
 * ...
 * &lt;eclipselink:class-mapping-descriptor xsi:type="eclipselink:xml-class-mapping-descriptor"&gt;
 *   &lt;eclipselink:class&gt;some.package.SomeClass&lt;/eclipselink:class&gt;
 *   &lt;eclipselink:alias&gt;SomeAlias&lt;/eclipselink:alias&gt;
 * </pre>
 * </li>
 * </ul>
 * An example <code><b>eclipselink-dbws.xml</b></code> descriptor file:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
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
 * </pre>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.0
 */
public class DBWSModel extends XRServiceModel {

}
