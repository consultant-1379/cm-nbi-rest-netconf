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

package com.ericsson.oss.presentation.cm.nbi.netconf.common;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.Status.TOO_MANY_REQUESTS;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatusRepresentation;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetconfNotSupportedException;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetworkElementNotFoundException;
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.TooManyNetconfPayloadRequestsException;

public class ErrorHandler {

    @Inject
    private SystemRecorder systemRecorder;

    public Response sendErrorResponse(final String requestId, String sourceEndPoint, final Exception e) {
        NetconfPayloadRequestStatusRepresentation netconfPayloadRequestRepresentation = new NetconfPayloadRequestStatusRepresentation();

        Status status = INTERNAL_SERVER_ERROR;

        netconfPayloadRequestRepresentation.setErrorTitle(NetconfPayloadConstants.INTERNAL_SERVER_ERROR);
        netconfPayloadRequestRepresentation.setErrorBody(e.getMessage() + NetconfPayloadConstants.INTERNAL_SERVER_ERROR_SOLUTION);

        if (e instanceof IllegalArgumentException) {
            netconfPayloadRequestRepresentation.setErrorTitle("Invalid Netconf Payload");
            netconfPayloadRequestRepresentation.setErrorBody("Netconf Payload provided is invalid, please verify.");
            status = BAD_REQUEST;
        }

        if (e instanceof NetworkElementNotFoundException) {
            netconfPayloadRequestRepresentation.setErrorTitle("Network Element not found");
            netconfPayloadRequestRepresentation.setErrorBody("Please check if network element is managed by ENM");
            status = NOT_FOUND;
        }

        if (e instanceof NetconfNotSupportedException) {
            netconfPayloadRequestRepresentation.setErrorTitle("Unsupported Network Element");
            netconfPayloadRequestRepresentation.setErrorBody("Network Element doesn't support CM Netconf Payload REST NBI feature");
            status = PRECONDITION_FAILED;
        }

        if (e instanceof TooManyNetconfPayloadRequestsException) {
            netconfPayloadRequestRepresentation.setErrorTitle("Too Many Requests");
            netconfPayloadRequestRepresentation.setErrorBody(e.getMessage());
            status = TOO_MANY_REQUESTS;
        }
        systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, sourceEndPoint, requestId,
                netconfPayloadRequestRepresentation.toString());
        return Response.status(status).entity(netconfPayloadRequestRepresentation).build();
    }

    public Response sendFailureResponse(final String requestId, final NetconfPayloadRequestStatusRepresentation netconfStatusResponse, final String errorTitle, final String errorBody,
            final Integer errorCode) {
        netconfStatusResponse.setErrorTitle(errorTitle);
        netconfStatusResponse.setErrorBody(errorBody);
        systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, NetconfPayloadConstants.NETCONF_PAYLOAD_EXECUTION_STATUS, requestId,
                netconfStatusResponse.toString());
        return Response.status(errorCode).entity(netconfStatusResponse).build();
    }
}
