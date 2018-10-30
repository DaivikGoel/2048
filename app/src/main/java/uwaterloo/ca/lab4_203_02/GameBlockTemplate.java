package uwaterloo.ca.lab4_203_02;


import android.content.Context;
import android.widget.ImageView;

public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView{
    GameBlockTemplate(Context myContext){
        super(myContext);
    }
    abstract public void setDestination();
    abstract public void move();
}
