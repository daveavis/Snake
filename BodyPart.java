/**
 * A single body part of the snake.
 * 
 * @author Dave Avis
 * @version 12.22.2018
 */
public class BodyPart
{
    private int row;
    private int col;
    
    /**
     * Constructor
     * 
     * @param row the row the body part is in.
     * @param col the column the body part is in.
     */
    public BodyPart( int row, int col )
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Set the location of this BodyPart.
     * 
     * @param row the row of this body part.
     * @param col the column of this body part.
     */
    public void setLocation( int row, int col )
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Get the row of this BodyPart.
     * 
     * @return the row of this BodyPart.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * Get the column of this BodyPart.
     * 
     * @return the column of this BodyPart.
     */
    public int getCol()
    {
        return col;
    }
    
    /**
     * Set the row of this BodyPart.
     * 
     * @param row the row this BodyPart should be.
     */
    public void setRow( int row )
    {
        this.row = row;
    }
    
    /**
     * Set the column of this BodyPart.
     * 
     * @param col the column this BodyPart should be.
     */
    public void setCol( int col )
    { 
        this.col = col;
    }
}