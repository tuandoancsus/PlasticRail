package game;

import tage.*;
import tage.shapes.ManualObject;

public class ManualShape extends ManualObject{
    private float[] vertices = new float[] {
        // Front Face (2 triangles)
        // Right side
        0.0f, 1.0f, 0.0f,  // Top
        1.0f, 0.0f, 0.0f,  // Right
        0.0f, -1.0f, 0.0f, // Bottom
        // Left side
        0.0f, 1.0f, 0.0f,  // Top
        0.0f, -1.0f, 0.0f, // Bottom
        -1.0f, 0.0f, 0.0f, // Left
    
    
        // Back Face 
        // Left side
        0.0f, 1.0f, 0.0f,  // Top
        -1.0f, 0.0f, 0.0f, // Left
        0.0f, -1.0f, 0.0f, // Bottom
        // Right side
        0.0f, 1.0f, 0.0f,  // Top
        0.0f, -1.0f, 0.0f, // Bottom
        1.0f, 0.0f, 0.0f   // Right
    };
    
    private float[] texcoords = new float[] {
        0.5f, 1.0f, // Top
        1.0f, 0.5f, // Right
        0.5f, 0.0f, // Bottom
    
        0.5f, 1.0f, // Top
        0.5f, 0.0f, // Bottom
        0.0f, 0.5f, // Left
    
        0.5f, 1.0f, // Top
        0.0f, 0.5f, // Left
        0.5f, 0.0f, // Bottom
    
        0.5f, 1.0f, // Top
        0.5f, 0.0f, // Bottom
        1.0f, 0.5f  // Right
    };
    
    private float[] normals = new float[] {
        0.0f, 0.0f, 1.0f, // Front - Triangle 1
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
    
        0.0f, 0.0f, 1.0f, // Front - Triangle 2
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
    
        0.0f, 0.0f, -1.0f, // Back - Triangle 1
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
    
        0.0f, 0.0f, -1.0f, // Back - Triangle 2
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f
    };
public ManualShape()
{ super();
setNumVertices(18);
setVertices(vertices);
setTexCoords(texcoords);
setNormals(normals);
setMatAmb(Utils.goldAmbient());
setMatDif(Utils.goldDiffuse());
setMatSpe(Utils.goldSpecular());
setMatShi(Utils.goldShininess());
}
}
