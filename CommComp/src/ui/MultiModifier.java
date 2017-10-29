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
package ui;

import java.util.ArrayList;
import java.util.List;

/**
 * A string modifier whose output is the composition of multiple other 
 * String modifiers. String modifiers are applied in the order that 
 * they were added.
 * 
 * @author Andrew_2
 */
public class MultiModifier implements StringModifier {
    
    private List<StringModifier> modifiers;
    
    /**
     * Create a MultiModifier with an empty list of string modifiers
     */
    public MultiModifier() {
        modifiers = new ArrayList<>();
    }
    
    /**
     * Add a string modifier to the end of the list of modifiers.
     * This modifier will be executed after all other modifiers 
     * currently in the list.
     * 
     * @param mod the string modifier to add
     */
    public void addModifier(StringModifier mod) {
        modifiers.add(mod);
    }
    
    /**
     * Modify the given input string by composing all the string modifiers
     * currently in the list of modifiers
     * 
     * @param input the input string to be modified
     * @return the ouput of the composed modifiers
     */
    @Override
    public String modify(String input) {
        String mod = input;
        for(int i = 0; i < modifiers.size(); i++) {
            mod = modifiers.get(i).modify(mod);
        }
        return mod;
    }
    
}
