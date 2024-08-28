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

public enum NetconfPayloadRequestStatus {
    INITIATED("INITIATED"), PARSING("PARSING"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED");

    private String status;

    NetconfPayloadRequestStatus(String status) {
	this.status = status;
    }

    public String getStatus() {
	return status;
    }

}
