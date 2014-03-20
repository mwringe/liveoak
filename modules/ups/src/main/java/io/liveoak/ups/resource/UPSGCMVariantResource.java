package io.liveoak.ups.resource;

import io.liveoak.spi.RequestContext;
import io.liveoak.spi.resource.async.PropertySink;
import io.liveoak.spi.resource.async.Responder;
import io.liveoak.spi.state.ResourceState;
import io.liveoak.ups.resource.config.UPSVariantConfig;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 */
public class UPSGCMVariantResource extends UPSVariantResource {

    final String googleProjectNumber;

    public UPSGCMVariantResource( UPSVariantsResource parent, UPSVariantConfig upsVariantConfig ) {
        super( parent, upsVariantConfig);
        googleProjectNumber = upsVariantConfig.getGoogleProjectNumber();
    }


    @Override
    public void createMember(RequestContext ctx, ResourceState state, Responder responder) throws Exception {
        responder.createNotSupported( this );
    }

    @Override
    public void readProperties(RequestContext ctx, PropertySink sink) throws Exception {
        sink.accept(UPSVariantConfig.NAME, getName());
        sink.accept("ups-url", getUPSURL());
        sink.accept(UPSVariantConfig.VARIANT_ID, getVariantId());
        sink.accept(UPSVariantConfig.VARIANT_SECRET, getVariantSecret());
        sink.accept(UPSVariantConfig.GOOGLE_PROJECT_NUMBER, googleProjectNumber);
        sink.close();
    }
}
