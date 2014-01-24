package com.kenny.util;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.kenny.data.BilingualBean;
/**
 * 
 * @author kenny
 */
public class BilingualItemParser extends DefaultHandler
{
    private ArrayList<BilingualBean> ItemList = null;
    private StringBuilder sb = new StringBuilder();
    private BilingualBean rb = null;
    private int ItemID = 0;// 编号
    private boolean Itemflag=false;// 1:Item 2:version,3services
    private Context m_ctx = null;
    private String errorMsg = "";
    private String buffer="";
    private int Pos=0;//总共多少分页
    public String GetBuffer()
    {
        return buffer;
    }
    public String GetLastError()
    {
        return errorMsg;
    }
    public int GetPagePos()
    {
        return Pos;
    }
    
    public ArrayList<BilingualBean> parseJokeByUrl(Context context, String url)
    {
        ItemList = new ArrayList<BilingualBean>();
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(this);
            URL urlStream = new URL(url);
            // 创建URL连接
            URLConnection connection;
            connection = urlStream.openConnection();
            // 设置参数
            connection.setConnectTimeout(10000);
            connection.addRequestProperty("User-Agent", "J2me/MIDP2.0");
            // 连接服务器
            connection.connect();
            InputStream inStream = connection.getInputStream();
            buffer=T.StreamToString(inStream);
//            String htmlcontent = KCommand.GZipStreamToString(inStream, 0);
//            buffer = htmlcontent;
            Itemflag = false;
            sb.setLength(0);
            InputStream isw = new ByteArrayInputStream(buffer.getBytes());
            InputSource is = new InputSource(isw);
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
    public ArrayList<BilingualBean> parseJokeByData(Context context, String Data)
    {
        ItemList = new ArrayList<BilingualBean>();
        try
        {
            m_ctx = context;
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
    public ArrayList<BilingualBean> parseRssByStream(Context context, InputStream in)
    {
        try
        {
            m_ctx = context;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(this);
            buffer = KCommand.GZipStreamToString(in, 0);
            sb.setLength(0);
            Itemflag = false;
            ItemList = new ArrayList<BilingualBean>();
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
        return new ArrayList<BilingualBean>();
    }
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        if (qName.equals("item") || "item".equals(localName))
        {
            Itemflag = true;//
            rb = new BilingualBean();
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
        if (qName.equals("pos") || "pos".equals(localName))
        {
        	Pos=Integer.valueOf(sb.toString().trim());
        }
        Item(uri, localName, qName);
        sb.setLength(0);
    }
    private void Item(String uri, String localName, String qName)
    {
        if(!Itemflag)
        {
            return;
        }
        if (qName.equals("item") || "item".equals(localName))
        {
            Itemflag = false;
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
        else if (qName.equals("desc") || "desc".equals(localName))
        {
            rb.setDesc(sb.toString().trim());
        }
        else if (qName.equals("num") || "num".equals(localName))
        {
            rb.setNum(sb.toString().trim());
        }
        else if (qName.equals("image") || "image".equals(localName))
        {
            rb.setImageUrl(sb.toString().trim());
        }
        else if (qName.equals("imageurl") || "imageurl".equals(localName))
        {
            rb.setImageUrl(sb.toString().trim());
        }
        else if (qName.equals("url") || "url".equals(localName))
        {
            rb.setUrl(sb.toString().trim());
        }
        else if (qName.equals("date") || "date".equals(localName))
        {
            rb.setDate(sb.toString().trim());
        }
        else if (qName.equals("views") || "views".equals(localName))
        {
            rb.setViews(sb.toString().trim());
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
