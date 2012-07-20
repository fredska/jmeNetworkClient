/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import mygame.network.client.MyGameClient;

/**
 *
 * @author root
 */
public class Main {
    public static void main(String[] args){
        MyGameClient app = new MyGameClient();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(800,600);
        settings.setFrameRate(60);
        
        app.setSettings(settings);
        app.start(JmeContext.Type.Display);
    }
}
