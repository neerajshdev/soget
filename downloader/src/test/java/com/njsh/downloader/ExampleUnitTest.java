package com.njsh.downloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void addition_isCorrect() throws IOException
    {
        URL link = new URL("https://www.instagram.com/reel/CjHhVf7JELW/?utm_source=ig_web_copy_link");
        Document document = Jsoup.parse(link, 5000);
        System.out.println(document.html());
    }
}