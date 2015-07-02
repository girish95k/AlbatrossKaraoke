package com.albatross.girish.albatrosskaraoke;

import android.util.Log;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class IniParser {

    public static ArrayList<Integer> getTiming(File f) {
        Ini ini = null;
        try {
            ini = new Ini(f);
        } catch (IOException e) {
            Log.e("KAR", "Ini", e);
        }
        Section section = ini.get("Eff2");
        final String BASE_NAME = "Sync";
        ArrayList<Integer> time = new ArrayList<Integer>();
        String propName = "";
        int i;
        for (i = 0, propName = BASE_NAME + i; section.get(propName) != null; propName = BASE_NAME
                + i, ++i) {
            propName = BASE_NAME + i;

            if(section.get(propName)==null)
                break;
            Log.e("propname", propName+"  " + i);

            time.addAll(getIntList(section.get(propName).split(",")));
            Log.e(i+"", getIntList(section.get(propName).split(","))+"");
        }
        System.out.println("getTimings: " + time);
        return time;
    }

    private static ArrayList<Integer> getIntList(String[] nums) {
        ArrayList<Integer> list = new ArrayList<>();
        for (String num : nums) {
            list.add(Integer.parseInt(num));
        }
        Log.e("number of syncs: ", list.size()+"");
        System.out.println(list);
        return list;
    }

    public static ArrayList<String> getLines(File f) {
        Ini ini = null;
        try {
            ini = new Ini(f);
        } catch (IOException e) {
            Log.e("KAR", "Ini", e);
        }

        Section section = ini.get("Eff2");
        int count = Integer.parseInt(section.get("TextCount"));
        ArrayList<String> lines = new ArrayList<String>(count);

        for (int i = 0; i < count; i++) {
            if(!section.get("Text" + i).equals(""))
            lines.add(section.get("Text" + i));
        }
        System.out.println(lines);
        return lines;
    }

    public static int getLinesNumber(File f) {
        Ini ini = null;
        try {
            ini = new Ini(f);
        } catch (IOException e) {
            Log.e("KAR", "Ini", e);
        }

        Section section = ini.get("Eff2");
        int count = Integer.parseInt(section.get("TextCount"));
        ArrayList<String> lines = new ArrayList<String>(count);

        for (int i = 0; i < count; i++) {
            //if(!section.get("Text" + i).equals(""))
            lines.add(section.get("Text" + i));
        }
        System.out.println(lines);
        return lines.size();
    }

    public static ArrayList<Integer> getCumulativeWordCounts(ArrayList<String> lines) {
        ArrayList<Integer> counts = new ArrayList<>();
        int tot = 0;
        for (Iterator<String> it = lines.iterator(); it.hasNext(); ) {
            tot += it.next().split(" ").length;
            counts.add(tot);
        }
        Log.e("number of words: ", counts.size()+"");
        System.out.println(counts);
        return counts;
    }
}
