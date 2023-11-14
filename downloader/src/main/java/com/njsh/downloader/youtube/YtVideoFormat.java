package com.centicbhaiya.downloader.youtube;

public class YtVideoFormat
{
    // url to this format
    private String url;
    private String mimeType;
    private String quality;

    public YtVideoFormat(String url, String mimetype, String quality)
    {
        this.url = url;
        this.mimeType = mimetype;
        this.quality = quality;
    }


    public String getUrl()
    {
        return url;
    }

    public String getMimeType()
    {
        String type = mimeType.substring(0, mimeType.indexOf(';')) ;
        return type;
    }

    public String getQuality()
    {
        return quality;
    }

    @Override
    public String toString()
    {
        return "YtVideoFormat{" +
                "url='" + url + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", quality='" + quality + '\'' +
                '}';
    }
}
