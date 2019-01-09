 /*
 *  Scene Graph:
 *  Scene origin
 *  |
 *  +-- [T(0.0,7.0,-21.0) R(90.0,1.0,0.0) S(26.0,1.0,0.0,0.0)] Sky plane
 *  |
 *  +-- [T(0.0,-1.0,-5.0) S(2.5,0.5,20)] Ground plane
 *  |
 *  +-- [T(0.26, -1.0, currentFenceZ) Rx(-90) S(0.5, 0.5, 0.5) ] Fence Post Right
 *  |
 *  +-- [T(-0.5f, -1.0f, currentFenceZ) Rx(-90) S(0.5, 0.5, 0.5) ] Fence Post Left
 *  |
 *  +-- [T(0.0, -0.25, currentFenceZ) S(0.755f, 0.1, 0.05) ] Fence panel top
 *  |
 *  +-- [T(0.0, -0.05, currentFenceZ) S(0.755f, 0.1, 0.05) ] Fence panel bottom
 *  |
 *  +-- [S(1.0, 1.0, 1.5) ] Sheep body
 *  |
 *  +-- [T(initialSheepBodyX-0.1f, initialSheepBodyY -0.2f, initialSheepBodyZ -1.75f) S(0.75, 0.75, 1.0)] Sheep Left eye
 *  |
 *  +-- [T(initialSheepBodyX+0.1f, initialSheepBodyY -0.2f, initialSheepBodyZ -1.75f) S(0.75, 0.75, 1.0)] Sheep right eye
 *  |
 *  +-- [T(initialSheepBodyX+0.0f, initialSheepBodyY -0.2f, initialSheepBodyZ -1.7f) S(0.75, 1.0, 1.0)] Sheep head
 *  |
 *  +-- [T(initialSheepBodyX -0.175, initialSheepBodyY -0.6, initialSheepBodyZ -1.6) S(0.05, 0.20, 0.05)] Sheep left front leg
 *  |
 *  +-- [T(initialSheepBodyX +0.175, initialSheepBodyY -0.6, initialSheepBodyZ -1.6) S(0.05, 0.20, 0.05)] Sheep right front leg
 *  |
 *  +-- [T(initialSheepBodyX -0.175, initialSheepBodyY -0.6, initialSheepBodyZ -1.4) S(0.05, 0.20, 0.05)] Sheep left back leg
 *  |
 *  +-- [T(initialSheepBodyX +0.175, initialSheepBodyY -0.6, initialSheepBodyZ -1.4) S(0.05, 0.20, 0.05)] Sheep right back leg
 *  |
 *  +-- [T(-2.1, -0.9, -5.2) Rx(-90.0) S(0.4, 0.4, 0.4)] Tree trunk
 *  |
 *  +-- [T(-2.5, -1.0, -5.0) Ry(45.0) S(0.4, 0.4, 0.4)] Tree leaves
 */
package coursework.leederjl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.opengl.Texture;

import GraphicsLab.*;

/**
 * This sample demonstrates the use of 3D translations to position objects within OpenGL's 3D environment
 * 
 * <p>Controls:
 * <ul>
 * <li>Press the escape key to exit the application.
 * <li>Hold the W, A, S, D, buttons to control the directionsal movement of the sheep
 * <li>While viewing the scene along the x, y or z axis, use the up and down cursor keys
 *      to increase or decrease the viewpoint's distance from the scene origin
 * </ul>
 * 
 * Author: Joshua Leeder
 * Date: 14/12/18
 * Version: 1.1
 *
 * <p>Adapted from Mark Bernard's LWJGL NeHe samples
 *
 * @author Anthony Jones and Dan Cornford
 */
public class Shapes extends GraphicsLab
{
	//TEXTURE INFO//
	/** ids for nearest, linear and mipmapped textures for the ground plane */
    private Texture groundTextures;
    /** ids for nearest, linear and mipmapped textures for the daytime back (sky) plane */
    private Texture skyTextures;
    
