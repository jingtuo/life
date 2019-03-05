package com.jingtuo.android.nos;

/**
 * 地理分区
 * @author JingTuo
 */

public enum Region {

    /**
     * The US Standard Nos Region. This region uses Nos servers located in the
     * United States.
     * <p>
     * This is the default Nos Region. All requests sent to
     */
    CN_Standard(null),

    /**
     * 杭州机房
     */
    CN_Hnagzhou("HZ"),

    /**
     * 建德机房
     */
    CN_Jiande("JD"),

    /**
     * 北京机房
     */
    CN_Beijing("BJ"),

    /**
     * 公有云北京
     */
    CN_NORTH_1("BJ"),
    /**
     * 公有云杭州
     */
    CN_EAST_1("HZ"),
    /**
     * 私有云杭州
     */
    CN_EAST_P0("HZ"),
    /**
     * 公有云建德
     */
    CN_EAST_3("JD"),
    /**
     * 私有云建德
     */
    CN_EAST_P1("JD");

    /** The unique ID representing each region. */
    private final String regionId;

    /**
     * Constructs a new region with the specified region ID.
     *
     * @param regionId
     *            The unique ID representing the Nos region.
     */
    Region(String regionId) {
        this.regionId = regionId;
    }

    @Override
    public String toString() {
        return regionId;
    }
}
