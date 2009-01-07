/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * A Method returns REF CURSOR
 */
public interface CursorMethod {
    public Type getReturnEleType();

    public boolean isSingleCol();

    public String singleColName();

    public boolean returnBeans();

    public boolean returnResultSet();
}
