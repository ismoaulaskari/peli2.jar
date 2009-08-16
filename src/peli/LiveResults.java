/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.util.ResourceBundle;

/**
 * Live results via HTTP to remote server
 * @author aulaskar
 */
public class LiveResults extends Thread {

    private ResourceBundle rules;
    private ClientHttpRequest postRequest = null;
    private boolean basicAuth = false;
    private String data = null;
    private String fileName = null;

    public LiveResults(String fileName) {
        this.rules = Constants.getInstance().getRules();
        this.fileName = fileName;

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
        System.err.print("run");
        int delay = Integer.parseInt(this.rules.getString("liveResultDelaySeconds"));
        try {
    
            this.data = Constants.getInstance().getLiveResults();
            if(this.data != null) { //send & reset
                this.sendFile(this.fileName, this.data);
                Constants.getInstance().setLiveResults(null);
            }
            this.sleep(10 + delay);
        } catch (InterruptedException ie) {
            return;
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
            InputStream is = new ByteArrayInputStream(data.getBytes());
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
            reply = serverReply.toString();
            serverReply.close();
        }
        return reply;
    }
}
