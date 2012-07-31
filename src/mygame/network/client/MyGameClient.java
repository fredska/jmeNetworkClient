/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.network.client;

import com.aeoniansoftware.network.BaseFieldGameMessage;
import com.aeoniansoftware.network.FieldGameMessageSerializer;
import com.aeoniansoftware.network.IFieldGameMessage;
import com.aeoniansoftware.network.JavaUtilFieldGameMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.Nifty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import mygame.HelloMessage;
import mygame.screenstate.RunningScreenController;

/**
 *
 * @author Work
 */
public class MyGameClient extends SimpleApplication implements MessageListener, ClientStateListener {
    
    private int PLAYER_NUMBER = 2;
    private boolean startLoop = false;
    
    //Gui ScreenState
    RunningScreenController startScreen;
    
    Client myClient;
    
    //A static node to handle other players connected to the server
    public static List<Node> otherPlayers;
    private List<IFieldGameMessage> messagequeue = new LinkedList<IFieldGameMessage>();
    public static Material basicMaterial;
    
    @Override
    public void simpleInitApp()
    {
        this.setPauseOnLostFocus(false);
        inputManager.setCursorVisible(true); // Allows Nifty to utilize the mouse
        flyCam.setDragToRotate(true);
        
        setDisplayFps(false);
        setDisplayStatView(false);
        //Register all Serialized Classes
        Serializer.registerClass(HelloMessage.class);
        FieldGameMessageSerializer.setMessageImplType(JavaUtilFieldGameMessage.class);
		FieldGameMessageSerializer serializer = new FieldGameMessageSerializer() ;
        Serializer.registerClass(IFieldGameMessage.class, serializer);
		Serializer.registerClass(JavaUtilFieldGameMessage.class, serializer);
        
        
        /*
         * Load the GUI Interface here!
         */
        startScreen = new RunningScreenController();
        stateManager.attach(startScreen);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        
        nifty.fromXml("Interface/RunningScreen.xml", "start", startScreen);
        
        //flyCam.setDragToRotate(true); // you need the mouse for clicking now 
        
        //Set Basic Material for listener
        basicMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        basicMaterial.setColor("Color", ColorRGBA.randomColor());
        
        try
        {
            myClient = Network.connectToServer("74.215.143.110", 6013, 6014);
            //myClient.addMessageListener(new ClientListener(), HelloMessage.class);
            myClient.addMessageListener(this, IFieldGameMessage.class);
            myClient.addClientStateListener(this);
            //myClient.addMessageListener(new ClientFieldMessageListener(), JavaUtilFieldGameMessage.class);
            
            
            myClient.start();
            
            setupLevel();
            
            //Start otherPlayers here
            otherPlayers = new ArrayList<Node>();
            
            //Speed up camera movement
            flyCam.setMoveSpeed(5f);
        }
        catch(IOException ie){ie.printStackTrace();}
       
        
    }
    
    
    private void setupLevel()
    {
        
        cam.setLocation(new Vector3f(0, 2f, -4f));
        cam.lookAtDirection(new Vector3f(0,-1, 3), Vector3f.UNIT_Y);
        Geometry geom = new Geometry("ground", new Box(50, 0.1f, 50));
        Material ground_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ground_mat.setColor("Color", ColorRGBA.Brown);
        geom.setMaterial(ground_mat);
        rootNode.attachChild(geom);
        
        Geometry box_1 = new Geometry("Box1", new Box(Vector3f.UNIT_X, 0.25f,0.25f,0.25f));
        Material box_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box_mat.setColor("Color", ColorRGBA.Red);
        box_1.setMaterial(box_mat);
        rootNode.attachChild(box_1);
        
        Geometry box_2 = new Geometry("Box2", new Box(Vector3f.UNIT_Y, 0.25f,0.25f,0.25f));
        Material box2_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box2_mat.setColor("Color", ColorRGBA.Green);
        box_2.setMaterial(box2_mat);
        rootNode.attachChild(box_2);
        
        Geometry box_3 = new Geometry("Box3", new Box(Vector3f.UNIT_Z, 0.25f,0.25f,0.25f));
        Material box3_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box3_mat.setColor("Color", ColorRGBA.Blue);
        box_3.setMaterial(box3_mat);
        rootNode.attachChild(box_3);
        
        
    }

