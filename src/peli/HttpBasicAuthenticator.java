/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peli;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Do http basic auth
 * @author aulaskar
 */
public class HttpBasicAuthenticator extends Authenticator {

    private String username,  password;

    public HttpBasicAuthenticator(String user, String pass) {
        username = user;
        password = pass;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        System.out.println("Requesting Host  : " + getRequestingHost());
        System.out.println("Requesting Port  : " + getRequestingPort());
        System.out.println("Requesting Prompt : " + getRequestingPrompt());
        System.out.println("Requesting Protocol: " + getRequestingProtocol());
        System.out.println("Requesting Scheme : " + getRequestingScheme());
        System.out.println("Requesting Site  : " + getRequestingSite());
        return new PasswordAuthentication(username, password.toCharArray());
    }
}
