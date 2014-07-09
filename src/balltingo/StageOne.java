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
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.sun.org.apache.bcel.internal.classfile.Code;

/**
 *
 * @author dominuskernel
 */
public class StageOne extends SimpleApplication{
    private Node boxNode;
    private Spatial scene, bar, ball, box[][];
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape, bar_phy, ball_phy, box_phy[][];
    private CollisionShape barShape, boxShape;
    private boolean left = false, right = false, begin=false;
    private Vector3f disappear;
    
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
       // bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        //set camera location
        cam.setLocation(new Vector3f(-10f, 28f, 0f));
        
        //the camera look at direction
        cam.lookAt(new Vector3f(0f, 16.5f, 0f), Vector3f.UNIT_Y);
        
        //Declare scene and models and set location for bar, ball and the boxes
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
        bulletAppState.getPhysicsSpace().addCollisionObject(ball_phy);
        ball_phy.setCollisionGroup(0);
        
        //declare boxNode
        boxNode = new Node("Boxes");
        
        //attach stage
        rootNode.attachChild(scene);
        rootNode.attachChild(bar);
        rootNode.attachChild(ball);
        rootNode.attachChild(boxNode);
        
        //declare and attach every box
        box= new Spatial[3][9];
        box_phy = new RigidBodyControl[3][9];
        
        float x = 0;
        float z = 0;
        
        for(int f=0;f<box.length;f++){
            for(int c=0;c<box[f].length; c++){
                box[f][c] = assetManager.loadModel("Models/box/box.j3o");
                box[f][c].setLocalScale(0.5f);
                box[f][c].setLocalTranslation(22f-x,6.5f,-8.2f+z);
                boxShape = CollisionShapeFactory.createBoxShape((Node)box[f][c]);
                box_phy[f][c] = new RigidBodyControl(boxShape,0f);
                box[f][c].addControl(box_phy[f][c]);
                bulletAppState.getPhysicsSpace().add(box[f][c]);
                z = z + 2f;
                boxNode.attachChild(box[f][c]);
            }
            z = 0f;
            x = x + 4f;
        }
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
        ball_phy.setLinearVelocity(Vector3f.UNIT_X.mult(0.1f));
    }
    
    //when the ball collisions with some box. The box disappear. 
    //This method is call in simpleUpdate
    public void collisionToBox(){
        CollisionResults results = new CollisionResults();
        boxNode.collideWith(ball.getWorldBound(), results);
        if(results.size()>0){
            CollisionResult closest = results.getFarthestCollision();
            Spatial s = closest.getGeometry();
            disappear= s.getWorldTranslation();
            for(int f=0;f<box.length;f++){
                for(int c=0;c<box[f].length;c++){
                    if(disappear.distance(box[f][c].getWorldTranslation())<0.5){
                        boxNode.detachChild(box[f][c]);
                    }
                }
            }
            for(int f=0;f<box.length;f++){
                for(int c=0;c<box[f].length;c++){
                    if(disappear.distance(box[f][c].getWorldTranslation())<0.5){
                        box_phy[f][c].getPhysicsSpace().remove(box[f][c]);
                }
            }
        }
        }
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
        if(begin==true){
            if(velocity >=0){
                ball_phy.applyForce(new Vector3f(10f,0,0), Vector3f.ZERO);
                ball_phy.setLinearVelocity(ball_phy.getLinearVelocity().mult(new Vector3f(1f,0f,1f)));
            }else{
                ball_phy.applyForce(new Vector3f(-10f,0,0),Vector3f.ZERO);
                ball_phy.setLinearVelocity(ball_phy.getLinearVelocity().mult(new Vector3f(1f,0f,1f)));
            }
            
        }
        collisionToBox();
    }
}
