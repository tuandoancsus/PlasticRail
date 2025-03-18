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

public class TurnAction extends AbstractInputAction{
    private GameObject avatar;
    private float direction;
    private boolean control;
    private MyGame game;

    public TurnAction(GameObject avatar, MyGame game) {
        this.avatar = avatar;
        this.game = game;
        control = true;

    }

    public TurnAction(GameObject avatar, MyGame game, float direction) {
        this.avatar = avatar;
        this.direction = direction;
        this.game = game;
        control = false;
    }

    @Override
    public void performAction(float time, Event e) {
        float keyValue = e.getValue();
        if (Math.abs(keyValue) < 0.2) return; // Deadzone check
        // if (game.getControllingDolphin()) {
        if (!control) {
            avatar.yawGlobal(direction * 0.01f);  // Rotate left (-) or right (+)
        } else {
            avatar.yawGlobal(keyValue * -0.01f);
        }
        // } else {
        //     game.getCamera().yaw(direction * 0.01f);
        // }
    }
}
