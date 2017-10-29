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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A string modifier to map hexadecimal escape codes to an 8-bit character with the
 * corresponding hexadecimal value
 * 
 * A hexadecimal escape code is defined as a backslash followed by exactly two
 * valid hexadecimal digit characters.
 * [\] [two hexadecimal digit characters]
 * 
 * A valid hexadecimal digit character is one of the following:
 * Digits 0-9
 * Lowercase letters a-f
 * Uppercase letters A-F
 * 
 * As the backslash character itself must be escaped when using String 
 * literals in java, the literal string
 * "\\5A"
 * will be correctly modfied to the string with hexadecimal value
 * 5A
 * However the literal string
 * "\51"
 * is not a valid literal string.
 * 
 * Most methods of user input for example JTextFields and the console will
 * automatically use the correct escape for backslash, but be aware.
 * For example the user input from the console
 * \16\A5\9F
 * would correctly map to the string with hexadecimal value
 * 16A59F
 * 
 * 
 * @author Andrew_2
 */
public class HexEscapeModifier implements StringModifier {

    /**
     * The regular expression that matches the hexadecimal escape code
     */
    public static final String hexEscPattern = "\\\\[A-Fa-f0-9]{2}";
    
    /**
     * Replace all hexadecimal escape codes with the appropriate hex value
     * @param input the input string to modify
     * @return the modified string
     */
    @Override
    public String modify(String input) {
        Pattern r = Pattern.compile(hexEscPattern);
        Matcher m = r.matcher(input);
        
        StringBuffer sb = new StringBuffer();
        
        while(m.find()) {
            char hex = (char)((Character.digit(m.group().charAt(1), 16) << 4) |
                    Character.digit(m.group().charAt(2), 16));
            m.appendReplacement(sb, ""+hex);
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
}
