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

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.sdk.cluster.MembershipChangeEvent

import spock.lang.Unroll

class NetconfClusterMembershipListenerSpec extends CdiSpecification {

    @ObjectUnderTest
    private NetconfClusterMembershipListener netconfClusterMembershipListener

    @MockedImplementation
    private MembershipChangeEvent membershipChangeEvent

    @Unroll
    def "Test Case to check master instance returns true"() {
        given: "Cluster"
        membershipChangeEvent.isMaster()>>true;

        when: "Cluster listener is invoked"
        netconfClusterMembershipListener.listenForMembershipChange(membershipChangeEvent)

        then: "Check if only 24Hrs old POs only got deleted"
        true == netconfClusterMembershipListener.isMasterInstance()
    }

    @Unroll
    def "Test Case to check master instance returns false"() {
        given: "Cluster"
        membershipChangeEvent.isMaster()>>false;

        when: "Cluster listener is invoked"
        netconfClusterMembershipListener.listenForMembershipChange(membershipChangeEvent)

        then: "Check if only 24Hrs old POs only got deleted"
        false == netconfClusterMembershipListener.isMasterInstance()
    }
}
