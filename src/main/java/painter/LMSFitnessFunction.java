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
 *  http://www.java2s.com/Open-Source/Java/Development/jgap-3.4.4/examples/gp/monalisa/core/LMSFitnessFunction.java.htm
 */
package painter;

import java.util.*;
import java.io.*;
import java.util.logging.*;
import java.awt.*;
import java.awt.image.*;
import org.jgap.*;
import org.jgap.impl.*;
import javax.imageio.*;

public class LMSFitnessFunction extends FitnessFunction {
    final private static Logger log =
        Logger.getLogger(LMSFitnessFunction.class.getName());
    public static Map<String, BufferedImage> tileCache = 
        new HashMap<String, BufferedImage>();
    private final GAConfiguration gaConf;
    private final int[] targetPixels;
    
    LMSFitnessFunction(GAConfiguration conf) {
        super();
        gaConf = conf;
        BufferedImage target = gaConf.getTarget();
        targetPixels = new int[target.getWidth() * target.getHeight()];
        PixelGrabber pg = new PixelGrabber(target, 0, 0, target.getWidth(),
            target.getHeight(), targetPixels, 0,
            target.getWidth());
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            Logger.getLogger(LMSFitnessFunction.class.getName()).log(Level.SEVERE, null,
                ex);
        }
    }
    
    @Override
    protected double evaluate(IChromosome ch) {
        BufferedImage generated = generateImage(ch);
        final int[] generatedPixels = new int[
            generated.getWidth() *
            generated.getHeight()];
        PixelGrabber pg = new PixelGrabber(
            generated, 0, 0, generated.getWidth(),
            generated.getHeight(), generatedPixels,
            0, generated.getWidth());
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        double sum = 0;
        for (int i = 0; i < generatedPixels.length && i < targetPixels.length; i++) {
            int c1 = targetPixels[i];
            int c2 = generatedPixels[i];
            int r = ( (c1 >> 16) & 0xff) - ( (c2 >> 16) & 0xff);
            int g = ( (c1 >> 8) & 0xff) - ( (c2 >> 8) & 0xff);
            int b = (c1 & 0xff) - (c2 & 0xff);
            sum += r * r + g * g + b * b;
        }
        return Math.sqrt(sum);
    }
    
    public BufferedImage generateImage(IChromosome ch) {
        try {
            int w = gaConf.getTarget().getWidth();
            int h = gaConf.getTarget().getHeight();
            BufferedImage img = new BufferedImage(
                w, h, 
                BufferedImage.TYPE_INT_RGB);
            Graphics2D gr = (Graphics2D) img.getGraphics();
            for(int i=0; i< gaConf.coords.size(); i++) {
                IntegerGene gene = (IntegerGene) ch.getGene(i);
                int fnn = gene.intValue();
                final String sfn = Painter.slices.get(fnn);
                if (tileCache.get(sfn) == null) {
                    log.info("caching " + sfn);
                    BufferedImage z = ImageIO.read(
                        new File(Painter.slices.get(fnn)));
                        tileCache.put(sfn, z);
                }
                BufferedImage slice = tileCache.get(sfn);
                XY xy = gaConf.coords.get(i);
                gr.drawImage(slice, xy.x, xy.y, null);
            }
            return img;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
