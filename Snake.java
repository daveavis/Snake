import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is an implementation of Conway's Game of Life. This is designed
 * so that a student could write the logic of the program and have it work
 * without having to learn about graphics programming, but a motivated 
 * student could modify the graphics if they wanted to.
 * 
 * @author Dave Avis
 * @version 12.19.2018
 */
public class Snake extends ApplicationAdapter implements InputProcessor
{
    // Required Constants and Variables
    private static final int ROWS = 50; // The number of rows in the world grid
    private static final int COLS = 50; // The number of columns in the world grid
    private static final String WINDOW_TITLE = "Snake"; // The text in the window titlebar
    private static final int WINDOW_WIDTH = 400; // width of the window in pixels.
    private static final int WINDOW_HEIGHT = 400; // height of the window in pixels.
    private static float timeStep = 0.1f; // the number of seconds between screen refreshes.
    private String[][] cells; // This is the grid of the world.
    // These are the possible values for each grid location, along with the 
    // associated color. Each row is a Value, Color pair. A complete list of
    // colors is below in the draw() method.
    private String[][] valuesAndColors = { 
        { "BACKGROUND", "BLACK" },  // A single Value, Color pair.
        { "BODY", "GREEN" },
        { "HEAD", "FOREST" },
        { "FOOD", "CYAN" },
        { "DEAD", "SCARLET" }
    };
    private char direction; // UDLR, the direction the snake should move
    private boolean gameOver = false;
    
    // Game Specific Constants and Variables
    private int headRow;
    private int headCol;
    private int tailRow;
    private int tailCol;
    private int foodRow;
    private int foodCol;
    private boolean ate; // did the snake just eat the food?
    private Food food;
    private ArrayList<BodyPart> snake = new ArrayList<BodyPart>();
    
    /**
     * Constructor. Initializes the world grid and populates it.
     */
    public Snake()
    {
        cells = new String[ROWS][COLS];
        food = new Food( ThreadLocalRandom.current().nextInt(0, ROWS-1), ThreadLocalRandom.current().nextInt(0, COLS-1), false ); // new food in random location and hidden
    
        headRow = ThreadLocalRandom.current().nextInt(3, ROWS-2);  // don't start at the edge
        headCol = ThreadLocalRandom.current().nextInt(3, COLS-2);
        tailRow = headRow;
        tailCol = headCol;
        snake.add( new BodyPart( headRow, headCol ) );
        direction = 'R';
        ate = false;
    }
    
    /**
     * Clears the board.  Sets every location to BACKGROUND.
     */
    private void clearBoard()
    {
        for( int row = 0; row < ROWS; row++ )
        {
            for( int col = 0; col < COLS; col++ )
            {
                cells[row][col] = "BACKGROUND";
            }
        }
    }
    
    /**
     * Updates each location of the board.
     * First clears the board, then goes through the snake BodyParts and sets those values,
     * then draws the food if it is visible.
     */
    public void updateBoard()
    {
        clearBoard();
        for( BodyPart part : snake )
        {
            if( gameOver )
            {
                cells[part.getRow()][part.getCol()] = "DEAD";
                //board.setCellValue( part.getRow(), part.getCol(), Constants.CellValue.DEAD );
            } else {
                cells[part.getRow()][part.getCol()] = "BODY";
                //board.setCellValue( part.getRow(), part.getCol(), Constants.CellValue.SNAKE );
            }
        }
        if( ! gameOver ) cells[snake.get(0).getRow()][snake.get(0).getCol()] = "HEAD";
        if( food.isShowing )
        {
            cells[food.getRow()][food.getCol()] = "FOOD";
            //board.setCellValue( food.getRow(), food.getCol(), Constants.CellValue.FOOD );
        }
    }
    
    /**
     * Handles arrow key input. Does not allow the snake to reverse direction, which would be fatal.
     */
    private void keyPressed( String key )
    {
        switch( key ) {
            case "UP": 
                if( direction != 'D' ) direction = 'U'; break;
            case "DOWN": 
                if( direction != 'U' ) direction = 'D'; break;
            case "LEFT": 
                if( direction != 'R' ) direction = 'L'; break;
            case "RIGHT": 
                if( direction != 'L' ) direction = 'R'; break;
            default: break;
        }
    }
    
