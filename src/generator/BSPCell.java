/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

/**
 *
 * @author 2014130020
 */
public interface BSPCell {

    public boolean isObstacle();
    
    public void setObstacle(boolean obstacle);

    public int getX();

    public int getY();
    
}
