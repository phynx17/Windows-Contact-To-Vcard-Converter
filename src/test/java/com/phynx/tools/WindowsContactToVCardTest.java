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

        WindowsContactToVCard wc = new WindowsContactToVCard();

        /*
        //String file = "/Users/pandupradhana/Temp/ContactsPapa/Sony.contact";
        //String file = "/Users/pandupradhana/Temp/ContactsPapa/Bandono.contact";
        String file = "/Users/pandupradhana/Temp/ContactsPapa/orig/dedii.contact";

        System.out.println(wc.convert(file));

        file = "/Users/pandupradhana/Temp/ContactsPapa/orig/Bandono.contact";
        System.out.println(wc.convert(file));
        */

        String file = "/Users/pandupradhana/Temp/ContactsPapa/Abimanyu.contact";
        System.out.println(wc.convert(file));

    }


}
