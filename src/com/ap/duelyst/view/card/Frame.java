package com.ap.duelyst.view.card;

import com.ap.duelyst.model.Utils;
import com.ap.duelyst.plist.NSDictionary;
import com.ap.duelyst.plist.NSObject;
import com.ap.duelyst.plist.PropertyListFormatException;
import com.ap.duelyst.plist.PropertyListParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Frame {
    private int posX;
    private int posY;
    private int width;
    private int height;

    public Frame(NSDictionary dict) {
        String str = dict.get("frame").toString();
        posX = extractInteger(str, Pattern.compile("(?<=\\{\\{)\\d+"));
        posY = extractInteger(str, Pattern.compile("\\d+(?=\\}\\,)"));
        width = extractInteger(str, Pattern.compile("(?<=\\,\\{)\\d+"));
        height = extractInteger(str, Pattern.compile("\\d+(?=\\}\\})"));
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private Integer extractInteger(String str, Pattern pattern) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(0));
        } else {
            return null;
        }
    }

    public static NSDictionary parseRootDictionary(String fileName) {
        try {
            File file = new File(Utils.getURI(fileName + ".plist"));
            return (NSDictionary) PropertyListParser.parse(file);
        } catch (IOException | PropertyListFormatException | ParseException
                | SAXException | ParserConfigurationException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Frame> getFrames(NSDictionary rootDict, FrameType frameType) {
        if (frameType == FrameType.ATTACK) {
            return getFrames(rootDict, Pattern.compile("\\_attack\\_"));
        } else if (frameType == FrameType.BREATHING) {
            return getFrames(rootDict, Pattern.compile("\\_breathing\\_"));
        } else if (frameType == FrameType.DEATH) {
            return getFrames(rootDict, Pattern.compile("\\_death\\_"));
        } else if (frameType == FrameType.RUN) {
            return getFrames(rootDict, Pattern.compile("\\_run\\_"));
        }
        return null;
    }

    private static ArrayList<Frame> getFrames(NSDictionary rootDict, Pattern pattern) {
        ArrayList<Frame> frames = new ArrayList<>();
        HashMap<String, NSObject> hashMap = rootDict.getHashMap();
        NSDictionary dict = (NSDictionary) hashMap.get("frames");
        hashMap = dict.getHashMap();
        Matcher matcher;
        NSDictionary dictionary;
        for (String name : hashMap.keySet()) {
            matcher = pattern.matcher(name);
            if (matcher.find()) {
                dictionary = (NSDictionary) hashMap.get(name);
                frames.add(new Frame(dictionary));
            }
        }
        return frames;
    }
}
