package com.phynx.tools;

import java.util.List;
import java.util.Map;

/**
 * Model for VCard based on vcard spec v3.0
 * Reference:
 * - http://www.imc.org/
 * - http://www.rfc-editor.org/rfc/pdfrfc/rfc6350.txt.pdf
 *
 * @author Pandu Pradhana
 * @since 12/3/13 7:30 PM
 */
public class VCard {

    public static final String PHYNX_PRODUCT_ID = "-//PhynxSoft//ContactConverter//ID";

    /**
     * Version
     */
    protected final float version;
    protected boolean hasWrapped = false;

    //Temporary buffer
    protected StringBuilder sb = new StringBuilder();

    /**
     * Default constructor
     */
    public VCard() {
        this(3.0f);
    }


    /**
     * Constructor with given version number
     * @param version version
     */
    public VCard(float version) {
        this.version = version;
        writeStart();
        writeVersion();
        writeMyMark();

    }


    /**
     * Wrapped the card
     * @return
     */
    public String wrapCard() {
        if (hasWrapped) {
            throw new IllegalStateException("The card already written down");
        }
        String _s = writeEnd().toString();
        //Clear
        sb.setLength(0);
        return _s;
    }


    /**
     * Get the VCard version
     * @return
     */
    public float getVersion() { return this.version; }


    /**
     * Common Fields
     *
     */
    /**
     * Write property with no types
     * @param properties property name
     * @param value value
     * @return
     */
    public StringBuilder writeProperty(VCardProperties properties, String value) {
        return writeProperty(properties, value, null);
    }


    /**
     * Write property to VCard
     * @param properties property
     * @param value the value
     * @param types set of types
     * @return
     */
    public StringBuilder writeProperty(VCardProperties properties, String value, List<VCardType> types) {
        sb.append(properties.getName());
        writeFieldProperties(types);
        sb.append(':').append(value);
        writeCRLF();
        return sb;
    }

    /**
     * Write multi lines of VCard property. For example the Base64 content of Photo
     * @param properties property
     * @param multilineValue values
     * @param types
     * @return
     */
    public StringBuilder writeProperty(VCardProperties properties,
                                       List<String> multilineValue, List<VCardType> types) {
        sb.append(properties.getName());
        writeFieldProperties(types);
        sb.append(':');
        for (String hexLine : multilineValue) {
            sb.append(hexLine);
            writeCRLF();
        }
        writeCRLF();
        return sb;
    }



    /*
    public StringBuilder writeFullName(String fullname) {
        sb.append("FN:").append(fullname);
        writeCRLF();
        return sb;
    }
    public StringBuilder writeOrganization(String organization) {
        sb.append("ORG:").append(organization);
        writeCRLF();
        return sb;
    }
    public StringBuilder writeContactId(String uuid) {
        sb.append("UID:").append(uuid);
        writeCRLF();
        return sb;
    }
    public StringBuilder writeNote(String note) {
        sb.append("NOTE:").append(note);
        writeCRLF();
        return sb;
    }
    public StringBuilder writePhone(String number, Map<String, String> types) {
        sb.append("TEL");
        writeFieldProperties(types);
        sb.append(':').append(number);
        writeCRLF();
        return sb;
    }
    public StringBuilder writeEmail(String email, Map<String, String> types) {
        sb.append("EMAIL");
        writeFieldProperties(types);
        sb.append(':').append(email);
        writeCRLF();
        return sb;
    }
    public StringBuilder writePhoto(List<String> hexStringLine, Map<String, String> types) {
        sb.append("PHOTO");
        writeFieldProperties(types);
        sb.append(':');
        for (String hexLine : hexStringLine) {
            sb.append(hexLine);
            writeCRLF();
        }
        writeCRLF();
        return sb;
    }
    */


    /**
     * Common Method. the writing part
     *
     */

    /**
     * write start of VCard. Must be called first time
     * @return current string buffer
     */
    protected StringBuilder writeStart() {
        sb.append("BEGIN:VCARD");
        writeCRLF();
        return sb;
    }

    /**
     * Write end of VCard
     * @return current string buffer
     */
    protected StringBuilder writeEnd() {
        sb.append("END:VCARD");
        writeCRLF();
        return sb;
    }

    /**
     * Write version
     * @return the version
     */
    protected StringBuilder writeVersion() {
        sb.append("VERSION:").append(Float.toString(getVersion()));
        writeCRLF();
        return sb;
    }

    /**
     * Write my Mark :)
     *
     */
    protected final StringBuilder writeMyMark() {
        sb.append("PRODID:").append(PHYNX_PRODUCT_ID);
        writeCRLF();
        return sb;
    }

    /**
     * Write the carriage return and line feed
     * @return
     */
    protected StringBuilder writeCRLF(){
        sb.append((char)0xD).append((char)0xA);
        return sb;
    }

    /**
     * Write properties for each fields
     * @param types
     * @return
     */
    protected StringBuilder writeFieldProperties(List<VCardType> types) {
        if (types != null) {
            for (VCardType cardType: types) {
                sb.append(";");
                sb.append(cardType.type).append("=").append(cardType.value);
            }
        }
        return sb;
    }

}
