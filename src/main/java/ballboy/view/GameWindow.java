package ballboy.view;

import ballboy.model.Entity;
import ballboy.model.GameEngine;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GameWindow {
    private static final double VIEWPORT_MARGIN_X = 100;
    private static final double VIEWPORT_MARGIN_Y = 50;
    private final int width;
    private final int height;
    private final double frameDurationMilli;
    private final Scene scene;
    private final Pane pane;
    private final GameEngine model;
    private final List<EntityView> entityViews;
    private final BackgroundDrawer backgroundDrawer;
    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    private boolean win;

    public GameWindow(GameEngine model, int width, int height, double frameDurationMilli) {
        this.model = model;
        this.width = width;
        this.height = height;
        this.frameDurationMilli = frameDurationMilli;
        pane = new Pane();
        scene = new Scene(pane, width, height);

        entityViews = new ArrayList<>();

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        backgroundDrawer = new BlockedBackground();
        backgroundDrawer.draw(model, pane);

        this.win = false;

    }

    public Scene getScene() {
        return scene;
    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameDurationMilli), t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void winner() {
        ImageView gameover = new ImageView("win.png");
        this.win = true;
        gameover.setFitHeight(this.height);
        gameover.setFitWidth(this.width);
        this.pane.getChildren().add(gameover);

    }

    private void draw() {

        if (model.ifEndGame() && !this.win) {
            this.winner();
        }

        model.tick();

        List<Entity> entities = model.getCurrentLevel().getEntities();

        for (EntityView entityView : entityViews) {
            entityView.markForDelete();
        }

        double heroXPos = model.getCurrentLevel().getHeroX();
        double viewportLeftBar = xViewportOffset + VIEWPORT_MARGIN_X;
        double viewportRightBar = viewportLeftBar + (width - 2 * VIEWPORT_MARGIN_X);

        if (heroXPos < viewportLeftBar) {
            xViewportOffset -= heroXPos - viewportLeftBar;
        } else if (heroXPos + model.getCurrentLevel().getHeroWidth() > viewportRightBar) {
            xViewportOffset += heroXPos + model.getCurrentLevel().getHeroWidth() - viewportRightBar;
        }

        heroXPos -= xViewportOffset;

        if (heroXPos < VIEWPORT_MARGIN_X) {
            if (xViewportOffset >= 0) { // Don't go further left than the start of the level
                xViewportOffset -= VIEWPORT_MARGIN_X - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        }

        double levelRight = model.getCurrentLevel().getLevelWidth();
        double screenRight = xViewportOffset + width - model.getCurrentLevel().getHeroWidth();
        if (screenRight > levelRight) {
            xViewportOffset = levelRight - width + model.getCurrentLevel().getHeroWidth();
        }

        double levelTop = 0.0;
        double levelBottom = model.getCurrentLevel().getLevelHeight();
        double heroYPos = model.getCurrentLevel().getHeroY();
        double heroHeight = model.getCurrentLevel().getHeroHeight();
        double viewportTop = yViewportOffset + VIEWPORT_MARGIN_Y;
        double viewportBottom = yViewportOffset + height - 2 * VIEWPORT_MARGIN_Y;

        if (heroYPos + heroHeight > viewportBottom) {
            // if below, shift down
            yViewportOffset += heroYPos + heroHeight - viewportBottom;
        } else if (heroYPos < viewportTop) {
            // if above, shift up
            yViewportOffset -= viewportTop - heroYPos;
        }

        double screenBottom = yViewportOffset + height;
        double screenTop = yViewportOffset;
        // shift back in the instance when we're near the boundary
        if (screenBottom > levelBottom) {
            yViewportOffset -= screenBottom - levelBottom;
        } else if (screenTop < 0.0) {
            yViewportOffset -= screenTop;
        }

        backgroundDrawer.update(xViewportOffset, yViewportOffset);

        for (Entity entity : entities) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

    public Pane getPane() {
        return pane;
    }

}
