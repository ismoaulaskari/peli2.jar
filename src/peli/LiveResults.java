/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.util.ResourceBundle;
import java.util.TimerTask;

/**
 * Live results via HTTP to remote server
 * @author aulaskar
 */
public class LiveResults extends TimerTask {

    private ResourceBundle rules;
    private ClientHttpRequest postRequest = null;
    private boolean basicAuth = false;
    private String data = null;
    private String fileName = null;

    public LiveResults(String fileName, String data) {
        this.rules = Constants.getInstance().getRules();
        this.fileName = fileName;
        this.data = data;

        //post results to website
        if (this.rules.containsKey("postLiveResultsToWeb") &&
                this.rules.getString("postLiveResultsToWeb").equalsIgnoreCase("true")) {

            try {
                this.postRequest = new ClientHttpRequest(this.rules.getString("uploadUrl")); //or fail
            } catch (IOException ex) {
                System.err.println("Starting live results failed" + ex);
            }

            if (this.rules.containsKey("userName") && this.rules.containsKey("passWord")) {
                this.basicAuth = true;
            }
        }

    }

    /**
     *  poll for sendable results
     */
    public void run() {
        int delay = Integer.parseInt(this.rules.getString("liveResultDelaySeconds"));
        try {
            Thread.sleep(1000 * delay);
        } catch (InterruptedException ex) {
        }
        if (this.data != null) { //send & reset
            this.sendFile(this.fileName, this.data);
        }
    }

    /**
     * send results, read response
     * @param data
     * @return
     */
    public void sendData(String data) {
        String reply = null;

        try {
            this.postRequest.setParameter("userfile", data);
            reply = makeRequest();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        System.err.println(reply);
    }

    public void sendFile(String fileName, String data) {
        String reply = null;

        try {
            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));                     
            this.postRequest.setParameter("userfile", fileName, is);
            reply = makeRequest();
            is.close();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        System.err.println(reply);
    }

    public void sendFile(File file) {
        String reply = null;

        try {
            this.postRequest.setParameter("userfile", file);
            reply = makeRequest();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        System.err.println(reply);
    }

    private String makeRequest() throws IOException {
        String reply = null;
        if (this.basicAuth == true) {
            Authenticator.setDefault(new HttpBasicAuthenticator(this.rules.getString("userName"), this.rules.getString("passWord")));
        }
        this.postRequest.connect();
        InputStream serverReply = this.postRequest.post();
        if (serverReply != null) {
            reply = convertStreamToString(serverReply);
            serverReply.close();
        }
        return reply;
    }

    public String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
