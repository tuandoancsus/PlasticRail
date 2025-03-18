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

public class FwdAction extends AbstractInputAction {
    private GameObject avatar;
    private MyGame game;
    private float direction;
    private boolean control;

    public FwdAction(GameObject avatar, MyGame game) {
        this.avatar = avatar;
        this.game = game;
        control = true;
    }

    public FwdAction(GameObject avatar, MyGame game, float direction) {
        this.avatar = avatar;
        this.game = game;
        this.direction = direction;
        control = false;
    }

    @Override
    public void performAction(float time, Event e) {
        float keyValue = e.getValue();
        if (Math.abs(keyValue) < 0.2) return; // Deadzone check

        // Get the updated camera position and avatar position:
        Vector3f camPos = game.getCamera().getLocation();
        Vector3f avatarPos = avatar.getWorldLocation();

        keyValue = keyValue * 0.5f;
        
        float maxDistance = 5.0f;
        float moveAmount = direction * 0.5f;

        if(!control) {
            avatar.fwdAction(moveAmount);
        } else {
            avatar.fwdAction((-1) * keyValue);
        }
        
        // if (game.getControllingDolphin()) {
            // avatar.fwdAction(moveAmount);
        // } else {
        //     // Get the camera's normalized forward direction
        //     Vector3f moveDirection = new Vector3f(game.getCamera().getN()).normalize().mul(moveAmount);
        
        //     // Compute projected camera position
        //     Vector3f projectedCameraPosition = new Vector3f(camPos).add(moveDirection);
        
        //     // Compute new distance after movement
        //     float newDistance = new Vector3f(projectedCameraPosition).sub(avatarPos).length();
        
        //     if (newDistance < maxDistance) {
        //         game.getCamera().fwdAction(moveAmount);
        //     }
        //     return;
        // }        
    }
}