package org.ostad.ostad_assignment_25.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownReadmeParserTest {

    private final MarkdownReadmeParser parser = new MarkdownReadmeParser();

    @Test
    void parseShouldExtractTopicsSubTopicsAndContent() {
        String markdown = """
                # Root
                
                ## Java Basics
                ### Variables
                Intro to variables
                ### Loops
                for/while details
                
                ## OOP
                ### Classes
                class details
                """;

        List<MarkdownReadmeParser.ParsedTopic> topics = parser.parse(markdown);

        assertThat(topics).hasSize(2);
        assertThat(topics.get(0).name()).isEqualTo("Java Basics");
        assertThat(topics.get(0).subTopics()).hasSize(2);
        assertThat(topics.get(0).subTopics().get(0).name()).isEqualTo("Variables");
        assertThat(topics.get(0).subTopics().get(0).content()).contains("Intro to variables");
    }
}
