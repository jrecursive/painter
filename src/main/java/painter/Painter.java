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
*/
package painter;

import java.io.*;
import java.util.*;
import java.awt.image.*;
import org.jgap.*;
import org.jgap.impl.*;
import javax.imageio.*;
import java.util.logging.*;

public class Painter {
    final private static Logger log =
        Logger.getLogger(Painter.class.getName());
    public static String targetFn = null;
    public static List<String> slices = 
        new ArrayList<String>();
    public static int tileSize = 10;
    public static int port = 8000;
    
    public static double bestPct = 0.75d;
    public static int popSz = 125;

    public Painter(BufferedImage img) throws Exception {
        Genotype genotype = null;
        GAConfiguration gaConf = new GAConfiguration(img);
        gaConf.setPreservFittestIndividual(true);        
        gaConf.addGeneticOperator(new CrossoverOperator(gaConf));
        gaConf.addGeneticOperator(new MutationOperator(gaConf, 
            new DefaultMutationRateCalculator(gaConf)));

        BestChromosomesSelector bestChromsSelector =
                new BestChromosomesSelector(gaConf, bestPct);
        bestChromsSelector.setDoubletteChromosomesAllowed(true);
        gaConf.addNaturalSelector(bestChromsSelector, true);
        
        int w = gaConf.getTarget().getWidth();
        int h = gaConf.getTarget().getHeight();
        int wCh = (int)Math.ceil(w / tileSize) + 1;
        int hCh = (int)Math.ceil(h / tileSize) + 1;
        int chunks = wCh * hCh;
        
        int xPos = 0, yPos = 0;
        for(int i=0; i<chunks; i++) {
            XY xy = new XY();
            xy.x = xPos;
            xy.y = yPos;
            gaConf.coords.add(xy);
            xPos += tileSize;
            if (xPos > w) {
                xPos = 0;
                yPos += tileSize;
            }
        }
        
        log.info(
            "tileSize = " + tileSize + 
            ", wCh = " + wCh + 
            ", hCh = " + hCh + 
            ", chunks = " + chunks);
        
        IChromosome sampleChromosome = new Chromosome(gaConf,
                new IntegerGene(gaConf, 0, slices.size()-1), chunks);

        gaConf.setSampleChromosome(sampleChromosome);
        gaConf.setPopulationSize(popSz);
        gaConf.setFitnessEvaluator(new DeltaFitnessEvaluator());
        final LMSFitnessFunction fitnessFunction = 
            new LMSFitnessFunction(gaConf);
        gaConf.setFitnessFunction(fitnessFunction);
        genotype = Genotype.randomInitialGenotype(gaConf);
        
        /* evolve */
        
        int generations = 0;
        double pfit = 0.0d;
        long pt = System.currentTimeMillis();
        while (true) {
            genotype.evolve();
            IChromosome fittest = genotype.getFittestChromosome();
            double fitness = fittest.getFitnessValue();
            log.info("fitness = " + fitness + " <pfit = " + pfit + ">");
            log.info("[" + generations + ":" + (System.currentTimeMillis() / 1000) +
                    "] optimize: best fitness so far: " + fitness);
            long tt = System.currentTimeMillis();
            long tt1 = tt - pt;
            BufferedImage winner = fitnessFunction.generateImage(fittest);
            ImageIO.write(winner, "jpg", new File("www/current.jpg"));
            log.info(
                "* generation: " + generations + " " + 
                "improvement: " + (pfit - fitness) + " " + 
                "time: " + tt1 + "ms" + 
                "\n");
            pt = tt;
            pfit = fitness;
            generations++;
        }
    }
    
    public static void processLibrary(String dir) throws Exception {
        File[] files = new File(dir).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                slices.add(dir + "/" + file.getName());
            }
        }
        log.info(files.length + " slices found");
    }
    
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true"); 
        if (args.length < 2) {
            log.info("usage: painter.Painter <target-file> <tile-dir> <tile-size> <port>");
            System.exit(-1);
        }
        targetFn = args[0];
        processLibrary(args[1]);
        if (args.length >= 3) {
            Painter.tileSize = Integer.parseInt(args[2]);
        }
        if (args.length == 4) {
            Painter.port = Integer.parseInt(args[3]);
        } else {
            Painter.port = 8000;
        }
        log.info("Painter: " + 
            "library = " + args[1] + 
            ", target = " + targetFn + 
            ", port = " + Painter.port +
            ", tileSize = " + Painter.tileSize);
        HTTPServer httpServer = new HTTPServer();
        new Painter(ImageIO.read(new File(args[0])));
    }
}
