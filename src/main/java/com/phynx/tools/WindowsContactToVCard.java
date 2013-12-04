package com.phynx.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Represent the Windows Contact format
 *
 * @author Pandu Pradhana
 * @since 12/4/13 12:41 AM
 */
public class WindowsContactToVCard {

    //Get the DOM Builder Factory
    final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    //For DOM operation
    private DocumentBuilder builder = null;


    /**
     * This is the MAIN part
     *
     * @param arg arguments
     */
    public static void main(String arg[]) {
        if (arg.length < 1) {
            out("Usage: java -jar contacts2vcf.jar [Options] [Path] [Target_dir]");
            out("    [Options]: This is mandatory, and if exists here's the options ");
            out("        -f : To indicate that the second argument is a file");
            out("    [Path]: file path or folder path");
            out("    [Target_dir]: target directory (default current execution path)");
            out("\n\n");
            out("~~~~~~~~~");
            out("PhynxSoft");
            out("~~~~~~~~~\n");
            return;
        }

        boolean oneFileOnly = false;
        String path;
        String targetDirectory = null;

        int idx = 0;
        String first = arg[idx];

        if (first.startsWith("-f")) {
            oneFileOnly = true;
            //Need second
            path = arg[++idx];
        } else {
            path = first;
        }

        if (arg.length > idx) {
            targetDirectory = arg[++idx];
        }


        WindowsContactToVCard wVcard = new WindowsContactToVCard();

        File fi = new File(path);
        if (isFileOK(fi)) {
            //&&(!oneFileOnly || (oneFileOnly && fi.isFile()))
            if (oneFileOnly) {
                processConvertSingleFile(fi, wVcard,targetDirectory);
            } else {

                File[] files = fi.listFiles(new FilenameFilter() {
                    public boolean accept(File file, String s) {
                        return s.endsWith(".contact");
                    }
                });

                long l = System.currentTimeMillis();
                out("Going to process " + files.length + " file(s)");

                for (File wcontact: files) {
                    processConvertSingleFile(wcontact, wVcard,targetDirectory);
                }

                out("Done convert, took " + (System.currentTimeMillis()-l) + "ms");

            }

        } else {
            out("Please check your path again. Not a valid argument supplied");
        }

    }




