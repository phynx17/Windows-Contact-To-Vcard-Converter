package com.phynx.tools;

import java.io.Serializable;

/**
 * List of VCard Properties.
 * Based on VCard version 3.0
 *
 * @author Pandu Pradhana
 * @since 12/4/13 11:42 AM
 */
public enum VCardProperties implements Serializable {

    /**
     * The list
     *
     */
    FULLNAME("FN"),
    NAME("N"),
    NICKNAME("NICKNAME"),
    ORGANIZATION("ORG"),
    CONTACT_ID("UID"),
    NOTE("NOTE"),
    PHONE("TEL"),
    EMAIL("EMAIL"),
    PHOTO("PHOTO"),
    BIRTHDAY("BDAY"),
    TEXTUAL_SOURCE("NAME"),
    SOURCE("SOURCE"),
    TIME_UPDATED("REV")
    ;

    private final String vcardPropertiesName;
    private VCardProperties(String s) { this.vcardPropertiesName = s;}
    /**
     * Get VCard properties name
     * @return
     */
    public String getName() { return this.vcardPropertiesName; }
}
