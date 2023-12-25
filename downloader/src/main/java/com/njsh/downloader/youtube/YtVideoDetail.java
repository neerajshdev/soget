package com.gd.downloader.youtube;

public class YtVideoDetail
{
    public static class Thumbnail
    {
        private String url;
        private int width;
        private int height;

        public String getUrl()
        {
            return url;
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }
    }

    private String title;
    private String[] keywords;
    private int lengthSeconds;
    private Thumbnail[] thumbnails;


    public String getTitle()
    {
        return title;
    }

    public String[] getKeywords()
    {
        return keywords;
    }

    public int getLengthSeconds()
    {
        return lengthSeconds;
    }

    public void setThumbnails(Thumbnail[] thumbnails)
    {
        this.thumbnails = thumbnails;
    }

    public Thumbnail[] getThumbnails()
    {
        return thumbnails;
    }
}
