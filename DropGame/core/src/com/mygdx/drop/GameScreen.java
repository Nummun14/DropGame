package com.mygdx.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Drop game;

    Texture dropImage, redDropImage, greenDropImage, bucketImage;
    Sound dropSound;
    Music rainMusic;
    Rectangle bucket;
    OrthographicCamera camera;
    Array<RainDrop> raindrops;
    long lastDropTime;
    int dropsGathered;
    private int rainSpeed = 300;
    private int maxSpeed = 550;
    private int timeBetweenDrops = 800000000;

    public GameScreen(final Drop game) {
        this.game = game;

        dropImage = new Texture("droplet.png");
        redDropImage = new Texture("redDroplet.png");
        greenDropImage = new Texture("greenDroplet.png");
        bucketImage = new Texture("bucket.png");

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
        rainMusic.play();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<RainDrop>();
        spawnRaindrop();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Score: " + dropsGathered, 10, 470);
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (RainDrop raindrop : raindrops) {
            drawRainDrop(raindrop.rectangle, raindrop.type);
        }
        game.batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            bucket.x -= 600 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            bucket.x += 600 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            bucket.x -= 600 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            bucket.x += 600 * Gdx.graphics.getDeltaTime();

        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;

        if (TimeUtils.timeSinceNanos(lastDropTime) > timeBetweenDrops)
            spawnRaindrop();

        Iterator<RainDrop> iter = raindrops.iterator();
        while (iter.hasNext()) {
            RainDrop raindrop = iter.next();
            raindrop.rectangle.y -= rainSpeed * Gdx.graphics.getDeltaTime();
            if (raindrop.rectangle.y + 64 < 0) {
                iter.remove();
                if (raindrop.type > 1) {
                    dispose();
                    game.setScreen(new MainMenuScreen(game));
                    break;
                }
            }
            if (raindrop.rectangle.overlaps(bucket)) {
                if (raindrop.type == 0) {
                    dispose();
                    game.setScreen(new MainMenuScreen(game));
                    break;
                } else if (raindrop.type == 1) {
                    if (rainSpeed < maxSpeed) {
                        rainSpeed += 25;
                        timeBetweenDrops -= 50000000;
                    }
                    dropsGathered --;
                }
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        redDropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(new RainDrop(raindrop));
        lastDropTime = TimeUtils.nanoTime();
    }

    private void drawRainDrop(Rectangle raindrop, int random) {
        if (random == 0) {
            game.batch.draw(redDropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
        } else if (random == 1) {
            game.batch.draw(greenDropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
        } else {
            game.batch.draw(dropImage, raindrop.x, raindrop.y, raindrop.width, raindrop.height);
        }
    }
}
