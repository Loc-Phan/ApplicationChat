/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Account;

import Account.Account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Chen-Yang
 */
public class SaveAccount {

    public static Document ReadFileXML(String fileName) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream in = new FileInputStream(new File(fileName));
        Document doc = builder.parse(in);

        return doc;
    }

    public static void WriteFileXML(Document doc, String fileName) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer t = transformerFactory.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));
            t.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
    public static void addAccount(Account temp, String fileName) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document doc = ReadFileXML(fileName);
        
        Element rootElement = doc.getDocumentElement();

        Element acc = doc.createElement("account");
        rootElement.appendChild(acc);
        Element fname = doc.createElement("firstname");
        fname.appendChild(doc.createTextNode(temp.getFirstName()));
        acc.appendChild(fname);
        Element lname = doc.createElement("lastname");
        lname.appendChild(doc.createTextNode(temp.getLastName()));
        acc.appendChild(lname);
        Element user = doc.createElement("username");
        user.appendChild(doc.createTextNode(temp.getUserName()));
        acc.appendChild(user);
        Element pass = doc.createElement("password");
        pass.appendChild(doc.createTextNode(temp.getPassWord()));
        acc.appendChild(pass);

        WriteFileXML(doc, fileName);
        
    }
    
    public static void createFileXML(Account temp, String fileName) throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbFactory
                        = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("login");
            doc.appendChild(rootElement);
            Element acc = doc.createElement("account");
            rootElement.appendChild(acc);
            Element fname = doc.createElement("firstname");
            fname.appendChild(doc.createTextNode(temp.getFirstName()));
            acc.appendChild(fname);
            Element lname = doc.createElement("lastname");
            lname.appendChild(doc.createTextNode(temp.getLastName()));
            acc.appendChild(lname);
            Element user = doc.createElement("username");
            user.appendChild(doc.createTextNode(temp.getUserName()));
            acc.appendChild(user);
            Element pass = doc.createElement("password");
            pass.appendChild(doc.createTextNode(temp.getPassWord()));
            acc.appendChild(pass);
            
            WriteFileXML(doc,fileName);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isExistsAccount(Account acc, String fileName) throws ParserConfigurationException, IOException, SAXException {
        Document doc = ReadFileXML(fileName);
        Element login = doc.getDocumentElement();
        NodeList accountList = login.getElementsByTagName("account");

        for (int i = 0; i < accountList.getLength(); i++) {
            Node node = accountList.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element account = (Element) node;
                if(acc.getUserName().compareTo(account.getElementsByTagName("username").item(0).getTextContent())==0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Account> ListAccount(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Account> ds = new ArrayList<Account>();
        Document doc = ReadFileXML(fileName);
        
        Element login = doc.getDocumentElement();
        NodeList accountList = login.getElementsByTagName("account");

        for (int i = 0; i < accountList.getLength(); i++) {
            Node node = accountList.item(i);
            Account acc = new Account();
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element account = (Element) node;
                //System.out.println(account.getElementsByTagName("firstname").item(0).getTextContent());
                acc.setFirstName(account.getElementsByTagName("firstname").item(0).getTextContent());
                acc.setLastName(account.getElementsByTagName("lastname").item(0).getTextContent());
                acc.setUserName(account.getElementsByTagName("username").item(0).getTextContent());
                acc.setPassWord(account.getElementsByTagName("password").item(0).getTextContent());
            }
            ds.add(acc);
        }
        return ds;
    }
    public static void main(String args[]) throws ParserConfigurationException, IOException, SAXException {
        List<Account> ds = ListAccount("Account.xml");
        System.out.println(ds.get(0).getPassWord());
    }
}
