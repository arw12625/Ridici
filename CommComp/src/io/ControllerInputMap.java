/*
 * The MIT License
 *
 * Copyright 2017 Andrew_2.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * A map from controller specific components to generalized controller inputs
 * 
 * For example the component name "BUTTON_9" could be mapped to the input 
 * name "BUTTON_START".
 * 
 * @author Andrew_2
 */
public class ControllerInputMap implements InputMap {
    
    private GameController gc;
    private JSONObject json;
    private Map<String, String> inputNames;
    private Map<String, Float> inputTransforms;
    
    public static final Float defaultTransform =  1f;
    public static final String GAMECUBE_JSON = "res/gamecube.json";
    
    /**
     * Construct a controller input map with specified game controller and 
     * the structure from the json objects
     * @param gc
     * @param json 
     */
    public ControllerInputMap(GameController gc, JSONObject json) {
        this(json);
        this.gc = gc;
    }
    
    /**
     * Construct an input map using the structure from the json object
     * @param json the json object containing the structure of the map
     */
    public ControllerInputMap(JSONObject json) {
        
        this.json = json;
        inputNames = new HashMap<>();
        inputTransforms = new HashMap<>();
        
        JSONObject inputNamesJSON = json.getJSONObject("inputNames");
        for(Iterator<String> inputNamesIter = inputNamesJSON.keys(); inputNamesIter.hasNext(); ) {
            String inputName = inputNamesIter.next();
            String rawInputName = inputNamesJSON.getString(inputName);
            inputNames.put(inputName, rawInputName);
        }
        
        JSONObject inputTransformsJSON = json.getJSONObject("inputTransforms");
        for(Iterator<String> inputNamesIter = inputTransformsJSON.keys(); inputNamesIter.hasNext(); ) {
            String inputName = inputNamesIter.next();
            Float mult = inputTransformsJSON.getFloat(inputName);
            inputTransforms.put(inputName, mult);
        }      
        
        
    }
    
    /**
     * Get the name of the input from the name of the controller component name
     * @param componentName the name of the controller component
     * @return 
     */
    @Override
    public float getInput(String componentName) {
        String rawName = inputNames.get(componentName);
        return inputTransforms.getOrDefault(componentName, defaultTransform)
                * gc.getValue(rawName);
    }

    /**
     * Set the game controller used by this map
     * @param gc the desired game controller to use
     */
    public void setGameController(GameController gc) {
        this.gc = gc;
    }
    
    /**
     * Create a controller input map for a gamecube controller
     * @return the controller input map created
     */
    public static ControllerInputMap loadGamecubeInputMap() {
        return loadControllerInputMap(Paths.get(GAMECUBE_JSON));
    }
    
    /**
     * Create a controller input map from the json file at the specified path
     * @param path the path of the json file with the map structure
     * @return the created controller map
     */
    public static ControllerInputMap loadControllerInputMap(Path path) {
        
        String text = "";
        try {
            text = new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            System.err.println("Could not read JSON file");
        }
        JSONTokener tokener = new JSONTokener(text);
        JSONObject obj = new JSONObject(tokener);

        return new ControllerInputMap(obj);
    }
    
}
