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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.SecurityTarget;

@Path("/nbi/cm/")
public class NetconfPayloadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetconfPayloadResource.class);
    public static final String CM_NBI_REST_NETCONF_SERVICE = "cm_nbi_rest_netconf";
    public static final String READ_ACTION = "read";
    public static final String EXECUTE_ACTION = "execute";

    @Inject
    private NetconfPayloadService netconfPayloadService;

    @Inject
    NetconfPayloadPersistenceService persistentService;

    @Context
    UriInfo uriInfo;

    @Authorize(resource=CM_NBI_REST_NETCONF_SERVICE,action=EXECUTE_ACTION)
    @PATCH
    @Path("v1/networkelement/{network-element-name}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response executeNetconfRequest(@SecurityTarget final @PathParam(value = "network-element-name") String networkElementName, final String netconfPayload) {
        LOGGER.info("Received Netconf Payload Request for Network Element {} with payload : {}", networkElementName, netconfPayload);
        return netconfPayloadService.processNetconfRequest(networkElementName, netconfPayload, uriInfo.getBaseUri());
    }

    @Authorize(resource=CM_NBI_REST_NETCONF_SERVICE,action=READ_ACTION)
    @GET
    @Path("v1/request/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNetconfRequestStatus(final @PathParam(value = "requestId") String requestId) {
        LOGGER.info("Received Netconf Payload Status Request for requestId {}", requestId);
        return netconfPayloadService.processStatusRequest(requestId);
    }

}
