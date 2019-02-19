package com.zuehlke.zSport;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomJsonDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String jsonDate = jsonParser.getText();
        // the json Date we get from our frontend is in ISO Date format, i.e. includes the Timezone as 'Z' = UTC
        // todo: extend this if statement to include other timezone formats, e.g. '.....[GMT+1]', '.....+01:00'
        if(jsonDate.contains("Z")){
            ZonedDateTime zonedDateTime =
                    ZonedDateTime.parse(jsonDate, DateTimeFormatter.ISO_DATE_TIME)
                            .withZoneSameInstant(ZoneId.systemDefault());
            return zonedDateTime.toLocalDateTime();
        }
        if(jsonDate.length()>=19) {
            String date = jsonDate.substring(0, 19);
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        }
        else {
            throw new JsonParseException(jsonParser, "Json Date has the wrong format. Use format 'yyyy-MM-dd'T'HH:mm:ss' or the ISO-8601 standard.");
        }
    }
}
