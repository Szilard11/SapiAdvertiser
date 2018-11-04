package ro.sapientia.ms.sapiadvertiser;

import com.google.firebase.database.FirebaseDatabase;

public class Database {
    FirebaseDatabase m_Database = null;
    Database()
    {
        this.m_Database = FirebaseDatabase.getInstance();
    }
    void writeToDatabase()
    {
    }
    String readFromDatabase()
    {
        String data = "";
        return data;
    }
}
