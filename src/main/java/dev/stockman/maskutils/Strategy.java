package dev.stockman.maskutils;

public enum Strategy {
    /**
     * Only fields in the configured set will be shown unmasked
     */
    WHITELIST,

    /**
     * All fields will be shown unmasked except those in the configured set
     */
    BLACKLIST

}
