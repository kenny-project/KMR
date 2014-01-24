package com.kenny.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.kenny.data.DailySentencebean;
import com.kenny.util.T;

/**
 * 每日一句
 * @author kenny
 */
public class DailySentenceParser extends DefaultHandler
{
	private List<DailySentencebean> ItemList=new ArrayList<DailySentencebean>();
	private StringBuilder sb = new StringBuilder();
	private DailySentencebean rb = null;
	private int flag;// 1:Item 2:version,3services
	private String errorMsg = "";
	private String buffer;
	private Context context;
	private int version = 0;

	public int getVersion()
	{
		return version;
	}

	public String GetBuffer()
	{
		return buffer;
	}

	public String GetLastError()
	{
		return errorMsg;
	}

	public List<DailySentencebean> parseByUrl(Context context, String url)
	{
		ItemList.clear();
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
			buffer = T.StreamToString(inStream);

			sb.setLength(0);
			InputStream isw = new ByteArrayInputStream(buffer.getBytes());
			InputSource is = new InputSource(isw);
			xmlReader.parse(is);// 查询分组列表
			factory = null;
			parser = null;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ItemList;
	}
	public List<DailySentencebean> parseStringByData(Context context, String Data)
	{
		this.context = context;
		ItemList.clear();
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
		} catch (Exception e)
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
			rb = new DailySentencebean();
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
		if (flag == 1)
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
		} else if (qName.equals("id") || "id".equals(localName))
		{
			rb.setId(sb.toString().trim());
		} else if (qName.equals("title") || "title".equals(localName))
		{
			rb.setTitle(sb.toString().trim());
		} else if (qName.equals("time") || "time".equals(localName))
		{
			rb.setTime(sb.toString().trim());
		} else if (qName.equals("img") || "img".equals(localName))
		{
			rb.setImg(sb.toString().trim());
		} else if (qName.equals("mp3") || "mp3".equals(localName))
		{
			rb.setMp3(sb.toString().trim());
		} else if (qName.equals("mp3size") || "mp3size".equals(localName))
		{
			rb.setMp3size(sb.toString().trim());
		} else if (qName.equals("ec") || "ec".equals(localName))
		{
			rb.setEc(sb.toString().trim());
		} else if (qName.equals("ce") || "ce".equals(localName))
		{
			rb.setCe(sb.toString().trim());
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
