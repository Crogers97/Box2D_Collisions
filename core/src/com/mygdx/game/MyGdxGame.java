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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.utils.TiledObjectUtil;

import static com.mygdx.game.utils.Constants.PPM;


public class MyGdxGame extends ApplicationAdapter {

	private boolean DEBUG = false;
	private final float SCALE = 2.0f;
	private OrthographicCamera camera;

	private OrthogonalTiledMapRenderer tmr;

	private TiledMap map;


	private SpriteBatch batch;
	private  Texture texture;
	private Box2DDebugRenderer b2dr;
	private World world;
	private Body player, platform;








	@Override
	public void create () {


		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w / SCALE, h / SCALE);


		world = new World(new Vector2(0, 0), false);
		b2dr = new Box2DDebugRenderer();

		texture = new Texture("Grandpa_singleR.png");
		map = new TmxMapLoader().load("Maps/untitled.tmx");
		tmr = new OrthogonalTiledMapRenderer(map);


		player = createBox(360, 200, 32, 48, false);
		platform = createBox(140, 130, 64, 32, true);
//		platform = createBox(560,230,64,32,true);

		batch = new SpriteBatch();


		TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects());


	}
	@Override
	public void render() {
		update(Gdx.graphics.getDeltaTime());
//		render
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(texture, player.getPosition().x * PPM -(texture.getWidth() / 17),player.getPosition().y * PPM -(texture.getHeight()/2));
		batch.end();

		tmr.setView(camera);
		tmr.render();

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
		tmr.dispose();
		map.dispose();
	}

	public void update(float delta){

		world.step(1/60f, 6,2);


		inputUpdate(delta);
		cameraUpdate(delta);
		tmr.setView(camera);
		batch.setProjectionMatrix(camera.combined);


	}

	public void inputUpdate(float delta){
		int horizontalForce = 0;
		int verticalForce = 0;

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				horizontalForce -=1;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){;
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
		//linear interpolation
		//a +(b-a) * lerp
		//b = target
		//a = current camera position

		position.x = camera.position.x +(player.getPosition().x * PPM - camera.position.x) * .1f;
		position.y = camera.position.y +( player.getPosition().y * PPM - camera.position.y) * .1f;
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
