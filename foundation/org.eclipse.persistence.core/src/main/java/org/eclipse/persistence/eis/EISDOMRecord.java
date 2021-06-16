/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.eis;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import jakarta.resource.cci.*;
import org.w3c.dom.*;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.oxm.record.XMLRecord;

/**
 * <p>An <code>EISDOMRecord</code> is a wrapper for a DOM tree.  It provides a
 * Record/Map API on an XML DOM element.  This can be used from the
 * platform to wrap adapter XML/DOM records to be used with TopLink XML.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class EISDOMRecord extends org.eclipse.persistence.oxm.record.DOMRecord implements DOMRecord, MappedRecord {

    /** The original adapter record. */
    protected jakarta.resource.cci.Record record;

    /** The record name. */
    protected String recordName;

    /** The record name. */
    protected String recordShortDescription;

    /** Used for introspected DOM records. */
    protected static Method domMethod;

    /**
     * Default constructor.
     */
    public EISDOMRecord() {
        super();
        setRecordName("");
        setRecordShortDescription("");
    }

    /**
     * Create a TopLink record from the JCA adapter record and DOM tree.
     */
    public EISDOMRecord(jakarta.resource.cci.Record record, Element dom) {
        super(dom);
        this.record = record;
        this.recordName = record.getRecordName();
        this.recordShortDescription = record.getRecordShortDescription();
        if (record instanceof XMLRecord) {
            this.session = ((XMLRecord)record).getSession();
        }
    }

    /**
     * Create a TopLink record from a DOM tree.
     */
    public EISDOMRecord(Element dom) {
        super(dom);
        this.recordName = "";
        this.recordShortDescription = "";
    }

    /**
     * Create a TopLink record from the JCA adapter record.
     * This attempts to introspect the record to retrieve the DOM tree.
     */
    public EISDOMRecord(jakarta.resource.cci.Record record) {
        this.record = record;
        this.recordName = record.getRecordName();
        this.recordShortDescription = record.getRecordShortDescription();
        if (record instanceof XMLRecord) {
            this.session = ((XMLRecord)record).getSession();
        }
        if (domMethod == null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        domMethod = AccessController.doPrivileged(new PrivilegedGetMethod(record.getClass(), "getDom", null, false));
                    }catch (PrivilegedActionException ex){
                        throw (Exception)ex.getCause();
                    }
                }else{
                    domMethod = PrivilegedAccessHelper.getMethod(record.getClass(), "getDom", null, false);
                }
            } catch (Exception notFound) {
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try{
                            domMethod = AccessController.doPrivileged(new PrivilegedGetMethod(record.getClass(), "getDOM", null, false));
                        }catch (PrivilegedActionException ex){
                            throw (Exception)ex.getCause();
                        }
                    }else{
                        domMethod = PrivilegedAccessHelper.getMethod(record.getClass(), "getDOM", null, false);
                    }
                 } catch (Exception cantFind) {
                    throw new EISException(cantFind);
                }
            }
        }
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    setDOM((Element)AccessController.doPrivileged(new PrivilegedMethodInvoker(domMethod, record, null)));
                }catch (PrivilegedActionException ex){
                    throw (Exception)ex.getCause();
                }
            }else{
                setDOM((Element)PrivilegedAccessHelper.invokeMethod(domMethod, record, null));
            }
        } catch (Exception error) {
            throw new EISException(error);
        }
    }

    /**
     * Return the JCA adapter record.
     */
    public jakarta.resource.cci.Record getRecord() {
        return record;
    }

    /**
     * Set the JCA adapter record.
     */
    public void setRecord(jakarta.resource.cci.Record record) {
        this.record = record;
        this.recordName = record.getRecordName();
        this.recordShortDescription = record.getRecordShortDescription();
        if (record instanceof XMLRecord) {
            this.session = ((XMLRecord)record).getSession();
        }
    }

    /**
     * Forward to the record.
     */
    @Override
    public String getRecordShortDescription() {
        return recordShortDescription;
    }

    /**
     * Forward to the record.
     */
    @Override
    public void setRecordShortDescription(String recordShortDescription) {
        this.recordShortDescription = recordShortDescription;
    }

    /**
     * Forward to the record.
     */
    @Override
    public String getRecordName() {
        return recordName;
    }

    /**
     * Forward to the record.
     */
    @Override
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * INTERNAL:
     * Build the nested record, this can be overwriten by subclasses to use their subclass instance.
     */
    @Override
    public XMLRecord buildNestedRow(Element element) {
        if (getRecord() != null) {
            return new EISDOMRecord(getRecord(), element);
        } else {
            return new EISDOMRecord(element);
        }
    }
}
