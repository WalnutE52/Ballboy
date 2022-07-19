# Ballboy
## ReadMe

'gradle run' will start the game
'gradle test' will run the tests.
‘gradle build jacocoTestReport' will generate a coverage report.

## How to run:
'gradle run' will start the game
Arrow button to control the hero.

## Features :
Level Transition
Squarecat
Score
Save/Load

## Design patterns:
1.Observer pattern
---class name:
Observer, CurrentLevelObserver，TotalLevelObserver, 
Level, LevelImpl, App
---file name
Observer.java, CurrentLevelObserver.java，TotalLevelObserver.java, 
Level.java, LevelImpl.java, App.java
2.Memento pattern
---class name
Memento，Caretaker，MementoOneLevel，CaretakerOneLevel
Level, LevelImpl, GameEngine, GameEngineImpl, KeyboardInputHandler
---file name
Memento.java, Caretaker.java，MementoOneLevel.java, CaretakerOneLevel.java
Level.java, LevelImpl.java, GameEngine.java, GameEngineImpl.java, KeyboardInputHandler.java

## Cheat Mode:
Press R will enter cheat mode

## Remind:
Once you finish the game, the winner page will come out, you need to use mouse to close the game window.




