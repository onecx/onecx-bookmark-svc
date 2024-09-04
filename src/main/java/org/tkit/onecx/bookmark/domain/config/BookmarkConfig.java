package org.tkit.onecx.bookmark.domain.config;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigDocFilename("onecx-bookmark-svc.adoc")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "onecx.bookmark")
public interface BookmarkConfig {

    /**
     * Enable or disable default bookmark
     */
    @WithName("default.enabled")
    @WithDefault("false")
    boolean defaultBookmarkEnabled();

    /**
     * Default bookmark URL
     */
    @WithName("default.url")
    @WithDefault("https://github.com/onecx")
    String defaultBookmarkUrl();

    /**
     * Default bookmark URL
     */
    @WithName("product-item-id")
    @WithDefault("PRODUCT_BASE_DOC_URL")
    String productItemId();
}
