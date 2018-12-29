/**
 * This is the food that the snake eats.
 * 
 * @author Dave Avis
 * @version 12.21.2018
 */
public class Food
{
    private int row;
    private int col;
    public boolean isShowing;
    
    /**
     * Constructor
     * 
     * @param row the row the food is in.
     * @param col the column the food is in.
     */
    public Food( int row, int col )
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Constructor
     * 
     * @param row the row the food is in.
     * @param col the column the food is in.
     * @param showing true if the food is visible.
     */
    public Food( int row, int col, boolean showing )
    {
        this.row = row;
        this.col = col;
        this.isShowing = showing;
    }
    
    /**
     * Set the location of the food.
     * 
     * @param row set the row the food is in.
     * @param col set the column the food is in.
     */
    public void setLocation( int row, int col )
    {
        this.row = row;
        this.col = col;
    }
    
    /**
     * Get the row the food is in.
     * 
     * @return the row the food is in.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * Get the column the food is in.
     * 
     * @return the column the food is in.
     */
    public int getCol()
    {
        return col;
    }
    
    /**
     * Set the row the food is in.
     * 
     * @param row the row the food should be in.
     */
    public void setRow( int row )
    {
        this.row = row;
    }
    
    /**
     * Set the column the food is in.
     * 
     * @param col the column the food should be in.
     */
    public void setCol( int col )
    { 
        this.col = col;
    }
}