package org.ostad.ostad_assignment_25.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MarkdownReadmeParser {

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");

    public List<ParsedTopic> parse(String markdown) {
        String[] lines = markdown.split("\\R");
        List<ParsedTopic> topics = new ArrayList<>();

        ParsedTopicBuilder currentTopic = null;
        ParsedSubTopicBuilder currentSubTopic = null;

        int topicOrder = 0;

        for (String rawLine : lines) {
            String line = rawLine.stripTrailing();
            Matcher matcher = HEADING_PATTERN.matcher(line.trim());

            if (matcher.matches()) {
                int level = matcher.group(1).length();
                String title = cleanHeading(matcher.group(2));

                if (level == 2) {
                    if (currentSubTopic != null && currentTopic != null) {
                        currentTopic.addSubTopic(currentSubTopic.build(currentTopic.nextSubTopicOrder()));
                        currentSubTopic = null;
                    }
                    if (currentTopic != null) {
                        topics.add(currentTopic.build(++topicOrder));
                    }
                    currentTopic = new ParsedTopicBuilder(title);
                } else if (level == 3) {
                    if (currentTopic == null) {
                        currentTopic = new ParsedTopicBuilder("General");
                    }
                    if (currentSubTopic != null) {
                        currentTopic.addSubTopic(currentSubTopic.build(currentTopic.nextSubTopicOrder()));
                    }
                    currentSubTopic = new ParsedSubTopicBuilder(title);
                }
                continue;
            }

            if (currentTopic == null) {
                continue;
            }

            if (currentSubTopic == null) {
                currentSubTopic = new ParsedSubTopicBuilder("Overview");
            }

            currentSubTopic.appendLine(rawLine);
        }

        if (currentSubTopic != null && currentTopic != null) {
            currentTopic.addSubTopic(currentSubTopic.build(currentTopic.nextSubTopicOrder()));
        }
        if (currentTopic != null) {
            topics.add(currentTopic.build(++topicOrder));
        }

        return topics;
    }

    private String cleanHeading(String heading) {
        String cleaned = heading.replaceAll("#+$", "").trim();
        return cleaned.isBlank() ? "Untitled" : cleaned;
    }

    private static class ParsedTopicBuilder {
        private final String name;
        private final Map<String, ParsedSubTopic> subTopics = new LinkedHashMap<>();
        private int subTopicOrder = 0;

        private ParsedTopicBuilder(String name) {
            this.name = name;
        }

        private int nextSubTopicOrder() {
            return ++subTopicOrder;
        }

        private void addSubTopic(ParsedSubTopic parsedSubTopic) {
            if (parsedSubTopic.content().isBlank()) {
                return;
            }
            subTopics.putIfAbsent(parsedSubTopic.name().toLowerCase(), parsedSubTopic);
        }

        private ParsedTopic build(int topicOrder) {
            return new ParsedTopic(name, topicOrder, new ArrayList<>(subTopics.values()));
        }
    }

    private static class ParsedSubTopicBuilder {
        private final String name;
        private final StringBuilder contentBuilder = new StringBuilder();

        private ParsedSubTopicBuilder(String name) {
            this.name = name;
        }

        private void appendLine(String line) {
            contentBuilder.append(line).append(System.lineSeparator());
        }

        private ParsedSubTopic build(int subTopicOrder) {
            return new ParsedSubTopic(name, subTopicOrder, contentBuilder.toString().trim());
        }
    }

    public record ParsedTopic(String name, int order, List<ParsedSubTopic> subTopics) {
    }

    public record ParsedSubTopic(String name, int order, String content) {
    }
}
