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

public enum NetconfPayloadRequestResult {
    SUCCESS("SUCCESS"), FAILURE("FAILURE");

    private String result;

    NetconfPayloadRequestResult(String result) {
	this.result = result;
    }

    public String getResult() {
	return result;
    }

}
