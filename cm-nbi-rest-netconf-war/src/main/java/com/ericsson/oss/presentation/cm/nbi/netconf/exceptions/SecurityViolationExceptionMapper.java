/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.presentation.cm.nbi.netconf.exceptions;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;


import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatusRepresentation;


@Provider
public class SecurityViolationExceptionMapper implements ExceptionMapper<SecurityViolationException> {

    @Override
    public Response toResponse(final SecurityViolationException exception) {
        NetconfPayloadRequestStatusRepresentation netconfPayloadRequestRepresentation = new NetconfPayloadRequestStatusRepresentation();
        netconfPayloadRequestRepresentation.setErrorTitle("Unauthorized user");
        netconfPayloadRequestRepresentation.setErrorBody("User is not authorized to use the functionality");

        return Response.status(UNAUTHORIZED).entity(netconfPayloadRequestRepresentation).build();

    }
}