    float globalTimer = 0;
    @Override
    public void simpleUpdate(float tpf) {
        if(startLoop)
        {
            //Process the Message Queue
            IFieldGameMessage fgm;
                       
            if(!messagequeue.isEmpty()) {
                fgm = messagequeue.get(0);
            } else {
                fgm = null;
            }

            if(fgm != null)
            {
                messagequeue.remove(0);
                updateOtherPlayers(fgm);
            }

            if(globalTimer > 0.075f)
            {
                //Send an message to the server with the player's info
                BaseFieldGameMessage thMessage = new JavaUtilFieldGameMessage();
                thMessage.setType(0xF6);
                thMessage.setOrd(Byte.MAX_VALUE);
                thMessage.setVector3f(12, cam.getLocation());
                thMessage.setQuaternion(12, cam.getRotation());
                thMessage.setInt(12, PLAYER_NUMBER);
                //thMessage.setString(12, "Hello World");
                myClient.send(thMessage);
                globalTimer = 0;
            }
        }
        
        globalTimer += tpf;
    }
    
    public void updateOtherPlayers(IFieldGameMessage fgm){
        
        Node player = (Node)rootNode.getChild("Player_"+fgm.getInt(12));
        
        //If no player found, create a new one!
        //Else update what's on the scenegraph
        if(player == null)  {
            rootNode.attachChild(createNewPlayer(fgm));
        } else {
            player.setLocalTranslation(fgm.getVector3f(12));
            player.setLocalRotation(fgm.getQuaternion(12));
        }
    }
    
    private Node createNewPlayer(IFieldGameMessage m)
    {
        Vector3f localPosition = m.getVector3f(12);
        Quaternion localRotation = m.getQuaternion(12);
        int playerName = m.getInt(12);
            Node player = new Node("Player_" + playerName);
            
            //Load player data into here, such as shape, material, etc...
            player.setLocalTranslation(localPosition);
            player.setLocalRotation(localRotation);
            player.setUserData("PlayerNum", playerName);
            
            Geometry geom_player = new Geometry("Player_Geom_"+playerName, 
                    new Sphere(10,10,0.75f));
            
            Material geom_player_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            geom_player_mat.setColor("Color", ColorRGBA.randomColor());
            geom_player.setMaterial(geom_player_mat);
            player.attachChild(geom_player);
            
            //Create some "axis"
            
            Geometry xAxis_player = new Geometry("Player_Geom_x_"+playerName,
                    new Box(Vector3f.UNIT_X, 1f, .1f, .1f));
            
            Material xAxis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            xAxis_mat.setColor("Color", ColorRGBA.Red);
            xAxis_player.setMaterial(xAxis_mat);
            player.attachChild(xAxis_player);
            
            Geometry yAxis_player = new Geometry("Player_Geom_x_"+playerName,
                    new Box(Vector3f.UNIT_Y, .1f, 1f, .1f));
            
            Material yAxis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            yAxis_mat.setColor("Color", ColorRGBA.Green);
            yAxis_player.setMaterial(yAxis_mat);
            player.attachChild(yAxis_player);
            
            Geometry zAxis_player = new Geometry("Player_Geom_x_"+playerName,
                    new Box(Vector3f.UNIT_Z, .1f, .1f, 1f));
            
            Material zAxis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            zAxis_mat.setColor("Color", ColorRGBA.Blue);
            zAxis_player.setMaterial(zAxis_mat);
            player.attachChild(zAxis_player);
            return player;
    }

    
    //Part of MessageListener
    public void messageReceived(Object source, Message m) {
        //The Callable method provides a thread-safe method for getting
        //messages and updating the scenegraph
        if(m instanceof IFieldGameMessage){
            final IFieldGameMessage fgm = (IFieldGameMessage)m;
            this.enqueue(new Callable<Void>(){

                public Void call() throws Exception {
                    messagequeue.add(fgm);
                    
                    return null;
                }
                
            });
        }
    }

    //Provides the "proper" way to close out the client
    @Override
    public void destroy() {
        myClient.close();
        super.destroy();
    }

    public void clientConnected(Client c) {
        System.out.println("Connected to " + c.getGameName());
        System.out.println("My Player ID is: " + c.getId());
        PLAYER_NUMBER = c.getId();
        
        //Start updateLoop;
        startLoop = true;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clientDisconnected(Client c, DisconnectInfo info) {
        startLoop = false;
        System.out.println("Disconnected!");
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void doStuff(){
        System.out.println("This shouldn't be happening here...");
    }
}
