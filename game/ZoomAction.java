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

public class ZoomAction extends AbstractInputAction{
    private Camera cam;
    private float direction;

    public ZoomAction(Camera c, float direction) {
        cam = c;
        this.direction = direction;
    }

    @Override
    public void performAction(float time, Event e) {
        cam.zoom(direction);
    }
}
