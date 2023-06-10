package net.starly.optimize.context;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class MessageContent {

    private static MessageContent instance;
    private final Map<MessageType, Map<String, List<String>>> messageMap = new HashMap<>();

    private MessageContent() {
    }

    public static MessageContent getInstance() {
        if (instance == null) instance = new MessageContent();
        return instance;
    }

    public void initialize(FileConfiguration file) {
        messageMap.clear();
        Arrays.stream(MessageType.values())
                .forEach(type -> {
                    ConfigurationSection configSection = file.getConfigurationSection(type.getKey());
                    if (configSection != null) initializeMessages(type, configSection);
                });
    }

    private void initializeMessages(MessageType type, ConfigurationSection configSection) {
        Map<String, List<String>> messages = messageMap.computeIfAbsent(type, key -> new HashMap<>());
        for (String key : configSection.getKeys(false)) {
            if (configSection.isList(key)) {
                List<String> list = configSection.getStringList(key).stream()
                        .map(str -> ChatColor.translateAlternateColorCodes('&', str))
                        .collect(Collectors.toList());
                messages.put(key, list);
            } else if (configSection.isString(key)) {
                List<String> list = new ArrayList<>();
                list.add(ChatColor.translateAlternateColorCodes('&', configSection.getString(key)));
                messages.put(key, list);
            } else {
                List<String> list = new ArrayList<>();
                list.add(configSection.getString(key));
                messages.put(key, list);
            }
        }
    }

    public Optional<String> getMessage(MessageType type, String key) {
        Map<String, List<String>> messageTypeMap = messageMap.get(type);
        if (messageTypeMap != null) {
            List<String> values = messageTypeMap.get(key);
            if (values != null && !values.isEmpty()) {
                return Optional.of(values.get(0));
            }
        }
        return Optional.empty();
    }

    public Optional<String> getMessageAfterPrefix(MessageType type, String key) {
        String prefix = getMessage(MessageType.NORMAL, "prefix").orElse("");
        return getMessage(type, key).map(message -> prefix + message);
    }

    public List<String> getMessages(MessageType type, String path) {
        return messageMap.getOrDefault(type, Collections.emptyMap()).getOrDefault(path, Collections.emptyList());
    }

    public int getInt(MessageType type, String key) {
        Optional<String> value = getMessage(type, key);
        if (value.isPresent()) {
            try {
                return Integer.parseInt(value.get());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public float getFloat(MessageType type, String key) {
        Optional<String> value = getMessage(type, key);
        if (value.isPresent()) {
            try {
                return Float.parseFloat(value.get());
            } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return 1;
    }

    public boolean getBoolean(MessageType type, String key) {
        Optional<String> value = getMessage(type, key);
        return value.map(Boolean::parseBoolean).orElse(false);
    }

    public List<Integer> getInts(MessageType type, String key) {
        List<String> values = messageMap.getOrDefault(type, Collections.emptyMap()).getOrDefault(key, Collections.emptyList());
        List<Integer> result = new ArrayList<>();
        for (String value : values) {
            try {
                result.add(Integer.parseInt(value));
            } catch (NumberFormatException e) { e.printStackTrace(); }
        }
        return result;
    }
}
