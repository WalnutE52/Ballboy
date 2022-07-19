package ballboy;

import ballboy.model.Entity;
import ballboy.model.GameEngine;
import ballboy.model.GameEngineImpl;
import ballboy.model.Level;
import ballboy.model.factories.BallboyFactory;
import ballboy.model.factories.CloudFactory;
import ballboy.model.factories.EnemyFactory;
import ballboy.model.factories.EntityFactoryRegistry;
import ballboy.model.factories.FinishFactory;
import ballboy.model.factories.SquareCatFactory;
import ballboy.model.factories.StaticEntityFactory;
import ballboy.model.levels.LevelImpl;
import ballboy.model.levels.PhysicsEngine;
import ballboy.model.levels.PhysicsEngineImpl;
import ballboy.model.observer.CurrentLevelObserver;
import ballboy.model.observer.Observer;
import ballboy.model.observer.TotalLevelObserver;
import ballboy.view.GameWindow;
import javafx.application.Application;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Application root.
 *
 * Wiring of the dependency graph should be done manually in the start method.
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<String, String> params = getParameters().getNamed();

        String s = "Java 11 sanity check";
        if (s.isBlank()) {
            throw new IllegalStateException("You must be running Java 11+. You won't ever see this exception though"
                    + " as your code will fail to compile on Java 10 and below.");
        }

        ConfigurationParser configuration = new ConfigurationParser();
        JSONObject parsedConfiguration = null;
        try {
            parsedConfiguration = configuration.parseConfig("config.json");
        } catch (ConfigurationParseException e) {
            System.out.println(e);
            System.exit(-1);
        }

        final double frameDurationMilli = 17;
        PhysicsEngine engine = new PhysicsEngineImpl(frameDurationMilli);

        EntityFactoryRegistry entityFactoryRegistry = new EntityFactoryRegistry();
        entityFactoryRegistry.registerFactory("cloud", new CloudFactory());
        entityFactoryRegistry.registerFactory("enemy", new EnemyFactory());
        entityFactoryRegistry.registerFactory("background", new StaticEntityFactory(Entity.Layer.BACKGROUND));
        entityFactoryRegistry.registerFactory("static", new StaticEntityFactory(Entity.Layer.FOREGROUND));
        entityFactoryRegistry.registerFactory("finish", new FinishFactory());
        entityFactoryRegistry.registerFactory("hero", new BallboyFactory());
        entityFactoryRegistry.registerFactory("cat", new SquareCatFactory());

        Integer levelIndex = ((Number) parsedConfiguration.get("currentLevelIndex")).intValue();
        JSONArray levelConfigs = (JSONArray) parsedConfiguration.get("levels");
        // JSONObject levelConfig = (JSONObject) levelConfigs.get(levelIndex);

        Text BlueScore = new Text(25, 30, "Blue: 0");
        Text RedScore = new Text(25, 45, "Red: 0");
        Text GreenScore = new Text(25, 60, "Green: 0");
        Text totalScore = new Text(25, 75, "TotalScore: 0");
        List<Level> allLevels = new ArrayList<>();
        List<CurrentLevelObserver> allCurObservers = new ArrayList<>();
        // List<LevelScoreObserver> scoreObs = new ArrayList<>();
        for (Object oneLevel : levelConfigs) {
            Level level = new LevelImpl((JSONObject) oneLevel, engine, entityFactoryRegistry, frameDurationMilli);
            allLevels.add(level);
            allCurObservers.add(new CurrentLevelObserver(level, BlueScore, RedScore, GreenScore));
            // scoreObs.add(new LevelScoreObserver(level, levelScore));
        }

        // Level level = new LevelImpl(levelConfig, engine, entityFactoryRegistry,
        // frameDurationMilli);
        Observer totalScoreObserver = new TotalLevelObserver(allLevels, totalScore);
        GameEngine gameEngine = new GameEngineImpl(levelIndex, allLevels);

        GameWindow window = new GameWindow(gameEngine, 640, 400, frameDurationMilli);
        window.getPane().getChildren().addAll(BlueScore, RedScore, GreenScore, totalScore);
        window.run();

        primaryStage.setTitle("Ballboy");
        primaryStage.setScene(window.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();

        window.run();
    }
}
