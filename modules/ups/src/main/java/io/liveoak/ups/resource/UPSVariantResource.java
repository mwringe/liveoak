package io.liveoak.ups.resource;

import io.liveoak.spi.resource.async.Resource;
import io.liveoak.ups.resource.config.UPSRootConfigResource;
import io.liveoak.ups.resource.config.UPSVariantConfig;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 */
public class UPSVariantResource implements Resource {

    String id;

    UPSVariantsResource parent;
    UPSVariantConfig variantConfig;

    public UPSVariantResource(UPSVariantsResource parent, UPSVariantConfig upsVariantConfig) {
        this.id = upsVariantConfig.getId();
        this.variantConfig = upsVariantConfig;
        this.parent = parent;
    }

    @Override
    public Resource parent() {
        return parent;
    }

    @Override
    public String id() {
        return id;
    }

    protected String getName() {
        return variantConfig.getName();
    }

    protected String getVariantId() {
        return variantConfig.getVariantId();
    }

    protected String getVariantSecret() {
        return variantConfig.getVariantSecret();
    }

    protected String getUPSURL() {
        return ((UPSRootConfigResource )variantConfig.parent()).getUPSServerURL();
    }

}
