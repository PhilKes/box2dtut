package blog.gamedevelopment.box2dtutorial.views;

import blog.gamedevelopment.box2dtutorial.Box2DTutorial;
import blog.gamedevelopment.box2dtutorial.DFUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndScreen implements Screen {

	private Box2DTutorial parent;
	private Skin skin;
	private Stage stage;
	private TextureAtlas atlas;
	private AtlasRegion background;
	
	public EndScreen(Box2DTutorial box2dTutorial){
		parent = box2dTutorial;
	}
	
	@Override
	public void show() {
		// get skin
		skin = parent.assMan.manager.get("skin/glassy-ui.json");
		atlas = parent.assMan.manager.get("images/loading.atlas");
		background = atlas.findRegion("background");
		
		// create button to go back to manu
		TextButton menuButton = new TextButton("Back", skin, "small");
		
		// create button listener
		menuButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				DFUtils.log("To the MENU");
				parent.changeScreen(Box2DTutorial.MENU);			
			}
		});
		
		// create stage and set it as input processor
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage); 
		
		// create table to layout iutems we will add
		Table table = new Table();
		table.setFillParent(true);
        table.setDebug(true);
        table.setBackground(new TiledDrawable(background));
		
		//create a Labels showing the score and some credits
		Label labelScore = new Label("Your Score", skin);
		Label labelHeight = new Label("Height:\t"+parent.lastScore+" Meters", skin);
		Label labelBaskets = new Label("Baskets:\t"+parent.baskets, skin);
		Label labelTotal = new Label("Total Score\n"+(parent.lastScore+parent.baskets*10), skin);
		
		// add items to table
		table.add(labelScore).colspan(2);
		table.row().padTop(10);
		table.add(labelHeight).colspan(2).left();
		table.row().padTop(10);
		table.add(labelBaskets).colspan(2).left();
		table.row().padTop(20);
		table.add(labelTotal).colspan(2).fillX().center();
		table.row().padTop(50);
		table.add(menuButton).colspan(2);
		
		//add table to stage
		stage.addActor(table);

	}

	@Override
	public void render(float delta) {
		// clear the screen ready for next set of images to be drawn
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
		//parent.changeScreen(Box2DTutorial.MENU);
	}

	@Override
	public void resize(int width, int height) {
		// change the stage's viewport when teh screen size is changed
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}

}
