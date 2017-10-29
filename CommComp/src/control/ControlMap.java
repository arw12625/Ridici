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
package control;

import io.InputMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A map of control names to the value of the corresponding name
 * 
 * For example, the name of the control "FORWARDS" would map to the value 
 * of the FORWARDS control which could be 1.0f
 * 
 * @author Andrew_2
 */
public class ControlMap {
    
    private InputMap im;
    private JSONObject json;
    private Map<String, Control> controls;
    
    /**
     * Construct a control map with the specified input map and the structure
     * from the json file
     * 
     * @param im the input map to use backing the control map
     * @param json the json object containing the structure of the map
     */
    public ControlMap(InputMap im, JSONObject json) {
        this(json);
        this.im = im;
    }
    
    /**
     * Construct a control map with the structure from the json file
     * 
     * @param json the json object containing the structure of the map
     */
    public ControlMap(JSONObject json) {
        
        this.json = json;
        controls = new HashMap<>();
        
        JSONObject controlMapJSON = json.getJSONObject("controlMap");
        for(Iterator<String> controlNamesIter = controlMapJSON.keys(); controlNamesIter.hasNext(); ) {
            String controlName = controlNamesIter.next();
            JSONObject controlJSON = controlMapJSON.getJSONObject(controlName);
            String inputName = controlJSON.getString("input");
            Float mult = controlJSON.getFloat("mult");
            Float deadband = controlJSON.getFloat("deadband");
            Control c = new SimpleControl(controlName, inputName, mult, deadband);
            controls.put(controlName, c);
        }        
        
    }
    
    /**
     * Set the input map backing this control map
     * @param im the input map to back this control map
     */
    public void setInputMap(InputMap im) {
        this.im = im;
    }
    
    /**
     * Get the value of a control from its name
     * 
     * @param controlName the name of the desired control
     * @return the value of the control
     */
    public float getValue(String controlName) {
        Control c = controls.get(controlName);
        String inputName = c.getInput();
        return c.transformRaw(im.getInput(inputName));
    }
    
    /**
     * Create a control map from the json file at the specified path
     * 
     * @param path the path of the json file to create the map
     * @return the constructed control map
     */
    public static ControlMap loadControlMap(Path path) {
        
        String text = "";
        try {
            text = new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            System.err.println("Could not read JSON file");
        }
        JSONTokener tokener = new JSONTokener(text);
        JSONObject obj = new JSONObject(tokener);

        return new ControlMap(obj);
    }
}
