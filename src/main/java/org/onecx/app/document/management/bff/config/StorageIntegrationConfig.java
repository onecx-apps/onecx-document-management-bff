package org.onecx.app.document.management.bff.config;

import io.quarkus.runtime.annotations.ConfigDocFilename;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Configuration for identifying uploaded files in the external storage service
 */
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "onecx.document.file-storage")
@ConfigDocFilename("onecx-document-management-bff-docs.adoc")
public interface StorageIntegrationConfig {

    /**
     * Name of the product that uploaded file should be identified by
     */
    @WithDefault("onecx-document-management")
    String productName();

    /**
     * Id of the application that uploaded file should be identified by
     */
    @WithDefault("onecx-document-management-bff")
    String applicationId();

    /**
     * Separator used for concatenating name of the file combined with attachment id and attachment file name
     */
    @WithDefault("_")
    String fileNameSeparator();
}
