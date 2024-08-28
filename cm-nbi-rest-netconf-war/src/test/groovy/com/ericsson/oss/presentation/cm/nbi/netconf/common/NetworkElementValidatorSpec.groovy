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
package com.ericsson.oss.presentation.cm.nbi.netconf.common

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.modeling.modelservice.ModelService
import com.ericsson.oss.itpf.modeling.modelservice.typed.TypedModelAccess
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetconfNotSupportedException
import com.ericsson.oss.presentation.cm.nbi.netconf.exceptions.NetworkElementNotFoundException

class NetworkElementValidatorSpec  extends CdiSpecification {

    @MockedImplementation
    ModelService modelService

    @MockedImplementation
    TypedModelAccess modelAccess

    @MockedImplementation
    TargetTypeInformation targetTypeInformation

    @ObjectUnderTest
    private NetworkElementValidator netconfPayloadNodeValidator

    protected RuntimeConfigurableDps runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps)

    def PersistenceObject addPo(networkElement, platformType, category, type){
        PersistenceObject targetPO   = runtimeDps.addPersistenceObject()
                .namespace("OSS_CM_NBI")
                .type("CmNbiNetconfPayloadRequest")
                .create()

        PersistenceObject managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement="+networkElement)
                .addAttribute('NetworkElement', networkElement)
                .addAttribute('platformType', platformType)
                .addAttribute("ossModelIdentity","nRatModelId")
                .addAttribute('category', category)
                .addAttribute('type', type)
                .target(targetPO)
                .build()
        return managedObject
    }


    def 'Node is available, NE support is decided by platform type(ECIM)'(){
        given : "Node and Platform Type "
        addPo("NR01", "ECIM", null, null)
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        flag == true
    }

    def 'Node is available, NE support is decided by platform type(CPP))'(){
        given : "Node and wrong Platform type "
        addPo("NR01", "CPP", null, null)
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        thrown(NetconfNotSupportedException)
    }

    def 'Node is available, NE support is decided by platform type(CBP-OI)'(){
        given : "Node and platform type "
        addPo("NR01", "CBP-OI", null, null)
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        flag == true
    }

    def 'Node is available, platform type is null'(){
        given : "Node, target category and target type "
        addPo("NR01", null, "NOD", "RadioNode")
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        thrown(Exception)
    }

    def 'Node is available and NE support is decided by platform type(IPOS-OI)'(){
        given : "Node and its platform type "
        addPo("NR01", "IPOS-OI", null, null)
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        flag == true
    }

    def 'Node is not available in dps, Exception is thrown'(){
        given : "Both node and platform type are not given"
        addPo(null, null, null, null)
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        thrown(NetworkElementNotFoundException)
    }

    def 'Node is available, platform is null and NE support is decided by ComConnectivityInformation'(){
        given : "Node, target category and target type "
        addPo("NR01", null, "NODE", "RadioNode")
        modelService.getTypedAccess() >> modelAccess
        modelAccess.getModelInformation(TargetTypeInformation.class) >> targetTypeInformation
        targetTypeInformation.getConnectivityInfoMoType(_, _) >> "ComConnectivityInformation"
        when: "rest call is triggered with"
        boolean flag = netconfPayloadNodeValidator.validateNetworkElement("NR01")
        then: "Verify the response"
        flag == true
    }
}