    /**
     * Move the snake. Also checks if the tail needs to grow or not.
     */
    public void moveSnake()
    {
        // move the head
        int prevHeadRow = snake.get(0).getRow();
        int prevHeadCol = snake.get(0).getCol();
        tailRow = snake.get( snake.size() - 1 ).getRow();
        tailCol = snake.get( snake.size() - 1 ).getCol();
        switch( direction ) {
            case 'U': snake.get(0).setRow( snake.get(0).getRow() + 1 ); break;
            case 'D': snake.get(0).setRow( snake.get(0).getRow() - 1 ); break;
            case 'R': snake.get(0).setCol( snake.get(0).getCol() + 1 ); break;
            case 'L': snake.get(0).setCol( snake.get(0).getCol() - 1 ); break;
            default: break;
        }
        
        // check for collision with walls, body, and food
        checkWallCollision();
        if( snake.size() > 2 ) checkBodyCollision();
        checkFoodCollision();
        
        // move the tail (if necessary)
        for( int i = snake.size() - 1; i > 1; i-- )
        {
            snake.get(i).setRow( snake.get(i-1).getRow() );
            snake.get(i).setCol( snake.get(i-1).getCol() );
        }
        if( snake.size() > 1 )
        {
            snake.get(1).setRow( prevHeadRow );
            snake.get(1).setCol( prevHeadCol );
        }
        if( ate )
        {
            snake.add( new BodyPart( tailRow, tailCol ) );
            ate = false;
        }
    }
    
    /**
     * Check if the snake collided with a visible food object.
     * If it does, then creates a new hidden food object.
     */
    public void checkFoodCollision()
    {
        if( snake.get(0).getRow() == food.getRow() && snake.get(0).getCol() == food.getCol() )
        {
            ate = true;
            food.isShowing = false;
            int newRow = ThreadLocalRandom.current().nextInt(0, ROWS-1);
            int newCol = ThreadLocalRandom.current().nextInt(0, COLS-1);
            while( ! cells[newRow][newCol].equals("BACKGROUND") )
            {
                newRow = ThreadLocalRandom.current().nextInt(0, ROWS-1);
                newCol = ThreadLocalRandom.current().nextInt(0, COLS-1);
            }
            food.setRow( newRow );
            food.setCol( newCol );
        }
    }
    
    /**
     * Check for collisions with walls
     */
    public void checkWallCollision()
    {
        if( snake.get(0).getRow() < 0 ) 
        {
            gameOver = true;
            snake.get(0).setRow( snake.get(0).getRow() + 1 );
        }
        if( snake.get(0).getRow() > ROWS - 1 )
        {
            gameOver = true;
            snake.get(0).setRow( snake.get(0).getRow() - 1 );
        }
        if( snake.get(0).getCol() < 0 )
        {
            gameOver = true;
            snake.get(0).setCol( snake.get(0).getCol() + 1 );
        }
        if( snake.get(0).getCol() > COLS - 1 )
        {
            gameOver = true;
            snake.get(0).setCol( snake.get(0).getCol() - 1 );
        }
    }
    
    /**
     * Check for collisions with other parts of the snake.
     */
    public void checkBodyCollision()
    {
        for( int i = 1; i < snake.size(); i++ )
        {
            if( snake.get(0).getRow() == snake.get(i).getRow() && snake.get(0).getCol() == snake.get(i).getCol() )
            {
                gameOver = true;
            }
        }
    }
    
    /**
     * This method is called every time the screen is refreshed (based on your
     * timeStep). This should call any methods needed to update the screen for the
     * next time it is drawn.
     */
    public void doUpdates()
    {
        if( ! gameOver )
        {
            // generate new food if needed
            if( ! food.isShowing && Math.random() < 0.2 )
            {
                food.isShowing = true;
            }
            moveSnake();
        }
        updateBoard();
    }
    
/////////////////////////////////////////////////////////
/////////// DO NOT EDIT BELOW THIS LINE /////////////////
///////////////////////////////////////////////////////// 
   
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private float timeSinceLastFrame = 0f;
    
    /**
     * Used to launch the application.
     * 
     * @param args not used.
     */
    public static void main( String[] args )
    {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

        cfg.title = WINDOW_TITLE;
        cfg.width = WINDOW_WIDTH;
        cfg.height = WINDOW_HEIGHT;

        new LwjglApplication(new Snake(), cfg);
    }
    
    /**
     * Sets up the Camera and the ShapeRenderer.
     */
    @Override
    public void create()
    {
        // setup the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, COLS, ROWS);
        camera.update();
        
        shapeRenderer = new ShapeRenderer();
        
        Gdx.input.setInputProcessor(this);
    }
    