    //PLANE LIST//
    /** display list id for the cube list */
    private final int cuboidList = 1;
    /** display list id for the unit plane */
    private final int planeList = 3;
    /** display list id for the tree list */
    private final int treeList = 4;
    
    //FENCE POSITIONS//
   /** Lower bound of the fence movement */
    private final float lowestFenceZ  = -5.0f;
    /** Sets the current fence Z position to the farthest position initially */
    private float currentFenceZ = lowestFenceZ;
    /** Upper bound for the fence movement */
    private final float highestFenceZ = 5.0f;
    
    //FILE PATHS//
    /** Extension for relative pathing */
    private String path = "textures/";
    
    //SHEEP MOVEMENT
    /** X position variable for the sheep */
    private float initialSheepBodyX = 0;
    /** Y position variable for the sheep */
    private float initialSheepBodyY = 0;
    /** Z position variable for the sheep */
    private float initialSheepBodyZ = 0;
    /** Rotation of the sheeps legs */
    private float legRotation= 0.0f;
    /** Movement speed of the sheep */
    private float sheepSpeed = 0.05f;
    /** Current sheep leg height */
    private float currentLegHeight = 0.0f;
    
    //BOUNDS FOR SHEEP MOVMEMENT//
    /** Left most X bound for sheep movement */
    private final float leftMostX = -10;
    /** Right most X bound for sheep movement */
    private final float rightMostX = 10;
    /**Farthest Z bound for sheep movement */
    private final float farMostZ = -15;
    /** Nearest Z bound for sheep movement */
    private final float nearMostZ = 50;
    /** Upper bounds for leg movement */
    private final float legUpperBound = 0.1f;
    /** Lower bounds for leg movement */
    private final float legLowerBound = 0;
    /** Boolean to determine what way to move legs */
    private boolean moveLegUp = false;
    
    //BOOLEANS FOR MOVMENTENT//
    /** Tracks if the sheep should move to the left */
    private boolean moveLeft = false;
    /** Tracks if the sheep should move to the right */
    private boolean moveRight = false;
    /** Tracks if the sheep should move further from the screen */
    private boolean moveOut = false;
    /** Tracks if the sheep should move towards the screen */
    private boolean moveIn = false;
    
    
    
    public static void main(String args[]){   
    	new Shapes().run(WINDOWED,"Shapes - Coursework",0.01f);
    }

    /**
     * Setting up the variables and objects for the scene
     */
    protected void initScene() throws Exception
    {
    	// load the textures
        skyTextures = loadTexture(path +"1125885.bmp");
        groundTextures = loadTexture(path + "grass.bmp");
        
    	// Global ambient light level
        float globalAmbient[]   = {0.2f,  0.2f,  0.2f, 1f};
        
        // Set the global ambient lighting
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(globalAmbient));

        
        // The light in the scene is white so my sheep appears white and the sun can be yellow
        float diffuse0[]  = { 0.5f,  0.5f, 0.5f, 1.0f};
        // Ambient light is set to low so it doesn't over shadow any other lights
        float ambient0[]  = { 0.1f,  0.1f, 0.1f, 1.0f};
        // The sun is positioned in the upper right corner of the screen
        float position0[] = { 1.2f, 1.7f, -2.75f, 1.0f}; 

        // supply OpenGL with the properties for the light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
  		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
        
        // Enable the first light
        GL11.glEnable(GL11.GL_LIGHT0);
        
        // Enable lighting calculations
        GL11.glEnable(GL11.GL_LIGHTING);
        // Enable that all normals are re-normalised after transformations automatically
        GL11.glEnable(GL11.GL_NORMALIZE);
        
        
        // Lists
        GL11.glNewList(cuboidList,GL11.GL_COMPILE);{   
        	drawUnitCube();
        }
        GL11.glEndList();
        
