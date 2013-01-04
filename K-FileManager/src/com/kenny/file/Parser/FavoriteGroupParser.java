package com.kenny.file.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.kenny.file.bean.FGroupInfo;
/**
 * 内容解析
 * @author kenny
 */
public class FavoriteGroupParser extends DefaultHandler
{
    private StringBuilder sb = new StringBuilder();
    private FGroupInfo rb=new FGroupInfo();
    private ArrayList<FGroupInfo> lists; 
    private String errorMsg = "";
    private String buffer;// 接收到的网络数据包
    private int PackCount=0;//包的总个数
    public FavoriteGroupParser()
    {
    	lists=new ArrayList<FGroupInfo>();
    }
    public int getPackCount()
    {
    	return PackCount;   
    }
    public String GetLastError()
    {
        return errorMsg;
    }
    public String GetBuffer()
    {
        return buffer;
    }
    public ArrayList<FGroupInfo> parseJokeByFile(Context context, String FileName)
            throws ParserConfigurationException, SAXException,
            MalformedURLException, IOException
    {
    	lists.clear();
        FileInputStream stream = context.openFileInput(FileName);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(this);
        
        InputSource is = new InputSource(stream);
        xmlReader.parse(is);// 查询分组列表
        factory = null;
        parser = null;
        return lists;
    }
    public ArrayList<FGroupInfo> parseJokeByStream(Context context,InputStream in)
    {
        try
        {
        	lists.clear();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(this);
//            buffer = KCommand.GZipStreamToString(in, 0);
//            InputStream isw = new ByteArrayInputStream(buffer.getBytes());
            InputSource is = new InputSource(in);
            xmlReader.parse(is);// 查询分组列表
            factory = null;
            parser = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lists;
    }
    public ArrayList<FGroupInfo> parseJokeByData(Context context, String Data)
    {
        try
        {
        	lists.clear();
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
        return lists;
    }
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
    	if (qName.equals("item") || "item".equals(localName))
        {
            //rb.setGroupID(sb.toString().trim());
            FGroupInfo rb=new FGroupInfo();
            rb.setId(Integer.valueOf(attributes.getValue(0)));
            rb.setTitle(attributes.getValue(1));
            rb.setLogo(attributes.getValue(2));
            rb.setCount(Integer.valueOf(attributes.getValue(3)));
            rb.setMinSize(Integer.valueOf(attributes.getValue(4)));
            rb.setEnds(attributes.getValue(5));
            lists.add(rb);
            //rb.setDesc(desc);
            //rb.setCount(count);
            
        }
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        // System.out.print(new String(ch,start,length));
        sb.append(new String(ch, start, length));
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {

        sb.setLength(0);
    }
    
    @Override
    public void startDocument() throws SAXException
    {
        // System.out.println("startDocument");
    }
    
    @Override
    public void endDocument() throws SAXException
    {
        // System.out.println("endDocument\n===================================");
        /*
         * for(int i=0;i<rssList.size();i++){ RssBean tmpBean=rssList.get(i); }
         */
    }
}
