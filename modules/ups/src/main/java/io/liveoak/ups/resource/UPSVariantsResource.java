package io.liveoak.ups.resource;

import io.liveoak.spi.RequestContext;
import io.liveoak.spi.resource.async.PropertySink;
import io.liveoak.spi.resource.async.Resource;
import io.liveoak.spi.resource.async.ResourceSink;
import io.liveoak.spi.resource.async.Responder;
import io.liveoak.spi.state.ResourceState;
import io.liveoak.ups.resource.config.UPSRootConfigResource;
import io.liveoak.ups.resource.config.UPSVariantConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 */
public class UPSVariantsResource implements Resource {

    public static final String ID = "variants";

    UPSRootResource parent;

    Map<String, UPSVariantResource> variants = new HashMap<>();

    UPSRootConfigResource configResource;


    public UPSVariantsResource(UPSRootResource parent, UPSRootConfigResource configResource) {
        this.parent = parent;
        this.configResource = configResource;
    }

    @Override
    public Resource parent() {
        return parent;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public void createMember(RequestContext ctx, ResourceState state, Responder responder) throws Exception {
        responder.createNotSupported( this );
    }

    public void readMembers(RequestContext ctx, ResourceSink sink) throws Exception {
        for (UPSVariantConfig variant: configResource.getVariants().values()) {
            if (variant.getGoogleProjectNumber() != null) {
                sink.accept(new UPSGCMVariantResource(this, variant ));
            }
        }
        sink.close();
    }

    public void readMember(RequestContext ctx, String id, Responder responder) throws Exception {
        UPSVariantConfig variant = configResource.getVariants().get( id );
        if (variant != null && variant.getGoogleProjectNumber() != null) {
            responder.resourceRead(new UPSGCMVariantResource( this, variant ));
        } else {
        responder.noSuchResource( id );
        }
    }


    @Override
    public void readProperties(RequestContext ctx, PropertySink sink) throws Exception {
        sink.close();
    }
}