    /**
     * Clean up.
     */
    @Override
    public void dispose()
    {
        shapeRenderer.dispose();
    }
    
    /**
     * Updates and draws the board after the specified time has passed.
     * The refresh interval is found in the variable timeStep.
     */
    @Override
    public void render()
    {
        timeSinceLastFrame += Gdx.graphics.getDeltaTime();
        if( timeSinceLastFrame > timeStep )
        {
            doUpdates();
            timeSinceLastFrame = 0f;
        }
        draw(); // don't put this in the if statement. render() must draw something everytime.
    }
    
    /**
     * Checks the value of each cell in the grid, updates it with the proper color,
     * and draws the cell.
     */
    public void draw()
    {
        Color color = Color.WHITE;
        
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        shapeRenderer.setProjectionMatrix( camera.combined );
        
        for( int row = 0; row < ROWS; row++ )
        {
            for( int col = 0; col < COLS; col++ )
            {
                for( int i = 0; i < valuesAndColors.length; i++ )
                {
                    if( cells[row][col] == valuesAndColors[i][0] )
                    {
                        switch( valuesAndColors[i][1] ) {
                            case "BLACK": color = Color.BLACK; break;
                            case "BLUE": color = Color.BLUE; break;
                            case "BROWN": color = Color.BROWN; break;
                            case "CHARTREUSE": color = Color.CHARTREUSE; break;
                            case "CLEAR": color = Color.CLEAR; break;
                            case "CORAL": color = Color.CORAL; break;
                            case "CYAN": color = Color.CYAN; break;
                            case "DARK_GRAY": color = Color.DARK_GRAY; break;
                            case "FIREBRICK": color = Color.FIREBRICK; break;
                            case "FOREST": color = Color.FOREST; break;
                            case "GOLD": color = Color.GOLD; break;
                            case "GOLDENROD": color = Color.GOLDENROD; break;
                            case "GRAY": color = Color.GRAY; break;
                            case "GREEN": color = Color.GREEN; break;
                            case "LIGHT_GRAY": color = Color.LIGHT_GRAY; break;
                            case "LIME": color = Color.LIME; break;
                            case "MAGENTA": color = Color.MAGENTA; break;
                            case "MAROON": color = Color.MAROON; break;
                            case "NAVY": color = Color.NAVY; break;
                            case "OLIVE": color = Color.OLIVE; break;
                            case "ORANGE": color = Color.ORANGE; break;
                            case "PINK": color = Color.PINK; break;
                            case "PURPLE": color = Color.PURPLE; break;
                            case "RED": color = Color.RED; break;
                            case "ROYAL": color = Color.ROYAL; break;
                            case "SALMON": color = Color.SALMON; break;
                            case "SCARLET": color = Color.SCARLET; break;
                            case "SKY": color = Color.SKY; break;
                            case "SLATE": color = Color.SLATE; break;
                            case "TAN": color = Color.TAN; break;
                            case "TEAL": color = Color.TEAL; break;
                            case "VIOLET": color = Color.VIOLET; break;
                            case "WHITE": color = Color.WHITE; break;
                            case "YELLOW": color = Color.YELLOW; break;
                            default: color = Color.WHITE; break;
                        }
                    }
                }
                drawBox( color, row, col );
            }
        }
    }
    
    /**
     * Draw a filled box of the proper color in the given cell.
     * 
     * @param color the color to fill the cell with.
     * @param row the row the cell is in.
     * @param col the column the cell is in.
     */
    public void drawBox( Color color, int row, int col )
    {
        shapeRenderer.begin( ShapeType.Filled );
        shapeRenderer.setColor( color );
        shapeRenderer.rect( col, row, 1, 1 );
        shapeRenderer.end();
    }
    
    // InputProcessor methods
    @Override
    public boolean keyDown(int keycode) 
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) 
    {
        if( keycode == Input.Keys.UP ) keyPressed( "UP" );
        if( keycode == Input.Keys.DOWN ) keyPressed( "DOWN" );
        if( keycode == Input.Keys.LEFT ) keyPressed( "LEFT" );
        if( keycode == Input.Keys.RIGHT ) keyPressed( "RIGHT" );
        return true;  // the event was handled
    }

    @Override
    public boolean keyTyped(char character) 
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) 
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) 
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) 
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) 
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount) 
    {
        return false;
    }
}