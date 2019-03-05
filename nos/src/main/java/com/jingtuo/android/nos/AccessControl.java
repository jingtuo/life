package com.jingtuo.android.nos;

/**
 *
 */
public enum AccessControl {
    /**
     * <p>
     * This is the default access control policy for any new buckets or objects.
     * </p>
     */
    Private("private"),

    /**
     * <p>
     * If this policy is used on an object, it can be read from a browser
     * without authentication.
     * </p>
     */
    PublicRead("public-read");

    /** The Nos x-nos-acl header value representing the canned acl */
    private final String cannedAclHeader;

    AccessControl(String cannedAclHeader) {
        this.cannedAclHeader = cannedAclHeader;
    }

    /**
     * Returns the Nos x-nos-acl header value for this canned acl.
     */
    @Override
    public String toString() {
        return cannedAclHeader;
    }
}
