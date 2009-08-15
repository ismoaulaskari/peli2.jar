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
public class LiveResults {

    private ResourceBundle rules;
    private ClientHttpRequest postRequest = null;
    private boolean basicAuth = false;

    public LiveResults() {
        this.rules = Constants.getInstance().getRules();

        //post results to website
        if (this.rules.containsKey("postLiveResultsToWeb") &&
                this.rules.getString("postLiveResultsToWeb").equalsIgnoreCase("true")) {

            try {
                this.postRequest = new ClientHttpRequest(this.rules.getString("uploadUrl")); //or fail
            } catch (IOException ex) {
                System.err.print("Starting live results failed" + ex);
            }

            if (this.rules.containsKey("userName") && this.rules.containsKey("passWord")) {
                this.basicAuth = true;
            }
        }

    }

    /**
     * send results, read response
     * @param data
     * @return
     */
    public String sendData(String data) {
        String reply = null;

        try {
            this.postRequest.setParameter("userfile", data);
            reply = makeRequest();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        return reply;
    }

    public String sendFile(String fileName, String data) {
        String reply = null;

        try {
            InputStream is = new ByteArrayInputStream(data.getBytes());
            this.postRequest.setParameter("userfile", fileName, is);
            reply = makeRequest();
            is.close();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        return reply;
    }

    public String sendFile(File file) {
        String reply = null;

        try {
            this.postRequest.setParameter("userfile", file);
            reply = makeRequest();
        } catch (IOException ex) {
            System.err.print(ex);
        }

        return reply;
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
