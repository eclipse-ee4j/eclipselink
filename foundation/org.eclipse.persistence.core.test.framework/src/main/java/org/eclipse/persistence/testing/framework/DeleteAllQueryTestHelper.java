/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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


package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

import java.util.Vector;

public class DeleteAllQueryTestHelper {

    public static String execute(Session mainSession, Class<?> referenceClass, Expression selectionExpression) {
        return execute(mainSession, referenceClass, selectionExpression, true);
    }

    public static String execute(Session mainSession, Class<?> referenceClass, Expression selectionExpression, boolean shouldDeferExecutionInUOW) {
        return execute(mainSession, referenceClass, selectionExpression, shouldDeferExecutionInUOW, true);
    }

    // mainSession - the ServerSession used.
    // referenceClass - the reference class of DeleteAllQuery to be tested
    // selectionExpression - selection expression of DeleteAllQuery to be tested
    // shouldDeferExecutionInUOW==true causes deferring query execution until uow.commit;
    // shouldDeferExecutionInUOW==false causes immediate query execution;
    // shouldHandleChildren==true means the test will be executed not only with the specified class,
    // but also with all its subclasses.
    // Each test will test DeleteAllQuery with the specified reference class
    // and all its subclasses
    // Example: for Vehicle.class  9 DeleteAllQueries will be tested.
    // shouldHandleChildren==false means the test will be executed with the specified class only.
    //
    // For each DeleteAllQuery its results compared with one-by-one deletion:
    //  selectionCriteria is used to select objects and then delete them one-by-one (using uow.deleteObject);
    //  the results are saved and after compared with DeleteAllQuery results:
    //    both inCache and inDb comparison performed;
    //    both deleted and remained objects should be the same;
    public static String execute(Session mainSession, Class<?> referenceClass, Expression selectionExpression, boolean shouldDeferExecutionInUOW, boolean handleChildren) {
        Class<?> rootClass = referenceClass;
        ClassDescriptor descriptor = mainSession.getClassDescriptor(referenceClass);
        if(descriptor.hasInheritance()) {
            ClassDescriptor parentDescriptor = descriptor;
            while(!parentDescriptor.getInheritancePolicy().isRootParentDescriptor()) {
                parentDescriptor = parentDescriptor.getInheritancePolicy().getParentDescriptor();
            }
            rootClass = parentDescriptor.getJavaClass();
        }
        String errorMsg = execute(mainSession, referenceClass, selectionExpression, shouldDeferExecutionInUOW, handleChildren, rootClass);

        if(errorMsg.isEmpty()) {
            return null;
        } else {
            return errorMsg;
        }
    }

    protected static String execute(Session mainSession, Class<?> referenceClass, Expression selectionExpression, boolean shouldDeferExecutionInUOW, boolean handleChildren,
                                    Class<?> rootClass) {
        StringBuilder errorMsg = new StringBuilder();
        clearCache(mainSession);

        // first delete using the original TopLink approach - one by one.
        UnitOfWork uow = mainSession.acquireUnitOfWork();
        // mainSession could be a ServerSession
        AbstractSession session = uow.getParent();

        // Will need to bring the db back to its original state
        // so that comparison of the deletion result would be possible.
        session.beginTransaction();

        Vector<?> objectsToDelete = uow.readAllObjects(referenceClass, selectionExpression);

        ClassDescriptor descriptor = mainSession.getClassDescriptor(referenceClass);

        uow.deleteAllObjects(objectsToDelete);
        mainSession.logMessage("***delete one by one");
        uow.commit();

        Vector<?> objectsLeftAfterOriginalDeletion = session.readAllObjects(rootClass);

        session.rollbackTransaction();

        // now delete using DeleteAllQuery.
        clearCache(mainSession);

        // bring all objects into cache
        session.readAllObjects(rootClass);

        uow = mainSession.acquireUnitOfWork();
        // mainSession could be a ServerSession
        session = uow.getParent();

        // Will need to bring the db back to its original state
        // so that the in case thre are children descriptors
        // they would still have objects to work with.
        session.beginTransaction();

        DeleteAllQuery query = new DeleteAllQuery(referenceClass, selectionExpression);
        query.setShouldDeferExecutionInUOW(shouldDeferExecutionInUOW);
        uow.executeQuery(query);
        mainSession.logMessage("***DeleteAllQuery for class " + referenceClass.getName());
        uow.commit();

        // verify that cache invalidation worked correctly:
        // deleted objects should've disappeared, others remain
        String classErrorMsg = "";
        for(int i=0; i < objectsToDelete.size(); i++) {
            Object deletedObject = session.readObject(objectsToDelete.elementAt(i));
            if(deletedObject != null) {
                classErrorMsg = classErrorMsg + "Deleted object "+ deletedObject +" is stil in cache; ";
                break;
            }
        }
        for(int i=0; i < objectsLeftAfterOriginalDeletion.size(); i++) {
            Object remainingObject = objectsLeftAfterOriginalDeletion.elementAt(i);
            Object remainingObjectRead = session.readObject(remainingObject);
            if(remainingObjectRead == null) {
                classErrorMsg = classErrorMsg + "Remaining object " + remainingObject +" is not in cache; ";
                break;
            }
        }

        // now let's verify that the objects were correctly deleted from the db
        clearCache(mainSession);
        // deleted objects should've disappeared, others remain
        for(int i=0; i < objectsToDelete.size(); i++) {
            Object deletedObject = session.readObject(objectsToDelete.elementAt(i));
            if(deletedObject != null) {
                classErrorMsg = classErrorMsg + "Deleted object "+ deletedObject + " is stil in db; ";
                break;
            }
        }
        for(int i=0; i < objectsLeftAfterOriginalDeletion.size(); i++) {
            Object remainingObject = objectsLeftAfterOriginalDeletion.elementAt(i);
            Object remainingObjectRead = session.readObject(remainingObject);
            if(remainingObjectRead == null) {
                classErrorMsg = classErrorMsg + "Remaining object " + remainingObject +" is not in db; ";
                break;
            }
        }

        session.rollbackTransaction();

        if(!classErrorMsg.isEmpty()) {
            String className = referenceClass.getName();
            String shortClassName = className.substring(className.lastIndexOf('.') + 1);
            errorMsg.append(" ").append(shortClassName).append(": ").append(classErrorMsg);
        }

        if(handleChildren) {
            if(descriptor.hasInheritance() && descriptor.getInheritancePolicy().hasChildren()) {
                for (ClassDescriptor childDescriptor : descriptor.getInheritancePolicy().getChildDescriptors()) {
                    Class<?> childReferenceClass = childDescriptor.getJavaClass();
                    errorMsg.append(execute(mainSession, childReferenceClass, selectionExpression, shouldDeferExecutionInUOW, handleChildren, rootClass));
                }
            }
        }
        return errorMsg.toString();
    }

    protected static void clearCache(Session mainSession) {
        mainSession.getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
