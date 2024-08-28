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

package com.ericsson.oss.presentation.cm.nbi.netconf.resources;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.itpf.sdk.eventbus.model.annotation.Modeled;
import com.ericsson.oss.itpf.sdk.recording.EventLevel;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestRepresentation;
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatusRepresentation;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.ConcurrentRequestsValidator;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.ErrorHandler;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.FileResource;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants;
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetworkElementValidator;
import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService;
import com.ericsson.oss.services.cm.nbi.netconf.ApplyNetconfPayloadEvent;
import com.ericsson.oss.services.cm.nbi.netconf.NetconfPayloadRequestResult;
import com.ericsson.oss.services.cm.nbi.netconf.NetconfPayloadRequestStatus;

public class NetconfPayloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfPayloadService.class);

    @Inject
    @Modeled
    private EventSender<ApplyNetconfPayloadEvent> netconfPayloadEventSender;

    @Inject
    NetconfPayloadPersistenceService persistentService;

    @Inject
    NetworkElementValidator networkElementValidator;

    @Inject
    ConcurrentRequestsValidator concurrentRequestsValidator;

    @Inject
    private FileResource fileResource;

    @Inject
    private ErrorHandler errorHandler;

    @Inject
    private SystemRecorder systemRecorder;

    private static final String NBI_REQUEST_URI = "nbi/cm/v1/request/";

    public Response processNetconfRequest(final String networkElementName, final String netconfPayload, final URI contextUri) {

        try {
            systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, NetconfPayloadConstants.EXECUTE_NETCONF_PAYLOAD, networkElementName, netconfPayload);

            concurrentRequestsValidator.validateNumberOfInprogressRequests();
            validateNetconfPayload(netconfPayload);
            networkElementValidator.validateNetworkElement(networkElementName);

            String requestId = UUID.randomUUID().toString();
            String netconfPayloadFileName = NetconfPayloadConstants.MSCMCE_FOLDER + requestId + "_request.txt";
            fileResource.writeToFile(netconfPayload, netconfPayloadFileName);
            Long netconfPayloadRequestPoId = persistentService.createCmNBiNetconfPayloadRequest(networkElementName, requestId);
            sendApplyNetconfPayloadEvent(networkElementName, netconfPayloadFileName, requestId, netconfPayloadRequestPoId);

            NetconfPayloadRequestRepresentation netconfPayloadRequest = new NetconfPayloadRequestRepresentation();
            final String statusResourceUrl = UriBuilder.fromUri(contextUri).path(NBI_REQUEST_URI + requestId).build().toString();
            netconfPayloadRequest = netconfPayloadRequest.build(statusResourceUrl);

            LOGGER.info("Successfully sent the Netconf Payload to execute on node : {} and use the url {} for querying status.", networkElementName, netconfPayloadRequest);

            systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, NetconfPayloadConstants.EXECUTE_NETCONF_PAYLOAD, networkElementName, statusResourceUrl);

            return Response.status(Response.Status.OK).entity(netconfPayloadRequest).build();

        } catch (Exception e) {
            LOGGER.error("Exception occured while processing Netconf Payload request for Network Element : {}", networkElementName, e);
            return errorHandler.sendErrorResponse(networkElementName, NetconfPayloadConstants.EXECUTE_NETCONF_PAYLOAD, e);
        }
    }

    private void validateNetconfPayload(final String netconfPayload) {
        if (netconfPayload == null || netconfPayload.trim().isEmpty())
            throw new IllegalArgumentException();
    }

    private void sendApplyNetconfPayloadEvent(final String networkElementName, final String netconfPayload, final String requestId, final Long netconfPayloadRequestPoId) {

        final ApplyNetconfPayloadEvent event = new ApplyNetconfPayloadEvent();
        event.setNodeAddress("NetworkElement=" + networkElementName);
        event.setProtocolInfo("CM");
        event.setCmNbiNetconfPayload(netconfPayload);
        event.setNetconfPayloadRequestId(requestId);
        event.setNetconfPayloadRequestPoId(netconfPayloadRequestPoId);
        LOGGER.info("ApplyNetconfPayloadEvent MTR is triggred for requestId : {}, network element : {}, netconfPayloadRequestPoId : {}", requestId, networkElementName, netconfPayloadRequestPoId);
        netconfPayloadEventSender.send(event);
    }

    public Response processStatusRequest(final String requestId) {
        NetconfPayloadRequestStatusRepresentation netconfRequestStatus = new NetconfPayloadRequestStatusRepresentation();
        systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, NetconfPayloadConstants.NETCONF_PAYLOAD_EXECUTION_STATUS, requestId,
                "Retrieving netconf payload execution status");
        try {
            Map<String, Object> poAttributeMap = persistentService.getNetconfPayloadRequestAttributes(requestId);
            LOGGER.debug("PO Attribute info : {} received for the requestId : {}", poAttributeMap, requestId);
            if (poAttributeMap != null && !poAttributeMap.isEmpty()) {
                final String status = (String) poAttributeMap.get(NetconfPayloadConstants.STATUS);
                final String result = (String) poAttributeMap.get(NetconfPayloadConstants.RESULT);
                netconfRequestStatus.setStatus(status != null ? com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatus.valueOf(status) : null);
                netconfRequestStatus.setResult(result != null ? com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestResult.valueOf(result) : null);
                if (NetconfPayloadRequestStatus.COMPLETED.toString().equals(status) && NetconfPayloadRequestResult.SUCCESS.toString().equals(result)) {
                    fillGetConfigResponses(netconfRequestStatus, (Map<String, Object>) poAttributeMap.get(NetconfPayloadConstants.ADDITIONAL_INFO));
                    return sendSuccessResponse(requestId, netconfRequestStatus);
                } else if (NetconfPayloadRequestStatus.COMPLETED.toString().equals(status) && NetconfPayloadRequestResult.FAILURE.toString().equals(result)) {
                    return processNetconfRequestFailureResponse(requestId, netconfRequestStatus, (Map<String, Object>) poAttributeMap.get(NetconfPayloadConstants.ADDITIONAL_INFO));
                }
                return sendSuccessResponse(requestId, netconfRequestStatus);
            } else {
                LOGGER.error("Job Status request with requestId: {} failed due to {}", requestId, NetconfPayloadConstants.REQUEST_ID_NOT_FOUND);
                return errorHandler.sendFailureResponse(requestId, netconfRequestStatus, NetconfPayloadConstants.REQUEST_ID_NOT_FOUND, NetconfPayloadConstants.REQUEST_ID_NOT_FOUND_TRY_AGAIN,
                        Response.Status.NOT_FOUND.getStatusCode());
            }
        } catch (Exception exception) {
            LOGGER.error("Job Status request with requestId: {} failed with exception : ", requestId, exception);
            return errorHandler.sendErrorResponse(requestId, NetconfPayloadConstants.NETCONF_PAYLOAD_EXECUTION_STATUS, exception);
        }
    }

    private void fillGetConfigResponses(final NetconfPayloadRequestStatusRepresentation netconfStatusResponse, final Map<String, Object> additionalInfo) throws FileNotFoundException {
        Object getConfigResponseFilePath = additionalInfo != null ? additionalInfo.get(NetconfPayloadConstants.FILE_PATH) : null;
        if (getConfigResponseFilePath != null) {
            netconfStatusResponse.setResultPayload(fileResource.getFileContentAsString(((String) getConfigResponseFilePath)));
        }
    }

    private Response processNetconfRequestFailureResponse(final String requestId, final NetconfPayloadRequestStatusRepresentation netconfStatusResponse, final Map<String, Object> additionalInfo) {
        final Integer errorCode = (Integer) additionalInfo.get(NetconfPayloadConstants.ERROR_CODE);
        final String errorTitle = (String) additionalInfo.get(NetconfPayloadConstants.ERROR_TITLE);
        final String errorDetails = (String) additionalInfo.get(NetconfPayloadConstants.ERROR_DETAILS);
        LOGGER.info("NetconfStatusResponse for failed request Id {} is errorCode: {}, errorTitle: {}, errorDetails: {}", requestId, errorCode, errorTitle, errorDetails);
        return errorHandler.sendFailureResponse(requestId, netconfStatusResponse, errorTitle, errorDetails, errorCode);
    }

    private Response sendSuccessResponse(final String requestId, final NetconfPayloadRequestStatusRepresentation netconfStatusResponse) {
        LOGGER.info("NetconfStatusResponse for request Id {} is {}", requestId, netconfStatusResponse);
        systemRecorder.recordEvent(NetconfPayloadConstants.CM_NBI_REST_NETCONF, EventLevel.DETAILED, NetconfPayloadConstants.NETCONF_PAYLOAD_EXECUTION_STATUS, requestId,
                netconfStatusResponse.toString());
        return Response.status(Response.Status.OK).entity(netconfStatusResponse).build();
    }

}
