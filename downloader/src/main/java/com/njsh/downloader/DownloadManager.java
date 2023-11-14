package com.centicbhaiya.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadManager
{
    private final ArrayList<DownloadTask> tasks;
    private static DownloadManager singleInstance;

    private DownloadManager()
    {
        tasks = new ArrayList<>();
    }

    public static DownloadManager getInstance()
    {
        if (singleInstance == null)
        {
            singleInstance = new DownloadManager();
        }
        return singleInstance;
    }

    public void addNewTask(String url, String dir, String filename, UpdateListener listener, long taskId)
    {
        try
        {
            URL url1 = new URL(url);
            DownloadTask downloadTask = new DownloadTask(url1, dir, filename);
            downloadTask.setUpdateListener(listener);
            downloadTask.setId(taskId);
            tasks.add(downloadTask);
            downloadTask.start();
        } catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
    }


    public ArrayList<DownloadTask> getTasks()
    {
        return tasks;
    }


    public void pause(long taskId)
    {
        int taskIx = -1;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).getId() == taskId)
            {
                taskIx = i;
            }
        }

        if (taskIx != -1)
        {
            DownloadTask downloadTask = tasks.get(taskIx);
            downloadTask.pause();
        }
    }

    public void start(long taskId)
    {
        int taskIx = -1;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).getId() == taskId)
            {
                taskIx = i;
            }
        }

        if (taskIx != -1)
        {
            DownloadTask downloadTask = tasks.get(taskIx);
            downloadTask.start();
        }
    }


    public void setUpdateListener(long taskId, UpdateListener upl)
    {
        int taskIx = -1;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).getId() == taskId)
            {
                taskIx = i;
            }
        }

        if (taskIx != -1)
        {
            DownloadTask downloadTask = tasks.get(taskIx);
            downloadTask.setUpdateListener(upl);
        }
    }


    /**
     * This function only removes the task from the app and cant delete the data saved in file
     * if the task if finished.
     *
     * @param taskId
     */
    public void removeTask(long taskId)
    {
        int taskIx = -1;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).getId() == taskId)
            {
                taskIx = i;
            }
        }

        if (taskIx != -1)
        {
            DownloadTask downloadTask = tasks.remove(taskIx);
        }
    }

    /**
     * this function removes the task and also deletes the downloaded data
     */
    public void deleteTask(long taskId)
    {
        int taskIx = -1;
        for (int i = 0; i < tasks.size(); i++)
        {
            if (tasks.get(i).getId() == taskId)
            {
                taskIx = i;
            }
        }

        if (taskIx != -1)
        {
            DownloadTask downloadTask = tasks.remove(taskIx);
        }
    }
}
