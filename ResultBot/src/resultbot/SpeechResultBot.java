/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resultbot;

import com.sun.speech.freetts.*;
import com.sun.speech.freetts.audio.*;
import javax.sound.sampled.*;

/**
 *
 * @author aulaskar
 */
public class SpeechResultBot extends ResultBot {

    private boolean noisy = true;
    private Voice voice;
    private AudioPlayer voicePlayer;

    
    public SpeechResultBot() {
        String voiceName = "kevin16";
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voiceName);

        if (voice == null) {
            System.out.println("Voice not found.");
            System.exit(1);
        }

        voice.allocate();

        voicePlayer = new JavaClipAudioPlayer();
        voicePlayer.setAudioFormat(new AudioFormat(8000, 16, 1, false, true));

    }
    
       // This method is not called from anywhere yet.
    public void exit() {
        voice.deallocate();
    }

    private void speak(String input, AudioPlayer player) {
        voice.setAudioPlayer(player);
        voice.speak(input);
    }    
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        message = message.trim();

        String input = sender + " on " + channel + " says: " + message;
        speak(input, voicePlayer);


    }
}