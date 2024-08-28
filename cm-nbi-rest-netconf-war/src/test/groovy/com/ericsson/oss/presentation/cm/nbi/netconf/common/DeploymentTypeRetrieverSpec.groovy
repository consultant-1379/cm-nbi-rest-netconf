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

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.resources.FileResourceImpl
import com.ericsson.oss.itpf.sdk.resources.Resource
import com.ericsson.oss.itpf.sdk.resources.Resources

import spock.lang.Unroll

import java.nio.file.Paths

import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.powermock.modules.junit4.PowerMockRunner
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import org.spockframework.runtime.Sputnik

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([Resources.class])
class DeploymentTypeRetrieverSpec extends CdiSpecification {

    @ObjectUnderTest
    DeploymentTypeRetriever deploymentTypeRetriever

    @Unroll
    def 'Retrieve Deployment Type from the Global Properties'(){
	given : "Global properties file"

	final Resource resource = new FileResourceImpl(Paths.get(getClass().getResource("/global.properties").toURI()).toString());
	resource.setURI(Paths.get(getClass().getResource("/global.properties").toURI()).toString());

	PowerMockito.mockStatic(Resources.class)
	PowerMockito.when(Resources.getFileSystemResource(NetconfPayloadConstants.GLOBAL_PROPERTIES_FILE_PATH)).thenReturn(resource)

	when: " when getEnmDeploymentType is invoked"
	String deploymentType = deploymentTypeRetriever.getEnmDeploymentType()

	then: "Verify the response"
	deploymentType == "Extra_Large_ENM"
    }

    @Unroll
    def 'Retrieve Deployment Type from the Global Properties without power mock, which means properties file is not loaded'(){
	given : "Global properties file"

	final Resource resource = new FileResourceImpl(Paths.get(getClass().getResource("/global.properties").toURI()).toString());
	resource.setURI(Paths.get(getClass().getResource("/global.properties").toURI()).toString());

	Resources.getFileSystemResource(NetconfPayloadConstants.GLOBAL_PROPERTIES_FILE_PATH) >> resource

	when: " when getEnmDeploymentType is invoked"
	String deploymentType = deploymentTypeRetriever.getEnmDeploymentType()

	then: "Verify the response"
	deploymentType == ""
    }

    @Unroll
    def 'Simple invocation of getDeploymentType without any inputs or mocking'(){
	given : "Global properties file"

	when: " when getEnmDeploymentType is invoked"
	deploymentTypeRetriever.getEnmDeploymentType()

	then: "Verify the response"
	notThrown(Exception)
    }
}
