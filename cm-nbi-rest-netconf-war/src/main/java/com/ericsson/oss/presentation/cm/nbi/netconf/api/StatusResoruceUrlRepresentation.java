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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResoruceUrlRepresentation {
    private HyperlinkRepresentation statusResourceUrl;

    public HyperlinkRepresentation getStatusResourceUrl() {
	return statusResourceUrl;
    }

    public void setStatusResourceUrl(HyperlinkRepresentation statusResourceUrl) {
	this.statusResourceUrl = statusResourceUrl;
    }

    @Override
    public String toString() {
	return "StatusResoruceUrlRepresentation [statusResourceUrl=" + statusResourceUrl + "]";
    }

}
