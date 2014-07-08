package at.maui.flopsydroid.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by maui on 08.07.2014.
 */
public class Ground extends Image implements OnDroidCollisionListener {

    public static final float GROUND_WIDTH = 336f;
    public static final float GROUND_HEIGHT = 36f;

    private Droid mAndy;
    private boolean mHit;

    public Ground(TextureRegion region, Droid andy) {
        super(region);

        mAndy = andy;
        addAction(Actions.forever(Actions.moveBy(-GROUND_WIDTH, 0f, 3f)));
        mHit = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getX() <= -GROUND_WIDTH) {
            setX(0);
        }
        if (checkCollision()) {
            mAndy.hitGround();
            /*if (mHit && Pipe.getPIPE_HIT() == 1) {
                mHit = false;
                // TODO: Vibrate
            }*/
        }
    }

    public boolean checkCollision() {
        if (mAndy.getY() <= GROUND_HEIGHT) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDroidCollision() {
        clearActions();
    }
}
