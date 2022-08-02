package org.eclipse.persistence.jpa.metadata;

import java.io.ObjectInputFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Project;

/**
 * Verify content of FileBasedProjectCache being read with ObjectInputStream.
 */
class FileBasedProjectCacheFilter implements ObjectInputFilter {

    // Initial size of 3nd and lower level whitelist cache.
    private static final int DEPTH3_WHITELIST_SIZE = 128;
    // Project class structure is already known so this can be built in static context to improve performance a bit.
    // Set of allowed classes on 2nd level: Project class fields
    private static final List<Class<?>> DEPTH2_CLASSES = initDepth2Whitelist();

    // Build Set of allowed classes on 2nd level based on Project class fields.
    private static List<Class<?>> initDepth2Whitelist() {
        final SessionLog log = AbstractSessionLog.getLog();
        final Field[] projectFields = Project.class.getDeclaredFields();
        final Set<Class<?>> whitelist = new HashSet<>(projectFields.length);
        for (Field f : projectFields) {
            final Class<?> fieldClass = f.getType();
            if (!fieldClass.isPrimitive() && !Modifier.isTransient(f.getModifiers()) && !whitelist.contains(fieldClass)) {
                final String className = fieldClass.getCanonicalName();
                // Eclipselink own classes of Project class fields
                if (className.startsWith("org.eclipse.persistence.") && isEclipseLinkFileJar(fieldClass, log)) {
                    log.log(SessionLog.FINER, String.format(
                            "FileBasedProjectCacheFilter :: added 2nd level EclipseLink class %s",
                            fieldClass.getCanonicalName()));
                    addContainingClasses(whitelist, fieldClass, log);
                    whitelist.add(fieldClass);
                // Java SE classes of Project class fields
                // FIXME: Some additional Java SE classes check may be useful here.
                } else if (className.startsWith("java.")) {
                    log.log(SessionLog.FINER, String.format(
                            "FileBasedProjectCacheFilter :: added 2nd level Java SE class %s",
                            fieldClass.getCanonicalName()));
                    addContainingClasses(whitelist, fieldClass, log);
                    whitelist.add(fieldClass);
                }
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(whitelist));
    }

    // Inner classes are added without additional check.
    private static void addContainingClasses(final Set<Class<?>> whitelist, final Class<?> serialClass, final SessionLog log) {
        // Stack to walk through declared classes tree.
        final Stack<Class<?>> stack = new Stack<>();
        for (Class<?> c : serialClass.getDeclaredClasses()) {
            stack.push(c);
        }
        // Tree traversal algorithm
        while(!stack.empty()) {
            Class<?> containingClass = stack.pop();
            for (Class<?> c : containingClass.getDeclaredClasses()) {
                stack.push(c);
            }
            // Process current inner class
            if (!whitelist.contains(containingClass)) {
                log.log(SessionLog.FINER, String.format(
                        "FileBasedProjectCacheFilter: added %s inner class: %s to cache",
                        serialClass.getName(), containingClass.getName()));
                whitelist.add(containingClass);
            } else {
                log.log(SessionLog.FINEST, String.format(
                        "FileBasedProjectCacheFilter: cache already contains %s inner class: %s",
                        serialClass.getName(), containingClass.getName()));
            }
        }
    }

    // Check whether Eclipselink own class is from known .jar file name.
    private static boolean isEclipseLinkFileJar(final Class<?> serialClass, final SessionLog log) {
        final URL classUrl = serialClass.getResource(
                '/' + serialClass.getName().replace('.', '/') + ".class");
        if (classUrl != null && "jar".equals(classUrl.getProtocol())) {
            final String classFile = classUrl.getPath();
            try {
                final URL fileUrl = new URL(classFile);
                if ("file".equals(fileUrl.getProtocol())) {
                    final String filePaths = fileUrl.getPath();
                    final int delimPos = filePaths.indexOf('!');
                    String file;
                    if (delimPos > 0) {
                        file = filePaths.substring(0, delimPos);
                    } else {
                        file = filePaths;
                    }
                    if (file != null) {
                        final int pathSepPos = file.lastIndexOf('/');
                        if (pathSepPos > 0 && file.length() > pathSepPos + 1) {
                            file = file.substring(pathSepPos + 1);
                        }
                        if (file.startsWith("org.eclipse.persistence") && file.contains(Version.getVersion())) {
                            log.log(SessionLog.FINER, String.format(
                                    "FileBasedProjectCacheFilter: class %s from known jar file %s",
                                    serialClass.getName(), file));
                            return true;
                        }
                    }
                }
            } catch (MalformedURLException e) {
                log.log(SessionLog.WARNING, String.format(
                        "FileBasedProjectCacheFilter: could not find source .jar of class %s",
                        serialClass.getName()));
            }
        } else {
            log.log(SessionLog.WARNING, String.format(
                    "FileBasedProjectCacheFilter: could not find source .jar of class %s",
                    serialClass.getName()));
        }
        return false;
    }

    private final SessionLog log;

    // Dynamic 2nd level whitelist cache for Project class fields only.
    private final Set<Class<?>> depth2Whitelist;
    // Dynamic 3rd and lower level whitelist cache for JDK and EclipseLink classes only.
    private final Set<Class<?>> depth3Whitelist;

    /**
     * Creates an instance of ObjectInputStream data of FileBasedProjectCache verifier.
     */
    FileBasedProjectCacheFilter(SessionLog log) {
        this.log = log;
        //log.setLevel(SessionLog.FINEST);
        depth2Whitelist = new HashSet<>(DEPTH2_CLASSES.size() * 2);
        depth3Whitelist = new HashSet<>(DEPTH3_WHITELIST_SIZE);
    }

    // ObjectInputStream data verification entry point.
    // This method is being called for every object instance being deserialized.
    @Override
    public Status checkInput(FilterInfo info) {
        try {
            // Skip checks when decision was already made by higher level filter.
            ObjectInputFilter serialFilter = ObjectInputFilter.Config.getSerialFilter();
            if (serialFilter != null) {
                ObjectInputFilter.Status status = serialFilter.checkInput(info);
                if (status != ObjectInputFilter.Status.UNDECIDED) {
                    // The process-wide filter overrides this filter
                    return status;
                }
            }
            // Check current object instance.
            final Class<?> serialClass = info.serialClass();
            if (serialClass != null) {
                log.log(SessionLog.FINE, String.format(
                        "FileBasedProjectCacheFilter: processing level %d class %s",
                        info.depth(), serialClass.getName()));
                switch ((int) info.depth()) {
                    // Only Project class instance is allowed on top level of the structure
                    // and class must also come from  known .jar file name.
                    case 1:
                        if (serialClass != Project.class || !isEclipseLinkFileJar(serialClass, log)) {
                            log.log(SessionLog.WARNING, String.format(
                                    "FileBasedProjectCacheFilter: rejected illegal top level FileBasedProjectCache class: %s",
                                    serialClass.getName()));
                            return Status.REJECTED;
                        }
                        break;
                    // Only classes from Project class declared fields are allowed.
                    case 2:
                        if (notOnDepth2Whitelist(serialClass)) {
                            log.log(SessionLog.WARNING, String.format(
                                    "FileBasedProjectCacheFilter: rejected illegal 2nd level FileBasedProjectCache class: %s",
                                    serialClass.getName()));
                            return Status.REJECTED;
                        }
                        break;
                    default:
                        if (notOnDepth3Whitelist(serialClass)) {
                            log.log(SessionLog.WARNING, String.format(
                                    "FileBasedProjectCacheFilter: rejected illegal 3rd+ level FileBasedProjectCache class: %s",
                                    serialClass.getName()));
                            return Status.REJECTED;
                        }
                }
                // Remote interface must be rejected
                if (info.serialClass() != null && Remote.class.isAssignableFrom(info.serialClass())) {
                    log.log(SessionLog.WARNING, String.format("FileBasedProjectCacheFilter: rejected illegal Remote FileBasedProjectCache class: %s", serialClass.getName()));
                    return Status.REJECTED;
                }
            }
            return Status.UNDECIDED;
        } catch (Throwable t) {
            log.log(SessionLog.SEVERE, "Exception in FileBasedProjectCacheFilter check", t);
            throw t;
        }
    }

    private boolean notOnDepth2Whitelist(final Class<?> serialClass) {
        // Evaluate already cached classes.
        if (depth2Whitelist.contains(serialClass)) {
            log.log(SessionLog.FINER, String.format(
                    "FileBasedProjectCacheFilter: 2nd level class %s found in cache",
                    serialClass.getName()));
            return false;
        }
        // All 2nd level classes must be instances of Project class declared fields
        for (Class<?> c : DEPTH2_CLASSES) {
            if (c.isInstance(serialClass)) {
                final String className = serialClass.getCanonicalName();
                // Eclipselink own classes of Project class fields
                if (className.startsWith("org.eclipse.persistence.") && isEclipseLinkFileJar(serialClass, log)) {
                    log.log(SessionLog.FINER, String.format(
                            "FileBasedProjectCacheFilter: 2nd level EclipseLink class %s checked and added to cache",
                            className));
                    depth2Whitelist.add(serialClass);
                    addContainingClasses(depth2Whitelist, serialClass, log);
                    return false;
                // Java SE classes of Project class fields
                // FIXME: Some additional Java SE classes check may be useful here.
                } else if (className.startsWith("java.")) {
                    log.log(SessionLog.FINER, String.format(
                            "FileBasedProjectCacheFilter: 2nd level Java SE class %s checked and added to cache",
                            className));
                    depth2Whitelist.add(serialClass);
                    addContainingClasses(depth2Whitelist, serialClass, log);
                    return false;
                }
            }
        }
        log.log(SessionLog.FINER, String.format(
                "FileBasedProjectCacheFilter: 2nd level class %s check failed",
                serialClass.getName()));
        return true;
    }

    // Check whether provided file is not on FileBasedProjectCache 3nd+ level white list.
    private boolean notOnDepth3Whitelist(final Class<?> serialClass) {
        // Evaluate already cached classes.
        if (depth3Whitelist.contains(serialClass)) {
            log.log(SessionLog.FINER, String.format(
                    "FileBasedProjectCacheFilter: 3rd+ level class %s found in cache",
                    serialClass.getName()));
            return false;
        }
        // Check 2nd level cache to avoid duplicate verification of already known class.
        if (depth2Whitelist.contains(serialClass)) {
            log.log(SessionLog.FINER, String.format(
                    "FileBasedProjectCacheFilter: 3rd+ level class %s found in 2nd level cache",
                    serialClass.getName()));
            depth3Whitelist.add(serialClass);
            return false;
        }
        final String className = serialClass.getCanonicalName();
        // Eclipselink own classes of Project class fields
        if (className.startsWith("org.eclipse.persistence") && isEclipseLinkFileJar(serialClass, log)) {
            log.log(SessionLog.FINER, String.format(
                    "FileBasedProjectCacheFilter: 3rd+ level EclipseLink class %s checked and added to cache",
                    className));
            depth3Whitelist.add(serialClass);
            addContainingClasses(depth3Whitelist, serialClass, log);
            return false;
        // Java SE classes of Project class fields
        // FIXME: Some additional Java SE classes check may be useful here.
        } else if (className.startsWith("java.")) {
            log.log(SessionLog.FINER, String.format(
                    "FileBasedProjectCacheFilter: 3rd+ level Java SE class %s checked and added to cache",
                    className));
            depth3Whitelist.add(serialClass);
            addContainingClasses(depth3Whitelist, serialClass, log);
            return false;
        }
        log.log(SessionLog.FINER, String.format(
                "FileBasedProjectCacheFilter: 3rd+ level class %s check failed",
                serialClass.getName()));
        return true;
    }

}
