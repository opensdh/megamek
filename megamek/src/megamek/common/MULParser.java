/*
* Copyright (c) 2014-2022 - The MegaMek Team. All Rights Reserved.
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU General Public License as published by the Free Software
* Foundation; either version 2 of the License, or (at your option) any later
* version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*/
package megamek.common;

import megamek.client.generator.RandomNameGenerator;
import megamek.codeUtilities.StringUtility;
import megamek.common.annotations.Nullable;
import megamek.common.enums.Gender;
import megamek.common.options.GameOptions;
import megamek.common.options.OptionsConstants;
import megamek.common.weapons.infantry.InfantryWeapon;
import megamek.utils.MegaMekXmlUtil;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Class for reading in and parsing MUL XML files. The MUL xsl is defined in
 * the docs directory.
 *
 * @author arlith
 */
public class MULParser {
    public static final String VERSION = "version";

    /**
     * The names of the various elements recognized by this parser.
     */
    private static final String RECORD = "record";
    private static final String SURVIVORS = "survivors";
    private static final String ALLIES = "allies";
    private static final String SALVAGE = "salvage";
    private static final String RETREATED = "retreated";
    private static final String DEVASTATED = "devastated";
    private static final String UNIT = "unit";
    private static final String ENTITY = "entity";
    private static final String PILOT = "pilot";
    private static final String CREW = "crew";
    private static final String CREWTYPE = "crewType";
    private static final String CREWMEMBER = "crewMember";
    private static final String KILLS = "kills";
    private static final String KILL = "kill";
    private static final String LOCATION = "location";
    private static final String ARMOR = "armor";
    private static final String SLOT = "slot";
    private static final String MOVEMENT = "motive";
    private static final String TURRETLOCK = "turretlock";
    private static final String TURRET2LOCK = "turret2lock";
    private static final String SI = "structural";
    private static final String HEAT = "heat";
    private static final String FUEL = "fuel";
    private static final String KF = "KF";
    private static final String SAIL = "sail";
    private static final String AEROCRIT = "acriticals";
    private static final String DROPCRIT = "dcriticals";
    private static final String TANKCRIT = "tcriticals";
    private static final String STABILIZER = "stabilizer";
    private static final String BREACH = "breached";
    private static final String BLOWN_OFF = "blownOff";
    private static final String C3I = "c3iset";
    private static final String C3ILINK = "c3i_link";
    private static final String NC3 = "NC3set";
    private static final String NC3LINK = "NC3_link";
    private static final String LINK = "link";
    private static final String RFMG = "rfmg";
    private static final String ESCCRAFT = "EscapeCraft";
    private static final String ID = "id";
    private static final String ESCCREW = "EscapedCrew";
    private static final String ESCPASS = "EscapedPassengers";
    private static final String NUMBER = "number";
    private static final String ORIG_PODS = "ONumberOfPods";
    private static final String ORIG_MEN = "ONumberOfMen";
    private static final String CONVEYANCE = "Conveyance";
    private static final String GAME = "Game";
    private static final String FORCE = "Force";
    private static final String FORCEATT = "force";

    /**
     * The names of attributes generally associated with Entity tags
     */
    private static final String CHASSIS = "chassis";
    private static final String MODEL = "model";
    private static final String CAMO_CATEGORY = "camoCategory";
    private static final String CAMO_FILENAME = "camoFileName";

    /**
     * The names of the attributes recognized by this parser. Not every
     * attribute is valid for every element.
     */

    private static final String NAME = "name";
    private static final String SIZE = "size";
    private static final String CURRENTSIZE = "currentsize";

    private static final String EXT_ID = "externalId";
    private static final String PICKUP_ID = "pickUpId";
    private static final String NICK = "nick";
    private static final String GENDER = "gender";
    private static final String CAT_PORTRAIT = "portraitCat";
    private static final String FILE_PORTRAIT = "portraitFile";
    private static final String GUNNERY = "gunnery";
    private static final String GUNNERYL = "gunneryL";
    private static final String GUNNERYM = "gunneryM";
    private static final String GUNNERYB = "gunneryB";
    private static final String PILOTING = "piloting";
    private static final String ARTILLERY = "artillery";
    private static final String TOUGH = "toughness";
    private static final String INITB = "initB";
    private static final String COMMANDB = "commandB";
    private static final String HITS = "hits";
    private static final String ADVS = "advantages";
    private static final String EDGE = "edge";
    private static final String IMPLANTS = "implants";
    private static final String QUIRKS = "quirks";
    private static final String TROOPER_MISS = "trooperMiss";
    private static final String DRIVER = "driver";
    private static final String COMMANDER = "commander";
    private static final String OFFBOARD = "offboard";
    private static final String OFFBOARD_DISTANCE = "offboard_distance";
    private static final String OFFBOARD_DIRECTION = "offboard_direction";
    private static final String HIDDEN = "hidden";
    public static final String DEPLOYMENT = "deployment";
    private static final String DEPLOYMENT_ZONE = "deploymentZone";
    private static final String NEVER_DEPLOYED = "neverDeployed";
    private static final String VELOCITY = "velocity";
    public static final String ALTITUDE = "altitude";
    private static final String AUTOEJECT = "autoeject";
    private static final String CONDEJECTAMMO = "condejectammo";
    private static final String CONDEJECTENGINE = "condejectengine";
    private static final String CONDEJECTCTDEST = "condejectctdest";
    private static final String CONDEJECTHEADSHOT = "condejectheadshot";
    private static final String EJECTED = "ejected";
    private static final String INDEX = "index";
    private static final String IS_DESTROYED = "isDestroyed";
    private static final String IS_REPAIRABLE = "isRepairable";
    private static final String POINTS = "points";
    private static final String TYPE = "type";
    private static final String SHOTS = "shots";
    private static final String CAPACITY = "capacity";
    private static final String IS_HIT = "isHit";
    private static final String MUNITION = "munition";
    private static final String STANDARD = "standard";
    private static final String INFERNO = "inferno";
    private static final String DIRECTION = "direction";
    private static final String INTEGRITY = "integrity";
    private static final String SINK = "sinks";
    private static final String LEFT = "left";
    private static final String AVIONICS = "avionics";
    private static final String SENSORS = "sensors";
    private static final String ENGINE = "engine";
    private static final String FCS = "fcs";
    private static final String CIC = "cic";
    private static final String LEFT_THRUST = "leftThrust";
    private static final String RIGHT_THRUST = "rightThrust";
    private static final String LIFE_SUPPORT = "lifeSupport";
    private static final String GEAR = "gear";
    private static final String DOCKING_COLLAR = "dockingcollar";
    private static final String KFBOOM = "kfboom";
    private static final String BAYDOORS = "doors";
    private static final String BAY = "transportBay";
    private static final String LOADED = "loaded";
    private static final String BAYDAMAGE = "damage";
    private static final String WEAPONS_BAY_INDEX = "weaponsBayIndex";
    private static final String MDAMAGE = "damage";
    private static final String MPENALTY = "penalty";
    private static final String C3MASTERIS = "c3MasterIs";
    private static final String C3UUID = "c3UUID";
    private static final String BOMBS = "bombs";
    private static final String BOMB = "bomb";
    private static final String LOAD = "load";
    private static final String BA_MEA = "modularEquipmentMount";
    private static final String BA_APM = "antiPersonnelMount";
    private static final String BA_APM_MOUNT_NUM = "baAPMMountNum";
    private static final String BA_APM_TYPE_NAME = "baAPMTypeName";
    private static final String BA_MEA_MOUNT_LOC = "baMEAMountLoc";
    private static final String BA_MEA_TYPE_NAME = "baMEATypeName";
    private static final String KILLED = "killed";
    private static final String KILLER = "killer";
    private static final String EXTRA_DATA = "extraData";

    public static final String ARMOR_DIVISOR = "armorDivisor";
    public static final String ARMOR_ENC = "armorEncumbering";
    public static final String DEST_ARMOR = "destArmor";
    public static final String SPACESUIT = "spacesuit";
    public static final String SNEAK_CAMO = "sneakCamo";
    public static final String SNEAK_IR = "sneakIR";
    public static final String SNEAK_ECM = "sneakECM";
    public static final String INF_SPEC = "infantrySpecializations";
    public static final String INF_SQUAD_NUM = "squadNum";


    /**
     * Special values recognized by this parser.
     */
    private static final String DEAD = "Dead";
    private static final String NA = "N/A";
    private static final String DESTROYED = "Destroyed";
    private static final String FRONT = "Front";
    private static final String REAR = "Rear";
    private static final String INTERNAL = "Internal";
    private static final String EMPTY = "Empty";
    private static final String SYSTEM = "System";


    /**
     * Stores all of the  Entity's read in. This is for general use saving and loading to the chat lounge
     */
    Vector<Entity> entities;

    /**
     * Stores all of the  surviving Entity's read in.
     */
    Vector<Entity> survivors;

    /**
     * Stores all of the allied Entity's read in.
     */
    Vector<Entity> allies;

    /**
     * Stores all of the enemy retreated entities read in.
     */
    Vector<Entity> retreated;

    /**
     * Stores all the salvage entities read in
     */
    Vector<Entity> salvage;

    /**
     * Stores all the devastated entities read in
     */
    Vector<Entity> devastated;

    /**
     * Keep a separate list of pilot/crews parsed because dismounted pilots may
     * need to be read separately
     */
    private Vector<Crew> pilots;

    /**
     * A hashtable containing the names of killed units as the key and the external id
     * of the killer as the value
     */
    private Hashtable<String, String> kills;

    StringBuffer warning;

    //region Constructors
    /**
     * This initializes all the variables utilised by the MUL parser.
     */
    private MULParser() {
        warning = new StringBuffer();
        entities = new Vector<>();
        survivors = new Vector<>();
        allies = new Vector<>();
        salvage = new Vector<>();
        retreated = new Vector<>();
        devastated = new Vector<>();
        kills = new Hashtable<>();
        pilots = new Vector<>();
    }

    /**
     * This is the standard MULParser constructor for a file. It initializes the values to parse
     * with, then parses the file using the provided options. The options may be null in cases
     * when the crew is not to be loaded as part of the MUL.
     *
     * @param file the file to parse, or null if there isn't anything to parse
     * @param options the game options to parse the MUL with, which may be null (only to be used
     *                when the crew is not to be loaded, as no saved optional Crew-based values are
     *                loaded).
     * @throws Exception if there is an issue with parsing the file
     */
    public MULParser(final @Nullable File file, final @Nullable GameOptions options) throws Exception {
        this();

        if (file == null) {
            return;
        }

        try (InputStream is = new FileInputStream(file)) {
            parse(is, options);
        }
    }

    /**
     * This is provided for unit testing only, and should not be part of general parsing.
     *
     * @param is the input stream to parse from
     * @param options the game options to parse the MUL with, which may be null (only to be used
     *                when the crew is not to be loaded, as no saved optional Crew-based values are
     *                loaded).
     */
    public MULParser(final InputStream is, final @Nullable GameOptions options) throws Exception {
        this();
        parse(is, options);
    }

