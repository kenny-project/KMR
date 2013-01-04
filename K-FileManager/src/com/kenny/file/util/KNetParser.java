package com.kenny.file.util;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.kenny.file.bean.NetClientBean;
import com.kenny.file.tools.GZip;
/**
 * 
 * @author kenny
 */
public class KNetParser extends DefaultHandler
{
    private ArrayList<NetClientBean> ItemList = null;
    private StringBuilder sb = new StringBuilder();
    private NetClientBean rb = null;
    private int flag;// 1:Item 2:version,3services
    private String errorMsg = "";
    private String buffer;
    public String GetBuffer()
    {
        return buffer;
    }
    public String GetLastError()
    {
        return errorMsg;
    }
    public ArrayList<NetClientBean> parseRssByUrl(InputStream in)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(this);
            buffer = GZip.GZipStreamToString(in, 0);
            sb.setLength(0);
            flag = 0;
            ItemList = new ArrayList<NetClientBean>();
            InputStream isw = new ByteArrayInputStream(buffer.getBytes());
            InputSource is = new InputSource(isw);
            xmlReader.parse(is);// 查询分组列表
            factory = null;
            parser = null;
            return ItemList;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errorMsg = e.getMessage();
        }
        return new ArrayList<NetClientBean>();
    }
    public ArrayList<NetClientBean> parseJokeStringByData(
            String Data)
    {
        ItemList = new ArrayList<NetClientBean>();
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(this);
            InputSource is = new InputSource(new StringReader(Data));
            xmlReader.parse(is);// 查询分组列表
            factory = null;
            parser = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ItemList;
    }
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        if (qName.equals("item") || "item".equals(localName))
        {
            flag = 1;//
            rb = new NetClientBean();
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        sb.append(new String(ch, start, length));
    }
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        if(flag==1)
        {
        Item(uri, localName, qName);
        }
        sb.setLength(0);
    }
    private void Item(String uri, String localName, String qName)
    {
        if (qName.equals("item") || "item".equals(localName))
        {
            flag = 0;
            ItemList.add(rb);
        }
        else if (qName.equals("id") || "id".equals(localName))
        {
            rb.setID(sb.toString().trim());
        }
        else if (qName.equals("title") || "title".equals(localName))
        {
            rb.setTitle(sb.toString().trim());
        }
        else if (qName.equals("un") || "un".equals(localName))
        {
            rb.setUserName(sb.toString().trim());
        }
        else if (qName.equals("ps") || "ps".equals(localName))
        {
            rb.setPassWord(sb.toString().trim());
        }
        else if (qName.equals("date") || "date".equals(localName))
        {
            rb.setDate(sb.toString().trim());
        }
        else if (qName.equals("host") || "host".equals(localName))
        {
            rb.setHost(sb.toString().trim());
        }
        else if (qName.equals("port") || "port".equals(localName))
        {
            rb.setPort(sb.toString().trim());
        }
        else if (qName.equals("type") || "type".equals(localName))
        {
            rb.setType(sb.toString().trim());
        }
    }
    @Override
    public void startDocument() throws SAXException
    {
        
    }
    @Override
    public void endDocument() throws SAXException
    {
        
    }
}
