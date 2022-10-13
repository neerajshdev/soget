package com.njsh.downloader.youtube;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YtParser
{
    private URL url;

    public YtParser(String url) throws MalformedURLException
    {
        this.url = new URL(url);
    }


    public YtVideo fetch() throws IOException
    {
        Document document = Jsoup.parse(url, 15000);
        Elements scriptElements = document.getElementsByTag("script");

        String content = null;
        Pattern pattern = Pattern.compile("(ytInitialPlayerResponse *?= *?)(\\{.*\\})");

        for (Element element : scriptElements)
        {
            String text = element.html();
            Matcher matcher  = pattern.matcher(text);
            if (matcher.find())
            {
                content = matcher.group(2);
            }
        }

        if (content != null)
        {
            System.out.println("json content: " + content);
            // parsing ytVideo here

            JsonElement jsonElement = JsonParser.parseString(content);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement streamingData =  jsonObject.get("streamingData");

            JsonObject streamingDataObj =  streamingData.getAsJsonObject();
            JsonElement formatElem =  streamingDataObj.get("formats");
            JsonElement adaptiveFormatElem =  streamingDataObj.get("adaptiveFormats");

            JsonArray formatsArray = formatElem.getAsJsonArray();
            JsonArray adaptiveFormatsArray = adaptiveFormatElem.getAsJsonArray();

            ArrayList<YtVideoFormat> ytVideoFormats = new ArrayList<>();
            Gson gson = new Gson();


            // add formats
            for (int i = 0; i < formatsArray.size(); i++ )
            {
                if (formatsArray.get(i).isJsonObject());
                {
                    JsonObject jsonObj = formatsArray.get(i).getAsJsonObject();
                    YtVideoFormat ytVideoFormat = gson.fromJson(jsonObj, YtVideoFormat.class);
                    ytVideoFormats.add(ytVideoFormat);
                }
            }


            // add adaptive formats
            for (int i = 0; i < adaptiveFormatsArray.size(); i++ )
            {
                if (adaptiveFormatsArray.get(i).isJsonObject());
                {
                    JsonObject jsonObj = adaptiveFormatsArray.get(i).getAsJsonObject();
                    YtVideoFormat ytVideoFormat = gson.fromJson(jsonObj, YtVideoFormat.class);
                    ytVideoFormats.add(ytVideoFormat);
                }
            }


            // get Video details
            YtVideoDetail ytVideoDetail = null;
            JsonElement videoDetailsElem = jsonObject.get("videoDetails");
            if (videoDetailsElem != null)
            {
                JsonObject videoDetailsObj = videoDetailsElem.getAsJsonObject();
                ytVideoDetail = gson.fromJson(videoDetailsObj, YtVideoDetail.class);
            }

            // thumbnails
            ArrayList<YtVideoDetail.Thumbnail> thumbnails = new ArrayList<>();
            if (videoDetailsElem != null)
            {
                JsonElement thumbnailElem = videoDetailsElem.getAsJsonObject().get("thumbnail");
                thumbnailElem = thumbnailElem.getAsJsonObject().get("thumbnails");
                JsonArray thumbnailArray =  thumbnailElem.getAsJsonArray();
                thumbnailArray.forEach(jsonElement1 ->
                {
                    YtVideoDetail.Thumbnail thumbnail = gson.fromJson(jsonElement1.getAsJsonObject(), YtVideoDetail.Thumbnail.class);
                    thumbnails.add(thumbnail);
                });
            }

            YtVideo ytVideo = new YtVideo();
            ytVideoDetail.setThumbnails(thumbnails.toArray(new YtVideoDetail.Thumbnail[0]));
            ytVideo.setDetail(ytVideoDetail);
            ytVideo.setFormats(ytVideoFormats.toArray(new YtVideoFormat[0]));

            return ytVideo;
        }

        return null;
    }
}
