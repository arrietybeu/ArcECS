package org.arc.component;

import lombok.Getter;
import lombok.Setter;
import org.arc.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * SelectButton component for NPCs with interactive buttons.
 * Each button can be dynamically assigned a function like opening shops, gift codes, etc.
 * 
 * @author Arriety
 */
public class SelectButton extends Component {
    
    /**
     * Enumeration of button action types.
     */
    public enum ButtonAction {
        OPEN_SHOP,          // Open a shop interface
        ENTER_GIFT_CODE,    // Open gift code entry
        START_QUEST,        // Start a quest
        COMPLETE_QUEST,     // Complete a quest
        TELEPORT,           // Teleport to a location
        UPGRADE_EQUIPMENT,  // Open equipment upgrade
        TRADE,              // Open trading interface
        BANK,               // Open bank/storage
        GUILD,              // Open guild interface
        CUSTOM              // Custom action
    }
    
    private final ConcurrentHashMap<String, String> buttons;
    private final ConcurrentHashMap<String, ButtonAction> buttonActions;
    private final ConcurrentHashMap<String, Boolean> buttonStates;
    /**
     * -- GETTER --
     *  Checks if interaction is enabled.
     *
     *
     * -- SETTER --
     *  Sets the interaction enabled state.
     *
     @return true if interaction is enabled, false otherwise
      * @param interactionEnabled the interaction enabled state
     */
    @Setter
    @Getter
    private boolean interactionEnabled = true;
    /**
     * -- GETTER --
     *  Gets the interaction range.
     *
     * @return the interaction range
     */
    @Getter
    private float interactionRange = 32f;
    /**
     * -- GETTER --
     *  Gets the dialog text shown when interacting with the NPC.
     *
     * @return the dialog text
     */
    @Getter
    private String dialogText = "";
    /**
     * -- GETTER --
     *  Gets the NPC name.
     *
     * @return the NPC name
     */
    @Getter
    private String npcName = "";
    
    /**
     * Creates a select button component.
     */
    public SelectButton() {
        buttons = new ConcurrentHashMap<>();
        buttonActions = new ConcurrentHashMap<>();
        buttonStates = new ConcurrentHashMap<>();
    }
    
    /**
     * Creates a select button component with NPC name.
     * @param npcName the NPC name
     */
    public SelectButton(String npcName) {
        this();
        this.npcName = npcName;
    }
    
    /**
     * Adds a button with the specified parameters.
     * @param id the button ID
     * @param text the button text
     * @param action the button action
     */
    public void addButton(String id, String text, ButtonAction action) {
        buttons.put(id, text);
        buttonActions.put(id, action);
        buttonStates.put(id, true); // Enabled by default
    }
    
    /**
     * Removes a button by ID.
     * @param buttonId the button ID
     */
    public void removeButton(String buttonId) {
        buttons.remove(buttonId);
        buttonActions.remove(buttonId);
        buttonStates.remove(buttonId);
    }
    
    /**
     * Gets a button text by ID.
     * @param buttonId the button ID
     * @return the button text, or null if not found
     */
    public String getButtonText(String buttonId) {
        return buttons.get(buttonId);
    }
    
    /**
     * Gets a button action by ID.
     * @param buttonId the button ID
     * @return the button action, or null if not found
     */
    public ButtonAction getButtonAction(String buttonId) {
        return buttonActions.get(buttonId);
    }
    
    /**
     * Gets all button IDs.
     * @return a map of button IDs to text
     */
    public Map<String, String> getAllButtons() {
        return new ConcurrentHashMap<>(buttons);
    }
    
    /**
     * Gets all enabled buttons.
     * @return a map of enabled button IDs to text
     */
    public Map<String, String> getEnabledButtons() {
        ConcurrentHashMap<String, String> enabledButtons = new ConcurrentHashMap<>();
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            if (buttonStates.getOrDefault(entry.getKey(), false)) {
                enabledButtons.put(entry.getKey(), entry.getValue());
            }
        }
        return enabledButtons;
    }
    
    /**
     * Checks if a button is enabled.
     * @param buttonId the button ID
     * @return true if enabled, false otherwise
     */
    public boolean isButtonEnabled(String buttonId) {
        return buttonStates.getOrDefault(buttonId, false);
    }
    
    /**
     * Sets a button's enabled state.
     * @param buttonId the button ID
     * @param enabled the enabled state
     */
    public void setButtonEnabled(String buttonId, boolean enabled) {
        if (buttons.containsKey(buttonId)) {
            buttonStates.put(buttonId, enabled);
        }
    }

    /**
     * Sets the interaction range.
     * @param interactionRange the interaction range
     */
    public void setInteractionRange(float interactionRange) {
        this.interactionRange = Math.max(0, interactionRange);
    }

    /**
     * Sets the dialog text.
     * @param dialogText the dialog text
     */
    public void setDialogText(String dialogText) {
        this.dialogText = dialogText != null ? dialogText : "";
    }

    /**
     * Sets the NPC name.
     * @param npcName the NPC name
     */
    public void setNpcName(String npcName) {
        this.npcName = npcName != null ? npcName : "";
    }
    
    /**
     * Enables or disables all buttons.
     * @param enabled the enabled state
     */
    public void setAllButtonsEnabled(boolean enabled) {
        for (String buttonId : buttons.keySet()) {
            buttonStates.put(buttonId, enabled);
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        buttons.clear();
        buttonActions.clear();
        buttonStates.clear();
        interactionEnabled = true;
        interactionRange = 32f;
        dialogText = "";
        npcName = "";
    }
    
    @Override
    public String toString() {
        return String.format("SelectButton{npc='%s', buttons=%d, enabled=%b, range=%.1f}", 
                npcName, buttons.size(), interactionEnabled, interactionRange);
    }
} 