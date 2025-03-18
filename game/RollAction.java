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

public class RollAction extends AbstractInputAction {
    private GameObject avatar;
    private float direction;
    private MyGame game;

    public RollAction(GameObject avatar, MyGame game, float direction) {
        this.avatar = avatar;
        this.direction = direction;
        this.game = game;
    }

    @Override
    public void performAction(float time, Event e) {
        float keyValue = e.getValue();
        if (Math.abs(keyValue) < 0.2) return; // Deadzone check
        // if (game.getControllingDolphin()) {
             avatar.roll(direction * 0.005f); // Roll left (-) or right (+)
        // } else {
        //     game.getCamera().roll(direction * 0.005f);
        // }
    }
}