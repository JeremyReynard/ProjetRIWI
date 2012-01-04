/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;

/**
 * Class Bm25Elements
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Elements extends Bm25Articles {
    
    public Bm25Elements(String request, Index index, int k1, int b) {
        super(request, index, k1, b);
    }
    
}
