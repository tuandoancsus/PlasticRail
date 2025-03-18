package game;

import tage.*;
import tage.input.InputManager;
import tage.nodeControllers.RotationController;
import tage.nodeControllers.BouncingController;
import tage.shapes.*;

import java.lang.Math;
import java.util.HashSet;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.joml.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;

	private int counter = 0;
	private double lastFrameTime, currFrameTime, elapsTime;

	private InputManager im;
	private GameObject floor, cub, tor, sphere, avatar, x, y, z, diamond, cubP, torP, sphereP;
	private Set<GameObject> defusedObjects = new HashSet<>(); // Track counted objects
	private ObjShape cubS, torS, sphereS, dolS, linxS, linyS, linzS, diamondS, cubPS, torPS, spherePS;
	private TextureImage doltx, wood, world, fluff, grass, detonated, safe, face, floorTile;
	private Light light1;
	private CameraOrbit3D orbitController;
	private Plane floorS;
	private RotationController rc;
	private BouncingController bc;

	private boolean youLose = false;

	public MyGame() { super(); }

	public static void main(String[] args)
	{	MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{	dolS = new ImportedModel("dolphinHighPoly.obj");
		torS = new Torus(0.5f, 0.2f, 48);
		cubS = new Cube();
		sphereS = new Sphere();

		// Small parts from satellites
		torPS = new Sphere();
		spherePS = new Sphere();
		cubPS = new Sphere();

		linxS = new Line(new Vector3f(0f,0f,0f), new Vector3f(3f,0f,0f));
		linyS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,3f,0f));
		linzS = new Line(new Vector3f(0f,0f,0f), new Vector3f(0f,0f,-3f));
		diamondS = new ManualShape();
		floorS = new Plane();
	}

	@Override
	public void loadTextures() {
		doltx = new TextureImage("Dolphin_HighPolyUV.png");
		wood = new TextureImage("wood.jpg");
		world = new TextureImage("earthmap1k.jpg"); 
		fluff = new TextureImage("fluff.jpg");
		grass = new TextureImage("grass.jpg");
		detonated = new TextureImage("ODG2510.jpg");
		safe = new TextureImage("safe.png");
		face = new TextureImage("face.png");
		floorTile = new TextureImage("1190.jpg");

	}

	@Override
	public void buildObjects()
	{   
		Matrix4f initialTranslation, initialScale, initialRotation;

		// Build dolphin avatar
		avatar = new GameObject(GameObject.root(), dolS, doltx);
		initialTranslation = (new Matrix4f()).translation(-1f, 1f, 1f); // Set y = 0
		avatar.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135.0f));
		avatar.setLocalRotation(initialRotation);

		// Build cube
		cub = new GameObject(GameObject.root(), cubS, wood, grass, detonated, safe);
		initialTranslation = (new Matrix4f()).translation(5, .25f, 0); // Set y = 0
		initialScale = (new Matrix4f()).scaling(0.5f);
		cub.setLocalTranslation(initialTranslation);
		cub.setLocalScale(initialScale);

		// Build torus along Z axis
		tor = new GameObject(GameObject.root(), torS, fluff, grass, detonated, safe);
		initialTranslation = (new Matrix4f()).translation(0, .125f, -5); // Set y = 0
		tor.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(0.25f);
		tor.setLocalScale(initialScale);
		tor.getRenderStates().setTiling(1);
		tor.getRenderStates().setTileFactor(20);
		System.out.println(tor.getParent());

		// Build sphere
		sphere = new GameObject(GameObject.root(), sphereS, world, grass, detonated, safe);
		initialTranslation = (new Matrix4f()).translation(3, .25f, -2); // Set y = 0
		initialScale = (new Matrix4f()).scaling(0.5f);
		sphere.setLocalTranslation(initialTranslation);
		sphere.setLocalScale(initialScale);

		initialScale = (new Matrix4f().scaling(.08f));  
		// Building small parts
		torP = new GameObject(GameObject.root(), torPS, wood);
		sphereP = new GameObject(GameObject.root(), spherePS, wood);
		cubP = new GameObject(GameObject.root(), cubPS, wood);
		torP.setLocalScale(initialScale);
		sphereP.setLocalScale(initialScale);
		cubP.setLocalScale(initialScale);

		(torP.getRenderStates()).disableRendering();
		(sphereP.getRenderStates()).disableRendering();
		(cubP.getRenderStates()).disableRendering();


		// Add X, Y, -Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
		(y.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
		(z.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

		floor = new GameObject(GameObject.root(), floorS, floorTile);
		floor.setLocalScale((new Matrix4f()).scaling(10.0f));
		floor.getRenderStates().setTiling(1);
		floor.getRenderStates().setTileFactor(20);

		// Build manual shape (diamond)
		diamond = new GameObject(GameObject.root(), diamondS, face, grass, detonated, safe);
		initialTranslation = (new Matrix4f()).translation(5, 0f, -5); // Set y = 0
		initialScale = (new Matrix4f()).scaling(0.5f);
		diamond.setLocalTranslation(initialTranslation);
		diamond.setLocalScale(initialScale);
	}


	@Override
	public void initializeLights()
	{	
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));

		(engine.getSceneGraph()).addLight(light1);
	}

	@Override
	public void initializeGame()
	{	lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);


		// ----------------- initialize camera ----------------
		im = engine.getInputManager();
		String gpName = im.getFirstGamepadName();// If gamepad plugged in, uncomment.
		//String gpName = im.getKeyboardName();		// For testing without gamepad.
		createViewports();
		Camera c = (engine.getRenderSystem())
		.getViewport("LEFT").getCamera();
		Camera rightCam = (engine.getRenderSystem())
		.getViewport("RIGHT").getCamera();
		orbitController = new CameraOrbit3D(
		c, avatar, gpName, engine);

		// ----------------- INPUTS SECTION -----------------------------
		FwdAction controllerFwdAction = new FwdAction(getAvatar(), this);
		FwdAction fwdAction = new FwdAction(getAvatar(), this, 1.0f);
		FwdAction bwdAction = new FwdAction(getAvatar(), this, -1.0f);
		ZoomAction camZoomIn = new ZoomAction(rightCam, 1.0f);
		ZoomAction camZoomOut = new ZoomAction(rightCam, -1.0f);
		PanAction camPanDown = new PanAction(rightCam, 1.0f);
		PanAction camPanUp = new PanAction(rightCam, -1.0f);
		PanAction camPanRight = new PanAction(rightCam, 2.0f);
		PanAction camPanLeft = new PanAction(rightCam, -2.0f);
		TurnAction turnAction = new TurnAction(getAvatar(), this);
		TurnAction turnActionRight = new TurnAction(getAvatar(), this, -1.0f);
		TurnAction turnActionLeft = new TurnAction(getAvatar(), this, 1.0f);
		PitchAction rotateUp = new PitchAction(getAvatar(), this, 1.0f);
		PitchAction rotateDown = new PitchAction(getAvatar(), this, -1.0f);
		RollAction rollRight = new RollAction(getAvatar(), this, 1.0f);
		RollAction rollLeft = new RollAction(getAvatar(), this, -1.0f);
		DefuseObject defuseAction = new DefuseObject(this);


		im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, controllerFwdAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.X, turnAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._0, defuseAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.W, fwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.S, bwdAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.D, turnActionRight,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.A, turnActionLeft,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.UP, rotateUp,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.DOWN, rotateDown,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.RIGHT, rollRight,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.LEFT, rollLeft,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.O, camZoomIn,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.P, camZoomOut,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.V, camPanUp,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.B, camPanDown,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.N, camPanRight,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.M, camPanLeft,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
		net.java.games.input.Component.Identifier.Key.E, defuseAction,
		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// ----------------- NODE CONTROLLERS -----------------------------
		rc = new RotationController( engine, new Vector3f(0,1,0), 0.001f);
		rc.addTarget(sphereP);
		rc.addTarget(torP);
		rc.addTarget(cubP);

		bc = new BouncingController();
		(engine.getSceneGraph()).addNodeController(bc);

		bc.toggle();
		
		(engine.getSceneGraph()).addNodeController(rc);
		// Toggles the rc
		rc.toggle();
	}

	@Override
	public void update()
	{	
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;
		// sphere.setLocalRotation((new Matrix4f()).rotation((float)elapsTime, 0, 1, 0)); // Rotation of sphere before Node Controller

		// build and set HUD
		int elapsTimeSec = Math.round((float)elapsTime);
		String counterStr = Integer.toString(counter);
		Vector3f hud1Color = new Vector3f(1,1,1);
		String dispStr1 = "Score = " + counterStr;
		String avatarWorldPos = avatar.getWorldLocation().toString();

		(engine.getHUDmanager()).setHUD1(
			dispStr1, 
			hud1Color, 
			(int)((engine.getRenderSystem()).getViewport("LEFT").getActualWidth() * .10) - ( dispStr1.length() / 2), 
			(int)((engine.getRenderSystem()).getViewport("LEFT").getActualHeight() * .10));
		(engine.getHUDmanager()).setHUD2(
			avatarWorldPos, 
			hud1Color, 
			(int)((engine.getRenderSystem()).getViewport("RIGHT").getActualLeft() 
				  + ((engine.getRenderSystem()).getViewport("RIGHT").getActualWidth() * .10) - (avatarWorldPos.length() / 2)), 
			(int)((engine.getRenderSystem()).getViewport("RIGHT").getActualBottom() * .10)
		);
		
		if (getLose()) {
			String dispStr2 = "YOU LOSE!";
			Vector3f redColor = new Vector3f(1,0,0);

			(engine.getHUDmanager()).setHUD1(dispStr2, redColor, (int)(((engine.getRenderSystem()).getViewport("LEFT").getActualWidth() / 2)
				 - ( dispStr2.length() / 2)), 
			(int)((engine.getRenderSystem()).getViewport("LEFT").getActualHeight() / 2));
			return;
		}

		// update inputs and camera
		im.update((float)elapsTimeSec);

		// Camera Control
		orbitController.updateCameraPosition();

		// Check if dolphin is close enough to disarm.
		updateDefusableStatus();

		if (counter == 3){ // Need to add Iterator 
			Vector3f hud3Color = new Vector3f(1,0,0);
			String dispStr3 = "YOU WIN!";

			(engine.getHUDmanager()).setHUD1(dispStr3, hud3Color, (int)(((engine.getRenderSystem()).getViewport("LEFT").getActualWidth() / 2)
			- ( dispStr3.length() / 2)), 
	   (int)((engine.getRenderSystem()).getViewport("LEFT").getActualHeight() / 2));			return;
		}
	}


	public Camera getCamera() {
		Camera cam;
		return cam = (engine.getRenderSystem()
			.getViewport("LEFT").getCamera());
	}


	public void updateDefusableStatus() {
		Vector3f avatarPos = avatar.getWorldLocation();
		float defuseDistanceMin = 1.0f;
		float defuseDistanceMax = 2.0f;
		
		GameObject[] gameObjectsArr = {cub, sphere, tor};
		
		for (GameObject obj : gameObjectsArr) {
			Vector3f objPos = obj.getWorldLocation();
			float avatarDistance = new Vector3f(avatarPos).sub(objPos).length();
			
			// If object is already detonated, simply set its detonation texture.
			if (obj.getDetonated()) {
				obj.setTextureImage(obj.getDetTexture());
			}
			// If object is defused, update its safe texture.
			else if (obj.getDefused()) {
				obj.setTextureImage(obj.getSafeTexture());
				hudDisarmed();
			}
			// If the avatar is too close (inside the minimum range), detonate the object.
			else if (avatarDistance < defuseDistanceMin) {
				setLose(); // Game over logic here
				obj.setDetonated(true);
				obj.setTextureImage(obj.getDetTexture());
			}
			// If the avatar is within the defuse range, mark it as "ready to defuse"
			else if (avatarDistance < defuseDistanceMax && avatarDistance > defuseDistanceMin) {
				obj.setDefusedArea(true);
				if (obj.getDefusedArea()) {
					obj.setTextureImage(obj.getDefusedTexture()); // highlighted ready to defuse
					hudClose();
				}
			}
			// Otherwise, revert to the original texture.
			else {
				obj.setDefusedArea(false);
				obj.setTextureImage(obj.getOriginalTexture());
			}
		}
	}

	private float deltaX = 0.1f;

	public void defuseNearbyObjects() {
		Vector3f avatarPos = avatar.getWorldLocation();
		float defuseDistanceMin = 1.0f;
		float defuseDistanceMax = 2.0f;
		
		GameObject[] gameObjectsArr = {cub, sphere, tor};
		
		for (GameObject obj : gameObjectsArr) {
			float avatarDistance = new Vector3f(avatarPos).sub(obj.getWorldLocation()).length();
			
			// Only defuse if the object is in range and is not already detonated or defused.
			if (!obj.getDetonated() && !obj.getDefused() &&
				(avatarDistance < defuseDistanceMax && avatarDistance > defuseDistanceMin)) {
				obj.setDefused(true);
				if (!defusedObjects.contains(obj)) {
					counter++;
					defusedObjects.add(obj);

					if (obj == cub) {
						cubP.setParent(avatar);
						(cubP.getRenderStates()).enableRendering();
						cubP.applyParentRotationToPosition(true);
						cubP.setLocalTranslation(new Matrix4f().translation(new Vector3f(deltaX, 0.2f, 0.0f))); 
					} else if (obj == sphere) {
						sphereP.setParent(avatar);
						(sphereP.getRenderStates()).enableRendering();
						sphereP.applyParentRotationToPosition(true);
						sphereP.setLocalTranslation(new Matrix4f().translation(new Vector3f(deltaX, 0.2f, 0.0f))); 
					} else if (obj == tor) {
						torP.setParent(avatar);
						(torP.getRenderStates()).enableRendering();
						torP.applyParentRotationToPosition(true);
						torP.setLocalTranslation(new Matrix4f().translation(new Vector3f(deltaX, 0.2f, 0.0f))); 
					}

					if (defusedObjects.contains(obj)) {
						deltaX -= .09f;
					}
					bc.addTarget(obj);
				}
			}
		}
	}
	
	
	public boolean getLose() { return youLose; }


	public GameObject getAvatar() { return avatar; }


	public void setLose() {
		youLose = true;
	}

	public void hudClose() {
		String counterStr = Integer.toString(counter);
		String closeEnough = "Score = " + counterStr + " Close enough.";
		Vector3f hud1Color = new Vector3f(1, 0, 0); // Red color
	
		// Set HUD1 with the adjusted relative position
		(engine.getHUDmanager()).setHUD1(closeEnough, hud1Color, (int)((engine.getRenderSystem()).getViewport("LEFT").getActualWidth() * .10), (int)((engine.getRenderSystem()).getViewport("LEFT").getActualHeight() * .10));
	}
	
	public void hudDisarmed() {
		String counterStr = Integer.toString(counter);
		String satDisarmed = "Score = " + counterStr + " Satellite disarmed.";
		Vector3f hud1Color = new Vector3f(0, 0, 1); // Blue color
	
		// Set HUD1 with the adjusted relative position
		(engine.getHUDmanager()).setHUD1(satDisarmed, hud1Color, (int)((engine.getRenderSystem()).getViewport("LEFT").getActualWidth() * .10), (int)((engine.getRenderSystem()).getViewport("LEFT").getActualHeight() * .10));
	}
	

	/**
	 * Creates two viewports: one for the left side of the screen and one for the right side.
	 * 
	 * The left viewport takes up the whole screen, while the right viewport occupies the bottom-right 
	 * corner. The method sets up cameras for both viewports, adjusting their position and orientation.
	 * The right viewport also has a green border.
	 */
	@Override
	public void createViewports() { 
		(engine.getRenderSystem()).addViewport("LEFT",0,0,1f,1f);
		(engine.getRenderSystem()).addViewport("RIGHT",.75f,0,.25f,.25f);
		Viewport leftVp = (engine.getRenderSystem()).getViewport("LEFT");
		Viewport rightVp = (engine.getRenderSystem()).getViewport("RIGHT");
		Camera leftCamera = leftVp.getCamera();
		Camera rightCamera = rightVp.getCamera();
		rightVp.setHasBorder(true);
		rightVp.setBorderWidth(4);
		rightVp.setBorderColor(0.0f, 1.0f, 0.0f);
		leftCamera.setLocation(new Vector3f(-2,0,2));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,-1));
		rightCamera.setLocation(new Vector3f(0,2,0));
		rightCamera.setU(new Vector3f(1,0,0));
		rightCamera.setV(new Vector3f(0,0,-1));
		rightCamera.setN(new Vector3f(0,-1,0));
	}


	/**
	 * This method handles the key press events and enables or disables rendering
	 * of the x, y, z axes based on the key pressed. It listens for specific key events
	 * (KeyEvent.VK_9 and KeyEvent.VK_0) to toggle the visibility of the axis objects.
	 * 
	 * <p>When the '9' key is pressed, the rendering of the x, y, z axes is disabled.</p>
	 * <p>When the '0' key is pressed, the rendering of the x, y, z axes is enabled.</p>
	 * 
	 * @param e the KeyEvent that triggered the key press.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_9:
				(x.getRenderStates()).disableRendering();
				(y.getRenderStates()).disableRendering();
				(z.getRenderStates()).disableRendering();
				break;
			case KeyEvent.VK_0:
				(x.getRenderStates()).enableRendering();
				(y.getRenderStates()).enableRendering();
				(z.getRenderStates()).enableRendering();
				break;
		}
		super.keyPressed(e);
	}
}