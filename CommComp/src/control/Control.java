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
 * An abstraction of control of a system.
 * Simply maps a raw input into an output.
 * 
 * For example a control named "FORWARDS" could map the raw value of the
 * input "LEFT_STICK_Y" to half of that
 * 
 * @author Andrew_2
 */
public interface Control {
    
    /**
     * Get name of control scheme
     * @return name of control scheme
     */
    public String getName();
    
    /**
     * Get name of input for control
     * @return name of input for control
     */
    public String getInput();
    
    /**
     * Map the raw input into an output
     * 
     * @param raw the raw input
     * @return the control output
     */
    public float transformRaw(float raw);
}
