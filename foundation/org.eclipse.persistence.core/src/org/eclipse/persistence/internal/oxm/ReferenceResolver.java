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
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

public class ReferenceResolver {
    public static final String KEY = "REFERENCE_RESOLVER";
    private ArrayList references;

    /**
     * Return an instance of this class for a given unit of work.
     *
     * @param uow
     * @return
     */
    public static ReferenceResolver getInstance(Session unitOfWork) {
        if (unitOfWork == null) {
            return null;
        }
        return (ReferenceResolver) unitOfWork.getProperty(KEY);
    }

    /**
     * The default constructor initializes the list of References.
     */
    public ReferenceResolver() {
        references = new ArrayList();
    }

    /**
     * Add a Reference object to the list - these References will
     * be resolved after unmarshalling is complete.
     *
     * @param ref
     */
    public void addReference(Reference ref) {
        references.add(ref);
    }

    /**
     * INTERNAL:
     * Create vectors of primary key values to be used for cache lookup.  The map
     * of vectors on the reference is keyed on the reference descriptors primary
     * key field names.  Each of these vectors contains all of the values for a
     * particular key - in the order that they we read in from the document.  For
     * example, if the key field names are A, B, and C, and there are three reference
     * object instances, then the hashmap would have the following:
     * (A=[1,2,3], B=[X,Y,Z], C=[Jim, Joe, Jane]).  If the primary key field names on
     * the reference descriptor contained [B, C, A], then the result of this method call
     * would be reference.primaryKeys=([X, Jim, 1], [Y, Joe, 2], [Z, Jane, 3]).
     *
     * @param reference
     */
    private void createPKVectorsFromMap(Reference reference) {
    	XMLCollectionReferenceMapping mapping = (XMLCollectionReferenceMapping) reference.getMapping();
    	Vector pks = new Vector();

    	Vector pkFields = mapping.getReferenceDescriptor().getPrimaryKeyFieldNames();
    	if (pkFields.size() <= 0) {
    		return;
    	}

    	Vector pkVals;
    	boolean init = true;

    	// for each primary key field name
    	for (Iterator pkFieldNameIt = pkFields.iterator(); pkFieldNameIt.hasNext(); ) {
    		pkVals = (Vector) reference.getPrimaryKeyMap().get(pkFieldNameIt.next());

    		if (pkVals == null) {
    			return;
    		}
    		// initialize the list of pk vectors once and only once
    		if (init) {
    			for (int i=0; i<pkVals.size(); i++) {
    				pks.add(new Vector());
    			}
    			init = false;
    		}

    		// now add each value for the current target key to it's own vector
        	for (int i=0; i<pkVals.size(); i++) {
    			Object val = pkVals.get(i);
				((Vector) pks.get(i)).add(val);
    		}
    	}
    	reference.primaryKeys = pks;
    }

    /**
     * Retrieve the reference for a given mapping instance.
     *
     * @param mapping
     */
    public Reference getReference(XMLObjectReferenceMapping mapping, Object sourceObject) {
        for (int x = 0; x < references.size(); x++) {
            Reference reference = (Reference) references.get(x);
            if (reference.getMapping() == mapping && reference.getSourceObject() == sourceObject) {
            	return reference;
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * @param session typically will be a unit of work
     */
    public void resolveReferences(AbstractSession session) {
        for (int x = 0, referencesSize = references.size(); x < referencesSize; x++) {
            Reference reference = (Reference) references.get(x);

            if (reference.getMapping() instanceof XMLCollectionReferenceMapping) {
                XMLCollectionReferenceMapping mapping = (XMLCollectionReferenceMapping) reference.getMapping();
                ContainerPolicy cPolicy = mapping.getContainerPolicy();
                Object currentObject = reference.getSourceObject();
                Object container = null;
                if (mapping.getReuseContainer()) {
                    container = mapping.getAttributeAccessor().getAttributeValueFromObject(currentObject);
                } else {
                    container = cPolicy.containerInstance();
                }

                // create vectors of primary key values - one vector per reference instance
                createPKVectorsFromMap(reference);
                // loop over each pk vector and get object from cache - then add to collection and set on object
                for (Iterator pkIt = reference.getPrimaryKeys().iterator(); pkIt.hasNext();) {
                    Vector pkVector = (Vector) pkIt.next();
                    Object value = session.getIdentityMapAccessor().getFromIdentityMap(pkVector, reference.getTargetClass());

                    if (value != null) {
                        cPolicy.addInto(value, container,  session);
                    }
                }
                // for each reference, get the source object and add it to the container policy
                // when finished, set the policy on the mapping
                mapping.setAttributeValueInObject(currentObject, container);
                if(mapping.getInverseReferenceMapping() != null) {
                    Object iterator = cPolicy.iteratorFor(container);
                    while(cPolicy.hasNext(iterator)) {
                        Object next = cPolicy.next(iterator, session);
                        if(mapping.getInverseReferenceMapping().getContainerPolicy() == null) {
                            mapping.getInverseReferenceMapping().getAttributeAccessor().setAttributeValueInObject(next, currentObject);
                        } else {
                            Object backpointerContainer = mapping.getInverseReferenceMapping().getAttributeAccessor().getAttributeValueFromObject(next);
                            if(backpointerContainer == null) {
                                backpointerContainer = mapping.getInverseReferenceMapping().getContainerPolicy().containerInstance();
                                mapping.getInverseReferenceMapping().getAttributeAccessor().setAttributeValueInObject(next, backpointerContainer);
                            }
                            mapping.getInverseReferenceMapping().getContainerPolicy().addInto(currentObject, backpointerContainer, session);
                        }
                    }
                }
            } else if (reference.getMapping() instanceof XMLObjectReferenceMapping) {
                Object value = session.getIdentityMapAccessor().getFromIdentityMap(reference.getPrimaryKeys(), reference.getTargetClass());
                XMLObjectReferenceMapping mapping = (XMLObjectReferenceMapping)reference.getMapping();
                if (value != null) {
                    mapping.setAttributeValueInObject(reference.getSourceObject(), value);
                }
                if (null != reference.getSetting()) {
                    reference.getSetting().setValue(value);
                }

                if(mapping.getInverseReferenceMapping() != null) {
                    AttributeAccessor backpointerAccessor = mapping.getInverseReferenceMapping().getAttributeAccessor();                    
                    if(mapping.getInverseReferenceMapping().getContainerPolicy() == null) {
                        backpointerAccessor.setAttributeValueInObject(value, reference.getSourceObject());
                    } else {
                        Object backpointerContainer = backpointerAccessor.getAttributeValueFromObject(value);
                        if(backpointerContainer == null) {
                            backpointerContainer = mapping.getInverseReferenceMapping().getContainerPolicy().containerInstance();
                            backpointerAccessor.setAttributeValueInObject(value, backpointerContainer);
                        }
                        mapping.getInverseReferenceMapping().getContainerPolicy().addInto(reference.getSourceObject(), backpointerContainer, session);
                    }
                }
            }
        }
        // release the unit of work, if required
        if (session.isUnitOfWork()) {
            ((UnitOfWork) session).release();
        }

        // reset the references list
        references = new ArrayList();
    }

}