    /**
     * Default Constructor
     *
     */
    public WindowsContactToVCard() {
        //Get the DOM Builder
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Error while building DocumentBuilder: " + e.getMessage());
        }

    }


    /**
     * As it name implies, convert the Windows Contact
     *
     * @param file file instance. Cannot be null
     * @return the converted VCard content
     *
     */
    public String convert(final String file) {
        return convert(new File(file));
    }


    /**
     * As it name implies, convert the Windows Contact
     *
     * @param file file instance. Cannot be null
     * @return the converted VCard content
     */
    public String convert(final File file) {
        isFileOK(file);
        VCard vcard = new VCard();
        try {
            Document document = builder.parse(file);
            //Skip the contact element
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    if (el.getTagName().equals("c:CreationDate")) {
                        writeFoundElement(el, vcard, VCardProperties.TIME_UPDATED);
                    } else if (el.getTagName().equals("c:Extended")) {
                        //what to do with this?
                    } else if (el.getTagName().equals("c:ContactIDCollection")) {
                        Node nodeContactId = getChildElement(el,"c:ContactID");
                        if (nodeContactId == null) continue;
                        Node nodeContactIdValue = getChildElement((Element)nodeContactId, "c:Value");
                        writeFoundElement(nodeContactIdValue, vcard, VCardProperties.CONTACT_ID);
                    } else if (el.getTagName().equals("c:NameCollection")) {
                        Node nodeName = getChildElement(el, "c:Name");
                        if (nodeName == null) continue;

                        Node nodeFN = getChildElement((Element)nodeName, "c:FormattedName");
                        writeFoundElement(nodeFN, vcard, VCardProperties.FULLNAME);

                        String _n = null;
                        Node nodeGivenName = getChildElement((Element) nodeName, "c:GivenName");
                        if (nodeGivenName != null) {
                            _n = getValueFromNode(nodeGivenName);
                        }
                        Node nodeFamName = getChildElement((Element) nodeName, "c:FamilyName");
                        if (nodeFamName != null) {
                            String __val = getValueFromNode(nodeFamName);
                            _n = _n == null ? __val : " " + __val;
                        }
                        if (_n != null) {
                            writeFoundElement(nodeFN, vcard, VCardProperties.NAME);
                        }

                    } else if (el.getTagName().equals("c:PhoneNumberCollection")) {
                        NodeList _phoneNumbers = el.getElementsByTagName("c:PhoneNumber");

                        int offset = -1;
                        for (int _ip = (offset+1); _ip < _phoneNumbers.getLength(); _ip++) {
                            offset++;
                            Node nodePhoneName = _phoneNumbers.item(_ip);
                            if (nodePhoneName == null) continue;

                            List<VCardType> types = new ArrayList<VCardType>();

                            Node nodePhoneNumber = getChildElement((Element) nodePhoneName, "c:Number");
                            Node nodePhoneLabel = getChildElement((Element)nodePhoneName, "c:LabelCollection");
                            NodeList nodeListLabel = ((Element)nodePhoneLabel).getElementsByTagName("c:Label");
                            //System.out.println("---> " + nodeListLabel.getLength());

                            int _labelOffset = -1;
                            for (int _ipl = (_labelOffset+1); _ipl < nodeListLabel.getLength(); _ipl++) {
                                _labelOffset++;
                                Node nodeLabel = getChildElement((Element) nodePhoneLabel, "c:Label",_labelOffset);
                                if (nodeLabel == null) {
                                    continue;
                                }
                                String typeLabel = adjustLabel(getValueFromNode(nodeLabel));
                                types.add(new VCardType("type", typeLabel));
                            }

                            writeFoundElement(nodePhoneNumber, vcard, VCardProperties.PHONE, types);

                        }


                    } else if (el.getTagName().equals("c:PhotoCollection")) {
                        //Only get the FIRST photo
                        Node nodePhoto = getChildElement(el, "c:Photo");
                        if (nodePhoto == null) continue;

                        Node nodePhotoValue = getChildElement((Element) nodePhoto, "c:Value");
                        if (nodePhotoValue == null) continue;

                        String mimeType = nodePhotoValue.getAttributes().getNamedItem("c:ContentType").getNodeValue();
                        if (mimeType != null) {
                            List<VCardType> types = new ArrayList<VCardType>();

                            // http://www.rfc-editor.org/rfc/rfc2426.txt
                            // Section 3.1.4
                            types.add(new VCardType("ENCODING","b"));
                            if (mimeType.startsWith("image/")) {
                                mimeType = mimeType.replace("image/","");
                            }
                            types.add(new VCardType("TYPE", mimeType.toUpperCase()));

                            String dataPhotoBase64 = getValueFromNode(nodePhotoValue);
                            List<String> _content = new ArrayList<String>();
                            int lines = 0;

                            /**
                             * TODO fix this on memory issue :p
                             */
                            StringTokenizer strTok = new StringTokenizer(dataPhotoBase64, "\n");
                            while (strTok.hasMoreTokens()) {
                                String _ll = strTok.nextToken();
                                _content.add(lines > 0 ? " " + _ll : _ll);
                                lines++;
                            }

                            vcard.writeProperty(VCardProperties.PHOTO, _content, types);
                        }

                    }

                }

            }

            return vcard.wrapCard();

        } catch (SAXException e) {
            throw new RuntimeException("Error while converting: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("I/O Error while converting: " + e.getMessage(), e);
        }

    }


    /**
     * Get child element
     * @param element element
     * @param elementName element name
     * @return node
     */
    public Node getChildElement(Element element, String elementName) {
        return getChildElement(element, elementName,-1);
    }


    /**
     * Get child element
     * @param element element
     * @param elementName element name
     * @return node
     */
    public Node getChildElement(Element element, String elementName, int offset) {
        Node foundNode = null;
        NodeList _tempnodeList = element.getChildNodes();
        if (offset > _tempnodeList.getLength()) return foundNode;
        for (int _i = (offset+1); _i < _tempnodeList.getLength(); _i++) {
            Node __n = _tempnodeList.item(_i);
            if (__n.getNodeType() == Node.ELEMENT_NODE) {
                Element _el = (Element) __n;
                if (_el.getTagName().equals(elementName)) {
                    foundNode = __n;
                    break;
                }
            }
        }
        return foundNode;
    }




    /**
     * Write to VCard for found element
     * @param node the element
     * @param vCard vcard
     * @param properties properties
     */
    protected void writeFoundElement(Node node, VCard vCard, VCardProperties properties) {
        if (node != null) {
            vCard.writeProperty(properties,getValueFromNode(node));
        }
    }

    /**
     * Write to VCard for found element
     * @param node the element
     * @param vCard vcard
     * @param properties vcard property
     * @param types  types (if any)
     */
    protected void writeFoundElement(Node node, VCard vCard, VCardProperties properties, List<VCardType> types) {
        if (node != null) {
            vCard.writeProperty(properties,getValueFromNode(node), types);
        }
    }



    /**
     * Get node value
     * @param node element
     * @return the value (string)
     */
    protected String getValueFromNode(Node node) {
        return node.getFirstChild().getNodeValue();
    }


    /**
     * Adjust label
     * @param label label
     * @return adjusted label
     */
    protected String adjustLabel(final String label) {
        String __l = label;
        if (__l != null) {
            if (__l.equals("Cellular")) {
                __l = "CELL";
            }
        }
        return __l;
    }


    /**
     * Check whether a File is OK. Breaks prematurely if NOT OK
     * @param file the file
     * @return if file Ok
     */
    static boolean isFileOK (String file) {
        return isFileOK(new File(file));
    }

    /**
     * Check whether a File is OK. Breaks prematurely if NOT OK
     * @param file the file
     * @return if file OK
     */
    static boolean isFileOK (File file) {
        if (file == null) {
            throw new IllegalArgumentException("Contact file cannot be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException(file.getAbsolutePath()
                                                + " does not exists. Please check the path again");
        }
        //return isWindowsContact(file);
        return true;
    }

    /**
     * Check if argument is windows contact
     * @param file file
     * @return if windows contact
     */
    static boolean isWindowsContact(File file) {
        return file != null && file.getName().endsWith(".contact");
    }


    /**
     * Write VCard content to a file
     *
     * @param targetFile target file
     * @param vcardBuffer Vcard instance
     */
    static void writeContentToVCardFile(File targetFile, VCard vcardBuffer) {
        writeContentToVCardFile(targetFile, vcardBuffer.wrapCard());
    }

    /**
     * Write content to file
     * @param targetFile target file
     * @param content the VCard content
     */
    static void writeContentToVCardFile(File targetFile, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(targetFile);
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("I/O error while writing to File: " + e.getMessage(),e);
        } finally {
            if (writer != null) {
                try {writer.close(); } catch (IOException e) { e.printStackTrace();  }
            }
        }
    }


    /**
     * Print to standard output
     * @param message any message
     */
    static void out(String message) {
        System.out.println(message);
    }


    /**
     * Process a single file
     * @param fi file source
     * @param targetDirectory target directory
     */
    static private void processConvertSingleFile(File fi, WindowsContactToVCard wVcard, String targetDirectory) {
        String fileName = fi.getName();
        if (!isWindowsContact(fi)) {
            out(fileName + " is not a valid windows contact file");
            return;
        }

        String newName = fileName.replace("contact", "") + "vcf";
        File targetFile;
        if (targetDirectory != null) {
            //Make sure exists
            File ___d = new File(targetDirectory);
            if (!___d.exists()) {
                if (!___d.mkdir()) {
                    throw new RuntimeException("Failed to create target directory");
                }
            }
            else {
                if (!___d.isDirectory()) {
                    throw new RuntimeException(___d.getAbsolutePath() + " is not directory");
                }
            }
            targetFile = new File(___d,newName);
        } else {
            targetFile = new File(newName);
        }


        //File vCardTarget = new File(newName);
        //System.out.println(wVcard.convert(fi));
        writeContentToVCardFile(targetFile, wVcard.convert(fi));
        out("Done convert " + fileName);
    }

}
