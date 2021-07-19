package managers;

import com.Engine;
import com.EngineInter;

public class EngineManager {

    public EngineInter eng;

    public EngineManager(){
        eng = new Engine();
    }

    public EngineInter getEng(){return eng;}

}
