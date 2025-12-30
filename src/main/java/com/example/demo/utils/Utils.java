package com.example.demo.utils;


import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {

    public static <T> boolean isRecordInTheDatabase(T id, CrudRepository<?, T> repository) {
        return repository.findById(id).isPresent();
    }


    private static Map<String, Object> getNotNullFields(Object updatedEntityDto)  {
        try {
            Map<String, Object> notNullFields = new HashMap<>();
            for (Field field : updatedEntityDto.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if (field.get(updatedEntityDto) != null){
                    notNullFields.put(field.getName(), field.get(updatedEntityDto));
                }
            }
            return notNullFields;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object fromUpdateDtoToEntity(Object entityToUpdate, Object updatedEntityDto)  {
        // prendo i campi che non sono null del mio update dto e dal mio entity da aggiornare

        try {
            Map<String, Object> updateDtoFields = getNotNullFields(updatedEntityDto);
        Map<String, Object> entityToUpdateFields = getNotNullFields(entityToUpdate);
        if (!isTheRecordDifferentFromTheUpdateDto(entityToUpdate, updatedEntityDto)){
            throw new RuntimeException("failed update due wrong json or record is already up to date");
        }

        Arrays.stream(entityToUpdate.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> updateDtoFields.containsKey(field.getName()))
                .filter( field -> {
                        // prendi solo i campi che tra l'update dto e l'entity
                        return !entityToUpdateFields.get(field.getName()).equals(updateDtoFields.get(field.getName()));
                })
                .forEach(
                        field -> {
                            try {
                                field.set(entityToUpdate, updateDtoFields.get(field.getName()));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
        return entityToUpdate;
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean isTheRecordDifferentFromTheUpdateDto(Object entityToUpdate, Object updatedEntityDto) throws IllegalAccessException {
        Map<String, Object> fieldsFromUpdateDto = getNotNullFields(updatedEntityDto);
        Map<String, Object> fieldsFromEntity = getNotNullFields(entityToUpdate);

        for (Map.Entry<String, Object> entry : fieldsFromEntity.entrySet()) {
            if (fieldsFromUpdateDto.containsKey(entry.getKey())){
                Object oldValue = entry.getValue();
                Object newValue = fieldsFromUpdateDto.get(entry.getKey());
                if (!Objects.equals(oldValue, newValue)){
                    return true;
                }
            }
        }

        return Arrays.stream(entityToUpdate.getClass().getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> fieldsFromUpdateDto.containsKey(field.getName()))
                .anyMatch(field -> {
                    Object oldValue = fieldsFromEntity.get(field.getName());
                    Object newValue = fieldsFromUpdateDto.get(field.getName());
                    return !Objects.equals(oldValue, newValue);
                });
    }



}
