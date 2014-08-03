/*
 * BASED ON http://www.java2s.com/Open-Source/Java/Development/jgap-3.4.4/examples/monalisa/core/GAConfiguration.java.htm
*/

package painter;

import java.util.*;
import java.awt.image.*;
import org.jgap.*;
import org.jgap.event.*;
import org.jgap.impl.*;
import org.jgap.util.*;

public class GAConfiguration
    extends Configuration implements ICloneable {
    final private BufferedImage targetImg;
    final public List<XY> coords = new ArrayList<XY>();
  
    public GAConfiguration(BufferedImage img)
        throws InvalidConfigurationException {
        super();
        targetImg = img;
        setBreeder(new GABreeder());
        setRandomGenerator(new StockRandomGenerator());
        setEventManager(new EventManager());
        setChromosomePool(new ChromosomePool());
    }

    public BufferedImage getTarget() {
        return targetImg;
    }

    @Override
    public Object clone() {
        try {
            return new GAConfiguration(targetImg);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
