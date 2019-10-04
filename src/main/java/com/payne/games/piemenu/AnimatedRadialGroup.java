package com.payne.games.piemenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.HashMap;


/**
 * An animated {@link RadialGroup}.<br>
 * A very simple folding/unfolding animation can be displayed whenever desired.<br>
 * Internally uses a {@link #currentAngle} attribute that is used for transitions
 * between states. Because of that, using {@link #setVisible(boolean)} might not
 * always reveal the Widget: you would have to ensure to call a setter before:
 * <pre>
 * {@code
 * myMenu.setCurrentAngle(myMenu.getStyle().totalDegreesDrawn);
 * myMenu.setVisible(true);
 * }
 * </pre>
 * The value of {@link #currentAngle} dictates the currently revealed amount of
 * angles, out of the total amount of degrees to be drawn. Its value is of 0
 * when the class is first initialized, and after a closing animation ended:
 * that is why you might end up not seeing the widget despite setting its
 * visibility to {@code true} if you haven't called the recommended line of code
 * provided above.
 */
@Deprecated
public class AnimatedRadialGroup extends RadialGroup {

    /**
     * How the widget looks.
     */
    private RadialGroupStyle style;

    /**
     * Duration of the animation.
     */
    private float duration;


    /* For internal use. */
    private boolean isOpening = false;
    private boolean isClosing = false;
    private float currentAngle = 0; // reused for transitions between entry and exit
    private static Vector2 vector2 = new Vector2();
    private HashMap<Actor, Color> originalColors = new HashMap<>();






    /**
     * An animated {@link RadialGroup}.<br>
     * A very simple folding/unfolding animation can be displayed whenever desired.<br>
     * Internally uses a {@link #currentAngle} attribute that is used for transitions
     * between states. Because of that, using {@link #setVisible(boolean)} might not
     * always reveal the Widget: you would have to ensure to call a setter before:
     * <pre>
     * {@code
     * myMenu.setCurrentAngle(myMenu.getStyle().totalDegreesDrawn);
     * myMenu.setVisible(true);
     * }
     * </pre>
     * The value of {@link #currentAngle} dictates the currently revealed amount of
     * angles, out of the total amount of degrees to be drawn. Its value is of 0
     * when the class is first initialized, and after a closing animation ended:
     * that is why you might end up not seeing the widget despite setting its
     * visibility to {@code true} if you haven't called the recommended line of code
     * provided above.
     *
     * @param sd used to draw everything but the contained actors.
     * @param style defines the way the widget looks like.
     */
    public AnimatedRadialGroup(ShapeDrawer sd, RadialGroupStyle style) {
        super(sd, style);
        this.style = getStyle();
    }

    /**
     * An animated {@link RadialGroup}.<br>
     * A very simple folding/unfolding animation can be displayed whenever desired.<br>
     * Internally uses a {@link #currentAngle} attribute that is used for transitions
     * between states. Because of that, using {@link #setVisible(boolean)} might not
     * always reveal the Widget: you would have to ensure to call a setter before:
     * <pre>
     * {@code
     * myMenu.setCurrentAngle(myMenu.getStyle().totalDegreesDrawn);
     * myMenu.setVisible(true);
     * }
     * </pre>
     * The value of {@link #currentAngle} dictates the currently revealed amount of
     * angles, out of the total amount of degrees to be drawn. Its value is of 0
     * when the class is first initialized, and after a closing animation ended:
     * that is why you might end up not seeing the widget despite setting its
     * visibility to {@code true} if you haven't called the recommended line of code
     * provided above.
     *
     * @param sd used to draw everything but the contained actors.
     * @param skin defines the way the widget looks like.
     */
    public AnimatedRadialGroup(ShapeDrawer sd, Skin skin) {
        super(sd, skin);
        this.style = getStyle();
    }