    /**
     * This is the standard MULParser constructor for a single element. It initializes the values to
     * parse with, then parses the element using the provided options. The options may be null in
     * cases when the crew is not to be loaded as part of the MUL.
     *
     * @param element the element to parse
     * @param options the game options to parse the MUL with, which may be null (only to be used
     *                when the crew is not to be loaded, as no saved optional Crew-based values are
     *                loaded).
     */
    public MULParser(final Element element, final @Nullable GameOptions options) {
        this();
        parse(element, options);
    }
    //endregion Constructors

    private void parse(final InputStream fin, final @Nullable GameOptions options) throws Exception {
        Document xmlDoc;

        try {
            final DocumentBuilder db = MegaMekXmlUtil.newSafeDocumentBuilder();
            xmlDoc = db.parse(fin);
        } catch (Exception e) {
            warning.append("Error parsing MUL file!\n");
            throw e;
        }

        final Element element = xmlDoc.getDocumentElement();
        element.normalize();

        final String version = element.getAttribute(VERSION);
        if (version.isBlank()) {
            warning.append("Warning: No version specified, correct parsing ")
                    .append("not guaranteed!\n");
        }
        parse(element, options);
    }

    private void parse(final Element element, final @Nullable GameOptions options) {
        // Then parse the element
        if (element.getNodeName().equalsIgnoreCase(RECORD)) {
            parseRecord(element, options);
        } else if (element.getNodeName().equalsIgnoreCase(UNIT)) {
            parseUnit(element, options, entities);
        } else if (element.getNodeName().equalsIgnoreCase(ENTITY)) {
            parseEntity(element, options, entities);
        } else {
            warning.append("Error: root element isn't a Record, Unit, or Entity tag! Nothing to parse!\n");
        }

        // Finally, output the warning if there is any
        if (hasWarningMessage()) {
            LogManager.getLogger().warn(getWarningMessage());
        }
    }

