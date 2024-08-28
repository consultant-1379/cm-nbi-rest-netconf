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
public class NetconfPayloadRequestStatusRepresentation {

    private NetconfPayloadRequestResult result;
    private NetconfPayloadRequestStatus status;
    private String resultPayload;
    private String errorTitle;
    private String errorBody;

    public NetconfPayloadRequestResult getResult() {
	return result;
    }

    public void setResult(NetconfPayloadRequestResult result) {
	this.result = result;
    }

    public NetconfPayloadRequestStatus getStatus() {
	return status;
    }

    public void setStatus(NetconfPayloadRequestStatus status) {
	this.status = status;
    }

    public String getResultPayload() {
	return resultPayload;
    }

    public void setResultPayload(String resultPayload) {
	this.resultPayload = resultPayload;
    }

    public String getErrorTitle() {
	return errorTitle;
    }

    public void setErrorTitle(String errorTitle) {
	this.errorTitle = errorTitle;
    }

    public String getErrorBody() {
	return errorBody;
    }

    public void setErrorBody(String errorBody) {
	this.errorBody = errorBody;
    }

    @Override
    public String toString() {
	return "NetconfPayloadRequestStatusRepresentation [result=" + result + ", status=" + status + ", resultPayload=" + resultPayload + ", errorTitle=" + errorTitle + ", errorBody=" + errorBody
		+ "]";
    }

}
