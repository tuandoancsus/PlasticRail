package game;

import tage.*;
import tage.input.InputManager;
import tage.shapes.*;

import java.lang.Math;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class RideDolphin extends AbstractInputAction {
    private MyGame game;
    private GameObject avatar;

    public RideDolphin(MyGame game, GameObject avatar) {
        this.game = game;
        this.avatar = avatar;
    }

    @Override
    public void performAction(float time, Event e) {
        //game.setControllingDolphin();
        Vector3f leftOffset = new Vector3f(game.getAvatar().getWorldRightVector()).mul(1.2f);
        game.getCamera().setLocation(game.getAvatar().getWorldLocation().add(leftOffset));
    }
}