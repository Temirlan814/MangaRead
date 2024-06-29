package com.example.demo;

import java.io.Serializable;
import java.util.Objects;

public class Comics implements Serializable {
    private String name;
    private String photo_url;
    public Comics(String name, String photo_url) {
        this.name = name;
        this.photo_url = photo_url;
    }
    public String getName() {
        return name;
    }
    public String getPhoto_url() {
        return photo_url;
    }
    @Override
    public boolean equals(Object obj) {
        // Проверяем, является ли объект тем же самым
        if (this == obj) {
            return true;
        }
        // Проверяем, является ли obj null или другим типом
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Приводим obj к типу Comics и сравниваем свойства
        Comics comics = (Comics) obj;
        return Objects.equals(name, comics.name) &&
                Objects.equals(photo_url, comics.photo_url);
        // Сравните другие поля, если необходимо
    }

    @Override
    public int hashCode() {
        // Используем `Objects.hash()` для генерации хэш-кода из свойств объекта
        return Objects.hash(name, photo_url);
        // Добавьте другие поля, если необходимо
    }
}
