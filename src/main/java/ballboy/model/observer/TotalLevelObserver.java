package ballboy.model.observer;

import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.List;

import ballboy.model.Level;

public class TotalLevelObserver implements Observer {

    private final List<Level> allLevels;
    private final Text text;

    public TotalLevelObserver(List<Level> allLevels, Text text) {
        this.allLevels = allLevels;
        for (Level level : allLevels) {
            level.registerObserver(this);
        }

        this.text = text;
    }

    @Override
    public void update() {
        int totalScore = 0;
        for (Level level : this.allLevels) {
            totalScore += level.getTotalScore();
        }
        int finalTotalScore = totalScore;
        Platform.runLater(() -> this.text.setText("TotalScore: " + finalTotalScore));
    }

}
