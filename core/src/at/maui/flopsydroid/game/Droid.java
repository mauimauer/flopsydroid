package at.maui.flopsydroid.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by maui on 08.07.2014.
 */
public class Droid extends Image {

    public static final int JUMP_HEIGHT = 30;
    public static final float JUMP_DURATION = 0.2f;
    public static final float ANIMATION_DURATION = 0.6f;

    private Action mCurrentAction;
    private Animation mAnimation;
    private TextureRegion mCurrentFrame;

    private float mDuration;
    private boolean mDead;
    private OnDroidCollisionListener mListener;

    public Droid(TextureRegion[] regions, OnDroidCollisionListener listener) {
        super(regions[0]);
        setOrigin(getWidth() / 2, getHeight() / 2);
        mAnimation = new Animation(ANIMATION_DURATION, regions);
        mDuration = 0;
        mDead = false;
        mListener = listener;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (mDead) {
            return;
        }

        // Update Andy's animation frame
        mDuration += delta;
        mCurrentFrame = mAnimation.getKeyFrame(mDuration, true);
        setDrawable(new TextureRegionDrawable(mCurrentFrame));
    }

    public boolean isDead() {
        return mDead;
    }

    public void gotHit() {
        mDead = true;

        removeAction(mCurrentAction);
        RotateToAction faceDown = new RotateToAction();
        faceDown.setDuration(JUMP_DURATION);
        faceDown.setRotation(-90);

        MoveToAction moveDown = new MoveToAction();
        moveDown.setDuration(getDownwardDuration(getX(), Ground.GROUND_HEIGHT));
        moveDown.setPosition(getX(), Ground.GROUND_HEIGHT);
        moveDown.setInterpolation(Interpolation.sineIn);

        mCurrentAction = new SequenceAction(faceDown, moveDown);
        addAction(mCurrentAction);

        if(mListener != null) {
            mListener.onDroidCollision();
            mListener = null;
        }
    }

    public void hitGround() {
        mDead = true;

        removeAction(mCurrentAction);
        MoveToAction moveDown = new MoveToAction();
        moveDown.setDuration(getDownwardDuration(getX(), Ground.GROUND_HEIGHT));
        moveDown.setPosition(getX(),  Ground.GROUND_HEIGHT);
        moveDown.setInterpolation(Interpolation.sineIn);
        mCurrentAction = new SequenceAction(moveDown);
        addAction(mCurrentAction);

        if(mListener != null) {
            mListener.onDroidCollision();
            mListener = null;
        }
    }

    public void tapped() {
        removeAction(mCurrentAction);

        float y = getY() + JUMP_HEIGHT;

        RotateToAction faceup = new RotateToAction();
        faceup.setDuration(JUMP_DURATION);
        faceup.setRotation(30);
        MoveToAction moveup = new MoveToAction();
        moveup.setDuration(JUMP_DURATION);
        moveup.setPosition(getX(), y);
        moveup.setInterpolation(Interpolation.sineIn);
        Action fly = new ParallelAction(faceup, moveup);
        RotateToAction faceDown = new RotateToAction();

        float duration = getDownwardDuration(y, Ground.GROUND_HEIGHT);
        faceDown.setDuration(duration);
        faceDown.setRotation(-90);
        MoveToAction moveDown = new MoveToAction();
        moveDown.setDuration(duration);
        moveDown.setPosition(getX(), Ground.GROUND_HEIGHT);
        moveDown.setInterpolation(Interpolation.sineIn);
        Action fall = new ParallelAction(faceDown, moveDown);

        mCurrentAction = new SequenceAction(fly, fall);
        addAction(mCurrentAction);
    }

    private float getDownwardDuration(float up, float down) {
        float dy = up - down;
        float duration;

        if (dy <=  JUMP_HEIGHT) {
            duration = JUMP_DURATION;
        } else {
            duration = 0.8f;
        }
        return duration;
    }
}
