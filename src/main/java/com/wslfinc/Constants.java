package com.wslfinc;

/**
 *
 * @author Wsl_F
 */
public class Constants {

    /**
     * Full path to the project folder. It's needed for tests.
     */
    public static final String PATH_TO_PROJECT
            = "/home/wslf/ProgramingProjects/AWS/CF-rating-prediction/";

    /**
     * Delay before send request to Codeforces. Value in milliseconds.
     */
    public static final int API_DELAY_MS = 250;

    /**
     * Status of successful codeforces JSON-response.
     */
    public static final String SUCCESSFUL_STATUS = "OK";

    public static final int MINIMAL_CONTEST_ID = 1;
    public static final int MAXIMAL_CONTEST_ID = 1_000_000;

    public static final String API_PREFIX = "http://codeforces.com/api";

    public static final int NEGATIVE_INFINITY = -1_000_000_000;

    public static final String PATH_TO_TEST_FILES = "file://" + PATH_TO_PROJECT + "resources/for_tests/";
}