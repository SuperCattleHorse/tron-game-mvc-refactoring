package com.tron.controller.fx;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tron.view.fx.FXStoryGameView;
import com.tron.view.fx.FXSurvivalGameView;
import com.tron.view.fx.FXTwoPlayerGameView;
import com.tron.view.fx.menu.FXMainMenuView;
import com.tron.view.fx.menu.FXPlayMenuView;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * <p>
 * Targeted behavior tests for {@link FXGameController} that lock down the scene
 * reuse and focus hand-off guarantees highlighted in the TDR strategy. By
 * injecting lightweight stubs we can validate controller behavior without booting
 * a full JavaFX runtime.
 * </p>
 */
@DisplayName("FXGameController Behavior Tests")
class FXGameControllerBehaviorTest {

    static {
        // Allow Mockito/ByteBuddy to mock JavaFX classes on Java 24 runtimes.
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    @BeforeEach
    void resetSingleton() throws Exception {
        Field instanceField = FXGameController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    /**
     * <p>Ensures {@link FXGameController#showMainMenu()} re-roots the existing scene instead
     * of rebuilding the entire JavaFX scene graph.</p>
     */
    @Test
    void testShowMainMenuReusesExistingScene() throws Exception {
        FXGameController controller = FXGameController.getInstance();
        Stage stage = mock(Stage.class);
        Scene scene = mock(Scene.class);

        FXMainMenuView mainMenuView = mock(FXMainMenuView.class);
        BorderPane mainMenuRoot = new BorderPane(new StackPane());
        when(mainMenuView.getRoot()).thenReturn(mainMenuRoot);

        setField(controller, "primaryStage", stage);
        setField(controller, "currentScene", scene);
        setField(controller, "mainMenuView", mainMenuView);

        controller.showMainMenu();

        verify(scene).setRoot(mainMenuRoot);
        verify(stage).setTitle("TRON Game - Main Menu");
        verify(stage, never()).setScene(any());
    }

    /**
     * <p>Validates the Survival mode entry sequence resets the view, updates the scene
     * root, and pushes focus back to the game canvas.</p>
     */
    @Test
    void testStartSurvivalModeResetsViewAndRequestsFocus() throws Exception {
        FXGameController controller = FXGameController.getInstance();
        Stage stage = mock(Stage.class);
        Scene scene = mock(Scene.class);

        FXSurvivalGameView survivalGameView = mock(FXSurvivalGameView.class);
        BorderPane survivalRoot = new BorderPane(new StackPane());
        when(survivalGameView.getRoot()).thenReturn(survivalRoot);

        setField(controller, "primaryStage", stage);
        setField(controller, "currentScene", scene);
        setField(controller, "survivalGameView", survivalGameView);

        controller.startSurvivalMode();

        verify(survivalGameView).reset();
        verify(scene).setRoot(survivalRoot);
        verify(stage).setTitle("TRON Game - Survival Mode");
        verify(survivalGameView).requestFocus();
    }

    /**
     * <p>Confirms {@link FXGameController#showPlayMenu()} swaps the scene root without
     * reconstructing the {@link Scene}.</p>
     */
    @Test
    void testShowPlayMenuReusesScene() throws Exception {
        FXGameController controller = FXGameController.getInstance();
        Stage stage = mock(Stage.class);
        Scene scene = mock(Scene.class);

        FXPlayMenuView playMenuView = mock(FXPlayMenuView.class);
        BorderPane playMenuRoot = new BorderPane(new StackPane());
        when(playMenuView.getRoot()).thenReturn(playMenuRoot);

        setField(controller, "primaryStage", stage);
        setField(controller, "currentScene", scene);
        setField(controller, "playMenuView", playMenuView);

        controller.showPlayMenu();

        verify(scene).setRoot(playMenuRoot);
        verify(stage).setTitle("TRON Game - Select Mode");
        verify(stage, never()).setScene(any());
    }

    /**
     * <p>Locks down the Two Player mode flow so resets, focus, and window titles stay intact.</p>
     */
    @Test
    void testStartTwoPlayerModeResetsAndFocusesView() throws Exception {
        FXGameController controller = FXGameController.getInstance();
        Stage stage = mock(Stage.class);
        Scene scene = mock(Scene.class);

        FXTwoPlayerGameView twoPlayerView = mock(FXTwoPlayerGameView.class);
        BorderPane twoPlayerRoot = new BorderPane(new StackPane());
        when(twoPlayerView.getRoot()).thenReturn(twoPlayerRoot);

        setField(controller, "primaryStage", stage);
        setField(controller, "currentScene", scene);
        setField(controller, "twoPlayerGameView", twoPlayerView);

        controller.startTwoPlayerMode();

        verify(twoPlayerView).reset();
        verify(scene).setRoot(twoPlayerRoot);
        verify(stage).setTitle("TRON Game - Two Player Mode");
        verify(twoPlayerView).requestFocus();
    }

    /**
     * <p>Ensures Story mode scene swaps continue to deliver focus and reuse the existing Scene.</p>
     */
    @Test
    void testStartStoryModeResetsAndFocusesView() throws Exception {
        FXGameController controller = FXGameController.getInstance();
        Stage stage = mock(Stage.class);
        Scene scene = mock(Scene.class);

        FXStoryGameView storyView = mock(FXStoryGameView.class);
        BorderPane storyRoot = new BorderPane(new StackPane());
        when(storyView.getRoot()).thenReturn(storyRoot);

        setField(controller, "primaryStage", stage);
        setField(controller, "currentScene", scene);
        setField(controller, "storyGameView", storyView);

        controller.startStoryMode();

        verify(storyView).reset();
        verify(scene).setRoot(storyRoot);
        verify(stage).setTitle("TRON Game - Story Mode");
        verify(storyView).requestFocus();
    }

    private static void setField(FXGameController controller, String fieldName, Object value) throws Exception {
        Field field = FXGameController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }
}
