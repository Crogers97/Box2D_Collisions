package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.game.utils.Constants.PPM;


public class MyGdxGame extends ApplicationAdapter {

	private final float SCALE = 2.0f;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private  Texture texture;
	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player, platform;

	Texture imgR;
	Texture imgL;
	Texture imgU;
	Texture imgD;
	Texture staticImgL;
	Texture staticImgR;
	Texture staticImgU;
	Texture staticImgD;


	TextureRegion[] animationFramesR;
	TextureRegion[] animationFramesL;
	TextureRegion[] animationFramesU;
	TextureRegion[] animationFramesD;
	Animation animationR;
	Animation animationL;
	Animation animationU;
	Animation animationD;

	float elapsedTime;
	float x;
	float y;

	@Override
	public void create () {

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / SCALE, h / SCALE);

		world = new World(new Vector2(0, 0), false);
		b2dr = new Box2DDebugRenderer();

		player = createBox(8, 10, 32, 48, false);
		platform = createBox(0, 0, 64, 32, true);

		batch = new SpriteBatch();
		imgR = new Texture("Grandpa_spriteR.png");
		imgL = new Texture("Grandpa_spriteL.png");
		imgU = new Texture("Grandpa_spriteU.png");
		imgD = new Texture("Grandpa_spriteD.png");


		staticImgL = new Texture("Grandpa_singleL.png");
		staticImgR = new Texture("Grandpa_singleR.png");
		staticImgU = new Texture("Grandpa_singleU.png");
		staticImgD = new Texture("Grandpa_singleD.png");


		TextureRegion[][] tmpFramesR = TextureRegion.split(imgR, 60, 48);
		TextureRegion[][] tmpFramesL = TextureRegion.split(imgL, 60, 48);
		TextureRegion[][] tmpFramesU = TextureRegion.split(imgU, 60, 48);
		TextureRegion[][] tmpFramesD = TextureRegion.split(imgD, 60, 48);

//		looping over animation
		animationFramesR = new TextureRegion[9];
		int indexR = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 1; j++) {
				animationFramesR[indexR++] = tmpFramesR[j][i];
			}
		}

		animationFramesL = new TextureRegion[9];
		int indexL = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 1; j++) {
				animationFramesL[indexL++] = tmpFramesL[j][i];
			}
		}

		animationFramesU = new TextureRegion[9];
		int indexU = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 1; j++) {
				animationFramesU[indexU++] = tmpFramesU[j][i];
			}
		}

		animationFramesD = new TextureRegion[9];
		int indexD = 0;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 1; j++) {
				animationFramesD[indexD++] = tmpFramesD[j][i];
			}
		}

	}
	@Override
	public void render() {
		update(Gdx.graphics.getDeltaTime());
//		render
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(staticImgR, player.getPosition().x * PPM -(staticImgR.getWidth() / 17),player.getPosition().y * PPM -(staticImgR.getHeight()/2));





		batch.end();

		b2dr.render(world, camera.combined.scl(PPM));



	}

	public void resize(int width, int height){
		camera.setToOrtho(false,width/SCALE,height/SCALE);

	}
	
	@Override
	public void dispose () {
		b2dr.dispose();
		world.dispose();
		batch.dispose();
	}

	public void update(float delta){

		world.step(1/60f, 6,2);


		inputUpdate(delta);
		cameraUpdate(delta);
		batch.setProjectionMatrix(camera.combined);


	}

	public void inputUpdate(float delta){
		int horizontalForce = 0;
		int verticalForce = 0;

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				horizontalForce -=1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){batch.draw((TextureRegion) animationR.getKeyFrame(elapsedTime,true), x, y);
				horizontalForce +=1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
				verticalForce +=1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
				verticalForce -=1;
		}


		player.setLinearVelocity(horizontalForce * 5, player.getLinearVelocity().y);
		player.setLinearVelocity( player.getLinearVelocity().x, verticalForce * 5);

	}

	public void cameraUpdate(float delta){
		Vector3 position = camera.position;
		position.x = player.getPosition().x * PPM;
		position.y = player.getPosition().y * PPM;
		camera.position.set(position);

		camera.update();
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic){

		Body pBody;
		BodyDef def = new BodyDef();


		if(isStatic)
			def.type = BodyDef.BodyType.StaticBody;
		else
			def.type = BodyDef.BodyType.DynamicBody;

		def.position.set(x/PPM,y/PPM);
		def.fixedRotation = true;
		pBody = world.createBody(def);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2/PPM,height/2/PPM);

		pBody.createFixture(shape, 1.0f);
		shape.dispose();

		return pBody;
	}


}
