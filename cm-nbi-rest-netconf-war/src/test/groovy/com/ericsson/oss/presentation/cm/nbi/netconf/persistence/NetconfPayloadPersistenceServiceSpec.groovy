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
package com.ericsson.oss.presentation.cm.nbi.netconf.persistence

import org.joda.time.DateTime

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps

import spock.lang.Unroll

public class NetconfPayloadPersistenceServiceSpec extends CdiSpecification {

    @ObjectUnderTest
    private NetconfPayloadPersistenceService netconfPayloadPersistenceService;

    protected RuntimeConfigurableDps runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps)

    private String requestId = "requestId";

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
                break;
            case 2:
                cmNbiNetconfRequestPO.put("status", "COMPLETED")
                cmNbiNetconfRequestPO.put("result", "FAILURE")
                break;
            case 3:
                cmNbiNetconfRequestPO.put("status", "COMPLETED")
                cmNbiNetconfRequestPO.put("result", "SUCCESS")
                break;
            default:
                cmNbiNetconfRequestPO.put("status", "IN_PROGRESS")
                cmNbiNetconfRequestPO.put("creationTime", new DateTime().minusHours(2).toDate());
                break;
        }
        return cmNbiNetconfRequestPO
    }

    @Unroll
    def "Test Case to check housekeeping fetches POs "() {
        given: "List of CmNbiNetconfPayloadRequest POs"
        prepareCmNbiNetconfRequestPO()

        when: "Housekeeping is trggered to delete CmNbiNetconfPayloadRequest POs"
        Iterator<PersistenceObject> resultSet = netconfPayloadPersistenceService.getAllNetconfPayloadRequestPOs()

        then: "Check if all POs are retrieved"
        resultSet.size() == 4
    }

    @Unroll
    def "Test Case to check housekeeping deletes OLD POs"() {
        given: "List of CmNbiNetconfPayloadRequest POs"
        prepareCmNbiNetconfRequestPO()
        Iterator<PersistenceObject> resultSet = netconfPayloadPersistenceService.getAllNetconfPayloadRequestPOs()

        when: "Housekeeping is trggered to delete CmNbiNetconfPayloadRequest POs"
        netconfPayloadPersistenceService.deletePO()

        then: "Check if delete POs called"
        noExceptionThrown()
    }

    @Unroll
    def "Test Case to check housekeeping doesnot deletes POs"() {
        given: "List of CmNbiNetconfPayloadRequest POs"
        prepareCmNbiNetconfRequestPO()
        Iterator<PersistenceObject> resultSet = netconfPayloadPersistenceService.getAllNetconfPayloadRequestPOs()

        when: "Housekeeping is trggered to delete CmNbiNetconfPayloadRequest POs"
        netconfPayloadPersistenceService.deletePO()

        then: "Check if delete POs called"
        noExceptionThrown()
    }
}
