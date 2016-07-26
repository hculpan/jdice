package org.culpan.jdice;
/*
 * Created on Mar 26, 2005
 *
 */

/**
 * @author harry
 *
 */
public class DiceSystemException extends RuntimeException {
    public DiceSystemException(String msg) {
        super(msg);
    }
    
    public DiceSystemException(String msg, Throwable c) {
        super(msg, c);
    }
}
