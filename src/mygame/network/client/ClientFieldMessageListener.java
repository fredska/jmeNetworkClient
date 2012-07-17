/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.network.client;

import com.aeoniansoftware.network.FieldMessageListener;
import com.aeoniansoftware.network.IFieldGameMessage;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import sun.security.util.PendingException;

/**
 *
 * @author Work
 */
public class ClientFieldMessageListener extends FieldMessageListener<Client> {

    @Override
    protected void handle(IFieldGameMessage m) {
        if(MyGameClient.otherPlayers == null)
            MyGameClient.otherPlayers = new ArrayList<Node>();
        
        Vector3f localPosition = m.getVector3f(12);
        Quaternion localRotation = m.getQuaternion(12);
        int playerNum = m.getInt(12);
        
        if(MyGameClient.otherPlayers.isEmpty())
        {
            Node player = new Node("Player" + playerNum);
            Geometry geom = new Geometry("Sphere", new Sphere(6,6,1));
            geom.setMaterial(MyGameClient.basicMaterial);
            player.attachChild(geom);
            player.setLocalTranslation(localPosition);
            player.setLocalRotation(localRotation);
            
            MyGameClient.otherPlayers.add(player);
        }
        boolean playerFound = false;
        for(Node player : MyGameClient.otherPlayers)
        {
            if(player.getName().equals("Player" + playerNum))
            {
                playerFound = true;
                player.setLocalTranslation(localPosition);
                player.setLocalRotation(localRotation);
                break;
            }
        }
            
        if(!playerFound)
        {
            Node player = new Node("Player" + playerNum);
            Geometry geom = new Geometry("Sphere", new Sphere(6,6,1));
            geom.setMaterial(MyGameClient.basicMaterial);
            player.attachChild(geom);
            player.setLocalTranslation(localPosition);
            player.setLocalRotation(localRotation);

            MyGameClient.otherPlayers.add(player);
        }
    }
}
