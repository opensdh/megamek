/*
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
 * Copyright © 2013 Edward Cullen (eddy@obsessedcomputers.co.uk)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

/*
 * HexTileset.java
 *
 * Created on May 9, 2002, 1:33 PM
 */

package megamek.client.ui.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import megamek.client.ui.swing.util.ImageCache;
import megamek.common.Board;
import megamek.common.Configuration;
import megamek.common.Hex;
import megamek.common.IGame;
import megamek.common.IHex;
import megamek.common.ITerrain;
import megamek.common.Terrains;
import megamek.common.event.BoardEvent;
import megamek.common.event.BoardListener;
import megamek.common.event.GameBoardChangeEvent;
import megamek.common.event.GameBoardNewEvent;
import megamek.common.event.GameListener;
import megamek.common.event.GameListenerAdapter;
import megamek.common.util.ImageUtil;
import megamek.common.util.MegaMekFile;
import megamek.common.util.StringUtil;

/**
 * Matches each hex with an appropriate image.
 *
 * @author Ben
 */
public class HexTileset implements BoardListener {

    /**
     * The image width of a hex image.
     */
    public static final int HEX_W = 84;

    /**
     * The image height of a hex image.
     */
    public static final int HEX_H = 72;

    public static final String TRANSPARENT_THEME = "transparent";
    
    private IGame game;

    private ArrayList<HexEntry> bases = new ArrayList<HexEntry>();
    private ArrayList<HexEntry> supers = new ArrayList<HexEntry>();
    private ArrayList<HexEntry> orthos = new ArrayList<HexEntry>();
    private Set<String> themes = new TreeSet<String>();
    private ImageCache<IHex, Image> basesCache = new ImageCache<IHex, Image>();
    private ImageCache<IHex, List<Image>> supersCache = new ImageCache<IHex, List<Image>>();
    private ImageCache<IHex, List<Image>> orthosCache = new ImageCache<IHex, List<Image>>();

    /**
     * Creates new HexTileset
     */
    public HexTileset(IGame g) {
        game = g;
        game.addGameListener(gameListener);
        game.getBoard().addBoardListener(this);
    }

    /** Clears the image cache for the given hex. */
    public synchronized void clearHex(IHex hex) {
        basesCache.remove(hex);
        supersCache.remove(hex);
        orthosCache.remove(hex);
    }

    /** Clears the image cache for all hexes. */
    public synchronized void clearAllHexes() {
        basesCache = new ImageCache<IHex, Image>();
        supersCache = new ImageCache<IHex, List<Image>>();
        orthosCache = new ImageCache<IHex, List<Image>>();
    }
    
    /**
     * This assigns images to a hex based on the best matches it can find.
     * <p/>
     * First it assigns any images to be superimposed on a hex. These images must
     * have a match value of 1.0 to be added, and any time a match of this level is
     * achieved, any terrain involved in the match is removed from further
     * consideration.
     * <p/>
     * Any terrain left is used to match a base image for the hex. This time, a
     * match can be any value, and the first, best image is used.
     */
    public synchronized Object[] assignMatch(IHex hex, Component comp) {
        IHex hexCopy = hex.duplicate();
        List<Image> ortho = orthoFor(hexCopy, comp);
        List<Image> supers = supersFor(hexCopy, comp);
        Image base = baseFor(hexCopy, comp);
        Object[] pair = new Object[] { base, supers, ortho };
        basesCache.put(hex, base);
        supersCache.put(hex, supers);
        orthosCache.put(hex, ortho);
        return pair;
    }

