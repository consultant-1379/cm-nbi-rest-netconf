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

package com.ericsson.oss.presentation.cm.nbi.netconf.common;

public class NetconfPayloadConstants {

    private NetconfPayloadConstants() {
    }

    public static final String UNSUPPORTED_NETWORK_ELEMENT = "Network Element provided doesn’t support Netconf protocol";

    public static final String REQUEST_ID_NOT_FOUND = "Request Id does not exist";

    public static final String REQUEST_ID_NOT_FOUND_TRY_AGAIN = "Try again with proper Request Id.";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String INTERNAL_SERVER_ERROR_SOLUTION = ", Suggested Solution : Use Log Viewer for more information.";

    public static final String GLOBAL_PROPERTIES_FILE_PATH = "/ericsson/tor/data/global.properties"; // NOSONAR

    public static final String MSCMCE_FOLDER = "/ericsson/tor/data/mscmce/";

    public static final String ENM_DEPLOYMENT_TYPE = "enm_deployment_type";

    public static final Long DEFAULT_NETCONF_PAYLOAD_REQUESTS_ALLOWED = 20l;

    // CmNbiNetconfPayloadRequest PO Attributes
    public static final String STATUS = "status";

    public static final String RESULT = "result";

    public static final String ADDITIONAL_INFO = "additionalInfo";

    public static final String REQUEST_ID = "requestId";

    public static final String NODE_ADDRESS = "nodeAddress";

    public static final String FILE_PATH = "filePath";

    public static final String ERROR_CODE = "errorCode";

    public static final String ERROR_TITLE = "errorTitle";

    public static final String ERROR_DETAILS = "errorDetails";

    //Housekeeping constants
    public static final String NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING_TIMER = "NetconfPayloadRequestHousekeepingTimer";

    public static final String DEFAULT_HOUSEKEEPING_SCHEDULE_TIME = "00:00";

    public static final String TIME_TAKEN = "TimeTakenInMillis";

    public static final String NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING = "NETCONF.NETCONF_PAYLOAD_REQUEST_PO_HOUSEKEEPING";

    public static final String CREATED_TIME = "createdTime";

    public static final int TWENTY_FOUR_HOURS = 24;

    public static final String COMPLETED = "COMPLETED";

    //Recording logs constants
    public static final String CM_NBI_REST_NETCONF = "CM_NBI_REST_NETCONF";

    public static final String EXECUTE_NETCONF_PAYLOAD = "EXECUTE_NETCONF_PAYLOAD";

    public static final String NETCONF_PAYLOAD_EXECUTION_STATUS = "NETCONF_PAYLOAD_EXECUTION_STATUS";

}
