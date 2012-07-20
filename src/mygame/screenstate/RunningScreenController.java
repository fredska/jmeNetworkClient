/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.screenstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author root
 */
public class RunningScreenController extends AbstractAppState implements ScreenController{
    private Nifty nifty;
    private Application app;
    private Screen screen;
    private String attribute;
    
    private String[] towerNames;
    public RunningScreenController() {
        /** Constructors here! **/
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        /** init the screen */
        this.app = app;
        this.attribute = "";
        towerNames = new String[]{"Tower_1","Tower_2","Tower_3","Tower_4"};
    }

    @Override
    public void update(float tpf) {
        /** any main loop action happens here */
        if(screen != null && this.attribute != null)
            screen.findElementByName("item_details").getRenderer(TextRenderer.class).setText(this.attribute);
        
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
        if(value.isEmpty())
            this.attribute = "";
        else if(value.equalsIgnoreCase("Tower_1"))
            this.attribute = "This tower is your standard Gun tower.. Bullets be damned!";
        else if(value.equalsIgnoreCase("Tower_2"))
            this.attribute = "This tower is laser based; A single beam of SUNSHINE!";
    }
    
    public String getTowerName(String value){
        if(value == null || value.isEmpty()) return "";
        int val = Integer.parseInt(value);
        if(val < towerNames.length && val > 0)
            return towerNames[Integer.parseInt(value)];
        else
            return "";
    }
    
    public String[] getTowerNames(){
        return towerNames;
    }

}