/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.workbench.expressions;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;

/**
 * INTERNAL:
 * An MWLiteralArgument is only used as the right hand side of a MWBinaryExpression
 * The user choose a type and then enters a value in the correct format of the chosen type
 */
public final class LiteralArgumentRepresentation extends ExpressionArgumentRepresentation {
    private Class type;
    private Object value;
    private ConversionManager conversionManager;

    /**
     * Default constructor - for TopLink use only.
     */
    private LiteralArgumentRepresentation() {
        super();
        this.conversionManager = new ConversionManager();
    }

    LiteralArgumentRepresentation(Class type, Object value) {
        this();
        this.type = type;
        this.value = value;
    }

    public Object buildValueFromString(String valueString, Class type) {
        // if no BldrClass has been specified yet, we throw away the value
        Class bldrClass = type;
        if (bldrClass != null) {
            try {
                Class javaClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        javaClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(bldrClass.getName()));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof ClassNotFoundException){
                            throw (ClassNotFoundException) ex.getCause();
                        }
                        throw (RuntimeException) ex.getCause();
                    }
                }else{
                    javaClass = PrivilegedAccessHelper.getClassForName(bldrClass.getName());
                }
                return this.conversionManager.convertObject(valueString, javaClass);
            } catch (ClassNotFoundException ex) {
                // it's very unlikely this will happen since
                // we try to restrict the class to common classes (e.g. String, Date)
            }
        }

        throw ConversionException.couldNotBeConverted(valueString, null);
    }

    public Object getConvertedValueWithoutSettingValue(Object before, Class type) {
        if (before != null) {
            Class beforeClass = before.getClass();
            try {
                Class javaClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        javaClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(type.getName()));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof ClassNotFoundException){
                            throw (ClassNotFoundException) ex.getCause();
                        }
                        throw (RuntimeException) ex.getCause();
                    }
                }else{
                    javaClass = PrivilegedAccessHelper.getClassForName(type.getName());
                }
                Object after = getConversionManager().convertObject(before, javaClass);
                return after;
            } catch (ClassNotFoundException ex) {
            } catch (ConversionException ex) {
                return null;
            }
            ;
        }
        return null;
    }

    public void convertValue() {
        Object before = getValue();
        if (before != null) {
            Class beforeClass = before.getClass();
            try {
                Class javaClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        javaClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(getType().getName()));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof ClassNotFoundException){
                            throw (ClassNotFoundException) ex.getCause();
                        }
                        throw (RuntimeException) ex.getCause();
                    }
                }else{
                    javaClass = PrivilegedAccessHelper.getClassForName(getType().getName());
                }
                Object after = getConversionManager().convertObject(before, javaClass);
                setValue(after);
            } catch (ClassNotFoundException ex) {
            } catch (ConversionException ex) {
                setValue(null);
            }
            ;
        }
    }

    public String displayString() {
        //only add the quotes if this is a string
        if (getValue() == null) {
            return "null";
        }
        return "\"" + getStringFromObjectValue(getValue()) + "\"";
    }

    public boolean isLiteralArgument() {
        return true;
    }

    public ConversionManager getConversionManager() {
        return conversionManager;
    }

    public String getStringFromObjectValue(Object myValue) {
        return (String)this.getConversionManager().convertObject(myValue, String.class);
    }

    public Class getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setType(Class type) {
        this.type = type;
        convertValue();
    }

    public void setValue(Object value) {
        this.value = value;
    }

    //Conversion to Runtime
    public Expression convertToRuntime(Expression builder) {
        //not sure if passing this the builder object will work or if it needs the firstExpression
        //Make sure to test this well.
        return new ConstantExpression(this.value, builder);
        //return this.value;
    }

    public static LiteralArgumentRepresentation convertFromRuntime(ConstantExpression runtimeExpression) {
        Object value = runtimeExpression.getValue();
        Class type = null;
        if (value != null) {
            type = value.getClass();
        }
        return new LiteralArgumentRepresentation(type, value);
    }

    public String convertToRuntimeString(String builderString) {
        //Bug#3183120 Only String type should have quotation
        if (type == String.class) {
            return displayString();
        } else {
            return getStringFromObjectValue(getValue());
        }
    }
}