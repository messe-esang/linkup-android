package net.esang.mlinkup.dreamsecurity.model;

import java.util.Objects;

public class ItemFunction {
    private final String title;
    private final String description;
    private final int destination;

    public ItemFunction(String title, String description, int destination) {
        this.title = title;
        this.description = description;
        this.destination = destination;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemFunction that = (ItemFunction) o;
        return destination == that.destination && Objects.equals(title, that.title) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, destination);
    }

    @Override
    public String toString() {
        return "ItemFunction{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", destination=" + destination +
                '}';
    }
}
