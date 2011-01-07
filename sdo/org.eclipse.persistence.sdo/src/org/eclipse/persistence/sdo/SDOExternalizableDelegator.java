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
package org.eclipse.persistence.sdo;

import java.io.IOException;
import java.io.ObjectInput;
import java.lang.reflect.Field;
import org.eclipse.persistence.sdo.helper.DataObjectInputStream;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.ExternalizableDelegator;
import commonj.sdo.impl.HelperProvider;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.internal.security.PrivilegedGetField;
import org.eclipse.persistence.internal.security.PrivilegedGetValueFromField;
import org.eclipse.persistence.internal.security.PrivilegedSetValueInField;

public class SDOExternalizableDelegator extends ExternalizableDelegator {

	static final PrivilegedGetField privilegedGetDelegateField = new PrivilegedGetField(ExternalizableDelegator.class, "delegate", true);

	public SDOExternalizableDelegator() {
		super();
	}

	public SDOExternalizableDelegator(Object target) {
		super(target);
	}

	public SDOExternalizableDelegator(Object target, HelperContext aContext) {
        // JIRA129: pass the helperContext to the constructor to enable non-static contexts
        // check for context type (if non-static SDOHelperContext then we need to cast to use the non-interface createResolvable function
        // to remove this instanceof check - add createResolvable to the HelperContext interface
        if (aContext instanceof SDOHelperContext) {
			setDelegate(((SDOHelperContext)aContext).createResolvable(target));
        } else {
            // use static helper
			setDelegate(HelperProvider.createResolvable(target));
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // This function is indirectly called by InputStream.readExternalData().
        // We reset the static helperContext set in the default constructor during is.readObject()
        // with the passed in context from the client (either static or dynamic instance)
        if (in instanceof DataObjectInputStream) {
            // only reset non-static implementations
            getDelegate().setHelperContext(((DataObjectInputStream)in).getHelperContext());
        }
		super.readExternal(in);
    }

	private SDOResolvable getDelegate() {
		try {	
			Field delegateField = (Field) privilegedGetDelegateField.run();
			PrivilegedGetValueFromField privilegedGetValueFromDelegateField = new PrivilegedGetValueFromField(delegateField, this);
			return (SDOResolvable) privilegedGetValueFromDelegateField.run();
		}catch (NoSuchFieldException nsfException){
			throw SDOException.errorAccessingExternalizableDelegator("delegate", nsfException);
		}catch (IllegalAccessException iaException){
			throw SDOException.errorAccessingExternalizableDelegator("delegate", iaException);
		}
	}

	private void setDelegate(Resolvable resolvable) {
		try {
			Field delegateField = (Field) privilegedGetDelegateField.run();
			PrivilegedSetValueInField privilegedSetValueInDelegateField = new PrivilegedSetValueInField(delegateField, this, resolvable);
			privilegedSetValueInDelegateField.run();
		} catch (NoSuchFieldException nsfException){
			throw SDOException.errorAccessingExternalizableDelegator("delegate", nsfException);
		} catch (IllegalAccessException iaException){
			throw SDOException.errorAccessingExternalizableDelegator("delegate", iaException);
		}		
	}

}
