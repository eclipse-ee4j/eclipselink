/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * A Method provides information about a single method of a type.
 */
public class PlsqlMethod extends Method {
    /**
     * Construct a Method
     */
    public PlsqlMethod(String name, String overloadNumber, int modifiers, Type returnType,
        Type[] parameterTypes, String[] parameterNames, int[] parameterModes,
        boolean[] parameterDefaults, int paramLen) {
        super(name, overloadNumber, modifiers, returnType, parameterTypes, parameterNames,
            parameterModes, parameterDefaults, paramLen);
    }
}
