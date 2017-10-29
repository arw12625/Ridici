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

/**
 * A simple implementation of a control consisting of a scaling and a deadband.
 * The control outputs a value 0 for inputs less than the deadband in 
 * absolute value.
 * 
 * @author Andrew_2
 */
public class SimpleControl implements Control {

    private String name;
    private String input;
    private float mult;
    private float deadband;
    
    /**
     * Construct a simple control with specified parameters
     * 
     * @param name the name of the control
     * @param input the name of the raw input
     * @param mult the value of the scaling multiplier
     * @param deadband the value of the deadband
     */
    public SimpleControl(String name, String input, float mult, float deadband) {
        this.name = name;
        this.input = input;
        this.mult = mult;
        this.deadband = deadband;
    }
    
    /**
     * Get the name of the control
     * 
     * @return the name of the control
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Get the name of the raw input the control uses
     * @return the name of the raw input the control uses
     */
    @Override
    public String getInput() {
        return input;
    }

    /**
     * Applies the scaling and deadband to the input
     * @param raw the value of the raw input for the control
     * @return the output value of the control
     */
    @Override
    public float transformRaw(float raw) {
        if(Math.abs(raw) > deadband) {
            return raw * mult;
        } else {
            return 0;
        }
        
    }
    
    
}
