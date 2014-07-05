/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package balltingo;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;

/**
 *
 * @author dominuskernel
 */
public class StageOne extends SimpleApplication{
    private Spatial scene, bar, ball;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape, bar_phy, ball_phy;
    private CollisionShape barShape;
    private boolean left = false, right = false, begin=true;
    
    public static void main(String[] args){
        StageOne app = new StageOne();
        app.start();
    }
    
    @Override
    public void simpleInitApp(){
        //the camera doesn't move
        flyCam.setEnabled(false);
        //flyCam.setMoveSpeed(50);
        
        //call the class for collisions
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        //set camera location
        cam.setLocation(new Vector3f(-10f, 28f, 0f));
        
        //the camera look at direction
        cam.lookAt(new Vector3f(0f, 16.5f, 0f), Vector3f.UNIT_Y);
        
        //Declare scene and model and set location for barra
        scene = assetManager.loadModel("Scenes/stage1/balltingo.j3o");
        scene.setLocalTranslation(0, 0, 1f);
        
        bar = assetManager.loadModel("Models/barra/barra.j3o");
        bar.setLocalScale(2f);
        bar.setLocalTranslation(-10f, 6.5f, 0f);
        
        
        ball = assetManager.loadModel("Models/bola/ball.j3o");
        ball.setLocalTranslation(0f, 8f, 0f);
        ball.setLocalScale(0.5f);
        
        //set the collision scene and barra model
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node)scene);
        landscape = new RigidBodyControl(sceneShape,0f);
        scene.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        barShape = CollisionShapeFactory.createDynamicMeshShape((Node)bar);
        bar_phy = new RigidBodyControl(barShape,0f);
        bar_phy.setPhysicsLocation(new Vector3f(-10f, 6.5f, 0f));
        bar_phy.setKinematic(true);
        bar.addControl(bar_phy);
        bulletAppState.getPhysicsSpace().add(bar);
        
        ball_phy = new RigidBodyControl(1f);
        ball.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball);
        //attach scene
        rootNode.attachChild(scene);
        rootNode.attachChild(bar);
        rootNode.attachChild(ball);
        
        //call to setUpKeys method
        setUpKeys();
    }
    
    //set the controls key for barra
    public void setUpKeys(){
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener,"Left","Right","Shoot");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean keyPressed, float tpf) {
            if(binding.equals("Left")){
                left = keyPressed;
            } else if(binding.equals("Right")){
                right = keyPressed;
            } else if(binding.equals("Shoot")){
                shootBall(); 
                begin = true;
            }
        }
    };
      
    //set the ball shoot
    public void shootBall(){
        ball_phy.setLinearVelocity(Vector3f.UNIT_X.mult(20f));
    }
    
    //set the actions
    @Override
    public void simpleUpdate(float tpf){
        if(left && bar.getLocalTranslation().z >= -8.5){
            bar.move(0,0,-20*tpf);  
        }
        if(right && bar.getLocalTranslation().z <= 8.5){
            bar.move(0,0,20*tpf);
        } 
        
        float velocity = ball_phy.getLinearVelocity().getX();
        if(begin=true){
            if(velocity >=0){
                ball_phy.applyForce(new Vector3f(5f,0,0), Vector3f.ZERO);
            }else{
                ball_phy.applyForce(new Vector3f(-5f,0,0),Vector3f.ZERO);
            }
        }
    }
}
