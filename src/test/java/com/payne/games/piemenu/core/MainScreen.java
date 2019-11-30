package com.payne.games.piemenu.core;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.payne.games.piemenu.Demonstration;
import com.payne.games.piemenu.genericTests.*;
import com.payne.games.piemenu.individuals.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainScreen extends BaseScreen {

    public static final List<Class<? extends BaseScreen>> registeredTests = new ArrayList<Class<? extends BaseScreen>>(Arrays.asList(
            Demonstration.class,

            CenterOnActor.class,
            DifferentActorsWidget.class,
            HitInfiniteMiddle.class,
            HitOutside.class,
            MgsxTests.class,
            RotatingFillParentWidget.class,
            SelfContainedPieMenu.class,
            ShapeDrawerLimit.class,
            TableWidgets.class,

            ButtonBound.class,
            ButtonToggle.class,
            ClickDrag.class,
            ClickToggle.class,
            CustomAnimation.class,
            KeyMap.class,
            Permanent.class,
            RadialButtons.class
    ));

    private Table tableRoot;

    public MainScreen(BaseGame game) {
        super(game);

        autoDispose = false;

        tableRoot = new Table(game.skin);
        tableRoot.setFillParent(true);

        Table localTableContent = new Table(game.skin);
        ScrollPane localScrollPane = new ScrollPane(localTableContent, game.skin);
        localScrollPane.setFlickScroll(false);
        localScrollPane.setOverscroll(false, false);
        localScrollPane.setFadeScrollBars(false);

        tableRoot.add("Select A Test")
                .expandX()
                .pad(10)
                .getActor().setFontScale(1.2f);
        tableRoot.row();
        tableRoot.add(localScrollPane).grow();
        tableRoot.row();
        tableRoot.add("Press CTRL + NumPad- to switch to the previous test\n" +
                "Press CTRL + NumPad+ to switch to the next test\n" +
                "Press CTRL + R to restart the current test\n" +
                "Press CTRL + D to toggle debug\n" +
                "Press Esc to exit to main menu")
                .left()
                .growX()
                .pad(10);

        for (int i = 0; i < registeredTests.size(); i++) {
            Class localClass = registeredTests.get(i);
            Label localLabel = new Label("Test : " + localClass.getSimpleName(), game.skin, "white");
            localLabel.setColor(Color.BLACK);
            localTableContent.add(localLabel).left();
            localTableContent.row();
            localLabel.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    game.stage.setScrollFocus(localScrollPane);
                    localLabel.setColor(Color.WHITE);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    if (!isOver()) {
                        localLabel.setColor(Color.BLACK);
                    }
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    try {
                        Constructor localConstructor = ClassReflection.getConstructor(localClass, BaseGame.class);
                        game.setScreen((Screen) localConstructor.newInstance(game));
                    } catch (ReflectionException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void show() {
        game.stage.addActor(tableRoot);
        game.refreshDebug();
    }
}
