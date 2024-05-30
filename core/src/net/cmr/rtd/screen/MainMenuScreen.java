package net.cmr.rtd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import net.cmr.rtd.RetroTowerDefense;
import net.cmr.util.AbstractScreenEX;
import net.cmr.util.Audio;
import net.cmr.util.Sprites;
import net.cmr.util.Sprites.AnimationType;
import net.cmr.util.Sprites.SpriteType;

public class MainMenuScreen extends AbstractScreenEX {
    
	public static final int MAJORVERSION = 1;
	public static final int MINORVERSION = 0;
	public static final int PATCHVERSION = 0;

    public MainMenuScreen() {
        super(INITIALIZE_ALL);

		Table table = new Table();
		table.setFillParent(true);

		/*Image icon = new Image(Sprites.drawable(SpriteType.ICON));
		table.add(icon);
		table.row();*/

		float iconPadding = 10;

		table.add(new Image(Sprites.drawable(AnimationType.SHOOTER_TOWER_2, 0))).padRight(iconPadding);

		Label label = new Label("Retro Tower Defense", Sprites.skin(), "default");
		label.setAlignment(Align.center);
		Interpolation interpolation = Interpolation.smooth;
		float duration = 2.0f;
		float offset = 3.0f;
		label.setPosition(label.getX(), label.getY());
		label.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, offset, duration, interpolation), Actions.moveBy(0, -offset, duration, interpolation))));
		table.add(label);

		String labelType = "small";

		table.add(new Image(Sprites.drawable(SpriteType.ICE_TOWER))).padLeft(iconPadding);
		table.row();

		TextButton creation = new TextButton("Play", Sprites.skin(), labelType);
		Audio.addClickSFX(creation);
		creation.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				RetroTowerDefense game = RetroTowerDefense.getInstance(RetroTowerDefense.class);
                game.setScreen(new SelectionScreen());
			}
		});
		table.add(creation).padLeft(100.0f).padRight(100.0f).space(10.0f).colspan(3).fillX();
		table.row();

		// TODO: Create level creation AND selection screen
		/*TextButton textButton = new TextButton("Play", Sprites.skin(), labelType);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameManagerDetails details = new GameManagerDetails();

				LevelSave levelSave = new LevelSave("level_one");
				RetroTowerDefense game = RetroTowerDefense.getInstance(RetroTowerDefense.class);
				game.joinSingleplayerGame(details, levelSave, "default", "quest_one");

				//GameSave save = new GameSave("testSave");
				//RetroTowerDefense.getInstance(RetroTowerDefense.class).joinSingleplayerGame(details, save);
			}
		});
		table.add(textButton).padLeft(100.0f).padRight(100.0f).space(10.0f).fillX();
		table.row();*/

		TextButton editor = new TextButton("Editor", Sprites.skin(), labelType);
		Audio.addClickSFX(editor);
		editor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				FileHandle handle = Gdx.files.external("editorWorld.dat");
				fadeToScreen(new EditorScreen(handle), .5f, Interpolation.linear, false);
			}
		});
		table.add(editor).padLeft(100.0f).padRight(100.0f).space(10.0f).colspan(3).fillX();
		table.row();

		TextButton textButton = new TextButton("Settings", Sprites.skin(), labelType);
		Audio.addClickSFX(textButton);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				RetroTowerDefense game = RetroTowerDefense.getInstance(RetroTowerDefense.class);
				game.setScreen(new SettingsScreen());	
			}
		});
		table.add(textButton).padLeft(100.0f).padRight(100.0f).space(10.0f).colspan(3).fillX();
		table.row();

		textButton = new TextButton("Exit", Sprites.skin(), labelType);
		Audio.addClickSFX(textButton);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		table.add(textButton).padLeft(100.0f).padRight(100.0f).space(10.0f).colspan(3).fillX();
		add(Align.center, table);

		Table table1 = new Table();
		table1.setFillParent(true);
		Table leftBottomTable = new Table();
		table1.add(leftBottomTable).expand().align(Align.bottomLeft);
		leftBottomTable.add(new Label("RTD v"+MAJORVERSION+"."+MINORVERSION+"."+PATCHVERSION, Sprites.skin(), "small")).pad(5.0f);
		add(Align.bottomLeft, table1);

		Table table2 = new Table();
		table2.setFillParent(true);

		Table icongroup = new Table();
		table2.add(icongroup).expand().align(Align.bottomRight);
		int size = 30;

		TextButton credits = new TextButton("Credits", Sprites.skin(), labelType);
		Audio.addClickSFX(credits);
		final Stage stage = this.stages.get(Align.center);
		credits.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				RetroTowerDefense game = RetroTowerDefense.getInstance(RetroTowerDefense.class);

				LabelStyle small = Sprites.skin().get("small", LabelStyle.class);

				Dialog creditsDialog = new Dialog("Credits", Sprites.skin());
				creditsDialog.pad(20);
				creditsDialog.padTop(50);
				creditsDialog.text("Programming, Music, Art: Colten Reissmann", small);
				creditsDialog.getContentTable().row();
				creditsDialog.text("Beta Testing: SirPotato42", small);
				creditsDialog.getContentTable().row();

				TextButton button = new TextButton("Close", Sprites.skin(), "small");
				button.pad(0, 15, 0, 15);

				creditsDialog.button(button, false);
				creditsDialog.show(stage);
			}
		});

		icongroup.add(credits).pad(5).width(credits.getWidth()+15);
		ImageButtonStyle style2 = new ImageButtonStyle();
		int patch = 5;
		style2.down = new NinePatchDrawable(new NinePatch(Sprites.sprite(SpriteType.BORDER_DOWN), patch, patch, patch, patch));
        style2.up = new NinePatchDrawable(new NinePatch(Sprites.sprite(SpriteType.BORDER_DEFAULT), patch, patch, patch, patch));
        style2.over = new NinePatchDrawable(new NinePatch(Sprites.sprite(SpriteType.BORDER_HOVER), patch, patch, patch, patch));
        style2.checked = new NinePatchDrawable(new NinePatch(Sprites.sprite(SpriteType.BORDER_DEFAULT), patch, patch, patch, patch));
        style2.disabled = new NinePatchDrawable(new NinePatch(Sprites.sprite(SpriteType.BORDER_DISABLED), patch, patch, patch, patch));
		style2.imageUp = Sprites.drawable(SpriteType.TROPHY);
		icongroup.add(new ImageButton(style2)).pad(5.0f).size(size);

		add(Align.bottomRight, table2);
    }

    @Override
    public void render(float delta) {
        game.batch().setColor(Color.WHITE);
        super.render(delta);
    }
    
    @Override
    public void hide() {
        super.hide();
    }

}
