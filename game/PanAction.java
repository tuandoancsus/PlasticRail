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

public class PanAction extends AbstractInputAction{
    private Camera cam;
    private float direction;

    public PanAction(Camera c, float direction) {
        cam = c;
        this.direction = direction;
    }

    @Override
    public void performAction(float time, Event e) {
        if((direction == 1.0f) || (direction == -1.0f)) {
            cam.panVertical(direction * 0.1f);
        } else if((direction == 2.0f) || (direction == -2.0f)) {
            cam.panHorizontal(direction/2 * 0.1f);
        }
    }
}
