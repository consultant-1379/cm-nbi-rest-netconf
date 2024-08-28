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

package com.ericsson.oss.presentation.cm.nbi.netconf.enums;

public enum NetconfPayloadSupportedPlatforms {

    ECIM("ECIM"), CBPOI("CBP-OI"), IPOSOI("IPOS-OI");

    private String platformType;

    private NetconfPayloadSupportedPlatforms(final String requestStatus) {
        this.platformType = requestStatus;
    }

    public String getPlatformType() {
        return platformType;
    }

}
