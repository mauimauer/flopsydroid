package at.maui.flopsydroid.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import at.maui.flopsydroid.FlopsyDroidGame;

/**
 * Created by maui on 07.07.2014.
 */
public class FlopsyScreen implements Screen {

    public final static float SPEED = 3f;
    public final static float PIPE_INTERVAL = 1.4f;

    private TextureAtlas mTextureAtlas;
    private Stage mStage;

    private Ground mGround;
    private Droid mAndy;

    private float mTimeSinceLastPipe;

    private boolean mInitialTap = false;

    private Label mScoreLabel;
    private int mScore = 0;
    private Label.LabelStyle mLabelStyle;

    private OnGlobalListener mGlobalListener;

    public FlopsyScreen(FlopsyDroidGame game, OnGlobalListener listener) {
        mStage = new Stage();

        mGlobalListener = listener;

        game.getAssetManager().load("flopsy.sprites", TextureAtlas.class);
        game.getAssetManager().finishLoading();
        mTextureAtlas = game.getAssetManager().get("flopsy.sprites", TextureAtlas.class);

        mLabelStyle = new Label.LabelStyle();
        mLabelStyle.font = new BitmapFont(Gdx.files.internal("flappyfont.fnt"),
                Gdx.files.internal("flappyfont.png"), false);

        mStage.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {

                if(event.getTarget().equals(mAndy)) {
                    mGround.onDroidCollision();
                    return true;
                }

                return false;
            }
        });
    }

    public void addScoreLabel() {
        mScoreLabel = new Label("0", mLabelStyle);
        mScoreLabel.setPosition(
                mStage.getViewport().getViewportWidth() / 2 - mScoreLabel.getWidth() / 2,
                mStage.getViewport().getViewportHeight() - mScoreLabel.getHeight());
        mStage.addActor(mScoreLabel);
    }

    @Override
    public void render(float delta) {

        // Clear frame
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle touches
        if(Gdx.input.justTouched()) {
            if(!mInitialTap)
                mInitialTap = true;

            if(mAndy.isDead()) {
                reset();
            } else {
                mAndy.tapped();
            }
        }

        mTimeSinceLastPipe += delta;
        if (mTimeSinceLastPipe > PIPE_INTERVAL && !mAndy.isDead() && mInitialTap) {
            mTimeSinceLastPipe = 0;
            addPipe();
        }

        mStage.act();
        mStage.draw();
    }

    public void addPipe() {
        int r = Utils.random(0, 7);
        float dy = r * 10;
        r = Utils.random(0, 1);
        if (r == 0) {
            dy = -dy;
        }
        Pipe pipe1 = new Pipe(mTextureAtlas.findRegion("pipe", 1), mStage, mAndy, true, new OnScoreListener() {
            @Override
            public void onScored() {
                mScore++;
                mScoreLabel.setText(""+mScore);

                if(mGlobalListener != null)
                    mGlobalListener.onScored();
            }
        });
        pipe1.setZIndex(1);
        float x = mStage.getViewport().getViewportWidth();
        float y = (mStage.getViewport().getViewportHeight() - Ground.GROUND_HEIGHT) / 2
                + Ground.GROUND_HEIGHT + Pipe.PIPE_HOLE / 2;
        pipe1.setPosition(x, y + dy);
        Pipe pipe2 = new Pipe(mTextureAtlas.findRegion("pipe", 2), mStage, mAndy);
        pipe2.setZIndex(1);
        y = (mStage.getViewport().getViewportHeight() - Ground.GROUND_HEIGHT) / 2
                + Ground.GROUND_HEIGHT - pipe2.getHeight()
                - Pipe.PIPE_HOLE / 2;
        pipe2.setPosition(x, y + dy);
        mStage.addActor(pipe1);
        mStage.addActor(pipe2);

        mScoreLabel.setZIndex(pipe1.getZIndex());
        mGround.setZIndex(pipe2.getZIndex());
        mAndy.setZIndex(pipe2.getZIndex());
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        reset();
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public void reset() {
        mStage.clear();
        mInitialTap = false;
        addBackground();
        mScore = 0;
        addAndy();
        addScoreLabel();
        addGround();
    }

    public void addAndy() {

        TextureRegion[] droidRegions = new TextureRegion[] {
                mTextureAtlas.findRegion("andy", 1), mTextureAtlas.findRegion("andy", 2),
                mTextureAtlas.findRegion("andy", 3) };
        mAndy = new Droid(droidRegions, new OnDroidCollisionListener() {
            @Override
            public void onDroidCollision() {
                mGround.onDroidCollision();

                if(mGlobalListener != null)
                    mGlobalListener.onGameOver(mScore);
            }
        });
        mAndy.setPosition(mStage.getViewport().getViewportHeight() / 2 - mAndy.getWidth(),
                mStage.getViewport().getViewportWidth() / 2);
        mStage.addActor(mAndy);
    }

    public void addGround() {
        mGround = new Ground(mTextureAtlas.findRegion("ground"), mAndy);
        mStage.addActor(mGround);
    }

    public void addBackground() {
        Image bg = new Image(mTextureAtlas.findRegion("bg"));
        mStage.addActor(bg);
    }
}
