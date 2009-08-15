/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

/**
 * Live results via HTTP to remote server
 * @author aulaskar
 */
public class LiveResults {

    private ResourceBundle rules;
    private ClientHttpRequest postRequest = null;

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
            this.postRequest.connect();        
            InputStream serverReply = this.postRequest.post();
            if(serverReply != null) {
                reply = serverReply.toString();
                serverReply.close();
            }

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
            this.postRequest.connect();
            InputStream serverReply = this.postRequest.post();
            is.close();
            if(serverReply != null) {
                reply = serverReply.toString();
                serverReply.close();
            }
        } catch (IOException ex) {
            System.err.print(ex);
        }

        return reply;
    }


    public String sendFile(File file) {
        String reply = null;

        try {
            this.postRequest.setParameter("userfile", file);
            this.postRequest.connect();
            InputStream serverReply = this.postRequest.post();
            if(serverReply != null) {
                reply = serverReply.toString();
                serverReply.close();
            }
        } catch (IOException ex) {
            System.err.print(ex);
        }

        return reply;
    }


}