    /**
     * An animated {@link RadialGroup}.<br>
     * A very simple folding/unfolding animation can be displayed whenever desired.<br>
     * Internally uses a {@link #currentAngle} attribute that is used for transitions
     * between states. Because of that, using {@link #setVisible(boolean)} might not
     * always reveal the Widget: you would have to ensure to call a setter before:
     * <pre>
     * {@code
     * myMenu.setCurrentAngle(myMenu.getStyle().totalDegreesDrawn);
     * myMenu.setVisible(true);
     * }
     * </pre>
     * The value of {@link #currentAngle} dictates the currently revealed amount of
     * angles, out of the total amount of degrees to be drawn. Its value is of 0
     * when the class is first initialized, and after a closing animation ended:
     * that is why you might end up not seeing the widget despite setting its
     * visibility to {@code true} if you haven't called the recommended line of code
     * provided above.
     *
     * @param sd used to draw everything but the contained actors.
     * @param skin defines the way the widget looks like.
     * @param style the name of the style to be extracted from the skin.
     */
    public AnimatedRadialGroup(ShapeDrawer sd, Skin skin, String style) {
        super(sd, skin, style);
        this.style = getStyle();
    }



    @Override
    public void layout() {
        boolean notAnimated = !isCurrentlyAnimated() && !originalColors.isEmpty();
        float degreesPerChild = currentAngle / getAmountOfChildren();
        float openingPercentage = currentAngle / style.totalDegreesDrawn;
        float half = 1f / 2;
        for (int i = 0; i < getAmountOfChildren(); i++) {
            Actor actor = getChildren().get(i);

            /* Positioning. */
            vector2.set((style.radius+style.innerRadius)/2, 0);
            vector2.rotate(degreesPerChild*(i + half) + style.startDegreesOffset);
            if(actor instanceof Image) {
                /* Adjusting images to fit within their sector. */
                float size = 2*(style.radius* MathUtils.sinDeg(degreesPerChild/2)
                        - (MathUtils.sinDeg(degreesPerChild/2))*(style.radius - style.innerRadius));
                size *= 1.26; // todo: hard-coded and should get tested more thoroughly
                actor.setSize(size, size);
            }
            actor.setPosition(vector2.x+style.radius, vector2.y+style.radius, Align.center);

            /* Updating alpha (fade-in animation). */
            if(isCurrentlyAnimated()) {
                if (!originalColors.containsKey(actor))
                    originalColors.put(actor, new Color(actor.getColor()));
                Color color = originalColors.get(actor);
                actor.setColor(color.r, color.g, color.b, color.a * openingPercentage);

            /* Restoring the colors' state. */
            } else if(notAnimated) {
                actor.setColor(originalColors.get(actor));
            }
        }
        if (notAnimated) {
            originalColors.clear();
        }
    }


    /**
     * Gets the widget to transition into an opening animation from its
     * currently drawn angle (i.e. {@link #currentAngle}).
     *
     * @param durationSeconds How long the animation will take to finish.
     */
    public void transitionToOpening(float durationSeconds) {
        duration = durationSeconds/1000f;
        isOpening = true;
        isClosing = false;
        setVisible(true);
    }

    /**
     * Gets the widget to start an opening animation from the beginning.
     *
     * @param durationSeconds How long the animation will take to finish.
     */
    public void animateOpening(float durationSeconds) {
        currentAngle = 0;
        transitionToOpening(durationSeconds);
    }

    /**
     * Gets the widget to transition into a closing animation from its
     * currently drawn angle (i.e. {@link #currentAngle}).
     *
     * @param durationSeconds How long the animation will take to finish.
     */
    public void transitionToClosing(float durationSeconds) {
        duration = durationSeconds/1000f;
        isClosing = true;
        isOpening = false;
    }

    /**
     * Gets the widget to start a closing animation from the beginning (i.e.
     * from its fully opened state).
     *
     * @param durationSeconds How long the animation will take to finish.
     */
    public void animateClosing(float durationSeconds) {
        currentAngle = style.totalDegreesDrawn;
        transitionToClosing(durationSeconds);
    }

