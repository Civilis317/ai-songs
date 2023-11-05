package net.playground.ai.songs.rest;

import lombok.extern.slf4j.Slf4j;
import net.playground.ai.songs.model.Song;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SongController {

    private final AiClient aiClient;

    public SongController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @GetMapping("/topsong/{year}")
    public Song getTopSong(@PathVariable("year") int year) {
        BeanOutputParser<Song> parser = new BeanOutputParser<>(Song.class);
        String promptString = """
                What was the Billboard number one year-end top 100 single for {year}?
                {format}
                """;
        PromptTemplate template = new PromptTemplate(promptString);
        template.add("year", year);
        template.add("format", parser.getFormat());
        template.setOutputParser(parser);

        Prompt prompt = template.create();
        AiResponse response = aiClient.generate(prompt);
        String json = response.getGeneration().getText();
        log.debug(json);

        return parser.parse(json);
    }
}
