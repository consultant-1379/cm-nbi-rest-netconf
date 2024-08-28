/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2021
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.presentation.cm.nbi.netconf.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetconfPayloadRequestRepresentation {

    @JsonProperty("_links")
    private StatusResoruceUrlRepresentation links;

    @JsonProperty("_links")
    public StatusResoruceUrlRepresentation getLinks() {
        return links;
    }

    public void setLinks(StatusResoruceUrlRepresentation links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "NetconfPayloadRequestRepresentation [links=" + links + "]";
    }

    public NetconfPayloadRequestRepresentation build(final String url) {
        HyperlinkRepresentation hyperlinkRepresentation = new HyperlinkRepresentation();
        hyperlinkRepresentation.setHref(url);

        StatusResoruceUrlRepresentation statusResoruceUrlRepresentation = new StatusResoruceUrlRepresentation();
        statusResoruceUrlRepresentation.setStatusResourceUrl(hyperlinkRepresentation);

        this.setLinks(statusResoruceUrlRepresentation);

        return this;
    }

}

