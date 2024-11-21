package com.example.petfinder.Models;

import com.google.gson.JsonObject;

public class GraphQLResponse {
    private JsonObject data;
    private JsonObject errors;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public JsonObject getErrors() {
        return errors;
    }

    public void setErrors(JsonObject errors) {
        this.errors = errors;
    }
}


