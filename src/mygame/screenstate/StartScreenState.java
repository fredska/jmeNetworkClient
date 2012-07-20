/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.screenstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author root
 */
public class StartScreenState extends AbstractAppState implements ScreenController{
    private Nifty nifty;
    private Application app;
    private Screen screen;
    
    public StartScreenState() {
        /** Constructors here! **/
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        /** init the screen */
        this.app = app;
    }

    @Override
    public void update(float tpf) {
        /** any main loop action happens here */
    }

    public void bind(Nifty nifty, Screen screen) {
        System.out.println("Binding of the page!");
        this.nifty = nifty;
        this.screen = screen;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
    
    
    public void doStuff(String value){
        System.out.println("Hovering over " + value);
    }

}
