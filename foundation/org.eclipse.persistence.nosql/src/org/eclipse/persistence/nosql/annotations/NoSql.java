/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.nosql.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** 
 * An NoSQL (No SQL, or Not Only SQL) database is any non-relational data source.<br>
 * This can include:<br>
 * <ul>
 * <li>NoSQL databases such as Oracle NoSQL, MongoDB, etc.
 * <li>XML databases
 * <li>Distributed cache stores such as Oracle Coherence
 * <li>Object databases
 * <li>Legacy databases, gateways and transaction system such as VSAM, ADA, CICS, IMS, MQSeries, Tuxedo, etc.
 * <li>ERP systems, such as SAP
 * </ul>
 * <p>
 * EclipseLink allows NoSQL data to be mapped to objects, and accessed through JPA and EclipseLink's APIs.
 * <p>
 * Entity and Embeddable objects can be used to map NoSQL data.
 * Most NoSQL data is hierarchical in form so usage of embeddable objects is common.
 * Some NoSQL adaptors support XML data, so NoSQL mapped objects can use XML mappings when
 * mapping to XML.
 * <p>
 * EclipseLink support NoSQL data access through the JavaEE Connector Architecture.
 * A JCA adaptor is required to map NoSQL data, this may be provided by EclipseLink,
 * provided by a third party such as Attunity, or custom built.
 * 
 * @see org.eclipse.persistence.eis.EISDescriptor
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface NoSql {
    /**
     * (Required) The database name of the database structure type.
     */
    String dataType() default "";

    /**
     * (Optional) Defines the order of the fields contained in the database structure type.
     */
    DataFormatType dataFormat() default DataFormatType.XML; 
}
