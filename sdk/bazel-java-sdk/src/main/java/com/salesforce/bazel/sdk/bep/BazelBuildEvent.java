package com.salesforce.bazel.sdk.bep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

/**
 * Base model of a Build Event Protocol (BEP) event, and parsing utilities.
 * Check for subclasses for specific event type implementations.
 * <p>
 * BEP is strangely difficult to parse. The contents of the json attributes are 
 * denormalized and not fit for computer consumption.
 * <p>
 * <a href="https://docs.bazel.build/versions/master/build-event-protocol.html">BEP Documentation</a>
 */
public class BazelBuildEvent {

    protected int index = 0;
    protected String eventType;
    
    protected boolean isLastMessage = false;
    protected boolean isError = false;
    
    protected BazelBuildEvent(String eventType, int index, JSONObject eventObject) {
        this.eventType = eventType;
        this.index = index;
        
        // any event (theoretically) could be the lastMessage, so check that here in the base event
        Object lastMessage = eventObject.get("lastMessage");
        if (lastMessage != null) {
            this.isLastMessage = true;
        }
    }
    
    /**
     * The numerical index of this event in the BEP file. Starts at 0. 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Is this the last message in the stream in the BEP file? If true, indicates that the 
     * operation (build, test, etc) is complete.
     */
    public boolean isLastMessage() {
        return isLastMessage;
    }

    /**
     * Does this event signal a failure of the entire operation (build error, test failure)?
     */
    public boolean isError() {
        return isError;
    }

    
    // INTERNALS
    
    /**
     * The contents of a BEP progress event is crowded with noisy characters and duplicated text.
     * This method cleans it up to an extent, but it will always be up to the caller of the SDK
     * to fully clean it depending on purpose.
     * <p>
     * This is in the base class, not the Progress subclass, just in case other event types
     * need to do the same processing.
     */
    protected static List<String> splitAndCleanAndDedupeLines(String rawString) {
        Set<Integer> hashcodes = new HashSet<>();
        List<String> lines = new ArrayList<>();
        String[] rawLines = rawString.split("\r");
        for (String line : rawLines) {
            line = stripControlCharacters(line);
            line = line.trim();
            if (line.isBlank()) {
                // ignore blank lines
                continue;
            }
            
            int hashcode = line.hashCode();
            if (hashcodes.contains(hashcode)) {
                // dupe line, ignore
                continue;
            }
            hashcodes.add(hashcode);
            lines.add(line);
        }
        return lines;
    }

    /**
     * Yep, there are strange characters (ncurses?) in the stderr for BEP progress events.
     * <p>
     * This is in the base class, not the Progress subclass, just in case other event types
     * need to do the same processing.
     */
    protected static String stripControlCharacters(String rawString) {
        // first, get rid of the unicode control characters
        String clean = rawString.replaceAll("[^\\x00-\\x7F]", "");
        
        // next, get rid of the ASCII control characters
        clean = clean.replace("[0m", "");
        clean = clean.replace("[1m", "");
        clean = clean.replace("[32m", "");
        clean = clean.replace("[31m", "");
        
        // and then these
        clean = clean.replace("[1A", "");
        clean = clean.replace("[K", "");
        
        // in test results, after the first INFO: Build completed, 1 test FAILED, 3 total actions line
        // there is a lot of duplicated text
        int totalActionsIndex = clean.indexOf("total actions");
        if (totalActionsIndex > 0) {
            clean = clean.substring(0, totalActionsIndex+13);
        }
        
        return clean;
    }
}

