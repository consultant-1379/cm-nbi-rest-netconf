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
package com.ericsson.oss.presentation.cm.nbi.netconf.resources

import java.nio.file.Paths

import javax.inject.Inject
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo
import javax.ws.rs.core.Response.Status

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.itpf.sdk.resources.Resource
import com.ericsson.oss.itpf.sdk.resources.Resources
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager
import com.ericsson.oss.itpf.sdk.resources.FileResourceImpl
import com.ericsson.oss.presentation.cm.nbi.netconf.api.HyperlinkRepresentation
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestResult
import com.ericsson.oss.presentation.cm.nbi.netconf.api.NetconfPayloadRequestStatus
import com.ericsson.oss.presentation.cm.nbi.netconf.api.StatusResoruceUrlRepresentation
import com.ericsson.oss.presentation.cm.nbi.netconf.common.DeploymentTypeRetriever
import com.ericsson.oss.presentation.cm.nbi.netconf.common.FileResource

import spock.lang.Unroll

import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.powermock.modules.junit4.PowerMockRunner
import org.spockframework.runtime.Sputnik

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([Resources.class])
class NetconfPayloadResourceSpec  extends CdiSpecification {

    @ObjectUnderTest
    private NetconfPayloadResource netconfPayloadResource

    @MockedImplementation
    UriInfo uriInfo;

    @MockedImplementation
    private DeploymentTypeRetriever deploymentTypeRetriever;

    @MockedImplementation
    Resource resource

    @MockedImplementation
    LockManager lockManager

    @MockedImplementation
    java.util.concurrent.locks.Lock lock
    
    @MockedImplementation
    FileResource fileResource

