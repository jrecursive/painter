/*
 * Painter
 * Copyright (c) 2014, John Muellerleile, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 *
 * This code is based upon code in the JGAP library:
 *  http://www.java2s.com/Open-Source/Java/Development/jgap-3.4.4/examples/monalisa/core/GAConfiguration.java.htm
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
