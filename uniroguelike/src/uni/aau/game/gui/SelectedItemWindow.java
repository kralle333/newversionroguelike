package uni.aau.game.gui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import uni.aau.game.gameobjects.Player;
import uni.aau.game.helpers.AssetManager;
import uni.aau.game.helpers.GameAction;
import uni.aau.game.items.Armor;
import uni.aau.game.items.Item;
import uni.aau.game.items.Weapon;

public class SelectedItemWindow extends Window
{
    private GameAction _selectedAction = new GameAction();
    public boolean hasAction(){return _selectedAction !=null;}
    public GameAction retrieveAction(){return _selectedAction;}
    private Item _selectedItem;
    public Item retrieveItem(){Item toReturn = _selectedItem;_selectedItem=null;return toReturn;}
    private Player _player;
    private BitmapFont _descriptionFont;

    public SelectedItemWindow(int x, int y, int width, int height,Color color, int frameSize,Color frameColor)
    {
        super(x,y,width,height,color,frameSize,frameColor);
        addButton("Use", 0.1f, 0.85f, 0.25f, 0.10f, Color.BLUE);
        addButton("Equip",0.10f,0.85f,0.25f,0.10f,Color.BLUE);
        addButton("Throw",0.40f,0.85f,0.25f,0.10f,Color.BLUE);
        addButton("Drop",0.70f,0.85f,0.25f,0.10f,Color.BLUE);
        hideButton("Equip");
        _selectedAction = new GameAction();
        _descriptionFont = AssetManager.getFont("description");
    }
    public void reset(Player player)
    {
        _player = player;
    }
    public void tap(float x, float y)
    {
        if(_windowRectangle.contains(x,y))
        {
            if(isPressed("Drop",x,y))
            {
                _selectedAction.setAction(_player, GameAction.Type.Drop, _player.getCurrentTile(), _selectedItem);
            }
            else if(isPressed("Throw",x,y))
            {
                _selectedAction.setAction(null,GameAction.Type.Throw,null,_selectedItem);
            }
            else if(_selectedItem instanceof Armor || _selectedItem instanceof Weapon)
            {
                if(isPressed("Equip",x,y))
                {
                    if(_player.isEquipped(_selectedItem))
                    {
                        if(_selectedItem.hasCurse())
                        {
                            GameConsole.addMessage("Some magical energy prevents the item from being unequipped");
                        }
                        else
                        {
                            _selectedAction.setAction(_player, GameAction.Type.Unequip, _player.getCurrentTile(), _selectedItem);
                        }
                    }
                    else
                    {
                        _selectedAction.setAction(_player,GameAction.Type.Equip,_player.getCurrentTile(),_selectedItem);
                    }

                }
            }
            else if(isPressed("Use",x,y))
            {
                _selectedAction.setAction(_player,GameAction.Type.Use,_player.getCurrentTile(),_selectedItem);
            }
        }
        else
        {
            hide();
        }
    }

    public void show(Item item)
    {
        _selectedItem=item;
        if(item instanceof Armor)
        {
            showButton("Equip");
            hideButton("Use");
        }
        else if(item instanceof Weapon)
        {
            if(((Weapon) item).isRanged())
            {
                hideButton("Equip");
            }
            else
            {
                showButton("Equip");
            }
            hideButton("Use");
        }
        else
        {
            showButton("Use");
            hideButton("Equip");
        }
        super.show();
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer)
    {
        if(_isOpen)
        {
            super.draw(batch, shapeRenderer);
            batch.begin();
            _descriptionFont.draw(batch,_selectedItem.getName(),_windowRectangle.x+5,_windowRectangle.y+5);
            _selectedItem.draw(batch,_windowRectangle.x+(_windowRectangle.width/2)-32,_windowRectangle.y+(_windowRectangle.height/2)-128,2);
            _descriptionFont.drawWrapped(batch,_selectedItem.getDescription(),_windowRectangle.x+5,_windowRectangle.y+(2*_windowRectangle.height/3)-64,_windowRectangle.width-10);
            batch.end();
        }
    }
}
