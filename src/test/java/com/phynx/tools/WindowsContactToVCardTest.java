package com.phynx.tools;

import org.junit.Test;

/**
 * A test
 *
 * @author Pandu Pradhana
 * @since 12/4/13 1:07 AM
 */
public class WindowsContactToVCardTest {

    @Test
    public void testConvert() {

        //String file = "/Users/pandupradhana/Temp/ContactsPapa/Sony.contact";
        //String file = "/Users/pandupradhana/Temp/ContactsPapa/Bandono.contact";
        String file = "/Users/pandupradhana/Temp/ContactsPapa/orig/dedii.contact";

        WindowsContactToVCard wc = new WindowsContactToVCard();
        System.out.println(wc.convert(file));

        file = "/Users/pandupradhana/Temp/ContactsPapa/orig/Bandono.contact";
        System.out.println(wc.convert(file));


    }


}
