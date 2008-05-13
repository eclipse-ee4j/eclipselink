package javax.persistence.osgi;

// import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import javax.persistence.Persistence;
import javax.persistence.Persistence.ProviderResolver;
import javax.persistence.spi.PersistenceProvider;

/**
 * Uses OSGi services to discover JPA persistence providers
 *
 * Also acts as a resolver class (called by the Persistence 
 * class when resolving providers).
 */
public class Activator implements BundleActivator, ProviderResolver {

    public static final String PERSISTENCE_PROVIDER = "javax.persistence.spi.PersistenceProvider";

	private BundleContext ctx;
	private ServiceTracker serviceTracker;
	
	//-------------------------------------------------------------
	// Bundle life cycle method
	//-------------------------------------------------------------
	public void start(BundleContext context) throws Exception {
		log("Persistence bundle starting...");
		
		// Init the bundle context
		this.ctx = context;
		
		// Set the persistence provider resolver to use OSGi services 
		Persistence.setProviderResolver(this);

		// Set up a tracker to add providers as they register themselves
		ServiceTrackerCustomizer customizer = new ProviderTrackerCustomizer(ctx);
		serviceTracker = new ServiceTracker(ctx, PERSISTENCE_PROVIDER, customizer);
		serviceTracker.open();

		log("Persistence bundle started.");
	}
	
	//-------------------------------------------------------------
	// Bundle life cycle method
	//-------------------------------------------------------------
	public void stop(BundleContext context) throws Exception {
		log("Persistence bundle stopping...");
		
		// Close the service tracker
		serviceTracker.close();
		serviceTracker = null;
		
		// Nil out the known provider list
		Persistence.resetProviders();

		log("Persistence bundle stopped.");
	}

	//-------------------------------------------------------------
	// Resolve/return existing providers 
	// (Implementation method for the ProviderResolver interface.)
	//-------------------------------------------------------------
	public Collection<PersistenceProvider> findAllProviders() throws IOException {
		log("OSGi - Find all providers.");

		Collection<PersistenceProvider> providers = new HashSet<PersistenceProvider>();
		
		// Provider bundles should require this bundle to be started before 
		// they start, so it is very unlikely that provider bundles will have 
		// already loaded and registered but we'll do this just in case... 

		ServiceReference[] refs = null;
		try { refs = ctx.getServiceReferences(PERSISTENCE_PROVIDER, null); }
		catch (InvalidSyntaxException invEx) {} // Can't happen since filter is null
		
		for (ServiceReference ref : refs) {
			providers.add((PersistenceProvider)ctx.getService(ref));
		}

		// Newly started providers should dynamically add themselves to the 
		// list of providers using OSGi service registration facilities
		return providers;
    }

    private void log(String message) {
    	System.out.println(message);
    }
}