    /**
     * Transitions from the current state to the other.<br>
     * If the widget is opening, it will now be closing, for example.<br>
     * Visibility plays a role in determining the current state (for example,
     * if the widget is not visible, it is assumed that it's as if it was closed).
     * @param durationSeconds
     */
    public void toggleVisibility(float durationSeconds) {
        if(isOpening || (isVisible() && !isClosing)) {
            transitionToClosing(durationSeconds);
        } else if(isClosing || !isVisible()) {
            transitionToOpening(durationSeconds);
        }
    }

    /**
     * @return {@code true} if the widget is being closed or opened.<br>
     *         {@code false} otherwise.
     */
    public boolean isCurrentlyAnimated() {
        return (isOpening || isClosing);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(isCurrentlyAnimated()) {
            float speed = delta / duration;

            /* Opening. */
            if(isOpening) {
                currentAngle += speed;
                if(currentAngle >= style.totalDegreesDrawn) { // finishing the animation
                    currentAngle = style.totalDegreesDrawn;
                    isOpening = false;
                }

                /* Closing. */
            } else {
                currentAngle -= speed;
                if(currentAngle <= 0) { // finishing the animation
                    currentAngle = 0;
                    isClosing = false;
                    setVisible(false);
                }
            }

            /* Updates children. Calls `layout()`. */
            invalidate();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawWithShapeDrawer(batch, parentAlpha, currentAngle);
        drawMe(batch, parentAlpha);
    }







    /**
     * @return the duration of the animation.
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Setting the duration to 0 will make any currently-running animation end
     * at the next {@code render()} call.
     *
     * @param duration a value corresponding to {@code durationInSecond/1000f}.
     */
    public void setDuration(float duration) {
        if(duration < 0)
            throw new IllegalArgumentException("duration cannot be negative.");
        this.duration = duration;
    }

    /**
     * @return {@code true} only when the Widget is currently running an opening
     *         animation.<br>
     *         To be more precise, for example: when an opening animation ends,
     *         this returns {@code false}
     */
    public boolean isOpening() {
        return isOpening;
    }

    /**
     * @return {@code true} only when the Widget is currently running a closing
     *         animation.<br>
     *         To be more precise, for example: when a closing animation ends,
     *         this returns {@code false}
     */
    public boolean isClosing() {
        return isClosing;
    }

    /**
     * After a closing animation, its value is equal to 0.<br>
     * After an opening animation, its value is equal to
     * {@code style.totalDegreesDrawn}.
     *
     * @return an angle in between 0 and {@code style.totalDegreesDrawn}. It
     *         represents the internal state of how far in an animation the widget
     *         is at. If its value is 15, for example, it means that only 15
     *         degrees, out of the total amount of degrees the widget should
     *         take, are being rendered on the screen.
     */
    public float getCurrentAngle() {
        return currentAngle;
    }

    /**
     * Use this if you want to manipulate the internal state of how much of the
     * widget should be drawn.<br>
     * After a closing animation, its value is equal to 0.<br>
     * After an opening animation, its value is equal to
     * {@code style.totalDegreesDrawn}.<br>
     * <br>
     * It is recommended to use
     * <pre>
     * {@code
     * myMenu.setCurrentAngle(myMenu.getStyle().totalDegreesDrawn);
     * myMenu.setVisible(true);
     * }
     * </pre>
     * when you want to display the widget to your users, for example.
     *
     * @param currentAngle amount of angles, out of the total, which should
     *                     currently be displayed.
     */
    public void setCurrentAngle(float currentAngle) {
        if(currentAngle > style.totalDegreesDrawn || currentAngle < 0)
            throw new IllegalArgumentException("currentAngle must be between 0 and `style.totalDegreesDrawn`.");
        this.currentAngle = currentAngle;
    }
}