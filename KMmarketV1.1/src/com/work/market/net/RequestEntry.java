package com.work.market.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class RequestEntry
{
	public final static int NET_SEARCH_TYPE = 0;
	public final static int FEEDBACK_TYPE = 1;
	public final static int NET_VOICE_TYPE = 2;
	public String url;
    public int             _type     = 0;
    public String          _id       = null;
    public HttpRequestBase _request  = null;
    public HttpResponse    _response = null;

    public RequestEntry(int type, HttpRequestBase request)
    {
        this(type, null, request);
    }

    public RequestEntry(int type, String id, HttpRequestBase request)
    {
        _type = type;
        _id = id;
        _request = request;
    }
    
    public RequestEntry(int type, String id, HttpRequestBase request, String url)
    {
        _type = type;
        _id = id;
        _request = request;
        this.url = url;
    }
}