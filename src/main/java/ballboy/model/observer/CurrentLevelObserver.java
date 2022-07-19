package ballboy.model.observer;

import java.util.List;

import ballboy.model.Level;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class CurrentLevelObserver implements Observer {

    private Level level;
    private Text textRed;
    private Text textGreen;
    private Text textBlue;
    private int blueScore;
    private int RedScore;
    private int GreenScore;

    public CurrentLevelObserver(Level level, Text textBlue, Text textRed, Text textGreen) {
        this.level = level;
        this.textRed = textRed;
        this.textBlue = textBlue;
        this.textGreen = textGreen;
        this.level.registerObserver(this);
    }

    @Override
    public void update() {
        this.blueScore = this.level.getBlueScore();
        Platform.runLater(() -> this.textBlue.setText("Blue: " + this.blueScore));
        this.RedScore = this.level.getRedScore();
        Platform.runLater(() -> this.textRed.setText("Red: " + this.RedScore));
        this.GreenScore = this.level.getGreenScore();
        Platform.runLater(() -> this.textGreen.setText("Green: " + this.GreenScore));

    }

}
