package com.gd.downloader.youtube;

public class YtVideo
{
    private YtVideoFormat[] formats;
    private YtVideoDetail detail;

    public YtVideoFormat[] getFormats()
    {
        return formats;
    }

    public YtVideoDetail getDetail()
    {
        return detail;
    }


    public void setFormats(YtVideoFormat[] formats)
    {
        this.formats = formats;
    }

    public void setDetail(YtVideoDetail detail)
    {
        this.detail = detail;
    }
}
