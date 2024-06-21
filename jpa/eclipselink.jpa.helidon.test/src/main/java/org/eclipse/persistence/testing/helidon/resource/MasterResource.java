package org.eclipse.persistence.testing.helidon.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.persistence.testing.helidon.provider.MasterProvider;

@Path("/master")
public class MasterResource {

    @Inject
    MasterProvider masterProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void masters() {
//        masterProvider.
    }

}
