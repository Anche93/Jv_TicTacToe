package org.example.datasource.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.domain.model.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Converter
public class MatrixConverter implements AttributeConverter<int[][], String> {

    private static final Logger log = LoggerFactory.getLogger(MatrixConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public MatrixConverter() {
    }

    @Override
    public String convertToDatabaseColumn(int[][] attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Ошибка преобразования матрицы в JSON: {}", e.getMessage());
            return "[[0,0,0],[0,0,0],[0,0,0]]";
        }
    }

    @Override
    public int[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new int[Constant.ROW][Constant.COL];
        }
        try {
            return mapper.readValue(dbData, int[][].class);
        } catch (IOException e) {
            log.error("Ошибка преобразования JSON в матрицу: {}", e.getMessage());
            return new int[Constant.ROW][Constant.COL];
        }
    }
}
