package ru.allformine.afmcp.net.http;

public class GETResponse {
    public String response;
    public int responseCode;

    public GETResponse(String response) {
        this.response = response;
        this.responseCode = 200;
    }

    public GETResponse(String response, int responseCode) {
        this.response = response;
        this.responseCode = responseCode;
    }

    public GETResponse(int responseCode) {
        this.response = null;
        this.responseCode = responseCode;
    }

    public String toString(){
        return "GETResponse{\"response\"=" + this.response + ",\"responseCode\"=" + this.responseCode + "}";
    }
}
