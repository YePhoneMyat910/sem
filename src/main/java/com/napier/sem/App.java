package com.napier.sem;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class App
{
    public static void main(String[] args)
    {
        try {
            // Connect to MongoDB
            MongoClient mongoClient = new MongoClient("mongo-dbserver");
            // Get or create the database
            MongoDatabase database = mongoClient.getDatabase("mydb");

            // Get or create a collection
            MongoCollection<Document> collection = database.getCollection("test");

            // Create a document to insert
            Document doc = new Document("name", "Kevin Sim")
                    .append("class", "DevOps")
                    .append("year", "2024")
                    .append("result", new Document("CW", 95).append("EX", 85));

            // Insert the document into the collection
            collection.insertOne(doc);

            // Retrieve and print the first document found
            Document myDoc = collection.find().first();
            System.out.println(myDoc.toJson());

            // Close the connection
            mongoClient.close();

        } catch (Exception e) {
            System.out.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }
}