    /**
     * Parse a Unit tag. Unit tags will contain a list of Entity tags.
     * @param unitNode the node containing the unit tag
     */
    private void parseRecord(final Element unitNode, final @Nullable GameOptions options) {
        NodeList nl = unitNode.getChildNodes();

        // Iterate through the children, looking for Entity tags
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != unitNode) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(UNIT)) {
                    parseUnit((Element) currNode, options, entities);
                } else if (nodeName.equalsIgnoreCase(SURVIVORS)) {
                    parseUnit((Element) currNode, options, survivors);
                } else if (nodeName.equalsIgnoreCase(ALLIES)) {
                    parseUnit((Element) currNode, options, allies);
                } else if (nodeName.equalsIgnoreCase(SALVAGE)) {
                    parseUnit((Element) currNode, options, salvage);
                } else if (nodeName.equalsIgnoreCase(RETREATED)) {
                    parseUnit((Element) currNode, options, retreated);
                } else if (nodeName.equalsIgnoreCase(DEVASTATED)) {
                    parseUnit((Element) currNode, options, devastated);
                } else if (nodeName.equalsIgnoreCase(KILLS)) {
                    parseKills((Element) currNode);
                } else if (nodeName.equalsIgnoreCase(ENTITY)) {
                    parseUnit((Element) currNode, options, entities);
                } else if (nodeName.equalsIgnoreCase(PILOT)) {
                    parsePilot((Element) currNode, options);
                } else if (nodeName.equalsIgnoreCase(CREW)) {
                    parseCrew((Element) currNode, options);
                }
            }
        }
    }

    /**
     * Parse a Unit tag. Unit tags will contain a list of Entity tags.
     * @param unitNode the node containing the unit tag
     * @param options the game options to parse using
     * @param list the list to add found entities to
     */
    private void parseUnit(final Element unitNode, final @Nullable GameOptions options,
                           final Vector<Entity> list) {
        NodeList nl = unitNode.getChildNodes();

        // Iterate through the children, looking for Entity tags
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != unitNode) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(ENTITY)) {
                    parseEntity((Element) currNode, options, list);
                } else if (nodeName.equalsIgnoreCase(PILOT)) {
                    parsePilot((Element) currNode, options);
                } else if (nodeName.equalsIgnoreCase(CREW)) {
                    parseCrew((Element) currNode, options);
                }
            }
        }
    }

    /**
     * Parse a kills tag.
     * @param killNode
     */
    private void parseKills(Element killNode) {
        NodeList nl = killNode.getChildNodes();

        // Iterate through the children, looking for Entity tags
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != killNode) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(KILL)) {
                    String killed = ((Element) currNode).getAttribute(KILLED);
                    String killer = ((Element) currNode).getAttribute(KILLER);
                    if (!killed.isBlank() && !killer.isBlank()) {
                        kills.put(killed, killer);
                    }
                }
            }
        }
    }

    /**
     * Parse an Entity tag. Entity tags will have a number of attributes such as model, chassis,
     * type, etc. They should also have a child Pilot tag, and they may also contain some number of
     * location tags.
     *
     * @param entityNode the node to parse the entity tag from
     * @param options the game options to parse using
     * @param list the list to add found entities to
     */
    private void parseEntity(final Element entityNode, final @Nullable GameOptions options,
                             final Vector<Entity> list) {
        // We need to get a new Entity, use the chassis and model to create one
        String chassis = entityNode.getAttribute(CHASSIS);
        String model = entityNode.getAttribute(MODEL);

        // Create a new entity
        Entity entity = getEntity(chassis, model);

        // Make sure we've got an Entity
        if (entity == null) {
            warning.append("Failed to load entity!");
            return;
        }

        // Set the attributes for the entity
        parseEntityAttributes(entity, entityNode);

        // Deal with any child nodes
        NodeList nl = entityNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);
            if (currNode.getParentNode() != entityNode) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(PILOT)) {
                    parsePilot(currEle, options, entity);
                } else if (nodeName.equalsIgnoreCase(CREW)) {
                    parseCrew(currEle, options, entity);
                } else if (nodeName.equalsIgnoreCase(LOCATION)) {
                    parseLocation(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(MOVEMENT)) {
                    parseMovement(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(TURRETLOCK)) {
                    parseTurretLock(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(TURRET2LOCK)) {
                    parseTurret2Lock(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(SI)) {
                    parseSI(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(HEAT)) {
                    parseHeat(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(FUEL)) {
                    parseFuel(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(KF)) {
                    parseKF(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(SAIL)) {
                    parseSail(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(BAY)) {
                    parseTransportBay(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(AEROCRIT)) {
                    parseAeroCrit(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(DROPCRIT)) {
                    parseDropCrit(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(TANKCRIT)) {
                    parseTankCrit(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(BOMBS)) {
                    parseBombs(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(C3I)) {
                    parseC3I(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(NC3)) {
                    parseNC3(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(BA_MEA)) {
                    parseBAMEA(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(BA_APM)) {
                    parseBAAPM(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(ESCCRAFT)) {
                    parseEscapeCraft(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(ESCPASS)) {
                    parseEscapedPassengers(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(ESCCREW)) {
                    parseEscapedCrew(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(ORIG_PODS)) {
                    parseOSI(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(ORIG_MEN)) {
                    parseOMen(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(CONVEYANCE)) {
                    parseConveyance(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(GAME)) {
                    parseId(currEle, entity);
                } else if (nodeName.equalsIgnoreCase(FORCE)) {
                    parseForce(currEle, entity);
                }
            }
        }

        //Now we should be done setting up the Entity, add it to the list
        list.add(entity);
    }

    /**
     * Create a new <code>Entity</code> instance given a mode and chassis name.
     *
     * @param chassis
     * @param model
     * @return
     */
    private Entity getEntity(String chassis, @Nullable String model) {
        Entity newEntity = null;

        // First check for ejected MechWarriors, vee crews, escape pods and spacecraft crews
        if (chassis.equals(EjectedCrew.VEE_EJECT_NAME) 
                || chassis.equals(EjectedCrew.SPACE_EJECT_NAME)) {
            return new EjectedCrew();
        } else if (chassis.equals(EjectedCrew.PILOT_EJECT_NAME)
                    || chassis.equals(EjectedCrew.MW_EJECT_NAME)) {
            return new MechWarrior();
        } else if (chassis.equals(EscapePods.POD_EJECT_NAME)) {
            return new EscapePods();
        }

        // Did we find required attributes?
        if (chassis.isBlank()) {
            warning.append("Could not find chassis for Entity.\n");
        } else {
            // Try to find the entity.
            StringBuffer key = new StringBuffer(chassis);
            MechSummary ms = MechSummaryCache.getInstance().getMech(key.toString());
            if (!StringUtility.isNullOrBlank(model)) {
                key.append(" ").append(model);
                ms = MechSummaryCache.getInstance().getMech(key.toString());
                // That didn't work. Try swapping model and chassis.
                if (ms == null) {
                    key = new StringBuffer(model);
                    key.append(" ").append(chassis);
                    ms = MechSummaryCache.getInstance().getMech(key.toString());
                }
            }
            // We should have found the mech.
            if (ms == null) {
                warning.append("Could not find Entity with chassis: ").append(chassis);
                if (!StringUtility.isNullOrBlank(model)) {
                    warning.append(", and model: ").append(model);
                }
                warning.append(".\n");
            } else {
                // Try to load the new mech.
                try {
                    newEntity = new MechFileParser(ms.getSourceFile(), ms.getEntryName()).getEntity();
                } catch (Exception ex) {
                    LogManager.getLogger().error("", ex);
                    warning.append("Unable to load mech: ")
                            .append(ms.getSourceFile()).append(": ")
                            .append(ms.getEntryName()).append(": ")
                            .append(ex.getMessage());
                }
            }
        }
        return newEntity;
    }

    /**
     * An Entity tag can define numerous attributes for the <code>Entity</code>,
     * check and set all of the relevant attributes.
     *
     * @param entity    The newly created Entity that we are setting state for
     * @param entityTag The Entity tag that defines the attributes
     */
    private void parseEntityAttributes(Entity entity, Element entityTag) {
        // commander
        boolean commander =
                Boolean.parseBoolean(entityTag.getAttribute(COMMANDER));
        entity.setCommander(commander);

        // hidden
        try {
            boolean isHidden =
                    Boolean.parseBoolean(entityTag.getAttribute(HIDDEN));
            entity.setHidden(isHidden);
        } catch (Exception e) {
            entity.setHidden(false);
        }

        // deploy offboard
        try {
            boolean offBoard =
                    Boolean.parseBoolean(entityTag.getAttribute(OFFBOARD));
            if (offBoard) {
                int distance = Integer.parseInt(entityTag
                        .getAttribute(OFFBOARD_DISTANCE));
                OffBoardDirection dir = OffBoardDirection.getDirection(Integer
                        .parseInt(entityTag.getAttribute(OFFBOARD_DIRECTION)));
                entity.setOffBoard(distance, dir);
            }
        } catch (Exception ignored) {
        }

        // deployment round
        try {
            int deployRound = Integer.parseInt(entityTag.getAttribute(DEPLOYMENT));
            entity.setDeployRound(deployRound);
        } catch (Exception e) {
            entity.setDeployRound(0);
        }

        // deployment zone
        try {
            int deployZone = Integer.parseInt(entityTag.getAttribute(DEPLOYMENT_ZONE));
            entity.setStartingPos(deployZone);
        } catch (Exception e) {
            entity.setStartingPos(Board.START_NONE);
        }



        // Was never deployed
        try {
            String ndeploy = entityTag.getAttribute(NEVER_DEPLOYED);
            boolean wasNeverDeployed = Boolean.parseBoolean(entityTag.getAttribute(NEVER_DEPLOYED));
            if (ndeploy.isBlank()) {
                // this will default to false above, but we want it to default to true
                wasNeverDeployed = true;
            }
            entity.setNeverDeployed(wasNeverDeployed);
        } catch (Exception ignored) {
            entity.setNeverDeployed(true);
        }

        if (entity.isAero()) {
            String velString = entityTag.getAttribute(VELOCITY);
            String altString = entityTag.getAttribute(ALTITUDE);

            IAero a = (IAero) entity;
            if (!velString.isBlank()) {
                int velocity = Integer.parseInt(velString);
                a.setCurrentVelocity(velocity);
                a.setNextVelocity(velocity);
            }

            if (!altString.isBlank()) {
                int altitude = Integer.parseInt(altString);
                if (altitude <= 0) {
                    a.land();
                } else {
                    a.liftOff(altitude);
                }
            }
        }

        // Camo
        entity.getCamouflage().setCategory(entityTag.getAttribute(CAMO_CATEGORY));
        entity.getCamouflage().setFilename(entityTag.getAttribute(CAMO_FILENAME));

        // external id
        String extId = entityTag.getAttribute(EXT_ID);
        if (extId.isBlank()) {
            extId = "-1";
        }
        entity.setExternalIdAsString(extId);

        // external id
        if (entity instanceof MechWarrior) {
            String pickUpId = entityTag.getAttribute(PICKUP_ID);
            if (pickUpId.isBlank()) {
                pickUpId = "-1";
            }
            ((MechWarrior) entity).setPickedUpByExternalId(pickUpId);
        }

        // quirks
        String quirks = entityTag.getAttribute(QUIRKS);
        if (!quirks.isBlank()) {
            StringTokenizer st = new StringTokenizer(quirks, "::");
            while (st.hasMoreTokens()) {
                String quirk = st.nextToken();
                String quirkName = Crew.parseAdvantageName(quirk);
                Object value = Crew.parseAdvantageValue(quirk);

                try {
                    entity.getQuirks().getOption(quirkName).setValue(value);
                } catch (Exception ignored) {
                    warning.append("Error restoring quirk: ").append(quirk).append(".\n");
                }
            }
        }

        // Setup for C3 Relinking
        String c3masteris = entityTag.getAttribute(C3MASTERIS);
        if (!c3masteris.isBlank()) {
            entity.setC3MasterIsUUIDAsString(c3masteris);
        }
        String c3uuid = entityTag.getAttribute(C3UUID);
        if (!c3uuid.isBlank()) {
            entity.setC3UUIDAsString(c3uuid);
        }

        // Load some values for conventional infantry
        if (entity.isConventionalInfantry()) {
            Infantry inf = (Infantry) entity;
            String armorDiv = entityTag.getAttribute(ARMOR_DIVISOR);
            if (!armorDiv.isBlank()) {
                inf.setArmorDamageDivisor(Double.parseDouble(armorDiv));
            }

            if (!entityTag.getAttribute(ARMOR_ENC).isBlank()) {
                inf.setArmorEncumbering(true);
            }

            if (!entityTag.getAttribute(SPACESUIT).isBlank()) {
                inf.setSpaceSuit(true);
            }

            if (!entityTag.getAttribute(DEST_ARMOR).isBlank()) {
                inf.setDEST(true);
            }

            if (!entityTag.getAttribute(SNEAK_CAMO).isBlank()) {
                inf.setSneakCamo(true);
            }

            if (!entityTag.getAttribute(SNEAK_IR).isBlank()) {
                inf.setSneakIR(true);
            }

            if (!entityTag.getAttribute(SNEAK_ECM).isBlank()) {
                inf.setSneakECM(true);
            }

            String infSpec = entityTag.getAttribute(INF_SPEC);
            if (!infSpec.isBlank()) {
                inf.setSpecializations(Integer.parseInt(infSpec));
            }
            
            String infSquadNum = entityTag.getAttribute(INF_SQUAD_NUM);
            if (!infSquadNum.isBlank()) {
                inf.setSquadN(Integer.parseInt(infSquadNum));
                inf.autoSetInternal();
            }
        }
    }

    /**
     * Convenience function that calls <code>parsePilot</code> with a null Entity.
     *
     * @param node The Pilot tag to create a <code>Crew</code> from
     * @param options The options to parse the crew based on
     */
    private void parsePilot(final Element node, final @Nullable GameOptions options) {
        parsePilot(node, options, null);
    }

    /**
     * Given a pilot tag, read the attributes and create a new <code>Crew</code> instance. If a
     * non-null <code>Entity</code> is passed, the new crew will be set as the crew for the given
     * <code>Entity</code>.
     *
     * @param pilotNode The Pilot tag to create a <code>Crew</code> from
     * @param options   The options to parse the crew based on
     * @param entity    If non-null, the new <code>Crew</code> will be set as
     *                  the crew of this <code>Entity</code>
     */
    private void parsePilot(final Element pilotNode, final @Nullable GameOptions options,
                            final Entity entity) {
        Map<String,String> attributes = new HashMap<>();
        for (int i = 0; i < pilotNode.getAttributes().getLength(); i++) {
            final Node node = pilotNode.getAttributes().item(i);
            attributes.put(node.getNodeName(), node.getTextContent());
        }

        Crew crew;
        if (null != entity) {
            crew = new Crew(entity.getCrew().getCrewType());
        } else {
            crew = new Crew(CrewType.SINGLE);
        }
        setCrewAttributes(options, entity, crew, attributes);
        setPilotAttributes(options, crew, 0, attributes);
        // LAMs have a second set of gunnery and piloting stats, so we create a dummy crew
        // and parse a copy of the attributes with the aero stats altered to their non-aero keys,
        // then copy the results into the aero skills of the LAMPilot.
        if (entity instanceof LandAirMech) {
            crew = LAMPilot.convertToLAMPilot((LandAirMech) entity, crew);
            Crew aeroCrew = new Crew(CrewType.SINGLE);
            Map<String,String> aeroAttributes = new HashMap<>(attributes);
            for (String key : attributes.keySet()) {
                if (key.contains("Aero")) {
                    aeroAttributes.put(key.replace("Aero", ""), attributes.get(key));
                }
            }
            setPilotAttributes(options, aeroCrew, 0, aeroAttributes);
            ((LAMPilot) crew).setGunneryAero(aeroCrew.getGunnery());
            ((LAMPilot) crew).setGunneryAeroM(aeroCrew.getGunneryM());
            ((LAMPilot) crew).setGunneryAeroB(aeroCrew.getGunneryB());
            ((LAMPilot) crew).setGunneryAeroL(aeroCrew.getGunneryL());
            ((LAMPilot) crew).setPilotingAero(aeroCrew.getPiloting());
            entity.setCrew(crew);
        }
        pilots.add(crew);
    }

    /**
     * Convenience function that calls <code>parseCrew</code> with a null Entity.
     *
     * @param node The crew tag to create a <code>Crew</code> from
     * @param options The game options to create the crew with
     */
    private void parseCrew(final Element node, final @Nullable GameOptions options) {
        parseCrew(node, options, null);
    }

    /**
     * Used for multi-crew cockpits.
     * Given a tag, read the attributes and create a new <code>Crew</code> instance. If a non-null
     * <code>Entity</code> is passed, the new crew will be set as the crew for the given
     * <code>Entity</code>.
     *
     * @param options  The <code>GameOptions</code> set when loading this crew
     * @param crewNode The crew tag to create a <code>Crew</code> from
     * @param entity   If non-null, the new <code>Crew</code> will be set as the crew of this Entity
     */
    private void parseCrew(final Element crewNode, final @Nullable GameOptions options,
                           final @Nullable Entity entity) {
        final Map<String, String> crewAttr = new HashMap<>();
        for (int i = 0; i < crewNode.getAttributes().getLength(); i++) {
            final Node node = crewNode.getAttributes().item(i);
            crewAttr.put(node.getNodeName(), node.getTextContent());
        }
        //Do not assign crew attributes until after individual crew members have been processed because
        //we cannot assign hits to ejected crew.

        Crew crew;
        CrewType crewType = null;
        if (crewAttr.containsKey(CREWTYPE)) {
            for (CrewType ct : CrewType.values()) {
                if (ct.toString().equalsIgnoreCase(crewAttr.get(CREWTYPE))) {
                    crewType = ct;
                    break;
                }
            }
        }
        crew = new Crew(Objects.requireNonNullElse(crewType, CrewType.SINGLE));
        pilots.add(crew);
        for (int i = 0; i < crew.getSlotCount(); i++) {
            crew.setMissing(true, i);
        }

        for (int n = 0; n < crewNode.getChildNodes().getLength(); n++) {
            final Node pilotNode = crewNode.getChildNodes().item(n);
            if (pilotNode.getNodeName().equalsIgnoreCase(CREWMEMBER)) {
                final Map<String, String> pilotAttr = new HashMap<>(crewAttr);
                for (int i = 0; i < pilotNode.getAttributes().getLength(); i++) {
                    final Node node = pilotNode.getAttributes().item(i);
                    pilotAttr.put(node.getNodeName(), node.getTextContent());
                }
                int slot = -1;
                if (pilotAttr.containsKey(SLOT) && !pilotAttr.get(SLOT).isBlank()) {
                    try {
                        slot = Integer.parseInt(pilotAttr.get(SLOT));
                    } catch (NumberFormatException ex) {
                        warning.append("Illegal crew slot index: ").append(pilotAttr.get(SLOT));
                    }
                }
                if (slot < 0 && slot >= crew.getSlotCount()) {
                    warning.append("Illegal crew slot index for ").append(crewType)
                            .append(" cockpit: ").append(slot);
                } else {
                    crew.setMissing(false, slot);
                    setPilotAttributes(options, crew, slot, pilotAttr);
                }
            }
        }
        setCrewAttributes(options, entity, crew, crewAttr);
    }

    /**
     * Helper method that sets field values for the crew as a whole, either from a <pilot> element
     * (single/collective crews) or a <crew> element (multi-crew cockpits). If an <code>Entity</code>
     * is provided, the crew will be assigned to it.
     *
     * @param options    The <code>GameOptions</code> set when loading this crew
     * @param entity     The <code>Entity</code> for this crew (or null if the crew has abandoned the unit).
     * @param crew       The crew to set fields for.
     * @param attributes Attribute values of the <code>pilot</code> or <code>crew</code>
     *                   element mapped to the attribute name.
     */
    private void setCrewAttributes(final @Nullable GameOptions options, final Entity entity,
                                   final Crew crew, final Map<String,String> attributes) {
        // init bonus
        int initBVal = 0;
        if ((attributes.containsKey(INITB)) && !attributes.get(INITB).isBlank()) {
            try {
                initBVal = Integer.parseInt(attributes.get(INITB));
            } catch (NumberFormatException ignored) {

            }
        }
        int commandBVal = 0;
        if ((attributes.containsKey(COMMANDB)) && !attributes.get(COMMANDB).isBlank()) {
            try {
                commandBVal = Integer.parseInt(attributes.get(COMMANDB));
            } catch (NumberFormatException ignored) {

            }
        }

        if (attributes.containsKey(SIZE)) {
            if (!attributes.get(SIZE).isBlank()) {
                int crewSize = 1;
                try {
                    crewSize = Integer.parseInt(attributes.get(SIZE));
                } catch (NumberFormatException ignored) {

                }
                crew.setSize(crewSize);
            } else if (null != entity) {
                crew.setSize(Compute.getFullCrewSize(entity));
                //Reset the currentSize equal to the max size
                crew.setCurrentSize(Compute.getFullCrewSize(entity));
            }
        }
        
        if (attributes.containsKey(CURRENTSIZE)) {
            if (!attributes.get(CURRENTSIZE).isBlank()) {
                int crewCurrentSize = 1;
                try {
                    crewCurrentSize = Integer.parseInt(attributes.get(CURRENTSIZE));
                } catch (NumberFormatException ignored) {

                }
                crew.setCurrentSize(crewCurrentSize);
            } else if (null != entity) {
                //Reset the currentSize equal to the max size
                crew.setCurrentSize(Compute.getFullCrewSize(entity));
            }
        }

        crew.setInitBonus(initBVal);
        crew.setCommandBonus(commandBVal);

        if ((options != null) && options.booleanOption(OptionsConstants.RPG_PILOT_ADVANTAGES)
                && attributes.containsKey(ADVS) && !attributes.get(ADVS).isBlank()) {
            StringTokenizer st = new StringTokenizer(attributes.get(ADVS), "::");
            while (st.hasMoreTokens()) {
                String adv = st.nextToken();
                String advName = Crew.parseAdvantageName(adv);
                Object value = Crew.parseAdvantageValue(adv);

                try {
                    crew.getOptions().getOption(advName).setValue(value);
                } catch (Exception e) {
                    warning.append("Error restoring advantage: ").append(adv).append(".\n");
                }
            }

        }

        if ((options != null) && options.booleanOption(OptionsConstants.EDGE)
                && attributes.containsKey(EDGE) && !attributes.get(EDGE).isBlank()) {
            StringTokenizer st = new StringTokenizer(attributes.get(EDGE), "::");
            while (st.hasMoreTokens()) {
                String edg = st.nextToken();
                String edgeName = Crew.parseAdvantageName(edg);
                Object value = Crew.parseAdvantageValue(edg);

                try {
                    crew.getOptions().getOption(edgeName).setValue(value);
                } catch (Exception e) {
                    warning.append("Error restoring edge: ").append(edg).append(".\n");
                }
            }
        }

        if ((options != null) && options.booleanOption(OptionsConstants.RPG_MANEI_DOMINI)
                && attributes.containsKey(IMPLANTS) && !attributes.get(IMPLANTS).isBlank()) {
            StringTokenizer st = new StringTokenizer(attributes.get(IMPLANTS), "::");
            while (st.hasMoreTokens()) {
                String implant = st.nextToken();
                String implantName = Crew.parseAdvantageName(implant);
                Object value = Crew.parseAdvantageValue(implant);

                try {
                    crew.getOptions().getOption(implantName).setValue(value);
                } catch (Exception e) {
                    warning.append("Error restoring implants: ").append(implant).append(".\n");
                }
            }
        }

        if (attributes.containsKey(EJECTED) && !attributes.get(EJECTED).isBlank()) {
            crew.setEjected(Boolean.parseBoolean(attributes.get(EJECTED)));
        }

        if (null != entity) {
            // Set the crew for this entity.
            entity.setCrew(crew);

            if (attributes.containsKey(AUTOEJECT) && !attributes.get(AUTOEJECT).isBlank()) {
                ((Mech) entity).setAutoEject(Boolean.parseBoolean(attributes.get(AUTOEJECT)));
            }

            if (attributes.containsKey(CONDEJECTAMMO) && !attributes.get(CONDEJECTAMMO).isBlank()) {
                ((Mech) entity).setCondEjectAmmo(Boolean.parseBoolean(attributes.get(CONDEJECTAMMO)));
            }

            if (attributes.containsKey(CONDEJECTENGINE) && !attributes.get(CONDEJECTENGINE).isBlank()) {
                ((Mech) entity).setCondEjectEngine(Boolean.parseBoolean(attributes.get(CONDEJECTENGINE)));
            }

            if (attributes.containsKey(CONDEJECTCTDEST) && !attributes.get(CONDEJECTCTDEST).isBlank()) {
                ((Mech) entity).setCondEjectCTDest(Boolean.parseBoolean(attributes.get(CONDEJECTCTDEST)));
            }

            if (attributes.containsKey(CONDEJECTHEADSHOT) && !attributes.get(CONDEJECTHEADSHOT).isBlank()) {
                ((Mech) entity).setCondEjectHeadshot(Boolean.parseBoolean(attributes.get(CONDEJECTHEADSHOT)));
            }
        }
    }

    /**
     * Helper method that parses attributes common to both single/collective crews and individual
     * slots of a unit with a multi-crew cockpit.
     *
     * @param crew The crew object for set values for
     * @param slot The slot of the crew object that corresponds to these attributes.
     * @param attributes A map of attribute values keyed to the attribute names.
     */
    private void setPilotAttributes(final @Nullable GameOptions options, final Crew crew,
                                    final int slot, final Map<String, String> attributes) {
        final boolean hasGun = attributes.containsKey(GUNNERY) && !attributes.get(GUNNERY).isBlank();
        final boolean hasRpgGun = attributes.containsKey(GUNNERYL) && !attributes.get(GUNNERYL).isBlank()
                && attributes.containsKey(GUNNERYM) && !attributes.get(GUNNERYM).isBlank()
                && attributes.containsKey(GUNNERYB) && !attributes.get(GUNNERYB).isBlank();

        // Did we find required attributes?
        if (!hasGun && !hasRpgGun) {
            warning.append("Could not find gunnery for pilot.\n");
        } else if (!attributes.containsKey(PILOTING) || attributes.get(PILOTING).isBlank()) {
            warning.append("Could not find piloting for pilot.\n");
        } else {
            // Try to get a good gunnery value.
            int gunVal = -1;
            if (hasGun) {
                try {
                    gunVal = Integer.parseInt(attributes.get(GUNNERY));
                } catch (NumberFormatException ignored) {

                }

                if ((gunVal < 0) || (gunVal > Crew.MAX_SKILL)) {
                    warning.append("Found invalid gunnery value: ")
                            .append(attributes.get(GUNNERY)).append(".\n");
                    return;
                }
            }

            // get RPG skills
            int gunneryLVal = -1;
            int gunneryMVal = -1;
            int gunneryBVal = -1;
            if (hasRpgGun) {
                if ((attributes.containsKey(GUNNERYL)) && !attributes.get(GUNNERYL).isBlank()) {
                    try {
                        gunneryLVal = Integer.parseInt(attributes.get(GUNNERYL));
                    } catch (NumberFormatException ignored) {

                    }

                    if ((gunneryLVal < 0) || (gunneryLVal > Crew.MAX_SKILL)) {
                        warning.append("Found invalid piloting value: ")
                                .append(attributes.get(GUNNERYL)).append(".\n");
                        return;
                    }
                }

                if ((attributes.containsKey(GUNNERYM)) && !attributes.get(GUNNERYM).isBlank()) {
                    try {
                        gunneryMVal = Integer.parseInt(attributes.get(GUNNERYM));
                    } catch (NumberFormatException ignored) {

                    }

                    if ((gunneryMVal < 0) || (gunneryMVal > Crew.MAX_SKILL)) {
                        warning.append("Found invalid piloting value: ")
                                .append(attributes.get(GUNNERYM)).append(".\n");
                        return;
                    }
                }

                if ((attributes.containsKey(GUNNERYB)) && !attributes.get(GUNNERYB).isBlank()) {
                    try {
                        gunneryBVal = Integer.parseInt(attributes.get(GUNNERYB));
                    } catch (NumberFormatException ignored) {

                    }

                    if ((gunneryBVal < 0) || (gunneryBVal > Crew.MAX_SKILL)) {
                        warning.append("Found invalid piloting value: ")
                                .append(attributes.get(GUNNERYB)).append(".\n");
                        return;
                    }
                }
            }

            if (!hasGun) {
                gunVal = (int) Math.floor((gunneryLVal + gunneryMVal + gunneryBVal) / 3.0);
            } else if (!hasRpgGun) {
                gunneryLVal = gunVal;
                gunneryMVal = gunVal;
                gunneryBVal = gunVal;
            }

            // Try to get a good piloting value.
            int pilotVal = -1;
            try {
                pilotVal = Integer.parseInt(attributes.get(PILOTING));
            } catch (NumberFormatException ignored) {

            }

            if ((pilotVal < 0) || (pilotVal > Crew.MAX_SKILL)) {
                warning.append("Found invalid piloting value: ")
                        .append(attributes.get(PILOTING)).append(".\n");
                return;
            }

            // toughness
            int toughVal = 0;
            if ((options != null) && options.booleanOption(OptionsConstants.RPG_TOUGHNESS)
                    && (attributes.containsKey(TOUGH)) && !attributes.get(TOUGH).isBlank()) {
                try {
                    toughVal = Integer.parseInt(attributes.get(TOUGH));
                } catch (NumberFormatException ignored) {

                }
            }

            int artVal = gunVal;
            if ((options != null) && options.booleanOption(OptionsConstants.RPG_ARTILLERY_SKILL)
                    && (attributes.containsKey(ARTILLERY)) && !attributes.get(ARTILLERY).isBlank()) {
                try {
                    artVal = Integer.parseInt(attributes.get(ARTILLERY));
                } catch (NumberFormatException ignored) {

                }
                if ((artVal < 0) || (artVal > Crew.MAX_SKILL)) {
                    warning.append("Found invalid artillery value: ")
                            .append(attributes.get(ARTILLERY)).append(".\n");
                    return;
                }
            }

            crew.setGunnery(gunVal, slot);
            crew.setGunneryL(gunneryLVal, slot);
            crew.setGunneryM(gunneryMVal, slot);
            crew.setGunneryB(gunneryBVal, slot);
            crew.setArtillery(artVal, slot);
            crew.setPiloting(pilotVal, slot);
            crew.setToughness(toughVal, slot);

            if ((attributes.containsKey(NAME)) && !attributes.get(NAME).isBlank()) {
                crew.setName(attributes.get(NAME), slot);
            } else {
                crew.setName(RandomNameGenerator.UNNAMED_FULL_NAME, slot);
            }

            if ((attributes.containsKey(NICK)) && !attributes.get(NICK).isBlank()) {
                crew.setNickname(attributes.get(NICK), slot);
            }

            if ((attributes.containsKey(GENDER)) && !attributes.get(GENDER).isBlank()) {
                crew.setGender(Gender.parseFromString(attributes.get(GENDER)), slot);
            }

            if ((attributes.containsKey(CAT_PORTRAIT)) && !attributes.get(CAT_PORTRAIT).isBlank()) {
                crew.getPortrait(slot).setCategory(attributes.get(CAT_PORTRAIT));
            }

            if ((attributes.containsKey(FILE_PORTRAIT)) && !attributes.get(FILE_PORTRAIT).isBlank()) {
                crew.getPortrait(slot).setFilename(attributes.get(FILE_PORTRAIT));
            }

            // Was the crew wounded?
            if (attributes.containsKey(HITS) && !attributes.get(HITS).isBlank()) {
                // Try to get a good hits value.
                int hitVal = -1;
                try {
                    hitVal = Integer.parseInt(attributes.get(HITS));
                } catch (NumberFormatException ignored) {

                }

                if (attributes.get(HITS).equals(DEAD)) {
                    crew.setDead(true, slot);
                    warning.append(crew.getNameAndRole(slot)).append(" is dead.\n");
                } else if ((hitVal < 0) || (hitVal > 5)) {
                    warning.append("Found invalid hits value: ")
                            .append(attributes.get(HITS)).append(".\n");
                } else {
                    crew.setHits(hitVal, slot);
                }

            }

            if ((attributes.containsKey(EXT_ID)) && !attributes.get(EXT_ID).isBlank()) {
                crew.setExternalIdAsString(attributes.get(EXT_ID), slot);
            }

            if (attributes.containsKey(EXTRA_DATA)) {
                try {
                    Map<String, String> extraData = new HashMap<>();
                    String[] valuePairs = attributes.get(EXTRA_DATA).split("\\|");
                    String[] values;
                    for (String valuePair : valuePairs) {
                        values = valuePair.split("=");
                        extraData.put(values[0], values[1]);
                    }
                    crew.setExtraDataForCrewMember(slot, extraData);
                } catch (Exception e) {
                    LogManager.getLogger().error("Error in loading MUL, issues with extraData elements!");
                }
            }
        }
    }

    /**
     * Parse a location tag and update the given <code>Entity</code> based on
     * the contents.
     *
     * @param locationTag
     * @param entity
     */
    private void parseLocation(Element locationTag, Entity entity) {
        // Look for the element's attributes.
        String index = locationTag.getAttribute(INDEX);
        String destroyed = locationTag.getAttribute(IS_DESTROYED);

        int loc;
        // Some units, like tanks and protos, keep track as Ammo slots as N/A
        // Since they don't have slot indices, they are accessed in order so
        // we keep track of the number of ammo slots processed for a loc
        int locAmmoCount = 0;
        // Did we find required attributes?
        if ((index == null) || index.isBlank()) {
            warning.append("Could not find index for location.\n");
            return;
        } else {
            // Try to get a good index value.
            loc = -1;
            try {
                loc = Integer.parseInt(index);
            } catch (NumberFormatException ignored) {

            }

            if (loc < 0) {
                warning.append(
                        "Found invalid index value for location: ")
                        .append(index).append(".\n");
                return;
            } else if (loc >= entity.locations()) {
                warning.append("The entity, ")
                        .append(entity.getShortName())
                        .append(" does not have a location at index: ")
                        .append(loc).append(".\n");
                return;
            } else {
                try {
                    if (Boolean.parseBoolean(destroyed)) {
                        destroyLocation(entity, loc);
                    }
                } catch (Exception ignored) {
                    warning.append("Found invalid isDestroyed value: ")
                            .append(destroyed).append(".\n");
                }
            } // End have-valid-index
        } // End have-required-fields

        // Handle children
        NodeList nl = locationTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != locationTag) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(ARMOR)) {
                    parseArmor(currEle, entity, loc);
                } else if (nodeName.equalsIgnoreCase(BREACH)) {
                    breachLocation(entity, loc);
                } else if (nodeName.equalsIgnoreCase(BLOWN_OFF)) {
                    blowOffLocation(entity, loc);
                } else if (nodeName.equalsIgnoreCase(SLOT)) {
                    locAmmoCount = parseSlot(currEle, entity, loc, locAmmoCount);
                } else if (nodeName.equalsIgnoreCase(STABILIZER)) {
                    String hit = currEle.getAttribute(IS_HIT);
                    if (!hit.isBlank()) {
                        ((Tank) entity).setStabiliserHit(loc);
                    }
                }
            }
        }
    }

    /**
     * Parse an armor tag for the given Entity and location.
     *
     * @param armorTag
     * @param entity
     * @param loc
     */
    private void parseArmor(Element armorTag, Entity entity, int loc) {
     // Look for the element's attributes.
        String points = armorTag.getAttribute(POINTS);
        String type = armorTag.getAttribute(TYPE);

        // Did we find required attributes?
        if (points.isBlank()) {
            warning.append("Could not find points for armor.\n");
        } else {

            // Try to get a good points value.
            int pointsVal = -1;
            try {
                pointsVal = Integer.parseInt(points);
            } catch (NumberFormatException ignored) {

            }

            if (points.equals(NA)) {
                pointsVal = IArmorState.ARMOR_NA;
            } else if (points.equals(DESTROYED)) {
                pointsVal = IArmorState.ARMOR_DESTROYED;
            } else if ((pointsVal < 0) || (pointsVal > 2000)) {
                warning.append("Found invalid points value: ")
                        .append(points).append(".\n");
                return;
            }

            // Assign the points to the correct location.
            // Sanity check the armor value before setting it.
            if (type.isBlank() || type.equals(FRONT)) {
                if (entity.getOArmor(loc) < pointsVal) {
                    warning.append("The entity, ")
                            .append(entity.getShortName())
                            .append(" does not start with ")
                            .append(pointsVal)
                            .append(" points of armor for location: ")
                            .append(loc).append(".\n");
                } else {
                    entity.setArmor(pointsVal, loc);
                }
            } else if (type.equals(INTERNAL)) {
                if (entity.getOInternal(loc) < pointsVal) {
                    warning.append("The entity, ")
                            .append(entity.getShortName())
                            .append(" does not start with ")
                            .append(pointsVal)
                            .append(" points of internal structure for " +
                                    "location: ")
                            .append(loc).append(".\n");
                } else {
                    entity.setInternal(pointsVal, loc);
                }
            } else if (type.equals(REAR)) {
                if (!entity.hasRearArmor(loc)) {
                    warning.append("The entity, ")
                            .append(entity.getShortName())
                            .append(" has no rear armor for location: ")
                            .append(loc).append(".\n");
                } else if (entity.getOArmor(loc, true) < pointsVal) {
                    warning.append("The entity, ")
                            .append(entity.getShortName())
                            .append(" does not start with ")
                            .append(pointsVal)
                            .append(" points of rear armor for location: ")
                            .append(loc).append(".\n");
                } else {
                    entity.setArmor(pointsVal, loc, true);
                }
            }
        }
    }

    /**
     * Parse a slot tag for the given Entity and location.
     *
     * @param slotTag
     * @param entity
     * @param loc
     */
    private int parseSlot(Element slotTag, Entity entity, int loc, int locAmmoCount) {
        // Look for the element's attributes.
        String index = slotTag.getAttribute(INDEX);
        String type = slotTag.getAttribute(TYPE);
        // String rear = slotTag.getAttribute( IS_REAR ); // is never read.
        String shots = slotTag.getAttribute(SHOTS);
        String capacity = slotTag.getAttribute(CAPACITY);
        String hit = slotTag.getAttribute(IS_HIT);
        String destroyed = slotTag.getAttribute(IS_DESTROYED);
        String repairable = (slotTag.getAttribute(IS_REPAIRABLE).isBlank() ? "true" : slotTag.getAttribute(IS_REPAIRABLE));
        String munition = slotTag.getAttribute(MUNITION);
        String standard = slotTag.getAttribute(STANDARD);
        String inferno = slotTag.getAttribute(INFERNO);
        String quirks = slotTag.getAttribute(QUIRKS);
        String trooperMiss = slotTag.getAttribute(TROOPER_MISS);
        String rfmg = slotTag.getAttribute(RFMG);
        String bayIndex = slotTag.getAttribute(WEAPONS_BAY_INDEX);

        // Did we find required attributes?
        if (index.isBlank()) {
            warning.append("Could not find index for slot.\n");
            return locAmmoCount;
        } else if (type.isBlank()) {
            warning.append("Could not find type for slot.\n");
            return locAmmoCount;
        } else {
            // Try to get a good index value.
            // Remember, slot index starts at 1.
            int indexVal = -1;
            try {
                indexVal = Integer.parseInt(index);
                indexVal -= 1;
            } catch (NumberFormatException excep) {
                // Handled by the next if test.
            }
            if (index.equals(NA)) {
                indexVal = IArmorState.ARMOR_NA;

                // Protomechs only have system slots,
                // so we have to handle the ammo specially.
                if (entity instanceof Protomech || entity instanceof GunEmplacement) {
                    // Get the saved ammo load.
                    EquipmentType newLoad = EquipmentType.get(type);
                    if (newLoad instanceof AmmoType) {
                        int counter = -1;
                        Iterator<Mounted> ammo = entity.getAmmo()
                                .iterator();
                        while (ammo.hasNext()
                                && (counter < locAmmoCount)) {

                            // Is this mounted in the current location?
                            Mounted mounted = ammo.next();
                            if (mounted.getLocation() == loc) {

                                // Increment the loop counter.
                                counter++;

                                // Is this the one we want to handle?
                                if (counter == locAmmoCount) {

                                    // Increment the counter of ammo
                                    // handled for this location.
                                    locAmmoCount++;

                                    // Reset transient values.
                                    mounted.restore();

                                    // Try to get a good shots value.
                                    int shotsVal = -1;
                                    try {
                                        shotsVal = Integer
                                                .parseInt(shots);
                                    } catch (NumberFormatException excep) {
                                        // Handled by the next if test.
                                    }
                                    if (shots.equals(NA)) {
                                        shotsVal = IArmorState.ARMOR_NA;
                                        warning.append(
                                                "Expected to find number of " +
                                                "shots for ")
                                                .append(type)
                                                .append(", but found ")
                                                .append(shots)
                                                .append(" instead.\n");
                                    } else if ((shotsVal < 0)
                                            || (shotsVal > 200)) {
                                        warning.append(
                                                "Found invalid shots value " +
                                                "for slot: ")
                                                .append(shots)
                                                .append(".\n");
                                    } else {

                                        // Change to the saved
                                        // ammo type and shots.
                                        mounted.changeAmmoType((AmmoType)
                                                newLoad);
                                        mounted.setShotsLeft(shotsVal);

                                    } // End have-good-shots-value

                                    // Stop looking for a match.
                                    break;

                                } // End found-match-for-slot

                            } // End ammo-in-this-loc

                        } // Check the next ammo.

                    } else {
                        // Bad XML equipment.
                        warning.append("XML file lists ")
                                .append(type)
                                .append(" equipment at location ")
                                .append(loc)
                                .append(".  XML parser expected ammo.\n");
                    } // End not-ammo-type

                } // End is-tank

                // TODO: handle slotless equipment.
                return locAmmoCount;
            } else if ((indexVal < 0)) {
                warning.append("Found invalid index value for slot: ")
                        .append(index).append(".\n");
                return locAmmoCount;
            }

            // Is this index valid for this entity?
            if (indexVal > entity.getNumberOfCriticals(loc)) {
                warning.append("The entity, ")
                        .append(entity.getShortName())
                        .append(" does not have ").append(index)
                        .append(" slots in location ").append(loc)
                        .append(".\n");
                return locAmmoCount;
            }

            // Try to get a good isHit value.
            boolean hitFlag = Boolean.parseBoolean(hit);

            // Is the location destroyed?
            boolean destFlag = Boolean.parseBoolean(destroyed);

            // Is the location repairable?
            boolean repairFlag = Boolean.parseBoolean(repairable);

            // Try to get the critical slot.
            CriticalSlot slot = entity.getCritical(loc, indexVal);

            // If we couldn't find a critical slot,
            // it's possible that this is "extra" ammo in a weapons bay, so we may attempt
            // to shove it in there
            if (slot == null) {
                if ((entity.usesWeaponBays() || (entity instanceof Dropship)) && !bayIndex.isBlank()) {
                    addExtraAmmoToBay(entity, loc, type, bayIndex);
                    slot = entity.getCritical(loc, indexVal);
                }
            }

            if (slot == null) {
                if (!type.equals(EMPTY)) {
                    warning.append("Could not find the ")
                            .append(type)
                            .append(" equipment that was expected at index ")
                            .append(indexVal).append(" of location ")
                            .append(loc).append(".\n");
                }
                return locAmmoCount;
            }

            // Is the slot for a critical system?
            if (slot.getType() == CriticalSlot.TYPE_SYSTEM) {

                // Does the XML file have some other kind of equipment?
                if (!type.equals(SYSTEM)) {
                    warning.append("XML file expects to find ")
                            .append(type)
                            .append(" equipment at index ")
                            .append(indexVal).append(" of location ")
                            .append(loc)
                            .append(", but Entity has a system.\n");
                }
            } else {
                // Nope, we've got equipment. Get this slot's mounted.
                Mounted mounted = slot.getMount();

                // Reset transient values.
                mounted.restore();

                // quirks
                if (!quirks.isBlank()) {
                    StringTokenizer st = new StringTokenizer(quirks, "::");
                    while (st.hasMoreTokens()) {
                        String quirk = st.nextToken();
                        String quirkName = Crew.parseAdvantageName(quirk);
                        Object value = Crew.parseAdvantageValue(quirk);

                        try {
                            mounted.getQuirks().getOption(quirkName).setValue(value);
                        } catch (Exception e) {
                            warning.append("Error restoring quirk: ").append(quirk).append(".\n");
                        }
                    }
                }

                // trooper missing equipment
                if (!trooperMiss.isBlank()) {
                    StringTokenizer st = new StringTokenizer(trooperMiss, "::");
                    int i = BattleArmor.LOC_TROOPER_1;
                    while (st.hasMoreTokens() && i <= BattleArmor.LOC_TROOPER_6) {
                        String tmiss = st.nextToken();
                        mounted.setMissingForTrooper(i, Boolean.parseBoolean(tmiss));
                        i++;
                    }
                }

                // Hit and destroy the mounted, according to the flags.
                mounted.setDestroyed(hitFlag || destFlag);

                mounted.setRepairable(repairFlag);

                mounted.setRapidfire(Boolean.parseBoolean(rfmg));

                // Is the mounted a type of ammo?
                if (mounted.getType() instanceof AmmoType) {
                    // Get the saved ammo load.
                    EquipmentType newLoad = EquipmentType.get(type);
                    if (newLoad instanceof AmmoType) {
                        // Try to get a good shots value.
                        int shotsVal = -1;
                        try {
                            shotsVal = Integer.parseInt(shots);
                        } catch (NumberFormatException excep) {
                            // Handled by the next if test.
                        }
                        if (shots.equals(NA)) {
                            shotsVal = IArmorState.ARMOR_NA;
                            warning.append(
                                    "Expected to find number of shots for ")
                                    .append(type)
                                    .append(", but found ")
                                    .append(shots)
                                    .append(" instead.\n");
                        } else if ((shotsVal < 0) || (shotsVal > 200)) {
                            warning.append(
                                    "Found invalid shots value for slot: ")
                                    .append(shots).append(".\n");
                        } else {

                            // Change to the saved ammo type and shots.
                            mounted.changeAmmoType((AmmoType) newLoad);
                            mounted.setShotsLeft(shotsVal);

                        } // End have-good-shots-value
                        try {
                            double capVal = Double.parseDouble(capacity);
                            mounted.setAmmoCapacity(capVal);
                        } catch (NumberFormatException excep) {
                            // Handled by the next if test.
                        }
                        if (capacity.equals(NA)) {
                            if (entity.hasETypeFlag(Entity.ETYPE_BATTLEARMOR)
                                    || entity.hasETypeFlag(Entity.ETYPE_PROTOMECH)) {
                                mounted.setAmmoCapacity(mounted.getOriginalShots()
                                         * ((AmmoType) mounted.getType()).getKgPerShot() * 1000);
                            } else {
                                mounted.setAmmoCapacity(mounted.getOriginalShots()
                                        * mounted.getTonnage()
                                        / ((AmmoType) mounted.getType()).getShots());
                            }
                        }


                    } else {
                        // Bad XML equipment.
                        warning.append("XML file expects ")
                                .append(type)
                                .append(" equipment at index ")
                                .append(indexVal)
                                .append(" of location ")
                                .append(loc)
                                .append(", but Entity has ")
                                .append(mounted.getType()
                                        .getInternalName())
                                .append("there .\n");
                    }

                } // End slot-for-ammo

                // Not an ammo slot... does file agree with template?
                else if (!mounted.getType().getInternalName()
                        .equals(type)) {
                    // Bad XML equipment.
                    warning.append("XML file expects ")
                            .append(type)
                            .append(" equipment at index ")
                            .append(indexVal)
                            .append(" of location ")
                            .append(loc)
                            .append(", but Entity has ")
                            .append(mounted.getType().getInternalName())
                            .append("there .\n");
                }

                // Check for munition attribute.
                if (!munition.isBlank()) {
                    // Retrieve munition by name.
                    EquipmentType munType = EquipmentType.get(munition);

                    // Make sure munition is a type of ammo.
                    if (munType instanceof AmmoType) {
                        // Change to the saved munition type.
                        mounted.getLinked().changeAmmoType(
                                (AmmoType) munType);
                    } else {
                        // Bad XML equipment.
                        warning.append("XML file expects")
                                .append(" ammo for munition argument of")
                                .append(" slot tag.\n");
                    }
                }
                if (entity.isSupportVehicle() && (mounted.getType() instanceof InfantryWeapon)) {
                    for (Mounted ammo = mounted.getLinked(); ammo != null; ammo = ammo.getLinked()) {
                        if (((AmmoType) ammo.getType()).getMunitionType() == AmmoType.M_INFERNO) {
                            if (!inferno.isBlank()) {
                                String[] fields = inferno.split(":");
                                ammo.setShotsLeft(Integer.parseInt(fields[0]));
                                ammo.setOriginalShots(Integer.parseInt(fields[1]));
                            }
                        } else {
                            if (!standard.isBlank()) {
                                String[] fields = standard.split(":");
                                ammo.setShotsLeft(Integer.parseInt(fields[0]));
                                ammo.setOriginalShots(Integer.parseInt(fields[1]));
                            }
                        }
                    }
                }

            } // End have-equipment

            // Hit and destroy the slot, according to the flags.
            slot.setHit(hitFlag);
            slot.setDestroyed(destFlag);
            slot.setRepairable(repairFlag);

        } // End have-required-fields
        return locAmmoCount;
    }

    /**
     * Parse a movement tag for the given <code>Entity</code>.
     *
     * @param movementTag
     * @param entity
     */
    private void parseMovement(Element movementTag, Entity entity) {
        String value = movementTag.getAttribute(MDAMAGE);
        try {
            int motiveDamage = Integer.parseInt(value);
            ((Tank) entity).setMotiveDamage(motiveDamage);
            if (motiveDamage >= ((Tank) entity).getOriginalWalkMP()) {
                ((Tank) entity).immobilize();
                ((Tank) entity).applyDamage();
            }
        } catch (Exception e) {
            warning.append("Invalid motive damage value in movement tag.\n");
        }
        value = movementTag.getAttribute(MPENALTY);
        try {
            int motivePenalty = Integer.parseInt(value);
            ((Tank) entity).setMotivePenalty(motivePenalty);
        } catch (Exception e) {
            warning.append("Invalid motive penalty value in movement tag.\n");
        }
    }

    /**
     * Parse a turretlock tag for the given <code>Entity</code>.
     *
     * @param turretLockTag
     * @param entity
     */
    private void parseTurretLock(Element turretLockTag, Entity entity) {
        String value = turretLockTag.getAttribute(DIRECTION);
        try {
            int turDir = Integer.parseInt(value);
            entity.setSecondaryFacing(turDir);
            ((Tank) entity).lockTurret(((Tank) entity).getLocTurret());
        } catch (Exception ex) {
            LogManager.getLogger().error("", ex);
            warning.append("Invalid turret lock direction value in movement tag.\n");
        }
    }

    /**
     * Parse a turret2lock tag for the given <code>Entity</code>.
     *
     * @param turret2LockTag
     * @param entity
     */
    private void parseTurret2Lock(Element turret2LockTag, Entity entity) {
        String value = turret2LockTag.getAttribute(DIRECTION);
        try {
            int turDir = Integer.parseInt(value);
            ((Tank) entity).setDualTurretOffset(turDir);
            ((Tank) entity).lockTurret(((Tank) entity).getLocTurret2());
        } catch (Exception ex) {
            LogManager.getLogger().error("", ex);
            warning.append("Invalid turret2 lock direction value in movement tag.\n");
        }
    }

    /**
     * Parse a si tag for the given <code>Entity</code>.
     *
     * @param siTag
     * @param entity
     */
    private void parseSI(Element siTag, Entity entity) {
        String value = siTag.getAttribute(INTEGRITY);
        try {
            int newSI = Integer.parseInt(value);
            ((Aero) entity).setSI(newSI);
        } catch (Exception ignored) {
            warning.append("Invalid SI value in structural integrity tag.\n");
        }
    }

    /**
     * Parse a heat tag for the given <code>Entity</code>.
     *
     * @param heatTag
     * @param entity
     */
    private void parseHeat(Element heatTag, Entity entity) {
        String value = heatTag.getAttribute(SINK);
        try {
            int newSinks = Integer.parseInt(value);
            ((Aero) entity).setHeatSinks(newSinks);
        } catch (Exception ignored) {
            warning.append("Invalid heat sink value in heat sink tag.\n");
        }
    }

    /**
     * Parse a fuel tag for the given <code>Entity</code>.
     *
     * @param fuelTag
     * @param entity
     */
    private void parseFuel(Element fuelTag, Entity entity) {
        String value = fuelTag.getAttribute(LEFT);
        try {
            int newFuel = Integer.parseInt(value);
            ((IAero) entity).setFuel(newFuel);
        } catch (Exception ignored) {
            warning.append("Invalid fuel value in fuel tag.\n");
        }
    }

    /**
     * Parse a kf tag for the given <code>Entity</code>.
     *
     * @param kfTag
     * @param entity
     */
    private void parseKF(Element kfTag, Entity entity) {
        String value = kfTag.getAttribute(INTEGRITY);
        try {
            int newIntegrity = Integer.parseInt(value);
            ((Jumpship) entity).setKFIntegrity(newIntegrity);
        } catch (Exception e) {
            warning.append("Invalid KF integrity value in KF integrity tag.\n");
        }
    }

    /**
     * Parse a sail tag for the given <code>Entity</code>.
     *
     * @param sailTag
     * @param entity
     */
    private void parseSail(Element sailTag, Entity entity) {
        String value = sailTag.getAttribute(INTEGRITY);
        try {
            int newIntegrity = Integer.parseInt(value);
            ((Jumpship) entity).setSailIntegrity(newIntegrity);
        } catch (Exception e) {
            warning.append("Invalid sail integrity value in sail integrity tag.\n");
        }
    }

    /**
     * Parse an aeroCrit tag for the given <code>Entity</code>.
     *
     * @param aeroCritTag
     * @param entity
     */
    private void parseAeroCrit(Element aeroCritTag, Entity entity) {
        String avionics = aeroCritTag.getAttribute(AVIONICS);
        String sensors = aeroCritTag.getAttribute(SENSORS);
        String engine = aeroCritTag.getAttribute(ENGINE);
        String fcs = aeroCritTag.getAttribute(FCS);
        String cic = aeroCritTag.getAttribute(CIC);
        String leftThrust = aeroCritTag.getAttribute(LEFT_THRUST);
        String rightThrust = aeroCritTag.getAttribute(RIGHT_THRUST);
        String lifeSupport = aeroCritTag.getAttribute(LIFE_SUPPORT);
        String gear = aeroCritTag.getAttribute(GEAR);

        Aero a = (Aero) entity;

        if (!avionics.isBlank()) {
            a.setAvionicsHits(Integer.parseInt(avionics));
        }

        if (!sensors.isBlank()) {
            a.setSensorHits(Integer.parseInt(sensors));
        }

        if (!engine.isBlank()) {
            a.setEngineHits(Integer.parseInt(engine));
        }

        if (!fcs.isBlank()) {
            a.setFCSHits(Integer.parseInt(fcs));
        }

        if (!cic.isBlank()) {
            a.setCICHits(Integer.parseInt(cic));
        }

        if (!leftThrust.isBlank()) {
            a.setLeftThrustHits(Integer.parseInt(leftThrust));
        }

        if (!rightThrust.isBlank()) {
            a.setRightThrustHits(Integer.parseInt(rightThrust));
        }

        if (!lifeSupport.isBlank()) {
            a.setLifeSupport(false);
        }

        if (!gear.isBlank()) {
            a.setGearHit(true);
        }
    }

    /**
     *  Parse a dropCrit tag for the given <code>Entity</code>.
     *  @param dropCritTag
     *  @param entity
     */
    private void parseDropCrit(Element dropCritTag, Entity entity) {
        String dockingcollar = dropCritTag.getAttribute(DOCKING_COLLAR);
        String kfboom = dropCritTag.getAttribute(KFBOOM);

        Dropship d = (Dropship) entity;

        if (!dockingcollar.isBlank()) {
            d.setDamageDockCollar(true);
        }

        if (!kfboom.isBlank()) {
            d.setDamageKFBoom(true);
        }
    }

    /**
     *  Parse cargo bay and door the given <code>Entity</code>.
     *  Borrowed all this from the code that handles vehicle stabilizer crits by location.
     *
     *  @param entity
     */
    private void parseTransportBay (Element bayTag, Entity entity) {
        // Look for the element's attributes.
        String index = bayTag.getAttribute(INDEX);

        int bay;
        // Did we find the required index?
        if (index.isBlank()) {
            warning.append("Could not find index for bay.\n");
            return;
        } else {
        // Try to get a good index value.
            bay = -1;
            try {
                bay = Integer.parseInt(index);
            } catch (NumberFormatException ignored) {
                // Handled by the next if test
            }

            if (bay < 0) {
                warning.append("Found invalid index value for bay: ").append(index).append(".\n");
                return;
            } else if (entity.getBayById(bay) == null) {
                warning.append("The entity, ")
                    .append(entity.getShortName())
                    .append(" does not have a bay at index: ")
                    .append(bay).append(".\n");
                return;
            }
        }

        Bay currentbay = entity.getBayById(bay);

        // Handle children for each bay.
        NodeList nl = bayTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != bayTag) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(BAYDAMAGE)) {
                    currentbay.setBayDamage(Double.parseDouble(currNode.getTextContent()));
                } else if (nodeName.equalsIgnoreCase(BAYDOORS)) {
                    currentbay.setCurrentDoors(Integer.parseInt(currNode.getTextContent()));
                } else if (nodeName.equalsIgnoreCase(LOADED)) {
                    currentbay.troops.add(Integer.parseInt(currNode.getTextContent()));
                }
            }
        }
    }

    /**
     * Parse a tankCrit tag for the given <code>Entity</code>.
     *
     * @param tankCrit
     * @param entity
     */
    private void parseTankCrit(Element tankCrit, Entity entity) {
        String sensors = tankCrit.getAttribute(SENSORS);
        String engine = tankCrit.getAttribute(ENGINE);
        String driver = tankCrit.getAttribute(DRIVER);
        String commander = tankCrit.getAttribute(COMMANDER);

        Tank t = (Tank) entity;

        if (!sensors.isBlank()) {
            t.setSensorHits(Integer.parseInt(sensors));
        }

        if (engine.equalsIgnoreCase("hit")) {
            t.engineHit();
            t.applyDamage();
        }

        if (driver.equalsIgnoreCase("hit")) {
            t.setDriverHit(true);
        }

        if (commander.equalsIgnoreCase("console")) {
            t.setUsingConsoleCommander(true);
        } else if (commander.equalsIgnoreCase("hit")) {
            t.setCommanderHit(true);
        }
    }

    /**
     * Parse a bombs tag for the given <code>Entity</code>.
     *
     * @param bombsTag
     * @param entity
     */
    private void parseBombs(Element bombsTag, Entity entity) {
        if (!(entity instanceof IBomber)) {
            warning.append("Found a bomb but Entity cannot carry bombs.\n");
            return;
        }

        // Deal with any child nodes
        NodeList nl = bombsTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != bombsTag) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(BOMB)) {
                    int[] bombChoices = ((IBomber) entity).getBombChoices();
                    String type = currEle.getAttribute(TYPE);
                    String load = currEle.getAttribute(LOAD);
                    if (!type.isBlank() && !load.isBlank()) {
                        int bombType = BombType.getBombTypeFromInternalName(type);
                        if ((bombType <= BombType.B_NONE) || (bombType >= BombType.B_NUM)) {
                            continue;
                        }

                        bombChoices[bombType] += Integer.parseInt(load);
                        ((IBomber) entity).setBombChoices(bombChoices);
                    }
                }
            }
        }
    }

    /**
     * Parse a c3i tag for the given <code>Entity</code>.
     *
     * @param c3iTag
     * @param entity
     */
    private void parseC3I(Element c3iTag, Entity entity) {
        // Deal with any child nodes
        NodeList nl = c3iTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != c3iTag) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(C3ILINK)) {
                    String link = currEle.getAttribute(LINK);
                    int pos = entity.getFreeC3iUUID();
                    if (!link.isBlank() && (pos != -1)) {
                        LogManager.getLogger().info("Loading C3i UUID " + pos + ": " + link);
                        entity.setC3iNextUUIDAsString(pos, link);
                    }
                }
            }
        }
    }

    /**
     * Parse an NC3 tag for the given <code>Entity</code>.
     *
     * @param nc3Tag
     * @param entity
     */
    private void parseNC3(Element nc3Tag, Entity entity) {
        // Deal with any child nodes
        NodeList nl = nc3Tag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);

            if (currNode.getParentNode() != nc3Tag) {
                continue;
            }
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String nodeName = currNode.getNodeName();
                if (nodeName.equalsIgnoreCase(NC3LINK)) {
                    String link = currEle.getAttribute(LINK);
                    int pos = entity.getFreeNC3UUID();
                    if (!link.isBlank() && (pos != -1)) {
                        LogManager.getLogger().info("Loading NC3 UUID " + pos + ": " + link);
                        entity.setNC3NextUUIDAsString(pos, link);
                    }
                }
            }
        }
    }
    
    /**
     * Parse an EscapeCraft tag for the given <code>Entity</code>.
     *
     * @param escCraftTag
     * @param entity
     */
    private void parseEscapeCraft(Element escCraftTag, Entity entity) {
        if (!(entity instanceof SmallCraft || entity instanceof Jumpship)) {
            warning.append("Found an EscapeCraft tag but Entity is not a " +
                    "Crewed Spacecraft!\n");
            return;
        }

        try {
            String id = escCraftTag.getAttribute(ID);
            ((Aero) entity).addEscapeCraft(id);
        } catch (Exception e) {
            warning.append("Invalid external entity id in EscapeCraft tag.\n");
        }
    }
    
    /**
     * Parse an EscapedPassengers tag for the given <code>Entity</code>.
     *
     * @param escPassTag
     * @param entity
     */
    private void parseEscapedPassengers(Element escPassTag, Entity entity) {
        if (!(entity instanceof EjectedCrew || entity instanceof SmallCraft)) {
            warning.append("Found an EscapedPassengers tag but Entity is not a " +
                    "Spacecraft Crew or Small Craft!\n");
            return;
        }
        // Deal with any child nodes
        NodeList nl = escPassTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String id = currEle.getAttribute(ID);
                String number = currEle.getAttribute(NUMBER);
                int value = Integer.parseInt(number);
                if (entity instanceof EjectedCrew) {
                    ((EjectedCrew) entity).addPassengers(id, value);
                } else {
                    ((SmallCraft) entity).addPassengers(id, value);
                }
            }
        }
    }

    /**
     * Parse an EscapedCrew tag for the given <code>Entity</code>.
     *
     * @param escCrewTag
     * @param entity
     */
    private void parseEscapedCrew(Element escCrewTag, Entity entity) {
        if (!(entity instanceof EjectedCrew || entity instanceof SmallCraft)) {
            warning.append("Found an EscapedCrew tag but Entity is not a " +
                    "Spacecraft Crew or Small Craft!\n");
            return;
        }
        // Deal with any child nodes
        NodeList nl = escCrewTag.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node currNode = nl.item(i);
            int nodeType = currNode.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element currEle = (Element) currNode;
                String id = currEle.getAttribute(ID);
                String number = currEle.getAttribute(NUMBER);
                int value = Integer.parseInt(number);
                if (entity instanceof EjectedCrew) {
                    ((EjectedCrew) entity).addNOtherCrew(id, value);
                } else {
                    ((SmallCraft) entity).addNOtherCrew(id, value);
                }
            }
        }
    }
    
    /**
     * Parse an original si tag for the given <code>Entity</code>. Used by Escape Pods
     *
     * @param OsiTag
     * @param entity
     */
    private void parseOSI(Element OsiTag, Entity entity) {
        String value = OsiTag.getAttribute(NUMBER);
        try {
            int newSI = Integer.parseInt(value);
            ((Aero) entity).set0SI(newSI);
        } catch (Exception ignored) {
            warning.append("Invalid SI value in original structural integrity tag.\n");
        }
    }
    
    /**
     * Parse an original men tag for the given <code>Entity</code>. Used by Escaped spacecraft crew
     *
     * @param OMenTag
     * @param entity
     */
    private void parseOMen(Element OMenTag, Entity entity) {
        String value = OMenTag.getAttribute(NUMBER);
        try {
            int newMen = Integer.parseInt(value);
            entity.initializeInternal(newMen, Infantry.LOC_INFANTRY);
        } catch (Exception ignored) {
            warning.append("Invalid internal value in original number of men tag.\n");
        }
    }
    
    /**
     * Parse a conveyance tag for the given <code>Entity</code>. Used to resolve crew damage to transported entities
     *
     * @param conveyanceTag
     * @param entity
     */
    private void parseConveyance(Element conveyanceTag, Entity entity) {
        String value = conveyanceTag.getAttribute(ID);
        try {
            int id = Integer.parseInt(value);
            entity.setTransportId(id);
        } catch (Exception e) {
            warning.append("Invalid transport id in conveyance tag.\n");
        }
    }
    
    /**
     * Parse an id tag for the given <code>Entity</code>. Used to resolve crew damage to transported entities
     *
     * @param idTag
     * @param entity
     */
    private void parseId(Element idTag, Entity entity) {
        String value = idTag.getAttribute(ID);
        //Safety. We don't want to mess with autoassigned game Ids
        if (entity.getGame() != null) {
            return;
        }
        try {
            int id = Integer.parseInt(value);
            entity.setId(id);
        } catch (Exception ignored) {
            warning.append("Invalid id in conveyance tag.\n");
        }
    }
    
    /**
     * Parse a force tag for the given <code>Entity</code>. 
     */
    private void parseForce(Element forceTag, Entity entity) {
        entity.setForceString(forceTag.getAttribute(FORCEATT));
    }

    /**
     * Parase a modularEquipmentMount tag for the supplied <code>Entity</code>.
     *
     * @param meaTag
     * @param entity
     */
    private void parseBAMEA(Element meaTag, Entity entity) {
        if (!(entity instanceof BattleArmor)) {
            warning.append("Found a BA MEA tag but Entity is not " +
                    "BattleArmor!\n");
            return;
        }

        String meaMountLocString = meaTag.getAttribute(BA_MEA_MOUNT_LOC);
        String manipTypeName = meaTag.getAttribute(BA_MEA_TYPE_NAME);

        // Make sure we got a mount number
        if (meaMountLocString.isBlank()) {
            warning.append("antiPersonnelMount tag does not specify a baMeaMountLoc!\n");
            return;
        }

        // We could have no mounted manipulator
        EquipmentType manipType = null;
        if (!manipTypeName.isBlank()) {
            manipType = EquipmentType.get(manipTypeName);
        }

        // Find the Mounted instance for the MEA
        Mounted mountedManip = null;
        int meaMountLoc = Integer.parseInt(meaMountLocString);
        boolean foundMea = false;
        for (Mounted m : entity.getEquipment()) {
            if ((m.getBaMountLoc() == meaMountLoc) && m.getType().hasFlag(MiscType.F_BA_MEA)) {
                foundMea = true;
                break;
            }
        }
        if (!foundMea) {
            warning.append("No modular equipment mount found in specified " + "location! Location: ")
                    .append(meaMountLoc).append("\n");
            return;
        }
        if (meaMountLoc == BattleArmor.MOUNT_LOC_LARM) {
            mountedManip = ((BattleArmor) entity).getLeftManipulator();
        } else if (meaMountLoc == BattleArmor.MOUNT_LOC_RARM) {
            mountedManip = ((BattleArmor) entity).getRightManipulator();
        }

        if (mountedManip != null) {
            entity.getEquipment().remove(mountedManip);
            entity.getMisc().remove(mountedManip);
        }

        // Was no manipulator selected?
        if (manipType == null) {
            return;
        }

        // Add the newly mounted maniplator
        try {
            int baMountLoc = mountedManip.getBaMountLoc();
            mountedManip = entity.addEquipment(manipType, mountedManip.getLocation());
            mountedManip.setBaMountLoc(baMountLoc);
        } catch (Exception ex) {
            LogManager.getLogger().error("", ex);
        }
    }

    /**
     * Parse a antiPersonnelMount tag for the supplied <code>Entity</code>.
     *
     * @param apmTag
     * @param entity
     */
    private void parseBAAPM(Element apmTag, Entity entity) {
        if (!(entity instanceof BattleArmor)) {
            warning.append("Found a BA APM tag but Entity is not BattleArmor!\n");
            return;
        }

        String mountNumber = apmTag.getAttribute(BA_APM_MOUNT_NUM);
        String apTypeName = apmTag.getAttribute(BA_APM_TYPE_NAME);

        // Make sure we got a mount number
        if (mountNumber.isBlank()) {
            warning.append("antiPersonnelMount tag does not specify a baAPMountNum!\n");
            return;
        }

        Mounted apMount = entity.getEquipment(Integer.parseInt(mountNumber));
        // We may mount no AP weapon
        EquipmentType apType = null;
        if (!apTypeName.isBlank()) {
            apType = EquipmentType.get(apTypeName);
        }

        // Remove any currently mounted AP weapon
        if ((apMount.getLinked() != null) && (apMount.getLinked().getType() != apType)) {
            Mounted apWeapon = apMount.getLinked();
            entity.getEquipment().remove(apWeapon);
            entity.getWeaponList().remove(apWeapon);
            entity.getTotalWeaponList().remove(apWeapon);
            // We need to make sure that the weapon has been removed
            // from the criticals, otherwise it can cause issues
            for (int loc = 0; loc < entity.locations(); loc++) {
                for (int c = 0; c < entity.getNumberOfCriticals(loc); c++) {
                    CriticalSlot crit = entity.getCritical(loc, c);
                    if ((crit != null) && (crit.getMount() != null) && crit.getMount().equals(apWeapon)) {
                        entity.setCritical(loc, c, null);
                    }
                }
            }
        }

        // Did the selection not change, or no weapon was selected
        if (((apMount.getLinked() != null) && (apMount.getLinked().getType() == apType))
                || (apType == null)) {
            return;
        }

        // Add the newly mounted weapon
        try {
            Mounted newWeap = entity.addEquipment(apType, apMount.getLocation());
            apMount.setLinked(newWeap);
            newWeap.setLinked(apMount);
            newWeap.setAPMMounted(true);
        } catch (Exception ex) {
            LogManager.getLogger().error("", ex);
        }
    }

    /**
     * Worker function that takes an entity, a location, an ammo type string and the critical index
     * of a weapons bay in the given location and attempts to add the ammo type there.
     * @param entity The entity we're working on loading
     * @param loc The location index on the entity
     * @param type The ammo type string
     * @param bayIndex The crit index of the bay where we want to load the ammo on the location where the bay is
     */
    private void addExtraAmmoToBay(Entity entity, int loc, String type, String bayIndex) {
        // here, we need to do the following:
        // 1: get the bay to which this ammo belongs, and add it to said bay
        // 2: add the ammo to the entity as a "new" piece of equipment
        // 3: add the ammo to a crit slot on the bay's location

        int bayCritIndex = Integer.parseInt(bayIndex);
        Mounted bay = entity.getCritical(loc, bayCritIndex - 1).getMount();

        Mounted ammo = new Mounted(entity, AmmoType.get(type));

        try {
            entity.addEquipment(ammo, loc, bay.isRearMounted());
        } catch (LocationFullException ignored) {
            // silently swallow it, since DropShip locations have about a hundred crit slots
        }

        bay.addAmmoToBay(entity.getEquipmentNum(ammo));
    }

    /**
     * Determine if unexpected XML entities were encountered during parsing.
     *
     * @return <code>true</code> if a non-fatal warning occurred.
     */
    public boolean hasWarningMessage() {
        return (warning.length() > 0);
    }

    /**
     * Get the warning message from the last parse.
     *
     * @return The <code>String</code> warning message from the last parse. If
     *         there is no warning message, then an <code>null</code> value is
     *         returned.
     */
    public String getWarningMessage() {
        if (warning.length() > 0) {
            return warning.toString();
        }
        return null;
    }

    /**
     * Returns a list of all of the  Entity's parsed from the input, should be
     * called after <code>parse</code>. This is for entities that we want to be loaded
     * into the chat lounge, so functional
     * @return
     */
    public Vector<Entity> getEntities() {
        Vector<Entity> toReturn = entities;
        for (Entity e : survivors) {
            if (e instanceof EjectedCrew) {
                continue;
            }
            toReturn.add(e);
        }
        return toReturn;
    }

    /**
     * Returns a list of all of the salvaged Entity's parsed from the input, should be
     * called after <code>parse</code>.
     * @return
     */
    public Vector<Entity> getSurvivors() {
        return survivors;
    }

    /**
     * Returns a list of all of the allied Entity's parsed from the input, should be
     * called after <code>parse</code>.
     * @return
     */
    public Vector<Entity> getAllies() {
        return allies;
    }

    /**
     * Returns a list of all of the salvaged Entity's parsed from the input, should be
     * called after <code>parse</code>.
     * @return
     */
    public Vector<Entity> getSalvage() {
        return salvage;
    }

    /**
     * Returns a list of all of the enemy retreated entities parsed from the input, should be
     * called after <code>parse</code>.
     * @return
     */
    public Vector<Entity> getRetreated() {
        return retreated;
    }

    /**
     * Returns a list of all of the devastated Entity's parsed from the input, should be
     * called after <code>parse</code>.
     * @return
     */
    public Vector<Entity> getDevastated() {
        return devastated;
    }

    /**
     * Returns a list of all of the Pilots parsed from the input, should be
     * called after <code>parse</code>.
     *
     * @return
     */
    public Vector<Crew> getPilots() {
        return pilots;
    }

    /**
     * Returns the kills hashtable
     *
     * @return
     */
    public Hashtable<String, String> getKills() {
        return kills;
    }

    /**
     * Marks all equipment in a location on an <code>Entity<code> as destroyed.
     *
     * @param en
     *            - the <code>Entity</code> whose location is destroyed.
     * @param loc
     *            - the <code>int</code> index of the destroyed location.
     */
    private void destroyLocation(Entity en, int loc) {
        // mark armor, internal as destroyed
        en.setArmor(IArmorState.ARMOR_DESTROYED, loc, false);
        en.setInternal(IArmorState.ARMOR_DESTROYED, loc);
        if (en.hasRearArmor(loc)) {
            en.setArmor(IArmorState.ARMOR_DESTROYED, loc, true);
        }

        // equipment marked missing
        for (Mounted mounted : en.getEquipment()) {
            if (mounted.getLocation() == loc) {
                mounted.setDestroyed(true);
            }
        }
        // all critical slots set as missing
        for (int i = 0; i < en.getNumberOfCriticals(loc); i++) {
            final CriticalSlot cs = en.getCritical(loc, i);
            if (cs != null) {
                cs.setDestroyed(true);
            }
        }
    }

    private void breachLocation(Entity en, int loc) {
        // equipment marked breached
        for (Mounted mounted : en.getEquipment()) {
            if (mounted.getLocation() == loc) {
                mounted.setBreached(true);
            }
        }
        // all critical slots set as breached
        for (int i = 0; i < en.getNumberOfCriticals(loc); i++) {
            final CriticalSlot cs = en.getCritical(loc, i);
            if (cs != null) {
                cs.setBreached(true);
            }
        }
        en.setLocationStatus(loc, ILocationExposureStatus.BREACHED);
    }

    private void blowOffLocation(Entity en, int loc) {
        en.setLocationBlownOff(loc, true);
        for (Mounted mounted : en.getEquipment()) {
            if (mounted.getLocation() == loc) {
                mounted.setMissing(true);
            }
        }
        for (int i = 0; i < en.getNumberOfCriticals(loc); i++) {
            final CriticalSlot cs = en.getCritical(loc, i);
            if (cs != null) {
                cs.setMissing(true);
            }
        }
    }
}
