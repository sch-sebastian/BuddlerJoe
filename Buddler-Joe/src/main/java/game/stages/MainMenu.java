package game.stages;

import engine.io.InputHandler;
import engine.render.Loader;
import engine.render.MasterRenderer;
import game.Game;
import gui.GuiTexture;
import gui.MenuButton;
import org.joml.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class MainMenu {

    private static GuiTexture mainMenu;

    private static MenuButton joinGame;
    private static MenuButton exitGame;


    private static List<GuiTexture> guis;


    public static void init(Loader loader) {


        //Main Menu
        mainMenu = new GuiTexture(loader.loadTexture("mainMenu"), new Vector2f(0, 0), new Vector2f(1, 1), 1);

        //Join Game
        joinGame = new MenuButton(loader, "joinGame1", "joinGame2", new Vector2f(0, 0), new Vector2f(.4f, .4f/3));

        //Exit Game
        exitGame = new MenuButton(loader, "exitGame1", "exitGame2", new Vector2f(0, -.3f), new Vector2f(.4f, .4f/3));
    }


    public static void update() {
        guis = new ArrayList<>();
        guis.add(mainMenu);

        //OpenGL Coordinates (0/0 = center of screen, -1/1 = corners)
        double x = 2*(InputHandler.getMouseX()/Game.window.getWidth())-1;
        double y = 1-2*(InputHandler.getMouseY()/Game.window.getHeight());

        if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE) || ((InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1) && exitGame.isHover(x, y)))) {
            Game.window.stop();
        } else if (InputHandler.isMouseDown(GLFW_MOUSE_BUTTON_1) && joinGame.isHover(x, y)) {
            Game.setStage(Game.Stage.PLAYING);
        }

        InputHandler.update();

        guis.add(joinGame.getHoverTexture(x,y));
        guis.add(exitGame.getHoverTexture(x,y));

        Game.window.update();

        Game.getGuiRenderer().render(guis);
    }
}
