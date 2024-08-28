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
package com.ericsson.oss.presentation.cm.nbi.netconf.exceptions

import javax.ws.rs.core.Response

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.SecurityViolationExceptionMapper
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatusRepresentation

class SecurityViolationExceptionMapperTest extends CdiSpecification {

    @MockedImplementation
    NetconfPayloadRequestStatusRepresentation netconfPayloadRequestRepresentation

    @ObjectUnderTest
    SecurityViolationExceptionMapper securityViolationExceptionMapper

    def "SecurityViolationException is handled in the mapper"() {
        given: "SecurityViolationException is passed"
            def secRequestExcep = new SecurityViolationException("junit")
        when: 'SecurityViolationException is to setResponse object'
            Response response = securityViolationExceptionMapper.toResponse(secRequestExcep)
        then: 'Assert Response'
            assert response.getStatus() == 401
    }
}
