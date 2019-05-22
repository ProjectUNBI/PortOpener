package com.unbi.portopener;

import java.io.*;
import java.net.Socket;

class ThreadProxy extends Thread {
    private Socket sClient;
    private final String SERVER_URL;
    private final int SERVER_PORT;

    ThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        this.start();
    }

    private InputStream inFromClient, inFromServer;
    private OutputStream outToClient, outToServer;

    public void closeStreams() {
        try {
            inFromClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inFromServer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outToServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            inFromClient = sClient.getInputStream();
            outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            inFromServer = server.getInputStream();
            outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = inFromClient.read(request)) != -1) {
                            outToServer.write(request, 0, bytes_read);
                            outToServer.flush();
                            //TODO CREATE YOUR LOGIC HERE
                        }
                    } catch (IOException e) {
                    }
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}