    protected RuntimeConfigurableDps runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps)

    def PersistenceObject prepareCmNbiNetconfRequestPO(requestId, condition){
	PersistenceObject cmNbiNetconfRequestPO = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.addAttributes(prepareCmNbiNetconfRequestPOAttributes(requestId, condition))
		.create()
	return cmNbiNetconfRequestPO
    }

    def PersistenceObject prepareMulipleCmNbiNetconfRequestPOs(networkElement, platformType, category, type){
	PersistenceObject cmNbiNetconfRequestPO1 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO2 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO3 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject targetPO   = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement="+networkElement)
		.addAttribute('NetworkElement', networkElement)
		.addAttribute('platformType', platformType)
		.addAttribute("ossModelIdentity","nRatModelId")
		.addAttribute('version', "1.0.0")
		.addAttribute('category', category)
		.addAttribute('type', type)
		.target(targetPO)
		.build()
	return managedObject
    }

    def PersistenceObject prepareMulipleCmNbiNetconfRequestPOsForLargeENM(networkElement, platformType, category, type){
	PersistenceObject cmNbiNetconfRequestPO1 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO2 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO3 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO4 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO5 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO6 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO7 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO8 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO9 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO10 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO11 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO12 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO13 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO14 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO15 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO16 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO17 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO18 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO19 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO20 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject targetPO   = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement="+networkElement)
		.addAttribute('NetworkElement', networkElement)
		.addAttribute('platformType', platformType)
		.addAttribute("ossModelIdentity","nRatModelId")
		.addAttribute('version', "1.0.0")
		.addAttribute('category', category)
		.addAttribute('type', type)
		.target(targetPO)
		.build()
	return managedObject
    }

    def PersistenceObject prepare20CmNbiNetconfRequestPOsWithOneCompletedStatusForLargeENM(networkElement, platformType, category, type){
	PersistenceObject cmNbiNetconfRequestPO1 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.addAttributes(prepareCmNbiNetconfRequestPOAttributes("SUCCESS", 1))
		.create()
	PersistenceObject cmNbiNetconfRequestPO2 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO3 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO4 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO5 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO6 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO7 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO8 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO9 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO10 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO11 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO12 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO13 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO14 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO15 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO16 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO17 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO18 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject cmNbiNetconfRequestPO19 = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()

	PersistenceObject targetPO   = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()
	PersistenceObject managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement="+networkElement)
		.addAttribute('NetworkElement', networkElement)
		.addAttribute('platformType', platformType)
		.addAttribute("ossModelIdentity","nRatModelId")
		.addAttribute('version', "1.0.0")
		.addAttribute('category', category)
		.addAttribute('type', type)
		.target(targetPO)
		.build()
	return managedObject
    }

    def PersistenceObject prepareCMNbiNetconfExecutePo(networkElement, platformType, category, type){
	PersistenceObject targetPO   = runtimeDps.addPersistenceObject()
		.namespace("OSS_CM_NBI")
		.type("CmNbiNetconfPayloadRequest")
		.create()

	PersistenceObject managedObject = runtimeDps.addManagedObject().withFdn("NetworkElement="+networkElement)
		.addAttribute('NetworkElement', networkElement)
		.addAttribute('platformType', platformType)
		.addAttribute("ossModelIdentity","nRatModelId")
		.addAttribute('version', "1.0.0")
		.addAttribute('category', category)
		.addAttribute('type', type)
		.target(targetPO)
		.build()
	return managedObject
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
		cmNbiNetconfRequestPO.put("additionalInfo", prepareFailureAdditionalInfo())
		break;
	    case 3:
		cmNbiNetconfRequestPO.put("status", "COMPLETED")
		cmNbiNetconfRequestPO.put("result", "SUCCESS")
		break;
	    case 4:
		cmNbiNetconfRequestPO.put("status", "COMPLETED")
		cmNbiNetconfRequestPO.put("result", "SUCCESS")
		cmNbiNetconfRequestPO.put("additionalInfo", prepareSuccessAdditionalInfoWithValidFile())
		break;
	    default:
		cmNbiNetconfRequestPO.put("status", "IN_PROGRESS")
		break;
	}
	return cmNbiNetconfRequestPO
    }

    def Map<String, Object> prepareSuccessAdditionalInfo(){
	final Map<String, Object> additionalInfo = new HashMap<>()
	additionalInfo.put("filePath", "text.xml")
	return additionalInfo
    }

    def Map<String, Object> prepareSuccessAdditionalInfoWithValidFile(){
	final Map<String, Object> additionalInfo = new HashMap<>()
	additionalInfo.put("filePath", "/getConfigResponse.txt")
	return additionalInfo
    }

    def Map<String, Object> prepareFailureAdditionalInfo(){
	final Map<String, Object> additionaInfo = new HashMap<>()
	additionaInfo.put("errorCode", 412)
	additionaInfo.put("errorTitle", "Netconf Execution Error")
	additionaInfo.put("errorDetails", "Netconf Execution Error")
	return additionaInfo
    }

    @Unroll
    def 'Verify successful netconf response with status as COMPLETED and result as SUCCESS with additional info'(){
	given : "Netconf status request Id "
	prepareCmNbiNetconfRequestPO("SUCCESS", 1)
	when: " \"v1/request/{requestId}\" rest call is triggered"
	Response response = netconfPayloadResource.getNetconfRequestStatus("SUCCESS")
	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }

    @Unroll
    def 'Verify successful netconf response with status as COMPLETED and result as FAILURE'(){
	given : "Netconf status request Id "
	prepareCmNbiNetconfRequestPO("SUCCESS", 2)
	when: " \"v1/request/{requestId}\" rest call is triggered"
	Response response = netconfPayloadResource.getNetconfRequestStatus("SUCCESS")
	then: "Verify the response"
	response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode()
    }

    @Unroll
    def 'Verify successful netconf response with status as COMPLETED and result as SUCCESS'(){
	given : "Netconf status request Id "
	prepareCmNbiNetconfRequestPO("SUCCESS", 3)
	when: " \"v1/request/{requestId}\" rest call is triggered"
	Response response = netconfPayloadResource.getNetconfRequestStatus("SUCCESS")
	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }

    @Unroll
    def 'Verify successful netconf response with valid getConfigResponses file'(){
	given : "Netconf status request Id "

	prepareCmNbiNetconfRequestPO("SUCCESS", 4)
	Resources.getFileSystemResource(Paths.get(getClass().getResource("/getConfigResponse.txt").toURI()).toString()) >> resource

	final Resource resource = new FileResourceImpl(Paths.get(getClass().getResource("/getConfigResponse.txt").toURI()).toString());
	resource.setURI(Paths.get(getClass().getResource("/getConfigResponse.txt").toURI()).toString());
	//print resource.asText
	//print "============================888888888"
	PowerMockito.mockStatic(Resources.class)
	PowerMockito.when(Resources.getFileSystemResource("/getConfigResponse.txt")).thenReturn(resource)

	when: " \"v1/request/{requestId}\" rest call is triggered"
	Response response = netconfPayloadResource.getNetconfRequestStatus("SUCCESS")

	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }

    @Unroll
    def 'Verify successful netconf response 5'(){
	given : "Netconf status request Id "
	prepareCmNbiNetconfRequestPO("SUCCESS", 5)
	when: " \"v1/request/{requestId}\" rest call is triggered"
	NetconfPayloadRequestResult.SUCCESS.getResult()
	NetconfPayloadRequestStatus.COMPLETED.getStatus()
	Response response = netconfPayloadResource.getNetconfRequestStatus("SUCCESS")
	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }

    @Unroll
    def 'Verify netconf response with invalid requestId'(){
	given : "Netconf status request Id "
	prepareCmNbiNetconfRequestPO("SUCCESS", 4)
	when: " \"v1/request/{requestId}\" rest call is triggered"
	Response response = netconfPayloadResource.getNetconfRequestStatus("Failure")
	then: "Verify the response"
	response.getStatus() == Status.NOT_FOUND.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request when node is not added in ENM'(){
	given : "NetworkElement and NetconfPayload"
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.NOT_FOUND.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request when node is added with unsupported platform in ENM'(){
	given : "NetworkElement and NetconfPayload"
	prepareCMNbiNetconfExecutePo("NR01", "CPP", "NODE", "RadioNode")
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.PRECONDITION_FAILED.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request when empty netconf payload is provided.'(){
	given : "NetworkElement and NetconfPayload"
	prepareCMNbiNetconfExecutePo("NR01", "CPP", "NODE", "RadioNode")
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", " ")
	then: "Verify the response"
	response.getStatus() == Status.BAD_REQUEST.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request when node is added with supported platform in ENM'(){
	given : "NetworkElement and NetconfPayload"
	prepareCMNbiNetconfExecutePo("NR01", "ECIM", "NODE", "RadioNode")
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	uriInfo.getBaseUri() >> URI.create("")
        PowerMockito.mockStatic(Resources.class)
        fileResource.writeTtoFile(_ as String, _ as String) >> true
	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request resource method behaves when number of allowed requests exceeds for extra small enm'(){
	given : "NetworkElement and NetconfPayload"
	prepareMulipleCmNbiNetconfRequestPOs("NR01", "ECIM", "NODE", "RadioNode")
	deploymentTypeRetriever.getEnmDeploymentType() >> "ENM_extra_small"
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.TOO_MANY_REQUESTS.getStatusCode()
    }

    @Unroll
    def 'Verify netconf execution request resource method behaves when number of allowed requests exceeds for large enm'(){
	given : "NetworkElement and NetconfPayload"
	prepareMulipleCmNbiNetconfRequestPOsForLargeENM("NR01", "ECIM", "NODE", "RadioNode")
	deploymentTypeRetriever.getEnmDeploymentType() >> "Extra_Large_ENM"
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	uriInfo.getBaseUri() >> URI.create("")
	HyperlinkRepresentation hyperlinkRepresentation = new HyperlinkRepresentation();
	hyperlinkRepresentation.setHref("");

	StatusResoruceUrlRepresentation statusResoruceUrlRepresentation = new StatusResoruceUrlRepresentation();
	statusResoruceUrlRepresentation.setStatusResourceUrl(hyperlinkRepresentation);

	when: " when curl command is executed"
	statusResoruceUrlRepresentation.getStatusResourceUrl()
	statusResoruceUrlRepresentation.toString()
	hyperlinkRepresentation.getHref()
	hyperlinkRepresentation.toString()
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.TOO_MANY_REQUESTS.getStatusCode()
    }


    @Unroll
    def 'Verify netconf execution request resource method behaves when number of allowed requests less than limit for large enm'(){
	given : "NetworkElement and NetconfPayload"
	prepare20CmNbiNetconfRequestPOsWithOneCompletedStatusForLargeENM("NR01", "ECIM", "NODE", "RadioNode")
	deploymentTypeRetriever.getEnmDeploymentType() >> "Extra_Large_ENM"
	lockManager.getDistributedLock("NetconfPayloadLock", "CmNbiNetconfPayloadLock") >> lock
	uriInfo.getBaseUri() >> URI.create("")
	HyperlinkRepresentation hyperlinkRepresentation = new HyperlinkRepresentation();
	hyperlinkRepresentation.setHref("");

	StatusResoruceUrlRepresentation statusResoruceUrlRepresentation = new StatusResoruceUrlRepresentation();
	statusResoruceUrlRepresentation.setStatusResourceUrl(hyperlinkRepresentation);
    
        PowerMockito.mockStatic(Resources.class)
        fileResource.writeTtoFile(_ as String, _ as String) >> true

	when: " when curl command is executed"
	Response response = netconfPayloadResource.executeNetconfRequest("NR01", "<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\"><edit-config> <target><running /></target><config><ManagedElement xmlns=\"urn:com:ericsson:ecim:ComTop\"><managedElementId>1</managedElementId> <SystemFunctions> <systemFunctionsId>1</systemFunctionsId> <Lm xmlns=\"urn:com:ericsson:ecim:RcsLM\"> <lmId>1</lmId> <fingerprint>Site2</fingerprint> </Lm> </SystemFunctions> </ManagedElement> </config> </edit-config></rpc> ]]>]]>")
	then: "Verify the response"
	response.getStatus() == Status.OK.getStatusCode()
    }
}