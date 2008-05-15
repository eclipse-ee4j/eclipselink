package org.eclipse.persistence.jpa.osgi;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class Activator implements BundleActivator, SynchronousBundleListener {
    private static BundleContext context;
    
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
        case BundleEvent.STARTING:
            registerBundle(event.getBundle());
            break;
    
            case BundleEvent.STOPPING:
              System.out.println("Bundle stopping: " + event.getBundle());
                deregisterBundle(event.getBundle());
                break;
        }
    }

    private void printEventName(BundleEvent event) {
        switch (event.getType()) {
        case BundleEvent.INSTALLED:
            System.out.print("INSTALLED");
            break;
        case BundleEvent.LAZY_ACTIVATION:
            System.out.print("LAZY_ACTIVATION");
            break;
        case BundleEvent.RESOLVED:
            System.out.print("RESOLVED");
            break;
        case BundleEvent.STARTED:
            System.out.print("STARTED");
            break;
        case BundleEvent.STARTING:
            System.out.print("STARTING");
            break;
        case BundleEvent.STOPPED:
            System.out.print("STOPPED");
            break;
        case BundleEvent.STOPPING:
            System.out.print("STOPPING");
            break;
        case BundleEvent.UNINSTALLED:
            System.out.print("UNINSTALLED");
            break;
        case BundleEvent.UNRESOLVED:
            System.out.print("UNRESOLVED");
            break;
        case BundleEvent.UPDATED:
            System.out.print("UPDATED");
            break;
        }
    }

    public void start(BundleContext context) throws Exception {
      System.out.println("Starting EclipseLink bundle...");
        Activator.context = context;
        registerBundleListener();
        registerProviderService();
    }

    private void registerBundleListener() {
        getContext().addBundleListener(this);
        Bundle bundles[] = getContext().getBundles();
        for (int i = 0; i < bundles.length; i++) {
            Bundle bundle = bundles[i];
            registerBundle(bundle);
        }
    }

    private void registerBundle(Bundle bundle) {
        if ((bundle.getState() & (Bundle.STARTING | Bundle.ACTIVE)) != 0) {
            try {
                String[] persistenceUnitNames = getPersistenceUnitNames(bundle);
                if (persistenceUnitNames != null) {
                    PersistenceProvider.addBundle(bundle, persistenceUnitNames);
                }
            } catch (Exception e) {
                System.out.println("Exception caught processing bundle: " + e);
                e.printStackTrace();
            }
        }
    }
    
    private String[] getPersistenceUnitNames(Bundle bundle) {
        String names = (String) bundle.getHeaders().get("JPA-PersistenceUnits");
        if (names != null) {
            return names.split(",");
        } else {
            return null;
        }
    }
    
    private void deregisterBundle(Bundle bundle) {
        org.eclipse.persistence.jpa.osgi.PersistenceProvider.removeBundle(bundle);
    }

    public void stop(BundleContext context) throws Exception {
        getContext().removeBundleListener(this);    
    }

    public static BundleContext getContext() {
        return Activator.context;
    }
    
    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";
    public static final String ECLIPSELINK_OSGI_PROVIDER = "org.eclipse.persistence.jpa.osgi.PersistenceProviderOSGi";
    
    public void registerProviderService() throws Exception {
        // Create and register ourselves as a JPA persistence provider service
        PersistenceProvider providerService = new PersistenceProvider();
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(PERSISTENCE_PROVIDER, ECLIPSELINK_OSGI_PROVIDER);
        getContext().registerService(PERSISTENCE_PROVIDER, providerService, props);
    }
}

