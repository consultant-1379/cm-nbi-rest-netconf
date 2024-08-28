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
package com.ericsson.oss.presentation.cm.nbi.netconf.housekeeping

import java.util.Map

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils


import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject
import com.ericsson.oss.presentation.cm.nbi.netconf.common.FileResource
import com.ericsson.oss.presentation.cm.nbi.netconf.common.NetconfPayloadConstants
import com.ericsson.oss.presentation.cm.nbi.netconf.persistence.NetconfPayloadPersistenceService
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.time.*;



import spock.lang.Unroll

public class NetconfPayloadRequestHousekeepingServiceSpec extends CdiSpecification {

    @ObjectUnderTest
    private NetconfPayloadRequestHousekeepingService netconfPayloadRequestHousekeepingService;

    protected RuntimeConfigurableDps runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps)

    @MockedImplementation
    private FileResource fileResource;

    private String requestId = "requestId";

    def setup(){
        DateTimeUtils.setCurrentMillisOffset(172800000);
    }

    def prepareCmNbiNetconfRequestPO(){
        PersistenceObject cmNbiNetconfRequestPO1 = runtimeDps.addPersistenceObject()
                .namespace("OSS_CM_NBI")
                .type("CmNbiNetconfPayloadRequest")
                .addAttributes(prepareCmNbiNetconfRequestPOAttributes(requestId, 1))
                .create()
        PersistenceObject cmNbiNetconfRequestPO2 = runtimeDps.addPersistenceObject()
                .namespace("OSS_CM_NBI")
                .type("CmNbiNetconfPayloadRequest")
                .addAttributes(prepareCmNbiNetconfRequestPOAttributes(requestId, 2))
                .create()
        PersistenceObject cmNbiNetconfRequestPO3 = runtimeDps.addPersistenceObject()
                .namespace("OSS_CM_NBI")
                .type("CmNbiNetconfPayloadRequest")
                .addAttributes(prepareCmNbiNetconfRequestPOAttributes(requestId, 3))
                .create()
        PersistenceObject cmNbiNetconfRequestPO4 = runtimeDps.addPersistenceObject()
                .namespace("OSS_CM_NBI")
                .type("CmNbiNetconfPayloadRequest")
                .addAttributes(prepareCmNbiNetconfRequestPOAttributes(requestId, 4))
                .create()
    }

    def Map<String, Object> prepareCmNbiNetconfRequestPOAttributes(String requestId, int condition){
        final Map<String, Object> cmNbiNetconfRequestPO = new HashMap<>()
        cmNbiNetconfRequestPO.put("requestId", requestId)
        cmNbiNetconfRequestPO.put("nodeAddress", "9875")
        switch(condition) {
            case 1:
                cmNbiNetconfRequestPO.put("status", "COMPLETED")
                cmNbiNetconfRequestPO.put("result", "SUCCESS")
                cmNbiNetconfRequestPO.put("additionalInfo", prepareSuccessAdditionalInfo())
                break;
            case 2:
                cmNbiNetconfRequestPO.put("status", "COMPLETED")
                cmNbiNetconfRequestPO.put("result", "FAILURE")
                break;
            case 3:
                cmNbiNetconfRequestPO.put("status", "COMPLETED")
                cmNbiNetconfRequestPO.put("result", "SUCCESS")
                cmNbiNetconfRequestPO.put("additionalInfo", prepareSuccessAdditionalInfo())
                break;
            default:
                cmNbiNetconfRequestPO.put("status", "IN_PROGRESS")
                cmNbiNetconfRequestPO.put("creationTime", new DateTime().minusHours(2).toDate());
                break;
        }
        return cmNbiNetconfRequestPO
    }

    def Map<String, Object> prepareSuccessAdditionalInfo(){
        final Map<String, Object> additionalInfo = new HashMap<>()
        additionalInfo.put("filePath", "text.xml")
        return additionalInfo
    }

    @Unroll
    def "Test Case to housekeepingCmNbiNetconfRequestPOs"() {
        given: "List of CmNbiNetconfPayloadRequest POs"
        Instant.now(Clock.fixed(Instant.parse("2018-08-22T10:00:00Z"),ZoneOffset.UTC))
        setup()
        prepareCmNbiNetconfRequestPO()
        fileResource.deleteFile("text.xml")>>true;

        when: "Housekeeping is trggered to delete CmNbiNetconfPayloadRequest POs"
        netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs()

        then: "Check if only 24Hrs old POs only got deleted"
        noExceptionThrown()
        2*fileResource.deleteFile(_)
    }

    @Unroll
    def "verify housekeepingCmNbiNetconfRequestPOs if no requests exist in DPS"() {
        given: "List of CmNbiNetconfPayloadRequest POs"
        Instant.now(Clock.fixed(Instant.parse("2018-08-22T10:00:00Z"),ZoneOffset.UTC))
        setup()
        fileResource.deleteFile("text.xml")>>true;

        when: "Housekeeping is trggered to delete CmNbiNetconfPayloadRequest POs"
        netconfPayloadRequestHousekeepingService.housekeepingCmNbiNetconfRequestPOs()

        then: "Check if flow executes smoothly with out any exception"
        noExceptionThrown()
    }
}
