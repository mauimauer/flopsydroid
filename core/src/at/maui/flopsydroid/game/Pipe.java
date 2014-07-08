package at.maui.flopsydroid.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by maui on 08.07.2014.
 */
public class Pipe extends Image {

    public static final float PIPE_HOLE = 102f;

    private Droid mAndy;

    private Stage mStage;
    private boolean mCountPipe = false;

    private OnScoreListener mListener;

    public Pipe(TextureRegion region, Stage stage, Droid droid) {
        this(region, stage, droid, false, null);
    }

    public Pipe(TextureRegion region, Stage stage, Droid droid, boolean countPipe, OnScoreListener listener) {
        super(region);

        mAndy = droid;
        mStage = stage;

        mCountPipe = countPipe;
        mListener = listener;

        MoveByAction moveLeft = new MoveByAction();
        moveLeft.setDuration(FlopsyScreen.SPEED);
        moveLeft.setAmountX(-Ground.GROUND_WIDTH);
        addAction(Actions.forever(moveLeft));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (mAndy.isDead()) {
            clearActions();
            return;
        }

        if (getX() < -getWidth()) {
            remove();
        }

        if (getX() <= mAndy.getX()) {
            if (mAndy.getY() >= mStage.getViewport().getViewportHeight()) {
                mAndy.gotHit();
            }

            if (mCountPipe && mListener != null) {
                mCountPipe = false;
                mListener.onScored();
            }
        }

        checkCollision();
    }

    public void addScore() {
        if (getX() <= mAndy.getX()) {

                //Screenplay.land.clearActions();
                //Flappybird.Sounds.get(config.SoundsHit).play();

            /*if (getScore) {
                getScore = false;
                bird.updateScore();
                Flappybird.Sounds.get(config.SoundsScore).play();
            }*/
        }
    }

    public void checkCollision() {
        if (isCollision()) {
            mAndy.gotHit();
            //Screenplay.land.clearActions();
            //Flappybird.Sounds.get(config.SoundsHit).play();
        }
    }

    public boolean isCollision() {
        float d = 20;
        float maxx1 = getX() + getWidth();
        float minx1 = getX() + d;
        float maxy1 = getY() + getHeight();
        float miny1 = getY() + d;
        float maxx2 = mAndy.getX() + mAndy.getWidth();
        float minx2 = mAndy.getX() + d;
        float maxy2 = mAndy.getY() + mAndy.getHeight();
        float miny2 = mAndy.getY() + d;
        return !(maxx1 < minx2 || maxx2 < minx1 || maxy2 < miny1 || maxy1 < miny2);
    }
}
