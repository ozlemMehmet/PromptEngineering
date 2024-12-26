package org.Plucky;

import org.Plucky.model.Wine;
import org.Plucky.callapi.CallApi;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Fetching red wines...");
        List<Wine> wines = new ArrayList<>();
        try {
            wines = CallApi.getWines();
        } catch (CallApi.WineApiException e) {
            System.err.println("Failed to fetch wines: " + e.getMessage());
            System.exit(1);
        }
        
        System.out.println("\nFound " + wines.size() + " wines:");
        for (Wine wine : wines) {
            System.out.println(wine);
        }
    }
}