    public synchronized Image getBase(IHex hex, Component comp) {
        Image i = basesCache.get(hex);
        if (i == null) {
            Object[] pair = assignMatch(hex, comp);
            return (Image) pair[0];
        }
        return i;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<Image> getSupers(IHex hex, Component comp) {
        List<Image> l = supersCache.get(hex);
        if (l == null) {
            Object[] pair = assignMatch(hex, comp);
            return (List<Image>) pair[1];
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<Image> getOrtho(IHex hex, Component comp) {
        List<Image> o = orthosCache.get(hex);
        if (o == null) {
            Object[] pair = assignMatch(hex, comp);
            return (List<Image>) pair[2];
        }
        return o;
    }

    /**
     * Returns a list of orthographic images to be tiled above the hex. As noted
     * above, all matches must be 1.0, and if such a match is achieved, all terrain
     * elements from the tileset hex are removed from the hex. Thus you want to pass
     * a copy of the original to this function.
     */
    private List<Image> orthoFor(IHex hex, Component comp) {
        ArrayList<Image> matches = new ArrayList<Image>();

        // find orthographic image matches
        for (HexEntry entry : orthos) {
            if (orthoMatch(hex, entry.getHex()) >= 1.0) {
                Image img = entry.getImage(comp, hex.getCoords().hashCode());
                if (img != null) {
                    matches.add(img);
                } else {
                    matches.add(ImageUtil.createAcceleratedImage(HEX_W, HEX_H));
                }
                // remove involved terrain from consideration
                for (int terr : entry.getHex().getTerrainTypes()) {
                    if (entry.getHex().containsTerrain(terr)) {
                        hex.removeTerrain(terr);
                    }
                }
            }
        }
        return matches;
    }

    /**
     * Returns a list of images to be superimposed on the hex. As noted above, all
     * matches must be 1.0, and if such a match is achieved, all terrain elements
     * from the tileset hex are removed from the hex. Thus you want to pass a copy
     * of the original to this function.
     */
    private List<Image> supersFor(IHex hex, Component comp) {
        ArrayList<Image> matches = new ArrayList<Image>();

        // find superimposed image matches
        for (HexEntry entry : supers) {
            if (superMatch(hex, entry.getHex()) >= 1.0) {
                Image img = entry.getImage(comp, hex.getCoords().hashCode());
                if (img != null) {
                    matches.add(img);
                } else {
                    matches.add(ImageUtil.createAcceleratedImage(HEX_W, HEX_H));
                }
                // remove involved terrain from consideration
                for (int terr : entry.getHex().getTerrainTypes()) {
                    if (entry.getHex().containsTerrain(terr)) {
                        hex.removeTerrain(terr);
                    }
                }
            }
        }
        return matches;
    }

    /**
     * Returns the best matching base image for this hex. This works best if any
     * terrain with a "super" image is removed.
     */
    private Image baseFor(IHex hex, Component comp) {
        HexEntry bestMatch = null;
        double match = -1;

        // match a base image to the hex
        for (HexEntry entry : bases) {

            // Metal deposits don't count for visual
            if (entry.getHex().containsTerrain(Terrains.METAL_CONTENT)) {
                hex.removeTerrain(Terrains.METAL_CONTENT);
            }

            double thisMatch = baseMatch(hex, entry.getHex());
            // stop if perfect match
            if (thisMatch == 1.0) {
                bestMatch = entry;
                break;
            }
            // compare match with best
            if (thisMatch > match) {
                bestMatch = entry;
                match = thisMatch;
            }
        }

        Image img = bestMatch.getImage(comp, hex.getCoords().hashCode());
        if (img == null) {
            img = ImageUtil.createAcceleratedImage(HEX_W, HEX_H);
        }
        return img;
    }

    // perfect match
    // all but theme
    // all but elevation
    // all but elevation & theme

    /** Recursion depth counter to prevent freezing from circular includes */
    public int incDepth = 0;

    public void loadFromFile(String filename) throws IOException {
        long startTime = System.currentTimeMillis();
        // make input stream for board
        Reader r = new BufferedReader(new FileReader(new MegaMekFile(Configuration.hexesDir(), filename).getFile()));
        // read board, looking for "size"
        StreamTokenizer st = new StreamTokenizer(r);
        st.eolIsSignificant(true);
        st.commentChar('#');
        st.quoteChar('"');
        st.wordChars('_', '_');
        while (st.nextToken() != StreamTokenizer.TT_EOF) {
            int elevation = 0;
            // int levity = 0;
            String terrain = null;
            String theme = null;
            String imageName = null;
            if ((st.ttype == StreamTokenizer.TT_WORD)
                    && (st.sval.equals("base") || st.sval.equals("super") || st.sval.equals("ortho"))) { //$NON-NLS-3$ //$NON-NLS-2$
                boolean bas = st.sval.equals("base"); //$NON-NLS-1$
                boolean sup = st.sval.equals("super"); //$NON-NLS-1$
                boolean ort = st.sval.equals("ortho"); //$NON-NLS-1$

                if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                    elevation = (int) st.nval;
                } else {
                    elevation = ITerrain.WILDCARD;
                }
                st.nextToken();
                terrain = st.sval;
                st.nextToken();
                theme = st.sval;
                themes.add(theme);
                st.nextToken();
                imageName = st.sval;
                // add to list
                if (bas) {
                    bases.add(new HexEntry(new Hex(elevation, terrain, theme), imageName));
                }
                if (sup) {
                    supers.add(new HexEntry(new Hex(elevation, terrain, theme), imageName));
                }
                if (ort) {
                    orthos.add(new HexEntry(new Hex(elevation, terrain, theme), imageName));
                }
            } else if ((st.ttype == StreamTokenizer.TT_WORD) && st.sval.equals("include")) {
                st.nextToken();
                incDepth++;
                if (incDepth < 100) {
                    String incFile = st.sval;
                    System.out.println("Including " + incFile); //$NON-NLS-1$
                    loadFromFile(incFile);
                }
            }
            // else if((st.ttype == StreamTokenizer.TT_WORD) &&
            // st.sval.equals("ortho")){}
        }
        r.close();
        themes.add(TRANSPARENT_THEME);
        long endTime = System.currentTimeMillis();
        
//        System.out.println("hexTileset: loaded " + bases.size() + " base images"); //$NON-NLS-2$ //$NON-NLS-2$
//        System.out.println("hexTileset: loaded " + supers.size() + " super images"); //$NON-NLS-2$ //$NON-NLS-2$
//        System.out.println("hexTileset: loaded " + orthos.size() + " ortho images"); //$NON-NLS-2$ //$NON-NLS-2$
        if (incDepth == 0) {
            System.out.println("hexTileset loaded in " + (endTime - startTime) + "ms.");
        }
        incDepth--;
    }

    /**
     * Initializes all the images in this tileset and adds them to the tracker
     */
    public void loadAllImages(Component comp, MediaTracker tracker) {
        for (HexEntry entry: bases) {
            if (entry.getImage() == null) {
                entry.loadImage(comp);
            }
            tracker.addImage(entry.getImage(), 1);
        }
        for (HexEntry entry: supers) {
            if (entry.getImage() == null) {
                entry.loadImage(comp);
            }
            tracker.addImage(entry.getImage(), 1);
        }
        for (HexEntry entry: orthos) {
            if (entry.getImage() == null) {
                entry.loadImage(comp);
            }
            tracker.addImage(entry.getImage(), 1);
        }
    }

    public Set<String> getThemes() {
        return new TreeSet<String>(themes);
    }

    /**
     * Adds all images associated with the hex to the specified tracker
     */
    public synchronized void trackHexImages(IHex hex, MediaTracker tracker) {

        Image base = basesCache.get(hex);
        List<Image> superImgs = supersCache.get(hex);
        List<Image> orthoImgs = orthosCache.get(hex);

        // add base
        tracker.addImage(base, 1);
        // add superImgs
        if (superImgs != null) {
            for (Image img: superImgs) {
                tracker.addImage(img, 1);
            }
        }
        if (orthoImgs != null) {
            for (Image img: orthoImgs) {
                tracker.addImage(img, 1);
            }
        }
    }

    /**
     * Match the two hexes using the "ortho" super* formula. All matches must be
     * exact, however the match only depends on the original hex matching all the
     * elements of the comparison, not vice versa.
     * <p/>
     * EXCEPTION: a themed original matches any unthemed comparison.
     */
    private double orthoMatch(IHex org, IHex com) {
        // check elevation
        if ((com.getLevel() != ITerrain.WILDCARD) && (org.getLevel() != com.getLevel())) {
            return 0;
        }

        // A themed original matches any unthemed comparison.
        if ((com.getTheme() != null) && !com.getTheme().equalsIgnoreCase(org.getTheme())) {
            return 0.0;
        }

        // org terrains must match com terrains
        if (org.terrainsPresent() < com.terrainsPresent())
            return 0.0;

        // check terrain
        int cTerrainTypes[] = com.getTerrainTypes();
        for (int i = 0; i < cTerrainTypes.length; i++) {
            int cTerrType = cTerrainTypes[i];
            ITerrain cTerr = com.getTerrain(cTerrType);
            ITerrain oTerr = org.getTerrain(cTerrType);
            if (cTerr == null) {
                continue;
            } else if ((oTerr == null)
                    || ((cTerr.getLevel() != ITerrain.WILDCARD) && (oTerr.getLevel() != cTerr.getLevel()))
                    || (cTerr.hasExitsSpecified() && (oTerr.getExits() != cTerr.getExits()))) {
                return 0;
            }
        }

        return 1.0;
    }

    /**
     * Match the two hexes using the "super" formula. All matches must be exact,
     * however the match only depends on the original hex matching all the elements
     * of the comparision, not vice versa.
     * <p/>
     * EXCEPTION: a themed original matches any unthemed comparason.
     */
    private double superMatch(IHex org, IHex com) {
        // check elevation
        if ((com.getLevel() != ITerrain.WILDCARD) && (org.getLevel() != com.getLevel())) {
            return 0;
        }

        // A themed original matches any unthemed comparison.
        if ((com.getTheme() != null) && !com.getTheme().equalsIgnoreCase(org.getTheme())) {
            return 0.0;
        }

        // org terrains must match com terrains
        if (org.terrainsPresent() < com.terrainsPresent())
            return 0.0;

        // check terrain
        int cTerrainTypes[] = com.getTerrainTypes();
        for (int i = 0; i < cTerrainTypes.length; i++) {
            int cTerrType = cTerrainTypes[i];
            ITerrain cTerr = com.getTerrain(cTerrType);
            ITerrain oTerr = org.getTerrain(cTerrType);
            if (cTerr == null) {
                continue;
            } else if ((oTerr == null)
                    || ((cTerr.getLevel() != ITerrain.WILDCARD) && (oTerr.getLevel() != cTerr.getLevel()))
                    || (cTerr.hasExitsSpecified() && (oTerr.getExits() != cTerr.getExits()))) {
                return 0;
            }
        }

        return 1.0;
    }

    /**
     * Match the two hexes using the "base" formula.
     * <p/>
     * Returns a value indicating how close of a match the original hex is to the
     * comparison hex. 0 means no match, 1 means perfect match.
     */
    private double baseMatch(IHex org, IHex com) {
        double elevation;
        double terrain;
        double theme;

        // check elevation
        if (com.getLevel() == ITerrain.WILDCARD) {
            elevation = 1.0;
        } else {
            elevation = 1.01 / (Math.abs(org.getLevel() - com.getLevel()) + 1.01);
        }

        // Determine maximum number of terrain matches.
        // Bug 732188: Have a non-zero minimum terrain match.
        double maxTerrains = Math.max(org.terrainsPresent(), com.terrainsPresent());
        double matches = 0.0;

        int[] orgTerrains = org.getTerrainTypes();

        for (int i = 0; i < orgTerrains.length; i++) {
            int terrType = orgTerrains[i];
            ITerrain cTerr = com.getTerrain(terrType);
            ITerrain oTerr = org.getTerrain(terrType);
            if ((cTerr == null) || (oTerr == null)) {
                continue;
            }
            double thisMatch = 0;

            if (cTerr.getLevel() == ITerrain.WILDCARD) {
                thisMatch = 1.0;
            } else {
                thisMatch = 1.0 / (Math.abs(oTerr.getLevel() - cTerr.getLevel()) + 1.0);
            }
            // without exit match, terrain counts... um, half?
            if (cTerr.hasExitsSpecified() && (oTerr.getExits() != cTerr.getExits())) {
                thisMatch *= 0.5;
            }
            // add up match value
            matches += thisMatch;
        }
        if (maxTerrains == 0) {
            terrain = 1.0;
        } else {
            terrain = matches / maxTerrains;
        }

        // check theme
        if ((com.getTheme() == org.getTheme())
                || ((com.getTheme() != null) && com.getTheme().equalsIgnoreCase(org.getTheme()))) {
            theme = 1.0;
        } else if ((org.getTheme() != null) && (com.getTheme() == null)) {
            // If no precise themed match, slightly favor unthemed comparisons
            theme = 0.001;
        } else {
            // also don't throw a match entirely out because the theme is off
            theme = 0.0001;
        }

        return elevation * terrain * theme;
    }

    private class HexEntry {
        private IHex hex;
        private Image image;
        private Vector<Image> images;
        private Vector<String> filenames;

        public HexEntry(IHex hex, String imageFile) {
            this.hex = hex;
            filenames = StringUtil.splitString(imageFile, ";"); //$NON-NLS-1$
        }

        public IHex getHex() {
            return hex;
        }

        public Image getImage() {
            return image;
        }

        public Image getImage(Component comp, int seed) {
            if ((null == images) || images.isEmpty()) {
                loadImage(comp);
            }
            if (images.isEmpty()) {
                return null;
            }
            if (images.size() > 1) {
                int rand = (seed % images.size());
                return images.elementAt(rand);
            }
            return images.firstElement();
        }

        public void loadImage(Component c2) {
            images = new Vector<Image>();
            for (String filename: filenames) {
                File imgFile = new MegaMekFile(Configuration.hexesDir(), filename).getFile();
                Image image = ImageUtil.loadImageFromFile(imgFile.toString());
                if (null != image) {
                    images.add(image);
                } else {
                    System.out.println("Received null image from " + "ImageUtil.loadImageFromFile!  File: " + imgFile);
                }
            }
        }

        @Override
        public String toString() {
            return "HexTileset: " + hex.toString();
        }
    }
    
    // The Board and Game listeners
    // The HexTileSet caches images with the hex object as key. Therefore it
    // must listen to Board events to clear changed (but not replaced) 
    // hexes from the cache. 
    // It must listen to Game events to catch when a board is entirely replaced
    // to be able to register itself to the new board.
    private GameListener gameListener = new GameListenerAdapter() {

        @Override
        public void gameBoardNew(GameBoardNewEvent e) {
            clearAllHexes();
            if (e.getOldBoard() != null) {
                e.getOldBoard().removeBoardListener(HexTileset.this);
            }
            if (e.getNewBoard() != null) {
                e.getNewBoard().addBoardListener(HexTileset.this);
            }
        }

        @Override
        public void gameBoardChanged(GameBoardChangeEvent e) {
            clearAllHexes();
        }
    };

    @Override
    public void boardNewBoard(BoardEvent b) {
        clearAllHexes();
    }

    @Override
    public void boardChangedHex(BoardEvent b) {
        clearHex(((Board)b.getSource()).getHex(b.getCoords()));
    }

    @Override
    public void boardChangedAllHexes(BoardEvent b) {
        clearAllHexes();
    }
}
