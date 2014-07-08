package at.maui.flopsydroid;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import at.maui.flopsydroid.game.FlopsyScreen;
import at.maui.flopsydroid.game.OnGlobalListener;

public class FlopsyDroidGame extends Game {

    private static final Vector2 VIEWPORT = new Vector2(320, 480);
    private AssetManager mAssetManager = new AssetManager();

    private OnGlobalListener mListener;

    public FlopsyDroidGame(OnGlobalListener listener) {
        mListener = listener;
    }

	@Override
	public void create () {
        setScreen(new FlopsyScreen(this, mListener));
	}

    @Override
    public void dispose() {
        mAssetManager.dispose();
        super.dispose();
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }
}