        GL11.glNewList(planeList,GL11.GL_COMPILE);{   
        	drawUnitPlane();
        }
        GL11.glEndList();
        
        GL11.glNewList(treeList,GL11.GL_COMPILE);{   
        	drawTree();
        }
        GL11.glEndList();
    }
    
    /**
     * Checks and handles the user input to the scene
     */
    protected void checkSceneInput()
    {
    	if(Keyboard.isKeyDown(Keyboard.KEY_W) && initialSheepBodyZ >= farMostZ)
        {
            
            moveOut = true;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_A) && initialSheepBodyX >= leftMostX)
        {
            
        	moveLeft = true;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_S) && initialSheepBodyZ <= nearMostZ)
        {   
        	moveIn = true;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_D) && initialSheepBodyX <= rightMostX)
        {
            
        	moveRight = true;
        }
        else{
        	resetBooleans();
        }
    }
    
    /**
     * Resets the booleans move values so the sheep does not move forever after one button press
     */
    private void resetBooleans(){
    	 	 moveLeft = false;
    	     moveRight = false;
    	     moveOut = false;
    	     moveIn = false;
    }
    
    /**
     * Updates where the objects are drawn and positioned, i.e. the sheep, in the scene
     */
    protected void updateScene()
    {
    	// Logic for the fence moving in the Z plane (gets to a Z max and returns to origin and repeats)
	  currentFenceZ += 0.05f * getAnimationScale();
	  if(currentFenceZ>highestFenceZ) {
		  currentFenceZ = lowestFenceZ;
	  }
	  
	  if(moveOut == true)
      {
		  moveLegs();
		  initialSheepBodyZ -= sheepSpeed * getAnimationScale();
      }
	  
      if(moveLeft == true)
      {
    	  moveLegs();
    	  initialSheepBodyX -= sheepSpeed * getAnimationScale();
      }
      
      if(moveIn == true)
      {   
    	  moveLegs();
    	  initialSheepBodyZ += sheepSpeed * getAnimationScale();
      }
      
      if(moveRight == true)
      {
    	  moveLegs();
    	  initialSheepBodyX += sheepSpeed * getAnimationScale();
      }
    }
        
    
    /**
     * Controls the movement of the legs up and down
     */
    public void moveLegs() {
    	if(currentLegHeight >= legUpperBound) {
    		moveLegUp = false;
    	}
    	else if(currentLegHeight <= legLowerBound) {
    		moveLegUp = true;
    	}
    	
    	if(moveLegUp == true) {
    		currentLegHeight += 0.025f * getAnimationScale();
    	}
    	
    	if(moveLegUp == false) {
    		currentLegHeight -= 0.025f * getAnimationScale();
    	}
    	
    	//currentLegHeight += 0.05f * getAnimationScale();
  	  	if(currentLegHeight > highestFenceZ) {
  		  currentFenceZ = lowestFenceZ;
  	  }
    }
    /**
     * Loads all element of the scene into the window
     */
    protected void renderScene()
    {
    	// Translation that is applied to all drawings below it
    	GL11.glTranslatef(0.0f, 0.0f, -2.5f);
    	
    	GL11.glPushMatrix();
    	
    	// draw the BACK plane
        GL11.glPushMatrix();
        {
            // disable lighting calculations so that they don't affect
            // the appearance of the texture 
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            // change the geometry colour to white so that the texture
            // is bright and details can be seen clearly
            Colour.WHITE.submit();
            
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,skyTextures.getTextureID());
            
            // position, scale and draw the back plane using its display list
            GL11.glTranslatef(0.0f,7.0f,-21.0f);
            GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
            GL11.glScaled(26.0f, 1.0f, 30.0f);
            GL11.glCallList(planeList);

            // disable textures and reset any local lighting changes
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
     
        // Draw GROUND plane
        GL11.glPushMatrix();
        {
            // disable lighting calculations so that they don't affect
            // the appearance of the texture 
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            // change the geometry colour to white so that the texture
            // is bright and details can be seen clearly
            Colour.WHITE.submit();
            
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,groundTextures.getTextureID());
            
            // position, scale and draw the ground plane using its display list
            GL11.glTranslatef(0.0f,-1.0f,-10.0f);
            GL11.glScalef(40.0f, 4.0f, 40.0f);
            GL11.glCallList(planeList);
            
            // disable textures and reset any local lighting changes
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
   
    	// Drawing CUBOID base
    	GL11.glPushMatrix();
    	{
    		// Material settings
            float cuboidShininess  = 25.0f;
            float cuboidSpecular[] = {0.1f, 1.0f, 0.1f, 1.0f};
            // Forest green RGB
            float cuboidDiffuse[]  = {0.34f, 1.39f, 0.34f, 1.0f};
            
            // Set the material properties for the cuboid
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, cuboidShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(cuboidSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(cuboidDiffuse));
            
	    	// Position the base
	        GL11.glTranslatef(0.0f, -1.0f, -5.0f);
	        GL11.glScalef(2.5f, 0.5f, 20.0f);
	        
	        // Draw the base by calling the appropriate display list
	        GL11.glCallList(cuboidList);
    	}
        GL11.glPopMatrix();
    
        // Drawing FENCE I don't think the fence can be added to a draw list as it won't be static (I hope)
        GL11.glPushMatrix();
        {
        	
        	// Set the material properties
            float fencePoleFrontShininess  = 30.0f;
            float fencePoleFrontSpecular[] = {0.2f, 0.2f, 0.1f, 1.0f};
            float fencePoleFrontDiffuse[]  = {0.38f, 0.29f, 0.07f, 1.0f};
            
            // Set the material properties for the fence using
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, fencePoleFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(fencePoleFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(fencePoleFrontDiffuse));
        	
            
            //Drawing FENCE (RIGHT post)
        	GL11.glPushMatrix();
            {
            	GL11.glTranslatef(0.26f, -1.0f, currentFenceZ);
            	GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                new Cylinder().draw(0.25f, 0.25f, 2.0f, 10, 10);
            }
            GL11.glPopMatrix();
        
        
            //Draw FENCE (LEFT post)
        	GL11.glPushMatrix();
            {
            	GL11.glTranslatef(-0.5f, -1.0f, currentFenceZ);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                new Cylinder().draw(0.25f, 0.25f, 2.0f, 10, 10);
            }
            GL11.glPopMatrix();
            
            //Fence panel top
            GL11.glPushMatrix();
            {
            	// position the base
    	        GL11.glTranslatef(0.0f, -0.25f, currentFenceZ);
    	        GL11.glScalef(0.755f, 0.1f, 0.05f);
    	        GL11.glCallList(cuboidList);
            }
            GL11.glPopMatrix();
            
            //Fence panel bottom
            GL11.glPushMatrix();
            {
    	        GL11.glTranslatef(0.0f, -0.05f, currentFenceZ);
    	        GL11.glScalef(0.755f, 0.1f, 0.05f);
    	        GL11.glCallList(cuboidList);
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        
        // Draw SUN
        GL11.glPushMatrix();
        {
            // Set the material properties
            float sunFrontShininess  = 40.0f;
            float sunFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
            float sunFrontDiffuse[]  = {1.0f, 1.0f, 0.0f, 1.0f};
            
            // set the material properties for the sun using OpenGL
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, sunFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(sunFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(sunFrontDiffuse));

            // position and draw the sun using a sphere quadric object
            GL11.glTranslatef(1.2f, 1.7f, -3.0f);
            new Sphere().draw(0.15f,20,10);
        }
        GL11.glPopMatrix();
        
        // Draw SHEEP
        GL11.glPushMatrix();
        {
        	// Set the material properties
            float sheepFrontShininess  = 5.0f;
            float sheepFrontSpecular[] = {0.3f, 0.3f, 0.3f, 1.0f};
            float sheepFrontDiffuse[]  = {4.0f, 4.0f, 4.0f, 1.0f};
            
            // set the material properties for the sheep
            //Change light to white
            //Have sun reflect yellow light and sheep reflect white light
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, sheepFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(sheepFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(sheepFrontDiffuse));
            
        	// Draw sheep BODY
            GL11.glPushMatrix();
            {
    
                GL11.glTranslatef(initialSheepBodyX +0.0f, initialSheepBodyY -0.5f, initialSheepBodyZ -1.5f);
                GL11.glScalef(1.0f, 1.0f, 1.5f);
            	new Sphere().draw(0.225f,20,10);
            }
            GL11.glPopMatrix();
            
            
            
            //Opened so I can apply a black colour to all of the contents (head and legs)
            GL11.glPushMatrix();
            
        	// Set the material properties
            float sheepLimbsFrontSpecular[] = {0.1f, 0.1f, 0.1f, 1.0f};
            float sheepLimbsFrontDiffuse[]  = {0.2f, 0.2f, 0.2f, 1.0f};
            
            // set the material properties for the sheep
            //Change light to white
            //Have sun reflect yellow light and sheep reflect white light
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(sheepLimbsFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(sheepLimbsFrontDiffuse));
            
	            // Draw sheep HEAD
	            GL11.glPushMatrix();
	            {
	            	
	                // Draw sheep EYE left
	                GL11.glPushMatrix();
	                {
	                	GL11.glTranslatef(initialSheepBodyX-0.1f, initialSheepBodyY -0.2f, initialSheepBodyZ -1.75f);
	                    GL11.glScalef(0.75f, 0.75f, 1.0f);
	                    new Sphere().draw(0.05f,20,10);
	                }
	                GL11.glPopMatrix();
	                
	                // Draw sheep RIGHT eye
	                GL11.glPushMatrix();
	                {
	                	GL11.glTranslatef(initialSheepBodyX + 0.1f, initialSheepBodyY -0.2f, initialSheepBodyZ -1.75f);
	                    GL11.glScalef(0.75f, 0.75f, 1.0f);
	                    new Sphere().draw(0.05f,20,10);
	                }
	                GL11.glPopMatrix();
	                
	                //Sheep head
	                GL11.glTranslatef(initialSheepBodyX + 0.0f, initialSheepBodyY -0.25f, initialSheepBodyZ -1.7f);
	                GL11.glScalef(0.75f, 1.0f, 1.0f);
	                new Sphere().draw(0.15f,20,10);
	            }
	            GL11.glPopMatrix();
           
	            GL11.glPushMatrix();
	            {
	            	//Rotation of legs
	            	GL11.glRotatef(legRotation, 0.1f, 0.0f, 0.0f);
	            	
		            // Draw sheep LEG (it's front left, front being the head)
		            GL11.glPushMatrix();
		            {
		            	GL11.glTranslatef(initialSheepBodyX -0.175f, currentLegHeight -0.65f, initialSheepBodyZ -1.6f);
		            	GL11.glScalef(0.05f, 0.20f, 0.05f);
		    	        GL11.glCallList(cuboidList);
		            }
		            GL11.glPopMatrix();
		            
		            // Draw sheep LEG (it's front right)
		            GL11.glPushMatrix();
		            {
		            	GL11.glTranslatef(initialSheepBodyX + 0.175f, currentLegHeight-0.65f, initialSheepBodyZ -1.6f);
		            	GL11.glScalef(0.05f, 0.20f, 0.05f);
		    	        GL11.glCallList(cuboidList);
		            }
		            GL11.glPopMatrix();
		            
		            // Draw sheep LEG (it's back left)
		            GL11.glPushMatrix();
		            {
		            	GL11.glTranslatef(initialSheepBodyX -0.175f, currentLegHeight-0.65f, initialSheepBodyZ -1.4f);
		            	GL11.glScalef(0.05f, 0.20f, 0.05f);
		    	        GL11.glCallList(cuboidList);
		            }
		            GL11.glPopMatrix();
		            
		            // Draw sheep LEG (it's back right)
		            GL11.glPushMatrix();
		            {
		            	GL11.glTranslatef(initialSheepBodyX + 0.175f, currentLegHeight-0.65f, initialSheepBodyZ -1.4f);
		            	GL11.glScalef(0.05f, 0.15f, 0.05f);
		    	        GL11.glCallList(cuboidList);
		            }
		            GL11.glPopMatrix();
	            }
		        GL11.glPopMatrix();
		            
            GL11.glPopMatrix();
            
            
            // draw the TREE 
            GL11.glPushMatrix();
            {
            	
            	// Draw tree Trunk
            	GL11.glPushMatrix();
                {
                	float trunkShininess  = 10.0f;
                    float trunkSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
                    float trunkFrontDiffuse[]  = {0.8f, 0.5f, 0.0f, 1.0f};
                    
                    // set the material properties for the sun using OpenGL
                    GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, trunkShininess);
                    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(trunkSpecular));
                    GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(trunkFrontDiffuse));
                    
                	GL11.glTranslatef(-2.1f, -0.9f, -5.2f);
                    GL11.glScaled(0.4f, 0.4f, 0.4f);
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                    new Cylinder().draw(0.25f, 0.25f, 2.0f, 10, 10);
                }
                GL11.glPopMatrix();
	        
	            //Top if the tree
	            GL11.glPushMatrix();
	            	
		            float leavesShininess  = 10.0f;
	                float leavesSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
	                float leavesFrontDiffuse[]  = {0.0f, 0.7f, 0.5f, 1.0f};
	                
	                // set the material properties for the sun using OpenGL
	                GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, leavesShininess);
	                GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(leavesSpecular));
	                GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(leavesFrontDiffuse));
	            
	                GL11.glTranslatef(-2.5f, -1.0f, -5.0f);
	                GL11.glScaled(0.4f, 0.4f, 0.4f);
	                GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
	                // position, scale and draw the back plane using its display list
	                GL11.glCallList(treeList);
	
	               
	                GL11.glPopAttrib();
	            }
            GL11.glPopMatrix();
            
        }
        GL11.glPopMatrix();
        

	        
        
    }
    /**
     * Set the values for the sample's viewpoint and projection settings. This will override the 
     * default settings in the GraphicsLab base class.
     */
    protected void setSceneCamera()
    {
    	// call the default behaviour defined in GraphicsLab. This will set a default 
    	// perspective projection and default camera settings ready 
    	// for some custom camera positioning below...  
    	super.setSceneCamera();
        
        // set the actual viewpoint using the gluLookAt command. This specifies the 
    	// viewer's (x,y,z) position, the point the viewer is looking 
    	// at (x,y,z) and the view-up direction (x,y,z), typically (0,1,0) - 
    	// i.e. the y-axis defines the up direction
        //       GLU.gluLookAt(0.0f, 0.0f, 10.0f,   // viewer location        
    	//     		      0.0f, 0.0f, 0.0f,    // view point loc.
    	//   		      0.0f, 1.0f, 0.0f);   // view-up vector
   }
   
    protected void cleanupScene()
    {// empty
    }

    /**
     * Draws a unit cube using the given colours for its 6 faces
     * 
     * @param near   The colour for the cube's near face
     * @param far    The colour for the cube's far face
     * @param left   The colour for the cube's left face
     * @param right  The colour for the cube's right face
     * @param top    The colour for the cube's top face
     * @param bottom The colour for the cube's bottom face
     */
    private void drawUnitCube()
    {
        // the vertices for the cube
    	// This code is a basic square that will be scales into a cuboid 
        Vertex v1 = new Vertex(-0.5f, -0.5f,  0.5f);
        Vertex v2 = new Vertex(-0.5f,  0.5f,  0.5f);
        Vertex v3 = new Vertex( 0.5f,  0.5f,  0.5f);
        Vertex v4 = new Vertex( 0.5f, -0.5f,  0.5f);
        Vertex v5 = new Vertex(-0.5f, -0.5f, -0.5f);
        Vertex v6 = new Vertex(-0.5f,  0.5f, -0.5f);
        Vertex v7 = new Vertex( 0.5f,  0.5f, -0.5f);
        Vertex v8 = new Vertex( 0.5f, -0.5f, -0.5f);

        // draw the near face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v3.toVector(),v2.toVector(),v1.toVector(),v4.toVector()).submit();
            
            v3.submit();
            v2.submit();
            v1.submit();
            v4.submit();
        }
        GL11.glEnd();

        // draw the left face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v2.toVector(),v6.toVector(),v5.toVector(),v1.toVector()).submit();
            
            v2.submit();
            v6.submit();
            v5.submit();
            v1.submit();
        }
        GL11.glEnd();

        // draw the right face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v3.toVector(),v4.toVector(),v8.toVector()).submit();
            
            v7.submit();
            v3.submit();
            v4.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the top face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v7.toVector(),v6.toVector(),v2.toVector(),v3.toVector()).submit();
            
            v7.submit();
            v6.submit();
            v2.submit();
            v3.submit();
        }
        GL11.glEnd();

        // draw the bottom face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v1.toVector(),v5.toVector(),v8.toVector()).submit();
            
            v4.submit();
            v1.submit();
            v5.submit();
            v8.submit();
        }
        GL11.glEnd();

        // draw the far face:
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v6.toVector(),v7.toVector(),v8.toVector(),v5.toVector()).submit();
            
            v6.submit();
            v7.submit();
            v8.submit();
            v5.submit();
        }
        GL11.glEnd();
    }
    

    /**
     * Draws a plane aligned with the X and Z axis, with its front face toward positive Y.
     *  The plane is of unit width and height, and uses the current OpenGL material settings
     *  for its appearance
     */
    private void drawUnitPlane()
    {
        Vertex v1 = new Vertex(-0.5f, 0.0f,-0.5f); // left,  back
        Vertex v2 = new Vertex( 0.5f, 0.0f,-0.5f); // right, back
        Vertex v3 = new Vertex( 0.5f, 0.0f, 0.5f); // right, front
        Vertex v4 = new Vertex(-0.5f, 0.0f, 0.5f); // left,  front
        
        // draw the plane geometry. order the vertices so that the plane faces up
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v3.toVector(),v2.toVector(),v1.toVector()).submit();
            
            GL11.glTexCoord2f(0.0f,0.0f);
            v4.submit();
            
            GL11.glTexCoord2f(1.0f,0.0f);
            v3.submit();
            
            GL11.glTexCoord2f(1.0f,1.0f);
            v2.submit();
            
            GL11.glTexCoord2f(0.0f,1.0f);
            v1.submit();
        }
        GL11.glEnd();
        
        // if the user is viewing an axis, then also draw this plane
        // using lines so that axis aligned planes can still be seen
        if(isViewingAxis())
        {
            // also disable textures when drawing as lines
            // so that the lines can be seen more clearly
            GL11.glPushAttrib(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                v4.submit();
                v3.submit();
                v2.submit();
                v1.submit();
            }
            GL11.glEnd();
            GL11.glPopAttrib();
        }
    }
    
    /**
     * Sets the vertices and normals of the tree object
     */
    private void drawTree(){
    	
	/////////////FRONT FACE///////////////////////
	    
	    //Middle of the tree
	    Vertex v10 = new Vertex( -3.0f, 2.0f, 0.75f);
	    Vertex v4 = new Vertex( 5.0f, 2.0f, 0.75f);
	    Vertex v5 = new Vertex( 2.0f, 4.0f, 0.75f);
	    Vertex v9 = new Vertex( 0.0f, 4.0f, 0.75f);
	    
	    //Top of the tree
	    Vertex v8 = new Vertex( -1.5f, 4.0f, 0.75f);
	    Vertex v6 = new Vertex( 3.5f, 4.0f, 0.75f);
	    Vertex v7 = new Vertex( 1.0f, 6.0f, 0.75f);
	    
	    /////////////BACK FACE///////////////////////
	    
	    //Middle of the tree
	    Vertex v16 = new Vertex( -3.0f, 2.0f, -0.75f);
	    Vertex v17 = new Vertex( 5.0f, 2.0f, -0.75f);
	    Vertex v18 = new Vertex( 2.0f, 4.0f, -0.75f);
	    Vertex v19 = new Vertex( 0.0f, 4.0f, -0.75f);
	    
	    //Top of the tree
	    Vertex v20 = new Vertex( -1.5f, 4.0f, -0.75f);
	    Vertex v21 = new Vertex( 3.5f, 4.0f, -0.75f);
	    Vertex v22 = new Vertex( 1.0f, 6.0f, -0.75f);
	    
		
		GL11.glEnd();
	
		//near face of middle of tree (front on)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v10.toVector(), v4.toVector(),v5.toVector(),v9.toVector()).submit();
			
			v10.submit();
			v4.submit();
			v5.submit();
			v9.submit();
			
		}
		GL11.glEnd();
		
		//near face of top of tree (front on)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v6.toVector(), v7.toVector()).submit();
						
			v8.submit();
		    v6.submit();
		    v7.submit();
					
		}
		GL11.glEnd();
		
		//far face of middle (front on)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v16.toVector(), v17.toVector(), v18.toVector(),
					v19.toVector()).submit();
	
	
			v16.submit();
	        v17.submit();
	        v18.submit();
	        v19.submit();
			
		}
		GL11.glEnd();
		
		//far face of top (front on)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v20.toVector(), v21.toVector(), v22.toVector()).submit();
	
	
			v20.submit();
	        v21.submit();
	        v22.submit();
					
		}
		GL11.glEnd();
	
		//Top (from right)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v21.toVector(), v22.toVector(),
					v7.toVector()).submit();
	
			v6.submit();
			v21.submit();
			v22.submit();
			v7.submit();
	
			
	
		}
		GL11.glEnd();
		
		//Middle (from right)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v4.toVector(), v17.toVector(),
					v18.toVector()).submit();
	
	
	
			v5.submit();
			v4.submit();
			v17.submit();
			v18.submit();
	
			GL11.glEnd();
	
		}
	
		//Top (from left)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v20.toVector(), v8.toVector(), v7.toVector(),
					v22.toVector()).submit();
	
			v20.submit();
			v8.submit();
			v7.submit();
			v22.submit();
	
			GL11.glEnd();
	
		}
	
		//Middle (from left)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v16.toVector(), v10.toVector(), v9.toVector(),
					v19.toVector()).submit();
	
			v16.submit();
			v10.submit();
			v9.submit();
			v19.submit();
	
			GL11.glEnd();
	
		}
	
		//Left side under top branch (from bottom)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v9.toVector(), v19.toVector(),
					v20.toVector()).submit();
	
			v8.submit();
			v9.submit();
			v19.submit();
			v20.submit();
	
			GL11.glEnd();
	
		}
		
		//Right side under top branch (from bottom)
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v21.toVector(), v18.toVector(),
					v5.toVector()).submit();
	
			v6.submit();
			v21.submit();
			v18.submit();
			v5.submit();
	
			GL11.glEnd();
	
		}
		//Bottom of the tree new
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v10.toVector(), v4.toVector(), v17.toVector(),
					v16.toVector()).submit();
	
			v10.submit();
			v4.submit();
			v17.submit();
			v16.submit();
	
			GL11.glEnd();
	
		}
    }
}


