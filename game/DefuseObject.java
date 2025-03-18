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

public class DefuseObject extends AbstractInputAction{
    private MyGame game;

    public DefuseObject(MyGame game) {
        this.game = game;
    }

    @Override
    public void performAction(float time, Event e) {
        game.defuseNearbyObjects();
    }
}
