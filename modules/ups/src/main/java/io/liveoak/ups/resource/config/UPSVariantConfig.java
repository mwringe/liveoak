package io.liveoak.ups.resource.config;

import io.liveoak.spi.RequestContext;
import io.liveoak.spi.resource.async.PropertySink;
import io.liveoak.spi.resource.async.Resource;
import io.liveoak.spi.state.ResourceState;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 */
public class UPSVariantConfig implements Resource {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String VARIANT_ID = "variant-id";
    public static final String VARIANT_SECRET = "variant-secret";
    public static final String GOOGLE_PROJECT_NUMBER = "google-project-number";

    protected ResourceState resourceState;

    protected UPSRootConfigResource parent;

    public UPSVariantConfig(UPSRootConfigResource parent, ResourceState resourceState) {
        this.resourceState = resourceState;
        this.parent = parent;
    }

    public String getId() {
        return (String) resourceState.getProperty( ID );
    }

    public String getName() {
        return (String) resourceState.getProperty( NAME );
    }

    public String getVariantId() {
        return (String) resourceState.getProperty( VARIANT_ID );
    }

    public String getVariantSecret() {
        return (String) resourceState.getProperty( VARIANT_SECRET );
    }

    public String getGoogleProjectNumber() {
        return (String) resourceState.getProperty( GOOGLE_PROJECT_NUMBER );
    }

    @Override
    public Resource parent() {
        return this.parent;
        //return null;
    }

    @Override
    public String id() {
        //return getId();
        return null;
    }

    @Override
    public void readProperties(RequestContext ctx, PropertySink sink) throws Exception {
        sink.accept( NAME, getName() );
        sink.accept( VARIANT_ID, getVariantId() );
        sink.accept(VARIANT_SECRET, getVariantSecret());
        sink.accept(GOOGLE_PROJECT_NUMBER, getGoogleProjectNumber());
        sink.close();
    }
}
