/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.descriptors;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.mappings.transformers.*;

/**
 * INTERNAL:
 * An implementation of FieldTransformation which holds onto a transformer class-name
 * which will be instantiated to do transformations
 *
 * @author  mmacivor
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class TransformerBasedFieldTransformation extends FieldTransformation {
    protected Class transformerClass;
    protected String transformerClassName;
    protected FieldTransformer transformer;

    public TransformerBasedFieldTransformation() {
        super();
    }

    public TransformerBasedFieldTransformation(FieldTransformer aTransformer) {
        transformer = aTransformer;
        if (transformer != null) {
        	setTransformerClass(transformer.getClass());
        	setTransformerClassName(transformer.getClass().getName());
        }
    }

    public Class getTransformerClass() {
        return transformerClass;
    }

    public void setTransformerClass(Class transformerClass) {
        this.transformerClass = transformerClass;
    }
    
    public String getTransformerClassName() {
        return transformerClassName;
    }

    public void setTransformerClassName(String transformerClassName) {
        this.transformerClassName = transformerClassName;
    }

    public FieldTransformer buildTransformer() throws Exception {
        if (transformer == null) {
            Class transformerClass = getTransformerClass();
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    transformer = (FieldTransformer)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(transformerClass));
                }catch (PrivilegedActionException ex){
                    throw (Exception)ex.getCause();
                }
            }else{
                transformer = (FieldTransformer)PrivilegedAccessHelper.newInstanceFromClass(transformerClass);
            }
        }
        return transformer;
    }
}
